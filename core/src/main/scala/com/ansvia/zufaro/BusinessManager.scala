package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import model.Tables._
import scala.collection.JavaConversions._
import scala.slick.lifted
import java.sql.Timestamp
import java.util.Date
import com.ansvia.zufaro.exception.ZufaroException

/**
 * Author: robin
 * Date: 8/3/14
 * Time: 10:39 PM
 *
 */

object BusinessManager {

    object state {

        /**
         * when a business is a draft,
         * use for project where not in production yet.
         */
        val DRAFT:Int = 1

        /**
         * when a business is in production.
         */
        val PRODUCTION:Int = 2
    }

    /**
     * Create new business
     * @param name business name.
     * @param desc business description.
     * @param fund
     * @param divInvestor
     * @param state see [[com.ansvia.zufaro.BusinessManager.state]]
     * @return
     */
    def create(name:String, desc:String, fund:Double, divInvestor:Double, state:Int) = {
        val id = Zufaro.db.withSession { implicit sess =>
            (Business returning Business.map(_.id)) += BusinessRow(0L, name, desc, fund, divInvestor, state)
        }
        getById(id).get
    }

    /**
     * Create a business group (experimental)
     * @param name business name.
     * @param desc business description.
     * @return
     */
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

    import BusinessGroupHelpers._
    import BusinessManager.state._

    implicit class businessWrapper(business:BusinessRow){

        private def p(d:Double) = d / 100.0

        def addProfit(amount:Double, mutator:{def id:Long}, mutatorRole:Int, additionalInfo:String=""):BusinessProfitRow = {

            if (business.state != PRODUCTION)
                throw new ZufaroException("business not in production state, got %d".format(business.state), 709)

            val busProfitId =
            Zufaro.db.withTransaction { implicit sess =>

                val _busProfitId = (BusinessProfit returning BusinessProfit.map(_.id)) += BusinessProfitRow(0L, business.id, amount, new Timestamp(new Date().getTime), mutator.id,
                    mutatorRole, additionalInfo:String)

                // kalkulasi bagi hasil

                //                val sysProfit = p(business.divideSys) * amount
                //                val invProfit = p(business.divideInvest) * amount


                val ivIb = for {
                    iv <- Invest if iv.busId === business.id && iv.busKind === BusinessKind.SINGLE
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

                val groupQ = for {
                    link <- BusinessGroupLink if link.busId === business.id
                    iv <- Invest if iv.busId === link.busGroupId && iv.busKind === BusinessKind.GROUP
                    g <- BusinessGroup if g.id === link.busGroupId
                    inv <- Investor if inv.id === iv.invId
                    ib <- InvestorBalance if ib.invId === inv.id
                } yield (iv.amount, inv.id, g, inv, ib)

                //                println("groupQ statement: " + groupQ.selectStatement)

                groupQ.foreach { case (investAmount, investId, g, inv, ib) =>
                    val investAmountNorm = investAmount / g.getMemberCount.toDouble

                    val margin = (investAmountNorm / business.fund) * amount
                    val dividen = margin * p(business.divideInvest)
                    val curBal = ib.amount + dividen

                    InvestorBalance.filter(_.id === ib.id).map(_.amount).update(curBal)

                    // tulis journal
                    Credit += CreditRow(0L, investId, dividen,
                        Some("bagi hasil dari bisnis " + business.name + " group of " + g.name), new Timestamp(new Date().getTime))
                }

                _busProfitId
            }

            // returning succeeded added profit info db object
            Zufaro.db.withSession(implicit sess => BusinessProfit.where(_.id === busProfitId).firstOption.get)
        }

        def getProfit:Double = {
            Zufaro.db.withSession( implicit sess =>
                BusinessProfit.where(_.id === business.id).map(_.amount).sum.run.getOrElse(0.0)
            )
        }


    }

}

trait BusinessGroupHelpers {

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

        def getMembers(offset:Int, limit:Int = -1) = {
            lazy val q = for {
                link <- BusinessGroupLink if link.busGroupId === businessGroup.id
                bus <- Business if bus.id === link.busId
            } yield bus
            if (limit > 0)
                q.drop(offset).take(limit)
            else
                q.drop(offset)
        }

        def getMemberCount:Int = {
            Zufaro.db.withSession(implicit sess =>
                BusinessGroupLink.where(_.busGroupId === businessGroup.id).length.run)
        }



    }
}

object BusinessHelpers extends BusinessHelpers
object BusinessGroupHelpers extends BusinessGroupHelpers

object BusinessKind {
    val SINGLE = 1
    val GROUP = 2
}

