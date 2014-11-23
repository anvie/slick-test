package com.ansvia.zufaro

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.driver.PostgresDriver.backend
import model.Tables._
import com.ansvia.zufaro.exception.{InvalidParameterException, AlreadyInvestedException, InsufficientBalanceException}
import com.ansvia.zufaro.model.{MutationKind, NoInitiator, Initiator}

//import com.ansvia.zufaro.macros.RequireMacro

/**
 * Author: robin
 * Date: 8/3/14
 * Time: 10:39 PM
 *
 */
object InvestorManager {

    object status {
        val INACTIVE = 0
        val ACTIVE = 1
        val SUSPENDED = 2
    }

    case class Address(address:String, city:String, province:String, country:String, postalCode:Long){
        def validate(){
            if (address == "")
                throw InvalidParameterException("No address")
            if (city == "")
                throw InvalidParameterException("No city")
            if (province == "")
                throw InvalidParameterException("No province")
            if (country == "")
                throw InvalidParameterException("No country")
            if (postalCode == 0L)
                throw InvalidParameterException("No postal code")
        }

    }
    case class Contact(address:Address, email:String, phone1:String, phone2:String)

    /**
     * Create new investor
     * @param name investor name
     * @param fullName investor name
     * @param role see [[com.ansvia.zufaro.model.InvestorRole]]
     * @return
     */
    def create(name:String, fullName:String, role:Int, password:String, contact:Contact) = {
        val passHash = PasswordUtil.hash(password)
        val userId = Zufaro.db.withTransaction { implicit session =>
            val userId = (Investor returning Investor.map(_.id)) += InvestorRow(0L, name, fullName,
                role, passHash,
                contact.address.address, contact.address.city,
                contact.address.province,
                contact.address.country,
                contact.address.postalCode,
                contact.email,
                contact.phone1,
                contact.phone2,
                status.ACTIVE)
            InvestorBalance += InvestorBalanceRow(0L, userId, 0.0)
            userId
        }
        getById(userId).get
    }

    /**
     * Get investor by id.
     * @param id investor id.
     * @return
     */
    def getById(id:Long) = {
        Zufaro.db.withSession { implicit session =>
            Investor.where(_.id === id).firstOption
        }
    }

    /**
     * Get investor by name
     * @param name investor name.
     * @return
     */
    def getByName(name:String) = {
        Zufaro.db.withSession { implicit session =>
            Investor.where(_.name === name).firstOption
        }
    }

    def getList(offset:Int, limit:Int):Seq[InvestorRow] = {
        Zufaro.db.withSession { implicit sess =>
            Investor.where(_.status === status.ACTIVE).drop(offset).take(limit).run
        }
    }

    /**
     * Delete and purge deleted investor related data.
     * CAUTION: this operation cannot be undone.
     * @param investor investor to delete from db.
     * @return
     */
    def delete(investor:InvestorRow) = {
        Zufaro.db.withTransaction { implicit sess =>
            Invest.where(_.invId === investor.id).delete
            Mutation.where(_.invId === investor.id).delete
            InvestorBalance.where(_.invId === investor.id).delete
            Investor.where(_.id === investor.id).delete
        }
    }


}

trait InvestorHelpers {

    import TimestampHelpers._

