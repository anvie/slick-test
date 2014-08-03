package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import model.Tables._
import com.ansvia.zufaro.model.UserRole
import com.ansvia.zufaro.exception.{AlreadyInvestedException, ZufaroException, InsufficientBalanceException}
//import com.ansvia.zufaro.macros.RequireMacro

/**
 * Author: robin
 * Date: 8/3/14
 * Time: 10:39 PM
 *
 */
object InvestorManager {


    def create(name:String, role:Int) = {
        Zufaro.db.withTransaction { implicit session =>
            val userId = (Investor returning Investor.map(_.id)) += InvestorRow(0L, name, role)
            InvestorBalance += InvestorBalanceRow(0L, userId, 0.0)
        }
    }

    def getById(id:Long) = {
        Zufaro.db.withSession { implicit session =>
            Investor.where(_.id === id).firstOption
        }
    }

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

                Invest += InvestRow(0, investor.id, business.id, amount)
                val q = for { bal <- InvestorBalance if bal.invId === investor.id } yield bal.amount
                val curAmount = q.first()

                if(curAmount < amount)
                    throw InsufficientBalanceException(f"Insufficient balance $curAmount%f < $amount%f")

                q.update(curAmount - amount)
            }
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



