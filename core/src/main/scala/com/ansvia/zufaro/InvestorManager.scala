package com.ansvia.zufaro

import com.ansvia.zufaro.exception.{AlreadyInvestedException, InsufficientBalanceException, InvalidParameterException}
import com.ansvia.zufaro.model.Tables._
import com.ansvia.zufaro.model.{IdentityType, Initiator, MutationKind, NoInitiator}

import scala.slick.driver.PostgresDriver.backend
import scala.slick.driver.PostgresDriver.simple._

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
//    case class Contact(address:Address, email:String, phone1:String, phone2:String)

    /**
     * Create new investor
     * @param investor to create.
     * @param password plain password
     * @return
     */
    def create(investor:Investor, password:String, contact:InvestorContact) = {
        assert(investor != null, "investor is null")
        assert(password != null, "password can't be null")
        assert(contact != null, "contact can't be null")
        assert(investor.id == 0L, "for creation don't accept id here")
        assert(contact.investorId == 0L, "for creation don't accept investorId here")

        val passHash = PasswordUtil.hash(password)
        val userId = Zufaro.db.withTransaction { implicit session =>

            val userId = (Investors.map(s => (s.name, s.fullName, s.role, s.sex, s.nation, s.birthPlace, s.birthDate, 
                s.religion, s.education, s.titleFront, s.titleBack, s.maritalStatus, s.motherName, s.passhash, 
                s.status)) returning Investors.map(_.id)) += 
                (investor.name, investor.fullName, investor.role, investor.sex, investor.nation, investor.birthPlace, investor.birthDate, investor.religion, investor.education,
                    investor.titleFront, investor.titleBack, investor.maritalStatus, investor.motherName, passHash, status.ACTIVE)

            InvestorBalances.map(s => (s.invId, s.amount)) += (userId, 0.0)

            InvestorContacts.map(s => (s.address, s.city, s.country, s.district, s.email, s.identityBasedOn,
                s.investorId, s.kind, s.homePhone, s.mobilePhone, s.bbPin, s.postalCode, s.province, s.village)) +=
                (contact.address, contact.city, contact.country, contact.district, contact.email,
                    contact.identityBasedOn, userId, contact.kind, contact.homePhone, contact.mobilePhone, contact.bbPin,
                    contact.postalCode,
                    contact.province, contact.village)

            userId
        }
        getById(userId).get
    }

    // @TODO(robin): test this
    def updateBasicInfo(invId:Long, investor:Investor, password:Option[String]=None, identityBasedOn:Int=IdentityType.KTP_BASED) = {
        val newId = if (investor.id > 0L && investor.id != invId)
            investor.id
        else
            invId   

        lazy val passHash = PasswordUtil.hash(password.get)
        Zufaro.db.withTransaction { implicit session =>
            if (password.isDefined){
                if (invId != newId){
                    (Investors.map(s => (s.id, s.name, s.fullName, s.role, s.sex, s.nation, s.birthPlace, s.birthDate,
                        s.religion, s.education, s.titleFront, s.titleBack, s.maritalStatus, s.motherName,
                        s.status, s.passhash))).update(
                            (newId, investor.name, investor.fullName, investor.role, investor.sex, investor.nation, investor.birthPlace, investor.birthDate, investor.religion, investor.education,
                                investor.titleFront, investor.titleBack, investor.maritalStatus, investor.motherName, status.ACTIVE, passHash))
                }else{
                    (Investors.map(s => (s.name, s.fullName, s.role, s.sex, s.nation, s.birthPlace, s.birthDate,
                        s.religion, s.education, s.titleFront, s.titleBack, s.maritalStatus, s.motherName,
                        s.status, s.passhash))).update(
                            (investor.name, investor.fullName, investor.role, investor.sex, investor.nation, investor.birthPlace, investor.birthDate, investor.religion, investor.education,
                                investor.titleFront, investor.titleBack, investor.maritalStatus, investor.motherName, status.ACTIVE, passHash))
                }
            }else{
                if (invId != newId){
                    (Investors.map(s => (s.id, s.name, s.fullName, s.role, s.sex, s.nation, s.birthPlace, s.birthDate,
                        s.religion, s.education, s.titleFront, s.titleBack, s.maritalStatus, s.motherName,
                        s.status))).update(
                            (newId, investor.name, investor.fullName, investor.role, investor.sex, investor.nation, investor.birthPlace, investor.birthDate, investor.religion, investor.education,
                                investor.titleFront, investor.titleBack, investor.maritalStatus, investor.motherName, status.ACTIVE))
                }else{
                    (Investors.map(s => (s.name, s.fullName, s.role, s.sex, s.nation, s.birthPlace, s.birthDate,
                        s.religion, s.education, s.titleFront, s.titleBack, s.maritalStatus, s.motherName,
                        s.status))).update(
                            (investor.name, investor.fullName, investor.role, investor.sex, investor.nation, investor.birthPlace, investor.birthDate, investor.religion, investor.education,
                                investor.titleFront, investor.titleBack, investor.maritalStatus, investor.motherName, status.ACTIVE))
                }

            }
        }
        getById(newId).get
    }


    /**
     * Update investor contact data.
     * @param invId id of investor to update.
     * @param contact new contact. the contact.kind is not used, overrided by `contactType` parameter.
     * @param contactType the contact type, see: [[com.ansvia.zufaro.model.ContactType]]
     * @return
     */
    def updateContactData(invId:Long, contact:InvestorContact, contactType:Int) = {
        Zufaro.db.withTransaction { implicit sess =>

            InvestorContacts.filter(_.investorId === invId)
                .map(s => (s.address, s.village, s.district, s.city, s.province, s.country, s.postalCode, s.email,
                    s.homePhone, s.mobilePhone, s.bbPin, s.identityBasedOn, s.kind))
                .update(
                    contact.address, contact.village, contact.district, contact.city, contact.province, contact.country,
                    contact.postalCode, contact.email, contact.homePhone, contact.mobilePhone, contact.bbPin,
                    contact.identityBasedOn, contactType
                )
        }
    }

    def updateID(invId:Long, newId:Long) = {
        Zufaro.db.withTransaction { implicit sess =>

//            InvestorContacts.filter(_.investorId === invId).map(_.investorId).update(newId)
//            InvestorOtherContacts.filter(_.investorId === invId).map(_.investorId).update(newId)

            Investors.filter(_.id === invId).map(_.id).update(newId)

        }
    }



    /**
     * Get investor by id.
     * @param id investor id.
     * @return
     */
    def getById(id:Long) = {
        Zufaro.db.withSession { implicit session =>
            Investors.filter(_.id === id).firstOption
        }
    }

    /**
     * Get investor by name
     * @param name investor name.
     * @return
     */
    def getByName(name:String) = {
        Zufaro.db.withSession { implicit session =>
            Investors.filter(_.name === name).firstOption
        }
    }

    def getList(offset:Int, limit:Int):Seq[Investor] = {
        Zufaro.db.withSession { implicit sess =>
            Investors.filter(_.status === status.ACTIVE).drop(offset).take(limit).run
        }
    }

    /**
     * Delete and purge deleted investor related data.
     * CAUTION: this operation cannot be undone.
     * @param investor investor to delete from db.
     * @return
     */
    def delete(investor:Investor) = {
        Zufaro.db.withTransaction { implicit sess =>
            Invests.filter(_.investorId === investor.id).delete
            Mutations.filter(_.invId === investor.id).delete
            InvestorBalances.filter(_.invId === investor.id).delete
            Investors.filter(_.id === investor.id).delete
        }
    }


}