    implicit class investorWrapper(investor:InvestorRow){
        def invest(business:BusinessRow, amount:Double) = {
            Zufaro.db.withTransaction { implicit sess =>

                // check is already invested
                val qex = for { iv <- Invest if iv.busId === business.id && iv.invId === investor.id } yield iv.amount

                val alreadyAmount = qex.firstOption.getOrElse(0.0)
                if (alreadyAmount > 0.0)
                    throw AlreadyInvestedException("Already invested: %s -> %s amount %.02f".format(investor.name, business.name, alreadyAmount))

                checkBalance(amount)

                Invest += InvestRow(0L, investor.id, business.id, amount, BusinessKind.SINGLE, now())

                // debit investor balance
                val q = for { bal <- InvestorBalance if bal.invId === investor.id } yield bal.amount
                val curAmount = q.first()
                q.update(curAmount - amount)

                // write mutation journal
                Mutation += MutationRow(0L, investor.id, MutationKind.DEBIT, amount,
                    Some(f"for business investment: ${business.name} ${business.id}"), None, now())
            }
        }

        /**
         * Invest into a group of business, will div by system.
         * @param businessGroup business group.
         * @param amount invest amount.
         * @return
         */
        def invest(businessGroup:BusinessGroupRow, amount:Double) = {
            Zufaro.db.withTransaction { implicit sess:backend.SessionDef =>
//
//                import BusinessHelpers._
//
//                businessGroup.getMembers(0).foreach { bus =>
//
//                }

                // check balance
                checkBalance(amount)

                Invest += InvestRow(0L, investor.id, businessGroup.id, amount, BusinessKind.GROUP, now())

                // debit investor balance
                val q = for { bal <- InvestorBalance if bal.invId === investor.id } yield bal.amount
                val curAmount = q.first()
                q.update(curAmount - amount)
            }
        }

        def removeInvestment(business:BusinessRow) = {
            Zufaro.db.withTransaction { implicit sess =>

                Invest.where(iv => iv.busId === business.id && iv.busKind === BusinessKind.SINGLE)
                    .delete

            }
        }


        /**
         * Check balance will throw InsufficientBalanceException when
         * balance is not enough for transaction.
         * @param amount amount to check.
         * @param sess session.
         * @return
         */
        private def checkBalance(amount:Double)(implicit sess:backend.SessionDef){
            val q = for { bal <- InvestorBalance if bal.invId === investor.id } yield bal.amount
            val curAmount = q.first()

            if(curAmount < amount)
                throw InsufficientBalanceException(f"Insufficient balance $curAmount%.02f < $amount%.02f")
        }

        def addBalance(by:Double, ref:Option[String]=None, initiator:Initiator=NoInitiator) = {
            Zufaro.db.withTransaction { implicit sess =>
                val q = for { bal <- InvestorBalance if bal.invId === investor.id } yield bal.amount
                q.update(q.first() + by)

                // write journal
                if (by > -0.1){
                    Mutation += MutationRow(0L, investor.id, MutationKind.CREDIT, by,
                        ref, Some(initiator.toString), now())
                }else{
                    Mutation += MutationRow(0L, investor.id, MutationKind.DEBIT, -by,
                        ref, Some(initiator.toString), now())
                }
            }
        }

        def subBalance(by:Double, ref:Option[String]=None, initiator:Initiator=NoInitiator) = {
            addBalance(-by, ref, initiator)
        }


        def getBalance = {
            Zufaro.db.withSession( implicit sess => InvestorBalance.where(_.invId === investor.id)
                  .firstOption.map(_.amount).getOrElse(0.0) )
        }

        def getBusiness(offset:Int, limit:Int, state:Int=BusinessManager.state.ANY):Seq[BusinessRow] = {
            Zufaro.db.withSession { implicit sess =>
                val rv =
                    state match {
                        case BusinessManager.state.ANY =>
                            for {
                                i <- Invest if i.invId === investor.id
                                bus <- Business if bus.id === i.busId
                            } yield bus
                        case _state =>
                            for {
                                i <- Invest if i.invId === investor.id
                                bus <- Business if bus.id === i.busId && bus.state === _state
                            } yield bus
                    }

                rv.drop(offset).take(limit).run
            }
        }

        /**
         * Get investor share for selected business.
         * @param bus business.
         * @return
         */
        def getShare(bus:BusinessRow) = {
            Zufaro.db.withSession { implicit sess =>
                val q = for {
                    iv <- Invest if iv.invId === investor.id && iv.busId === bus.id
                } yield iv.amount
                q.firstOption.getOrElse(0.0)
            }
        }


        def getDepositMutations(offset:Int, limit:Int):Seq[MutationRow] = {
            Zufaro.db.withSession { implicit sess =>
                val rv = for {
                    m <- Mutation if m.invId === investor.id
                } yield m
                rv.drop(offset).take(limit).sortBy(_.ts.desc).run
            }
        }



    }
}

object InvestorHelpers extends InvestorHelpers



