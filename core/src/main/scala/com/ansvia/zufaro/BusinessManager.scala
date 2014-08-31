package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import scala.slick.driver.H2Driver.backend
import model.Tables._
import com.ansvia.zufaro.exception.{IllegalStateException, ZufaroException}
import com.ansvia.commons.logging.Slf4jLogger
import com.ansvia.zufaro.model.{NoInitiator, Initiator, MutationKind, ShareMethod}
import java.util.Date
import com.ansvia.zufaro.BusinessManager.ShareReport

/**
 * Author: robin
 * Date: 8/3/14
 * Time: 10:39 PM
 *
 */

object BusinessManager {

    import TimestampHelpers._

    object state {

        val ANY:Int = 0

        /**
         * when a business is a draft,
         * use for project where not in production yet.
         */
        val DRAFT:Int = 1

        /**
         * when a business is in production.
         */
        val PRODUCTION:Int = 2

        val CLOSED:Int = 3

        def toStr(s:Int) = s match {
            case DRAFT => "DRAFT"
            case PRODUCTION => "PRODUCTION"
            case CLOSED => "CLOSED"
        }

    }

    object sharePeriod {
        val WEEKLY = 1
        val MONTHLY = 2
        val YEAR = 3
    }

    object reportStatus {
        val NEW = 0
        val ACCEPTED = 1
        val INVALID = 2
    }

    case class ShareReport(investorId:Long, investorName:String, amount:Double, date:Date)

