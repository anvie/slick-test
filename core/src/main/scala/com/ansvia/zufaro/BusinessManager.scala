package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import model.Tables._
import scala.collection.JavaConversions._
import scala.slick.lifted
import java.sql.Timestamp
import java.util.Date

/**
 * Author: robin
 * Date: 8/3/14
 * Time: 10:39 PM
 *
 */

object BusinessManager {


    def create(name:String, desc:String) = {
        Zufaro.db.withSession { implicit sess =>
            (Business returning Business.map(_.id)) += BusinessRow(0L, name, desc, 0.0, 0.0)
        }
    }

    def getById(id:Long) = {
        Zufaro.db.withSession { implicit session =>
            Business.where(_.id === id).firstOption
        }
    }

    def getByName(name:String) = {
        Zufaro.db.withSession { implicit session =>
            Business.where(_.name === name).firstOption
        }
    }

}

trait BusinessHelpers {

    implicit class businessWrapper(business:BusinessRow){

        private def p(d:Double) = d / 100.0

        def addProfit(amount:Double, mutator:{def id:Long}, mutatorRole:Int, additionalInfo:String=""){
            Zufaro.db.withTransaction { implicit sess =>
                BusinessProfit += BusinessProfitRow(0L, business.id, amount, new Timestamp(new Date().getTime), mutator.id,
                    mutatorRole, additionalInfo:String)

                // kalkulasi bagi hasil

                val sysProfit = p(business.divideSys) * amount
                val invProfit = p(business.divideInvest) * amount


                val ivIb = for {
                    iv <- Invest if iv.busId === business.id
                    ib <- InvestorBalance if ib.invId === iv.invId
                } yield (iv, ib)

                ivIb.foreach { case (iv, ib) =>

                    val curBal = ib.amount + invProfit

                    InvestorBalance.filter(_.id === ib.id).map(_.amount).update(curBal)

                    // tulis journal
                    Credit += CreditRow(0L, iv.invId, invProfit, Some("bagi hasil dari bisnis " + business.name), new Timestamp(new Date().getTime))
                }
            }
        }

        def getProfit:Double = {
            Zufaro.db.withSession( implicit sess =>
                BusinessProfit.where(_.id === business.id).map(_.amount).sum.run.getOrElse(0.0)
            )
        }


    }
}

object BusinessHelper extends BusinessHelpers