trait InvestorHelpers {

    implicit class investorWrapper(investor:Investor){
        def invest(business:Business, amount:Double) = {
            Zufaro.db.withTransaction { implicit sess =>

                // check is already invested
                val qex = for { 
                    iv <- Invests if iv.businessId === business.id && iv.investorId === investor.id 
                } yield iv.amount

                val alreadyAmount = qex.firstOption.getOrElse(0.0)
                if (alreadyAmount > 0.0)
                    throw AlreadyInvestedException("Already invested: %s -> %s amount %.02f".format(investor.name, business.name, alreadyAmount))

                checkBalance(amount)

                Invests.map(s => (s.investorId, s.businessId, s.amount, s.businessKind)) +=
                    (investor.id, business.id, amount, BusinessKind.SINGLE)
                
                //+= InvestRow(0L, investor.id, business.id, amount, BusinessKind.SINGLE, now())

                // debit investor balance
                val q = for { bal <- InvestorBalances if bal.invId === investor.id } yield bal.amount
                val curAmount = q.first
                q.update(curAmount - amount)

                // write mutation journal
//                Mutation += Mutation(0L, investor.id, MutationKind.DEBIT, amount,
//                    Some(f"for business investment: ${business.name} ${business.id}"), None, now())
                
                Mutations.map(s => (s.invId, s.kind, s.amount, s.ref)) += 
                    (investor.id, MutationKind.DEBIT, amount,
                        Some(f"for business investment: ${business.name} ${business.id}"))
            }
        }

        /**
         * Invest into a group of business, will div by system.
         * @param businessGroup business group.
         * @param amount invest amount.
         * @return
         */
        def invest(businessGroup:BusinessGroup, amount:Double) = {
            Zufaro.db.withTransaction { implicit sess:backend.SessionDef =>
//
//                import BusinessHelpers._
//
//                businessGroup.getMembers(0).foreach { bus =>
//
//                }

                // check balance
                checkBalance(amount)

//                Invest += InvestRow(0L, investor.id, businessGroup.id, amount, BusinessKind.GROUP, now())

                Invests.map(s => (s.investorId, s.businessId, s.businessKind, s.amount)) +=
                    (investor.id, businessGroup.id, BusinessKind.GROUP, amount)

                // debit investor balance
                val q = for { bal <- InvestorBalances if bal.invId === investor.id } yield bal.amount
                val curAmount = q.first
                q.update(curAmount - amount)
            }
        }

        def removeInvestment(business:Business) = {
            Zufaro.db.withTransaction { implicit sess =>

                Invests.filter(iv => iv.businessId === business.id && iv.busKind === BusinessKind.SINGLE)
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
            val q = for { bal <- InvestorBalances if bal.invId === investor.id } yield bal.amount
            val curAmount = q.first

            if(curAmount < amount)
                throw InsufficientBalanceException(f"Insufficient balance $curAmount%.02f < $amount%.02f")
        }

        def addBalance(by:Double, ref:Option[String]=None, initiator:Initiator=NoInitiator) = {
            Zufaro.db.withTransaction { implicit sess =>
                val q = for { bal <- InvestorBalances if bal.invId === investor.id } yield bal.amount
                q.update(q.first + by)

                // write journal
                if (by > -0.1){
//                    Mutation += Mutation(0L, investor.id, MutationKind.CREDIT, by,
//                        ref, Some(initiator.toString), now())

                    Mutations.map(s => (s.invId, s.kind, s.amount, s.ref, s.initiator)) +=
                        (investor.id, MutationKind.CREDIT, by, ref, Some(initiator.toString))

                }else{
//                    Mutation += Mutation(0L, investor.id, MutationKind.DEBIT, -by,
//                        ref, Some(initiator.toString), now())

                    Mutations.map(s => (s.invId, s.kind, s.amount, s.ref, s.initiator)) +=
                        (investor.id, MutationKind.DEBIT, -by, ref, Some(initiator.toString))
                }
            }
        }

        def subBalance(by:Double, ref:Option[String]=None, initiator:Initiator=NoInitiator) = {
            addBalance(-by, ref, initiator)
        }


        def getBalance = {
            Zufaro.db.withSession( implicit sess => InvestorBalances.filter(_.invId === investor.id)
                  .firstOption.map(_.amount).getOrElse(0.0) )
        }

        def getBusiness(offset:Int, limit:Int, state:Int=BusinessManager.state.ANY):Seq[Business] = {
            Zufaro.db.withSession { implicit sess =>
                val rv =
                    state match {
                        case BusinessManager.state.ANY =>
                            for {
                                i <- Invests if i.investorId === investor.id
                                bus <- Businesses if bus.id === i.businessId
                            } yield bus
                        case _state =>
                            for {
                                i <- Invests if i.investorId === investor.id
                                bus <- Businesses if bus.id === i.businessId && bus.state === _state
                            } yield bus
                    }

                rv.drop(offset).take(limit).run
            }
        }

        def getContact = {
            Zufaro.db.withSession { implicit sess =>
                val q = for {
                    contact <- InvestorContacts if contact.investorId === investor.id
                } yield contact
                q.first
            }
        }


        /**
         * Get investor share for selected business.
         * @param bus business.
         * @return
         */
        def getShare(bus:Business) = {
            Zufaro.db.withSession { implicit sess =>
                val q = for {
                    iv <- Invests if iv.investorId === investor.id && iv.businessId === bus.id
                } yield iv.amount
                q.firstOption.getOrElse(0.0)
            }
        }


        def getDepositMutations(offset:Int, limit:Int):Seq[Mutation] = {
            Zufaro.db.withSession { implicit sess =>
                val rv = for {
                    m <- Mutations if m.invId === investor.id
                } yield m
                rv.drop(offset).take(limit).sortBy(_.ts.desc).run
            }
        }



    }
}

object InvestorHelpers extends InvestorHelpers