    /**
     * Create new business
     * @param name business name.
     * @param desc business description.
     * @param tags business tags, separated by comma.
     * @param fund fund
     * @param share investment share.
     * @param state see [[com.ansvia.zufaro.BusinessManager.state]]
     * @param shareTime time to share.
     * @param _sharePeriod share period.
     * @return
     */
    def create(name:String, desc:String, tags:String, fund:Double, share:Double, state:Int,
               shareTime:Int=1, _sharePeriod:Int=sharePeriod.MONTHLY) = {

        val id = Zufaro.db.withSession { implicit sess =>

            val _id = (Business returning Business.map(_.id)) += BusinessRow(0L, name, desc, tags, fund,
                share, state, shareTime, _sharePeriod, 0.0, now())

            // create business account balance
//            BusinessBalance += BusinessBalanceRow(0L, _id, 0.0)

            _id
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

    def getList(offset:Int, limit:Int, _state:Int):Seq[BusinessRow] = {
        Zufaro.db.withSession { implicit sess =>
            _state match {
                case state.ANY =>
                    Business.drop(offset).take(limit).run
                case s =>
                    Business.where(_.state === s).drop(offset).take(limit).run
            }
        }
    }

    def getRunningBusinessList(offset:Int, limit:Int) = {
        getList(offset, limit, state.PRODUCTION)
    }

    def getProjectBusinessList(offset:Int, limit:Int) = {
        getList(offset, limit, state.DRAFT)
    }

    def getClosedBusinessList(offset:Int, limit:Int) = {
        getList(offset, limit, state.CLOSED)
    }


    def delete(business:BusinessRow){
        Zufaro.db.withTransaction { implicit sess =>
            Invest.where(_.busId === business.id).delete
            ProjectWatcher.where(_.busId === business.id).delete
            BusinessProfit.where(_.busId === business.id).delete
            Business.where(_.id === business.id).delete
        }
    }



}

trait BusinessHelpers {

    import BusinessManager.state._
    import BusinessManager.reportStatus
    import TimestampHelpers._
    import ZufaroHelpers._

    implicit class businessWrapper(business:BusinessRow) extends Slf4jLogger {

        private def p(d:Double) = d / 100.0



        def addProfit(omzet:Double, profit:Double, mutator:{def id:Long}, mutatorRole:Int, additionalInfo:String=""):BusinessProfitRow = {

            if (business.state != PRODUCTION)
                throw new ZufaroException("business not in production state, got %d".format(business.state), 709)

            val busProfitId =
            Zufaro.db.withTransaction { implicit sess =>

                val _busProfitId = (BusinessProfit returning BusinessProfit.map(_.id)) += 
                    BusinessProfitRow(0L, business.id, omzet, 
                    profit, now(), mutator.id,
                    mutatorRole, additionalInfo, shared=false, sharedAt=now(), reportStatus.NEW)

                debug(f"profit added for business id `${business.id}`, omzet $omzet%.02f, " +
                    f"profit $profit%.02f, ref id: ${_busProfitId}")

                _busProfitId
            }

            // returning succeeded added profit info db object
            Zufaro.db.withSession(implicit sess => BusinessProfit.where(_.id === busProfitId).firstOption.get)
        }

        def getProfit:Double = {
            Zufaro.db.withSession( implicit sess =>
                BusinessProfit.where(_.busId === business.id).map(_.profit).sum.run.getOrElse(0.0)
            )
        }

        def getIncomeReport(offset:Int, limit:Int):Seq[BusinessProfitRow] = {
            Zufaro.db.withSession { implicit sess =>
                BusinessProfit.where(_.busId === business.id).sortBy(_.ts.desc).run
            }
        }
        
        def getShareReport(busProfId:Long, offset:Int, limit:Int):Seq[ShareReport] = {
            Zufaro.db.withSession { implicit sess =>
                val q = for {
                    b <- ProfitShareJournal if b.busId === business.id && b.busProfId === busProfId
                    inv <- Investor if inv.id === b.invId
                } yield (inv.id, inv.name, b.amount, b.ts)
            
                q.run.map { case (investorId, investorName, amount, ts) =>
                    ShareReport(investorId, investorName, amount, new Date(ts.getTime))
                }
            }
        }

        def getAccountMutationReport(offset:Int, limit:Int):Seq[BusinessAccountMutationRow] = {
            Zufaro.db.withSession { implicit sess =>
                BusinessAccountMutation.where(_.busId === business.id).sortBy(_.ts.desc).run
            }
        }

        /**
         * Share semua ke investors dari semua un-shared profit report.
         */
        def doShareProcess(shareMethod:ShareMethod){
            debug("do share process...")
            Zufaro.db.withTransaction { implicit sess =>
                val bps = for {
                    bp <- BusinessProfit if bp.busId === business.id && bp.shared === false
                } yield bp

                bps.foreach { bp =>
                    doShareProcess(bp, shareMethod)
                }
            }
        }

//        def doShareProcess(bp:BusinessProfitRow, shareMethod:ShareMethod)(implicit sess:backend.SessionDef){
//            // double check
//            if (BusinessProfit.where(x => x.id === bp.id && x.shared === true).length.run > 0)
//                throw IllegalStateException("Illegal operation")
//            doShareProcess(bp.profit, shareMethod)
//            BusinessProfit.where(_.id === bp.id).map(d => (d.shared, d.sharedAt))
//                .update((true, now()))
//        }


        def doShareProcess(bp:BusinessProfitRow, shareMethod:ShareMethod)(implicit sess:backend.SessionDef){
            // kalkulasi bagi hasil

            if (BusinessProfit.where(x => x.id === bp.id && x.shared === true).length.run > 0)
                throw IllegalStateException("Illegal operation")

            val ivIb = for {
                iv <- Invest if iv.busId === business.id && iv.busKind === BusinessKind.SINGLE
                ib <- InvestorBalance if ib.invId === iv.invId
            } yield (iv, ib)

            var totalShared = 0D

            ivIb.foreach { case (iv, ib) =>

                val margin = (iv.amount / business.fund) * bp.profit
                val share = margin * p(business.share)
                val curBal = ib.amount + share

                InvestorBalance.filter(_.id === ib.id).map(_.amount).update(curBal)

                // tulis business journal
                ProfitShareJournal += ProfitShareJournalRow(business.id, bp.id, iv.invId, share, shareMethod.method,
                    Some(shareMethod.initiator.toString), now())


                // tulis personal journal
                Mutation += MutationRow(0L, iv.invId, MutationKind.CREDIT,
                    share, Some("bagi hasil dari bisnis: " + business.name),
                    None, now())

                // tulis activity stream
                Activity.publish("Profit share",
                    s"""From business: "${business.name}" amount of ${share format IDR}""",
                        iv.invId, Activity.kind.INVESTOR)

                totalShared += share

                debug(f"profit shared from `${business.name} (${business.id})` " +
                    f"amount of $share%.02f to investor id `${iv.invId}`")
            }

            BusinessProfit.where(_.id === bp.id).map(d => (d.shared, d.sharedAt, d.status))
                .update((true, now(), reportStatus.ACCEPTED))


            /************************************************
             * BUSINESS ACCOUNT MUTATION
             ***********************************************/

            val income = bp.profit - totalShared
            if (income > 0.0){
                val balQ = Business.where(_.id === business.id).map(_.saving)
                val newBalance = balQ.firstOption.getOrElse(0.0) + income
                balQ.update(newBalance)
                
                BusinessAccountMutation += BusinessAccountMutationRow(0L, business.id, MutationKind.CREDIT, income,
                    f"Profit from business `${business.name}` #${business.id}",
                    Initiator.system.toString(), now())
            }


            //// group business share not supported yet, fix this if necessary
//            val groupQ = for {
//                link <- BusinessGroupLink if link.busId === business.id
//                iv <- Invest if iv.busId === link.busGroupId && iv.busKind === BusinessKind.GROUP
//                g <- BusinessGroup if g.id === link.busGroupId
//                inv <- Investor if inv.id === iv.invId
//                ib <- InvestorBalance if ib.invId === inv.id
//            } yield (iv.amount, inv.id, g, inv, ib)
//
//            //                println("groupQ statement: " + groupQ.selectStatement)
//
//            groupQ.foreach { case (investAmount, investId, g, inv, ib) =>
//                val investAmountNorm = investAmount / g.getMemberCount.toDouble
//
//                val margin = (investAmountNorm / business.fund) * profit
//                val dividen = margin * p(business.share)
//                val curBal = ib.amount + dividen
//
//                InvestorBalance.filter(_.id === ib.id).map(_.amount).update(curBal)
//
//                // tulis journal
//                Credit += CreditRow(0L, investId, dividen,
//                    Some("bagi hasil dari bisnis " + business.name + " group of " + g.name), new Timestamp(new Date().getTime))
//            }
        }

        def invalidateReport(bp:BusinessProfitRow){
            Zufaro.db.withTransaction { implicit sess =>
                // @TODO(robin): use `state=reportStatus.INVALID` instead of permanently deletion
                BusinessProfit.where(_.id === bp.id).delete
            }
        }



        /**
         * Check whether client is granted to access.
         * @param client API client.
         * @param access access name to check.
         * @return
         */
        def isGranted(client:ApiClientRow, access:String):Boolean = {
            Zufaro.db.withSession { implicit sess =>
                val target = s"bus=${business.id}"
                (ApiClientAccess.where(ac => ac.apiClientId === client.id &&
                    ac.grant === access &&
                    ac.target === target).length.run > 0) |
                (ApiClientAccess.where(ac => ac.apiClientId === client.id &&
                    ac.target === target &&
                    ac.grant === "all").length.run > 0)

            }
        }

        private def stateStr(state:Int) = {
            state match {
                case DRAFT => "project"
                case PRODUCTION => "running"
                case CLOSED => "closed"
            }
        }

        def makeProduction() = {
            Zufaro.db.withTransaction { implicit sess =>

                val beforeStr = stateStr(business.state)

                Business.where(_.id === business.id).map(_.state).update(PRODUCTION)

                // publish activity
                val q = for {
                    iv <- Invest if iv.busId === business.id
                    investor <- Investor if investor.id === iv.invId
                } yield investor.id


                val afterStr = stateStr(PRODUCTION)

                q.foreach { investorId =>
                    Activity.publish("Project progress", "Project \"" + business.name + "\" " +
                        s"status changed from $beforeStr to $afterStr." +
                        "This project ready for production.",
                        investorId, Activity.kind.INVESTOR)
                }
            }
        }

        def makeDraft() = {
            Zufaro.db.withTransaction { implicit sess =>
                val beforeStr = stateStr(business.state)

                Business.where(_.id === business.id).map(_.state).update(DRAFT)

                // publish activity
                val q = for {
                    iv <- Invest if iv.busId === business.id
                    investor <- Investor if investor.id === iv.invId
                } yield investor.id


                val afterStr = stateStr(DRAFT)

                q.foreach { investorId =>
                    Activity.publish("Project progress", s"""Project "${business.name}" """ +
                        s"""status changed from $beforeStr to $afterStr""", investorId, Activity.kind.INVESTOR)
                }
            }


        }

        def close() = {
            Zufaro.db.withTransaction { implicit sess =>

                val beforeStr = stateStr(business.state)

                Business.where(_.id === business.id).map(_.state).update(CLOSED)

                // publish activity
                val q = for {
                    iv <- Invest if iv.busId === business.id
                    investor <- Investor if investor.id === iv.invId
                } yield investor.id


                val afterStr = stateStr(CLOSED)

                q.foreach { investorId =>
                    Activity.publish("Project progress", s"""Project "${business.name}" """ +
                        s"""status changed from $beforeStr to $afterStr""", investorId, Activity.kind.INVESTOR)
                }
            }
        }


        // for project

        private def requireProject() = {
            if (business.state != DRAFT)
                throw IllegalStateException("Report only for project")
        }


        def getPercentageDone() = {
            requireProject()
            Zufaro.db.withSession { implicit sess =>
                val s = ProjectReport.where(_.busId === business.id).sortBy(_.ts.desc)
                    .map(_.percentage).take(1).run
                s.headOption.getOrElse(0.0)
            }
        }


        def getProjectReports(offset:Int, limit:Int):Seq[ProjectReportRow] = {
            requireProject()
            Zufaro.db.withSession { implicit sess =>
                ProjectReport.where(p => p.busId === business.id).sortBy(_.ts.desc).drop(offset).take(limit).run
            }
        }

        def writeProjectReport(info:String, percentageDone:Double, initiator:Initiator) = {
            requireProject()
            Zufaro.db.withSession { implicit sess =>

                val beforeStr = business.getPercentageDone()

                ProjectReport += ProjectReportRow(0L, business.id, info, percentageDone, initiator.toString, now())

                // publish activity
                val q = for {
                    iv <- Invest if iv.busId === business.id
                    investor <- Investor if investor.id === iv.invId
                } yield investor.id


                val afterStr = percentageDone

                q.foreach { investorId =>
                    Activity.publish("Project progress", s"""Project "${business.name}" """ +
                        f"""progress changed from $beforeStr%.02f%% to $afterStr%.02f%%, Info : $info""",
                        investorId, Activity.kind.INVESTOR)
                }
            }
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

