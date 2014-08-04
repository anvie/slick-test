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


    def create(name:String, desc:String, fund:Double, divSys:Double, divInvestor:Double) = {
        val id = Zufaro.db.withSession { implicit sess =>
            (Business returning Business.map(_.id)) += BusinessRow(0L, name, desc, fund, divSys, divInvestor)
        }
        getById(id).get
    }

    def createGroup(name:String, desc:String):BusinessGroupRow = {
        val id = Zufaro.db.withSession { implicit sess =>
            (BusinessGroup returning BusinessGroup.map(_.id)) += BusinessGroupRow(0L, name, desc)
        }
        getGroupById(id).get
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

    def getGroupById(id:Long) = {
        Zufaro.db.withSession { implicit session =>
            BusinessGroup.where(_.id === id).firstOption
        }
    }

    def getGroupByName(name:String) = {
        Zufaro.db.withSession { implicit session =>
            BusinessGroup.where(_.name === name).firstOption
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

//                val sysProfit = p(business.divideSys) * amount
//                val invProfit = p(business.divideInvest) * amount


                val ivIb = for {
                    iv <- Invest if iv.busId === business.id
                    ib <- InvestorBalance if ib.invId === iv.invId
                } yield (iv, ib)

                ivIb.foreach { case (iv, ib) =>

                    val margin = (iv.amount / business.fund) * amount
                    val dividen = margin * p(business.divideInvest)
                    val curBal = ib.amount + dividen

                    InvestorBalance.filter(_.id === ib.id).map(_.amount).update(curBal)

                    // tulis journal
                    Credit += CreditRow(0L, iv.invId, dividen, Some("bagi hasil dari bisnis " + business.name), new Timestamp(new Date().getTime))
                }
            }
        }

        def getProfit:Double = {
            Zufaro.db.withSession( implicit sess =>
                BusinessProfit.where(_.id === business.id).map(_.amount).sum.run.getOrElse(0.0)
            )
        }


    }

    implicit class businessGroupWrapper(businessGroup:BusinessGroupRow){

        /**
         * Add group member.
         * @param business member to remove.
         * @return
         */
        def addMembers(business:BusinessRow*) = {
            Zufaro.db.withTransaction { implicit sess =>
                business.foreach { bus =>
                    BusinessGroupLink += BusinessGroupLinkRow(0L, businessGroup.id, bus.id)
                }
            }
        }

        /**
         * Remove group member.
         * @param business member to remove.
         * @return
         */
        def rmMember(business:BusinessRow) = {
            Zufaro.db.withSession { implicit sess =>
                BusinessGroupLink += BusinessGroupLinkRow(0L, businessGroup.id, business.id)
                BusinessGroupLink.where(link => link.busId === business.id && link.busGroupId === businessGroup.id).delete
            }
        }

        def getMembers(offset:Int, limit:Int) = {
            lazy val q = for {
                link <- BusinessGroupLink if link.busGroupId === businessGroup.id
                bus <- Business if bus.id === link.busId
            } yield bus
            q.drop(offset).take(limit)
        }

    }
}

object BusinessHelper extends BusinessHelpers

object BusinessKind {
    val SINGLE = 1
    val GROUP = 2
}

