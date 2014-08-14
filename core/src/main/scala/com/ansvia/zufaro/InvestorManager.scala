package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import scala.slick.driver.H2Driver.backend
import model.Tables._
import com.ansvia.zufaro.exception.{AlreadyInvestedException, InsufficientBalanceException}
//import com.ansvia.zufaro.macros.RequireMacro

/**
 * Author: robin
 * Date: 8/3/14
 * Time: 10:39 PM
 *
 */
object InvestorManager {


    /**
     * Create new investor
     * @param name investor name
     * @param role see [[com.ansvia.zufaro.model.InvestorRole]]
     * @return
     */
    def create(name:String, role:Int, password:String) = {
        val passHash = PasswordUtil.hash(password)
        val userId = Zufaro.db.withTransaction { implicit session =>
            val userId = (Investor returning Investor.map(_.id)) += InvestorRow(0L, name, role, passHash)
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


}

trait InvestorHelpers {

    implicit class investorWrapper(investor:InvestorRow){
        def invest(business:BusinessRow, amount:Double) = {
            Zufaro.db.withTransaction { implicit sess =>

                // check is already invested
                val qex = for { iv <- Invest if iv.busId === business.id && iv.invId === investor.id } yield iv.amount

                val alreadyAmount = qex.firstOption.getOrElse(0.0)
                if (alreadyAmount > 0.0)
                    throw AlreadyInvestedException("Already invested: %s -> %s amount %.02f".format(investor.name, business.name, alreadyAmount))

                checkBalance(amount)

                Invest += InvestRow(0L, investor.id, business.id, amount, BusinessKind.SINGLE)

                // debit investor balance
                val q = for { bal <- InvestorBalance if bal.invId === investor.id } yield bal.amount
                val curAmount = q.first()
                q.update(curAmount - amount)
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

                Invest += InvestRow(0L, investor.id, businessGroup.id, amount, BusinessKind.GROUP)

                // debit investor balance
                val q = for { bal <- InvestorBalance if bal.invId === investor.id } yield bal.amount
                val curAmount = q.first()
                q.update(curAmount - amount)
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
                throw InsufficientBalanceException(f"Insufficient balance $curAmount%f < $amount%f")
        }


        def rmInvest(business:BusinessRow) = {
            Zufaro.db.withTransaction { implicit sess =>
                Invest.where(iv => iv.busId === business.id && iv.invId === investor.id).delete
            }
        }

        def addBalance(by:Double) = {
            Zufaro.db.withTransaction { implicit sess =>
                val q = for { bal <- InvestorBalance if bal.invId === investor.id } yield bal.amount
                q.update(q.first() + by)
            }
        }


        def getBalance = {
            Zufaro.db.withSession( implicit sess => InvestorBalance.where(_.invId === investor.id)
                  .firstOption.map(_.amount).getOrElse(0.0) )
        }
    }
}

object InvestorHelpers extends InvestorHelpers



