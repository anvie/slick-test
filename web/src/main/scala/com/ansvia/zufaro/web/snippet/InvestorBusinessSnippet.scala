package com.ansvia.zufaro.web.snippet

/**
 * Author: robin
 * Date: 8/21/14
 * Time: 8:34 PM
 *
 */


import java.util.Date

import com.ansvia.zufaro._
import com.ansvia.zufaro.exception.ZufaroException
import com.ansvia.zufaro.model.Tables.{Business, BusinessAccountMutation, BusinessProfit, _}
import com.ansvia.zufaro.model.{MutationKind, ShareMethod}
import com.ansvia.zufaro.web.Auth
import com.ansvia.zufaro.web.lib.MtTabInterface
import com.ansvia.zufaro.web.util.JsUtils
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.http.{ParsePath, RewriteRequest, RewriteResponse, _}
import net.liftweb.util.Helpers._
import net.liftweb.util._

import scala.xml.{Node, NodeSeq, Text}


class InvestorBusinessSnippet {

    import com.ansvia.zufaro.BusinessHelpers._
    import com.ansvia.zufaro.InvestorHelpers._
    import com.ansvia.zufaro.ZufaroHelpers._

    private def busId = S.param("busId").openOr("0").toLong
    private def busO = BusinessManager.getById(busId)
    private def inv = Auth.currentInvestor.is.openOrThrowException("Investor should login first")


    private def buildBusinessListItem(bus:Business, state:String):Node = {
        def updateList() = {
            state match {
                case "running" =>
                    val ns = BusinessManager.getRunningBusinessList(0, 10).flatMap(b => buildBusinessListItem(b, state))
                    SetHtml("ListRunning",NodeSeq.fromSeq(ns))
                case "project" =>
                    val ns = BusinessManager.getProjectBusinessList(0, 10).flatMap(b => buildBusinessListItem(b, state))
                    SetHtml("ListProject", NodeSeq.fromSeq(ns))
                case "closed" =>
                    val ns = BusinessManager.getClosedBusinessList(0, 10).flatMap(b => buildBusinessListItem(b, state))
                    SetHtml("ListClosed", NodeSeq.fromSeq(ns))
            }
        }

        val deleter = {
            JsUtils.ajaxConfirm("Are you sure to remove this business from your investment?" +
                " WARNING! This action will make your investment loss and cannot be undone!", Text("Remove"),
                "Remove from your investment"){

                inv.removeInvestment(bus)

                updateList() & JsUtils.showNotice("Success")
            }
        }

        def reporter() = {
            bus.state match {
                case BusinessManager.state.PRODUCTION =>
                    <li><a href={s"/admin/business/${bus.id}/report"}>Report</a></li>
                case BusinessManager.state.DRAFT =>
                    <li><a href={s"/admin/business/${bus.id}/project-report"}>Progress Report</a></li>
                case _ =>
                    NodeSeq.Empty
            }
        }

        // only for project (non running business)
        val progress = {
            state match {
                case "project" if bus.state == BusinessManager.state.DRAFT =>
                    val p = bus.getPercentageDone()
                    val percentageStr = f"$p%.02f%%"

                    <td>
                        <div class="progress">
                            <div class="progress-bar" role="progressbar" aria-valuenow={p.toString}
                                 aria-valuemin="0" aria-valuemax="100" style={s"width: $percentageStr;"}>
                                {percentageStr}
                            </div>
                        </div>
                    </td>:NodeSeq
                case _ =>
                    NodeSeq.Empty
            }
        }

//        val saving = state match {
//            case "running" | "closed" =>
//                <td>{bus.saving format IDR}</td>
//            case _ =>
//                NodeSeq.Empty
//        }


        <tr>
            <td>{bus.id.toString}</td>
            <td>{bus.name}</td>
            <td>{bus.desc}</td>
            <td>{bus.fund format IDR}</td>
            <td>{f"${bus.share}%.01f%%"}</td>
            {progress}
            <td>

                <div class="dropdown">
                    <a data-toggle="dropdown" href="#"><span class="glyphicon glyphicon-cog"></span></a>

                    <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
                        {reporter()}
                        <li class="divider"></li>
                        <li>{deleter}</li>
                    </ul>
                </div>


            </td>
        </tr>:Node
    }


    def businessList:CssSel = {
        S.attr("state").openOr("") match {
            case "running" =>
//                val business = BusinessManager.getRunningBusinessList(0, 10)
                val business = inv.getBusiness(0, 10, state=BusinessManager.state.PRODUCTION)
                "#ListRunning *" #> NodeSeq.fromSeq(business.map { bus => buildBusinessListItem(bus, "running") })
            case "project" =>
//                val business = BusinessManager.getProjectBusinessList(0, 30)
                val business = inv.getBusiness(0, 10, state=BusinessManager.state.DRAFT)
                "#ListProject *" #> NodeSeq.fromSeq(business.map { bus => buildBusinessListItem(bus, "project") })
            case "closed" =>
//                val business = BusinessManager.getClosedBusinessList(0, 30)
                val business = inv.getBusiness(0, 10, state=BusinessManager.state.CLOSED)
                "#ListClosed *" #> NodeSeq.fromSeq(business.map { bus => buildBusinessListItem(bus, "closed") })
        }
    }

    def reportTitle:NodeSeq = {
        <p>Business <strong>{BusinessManager.getById(S.param("busId").openOr("0").toLong).map(_.name).getOrElse("-")}</strong></p>
    }

    private def buildReportListItem(bus:Business, bp:BusinessProfit):Node = {

        def doShareProcess() = () => {
            try {
                if (bp.shared)
                    throw new ZufaroException("Illegal Operation", 921)

                val shareMethod = ShareMethod(ShareMethod.MANUAL, Auth.getInitiator)

                Zufaro.db.withTransaction(implicit sess => bus.doShareProcess(bp, shareMethod))

                JsUtils.showNotice("Success") & updateList(bus)
            }
            catch {
                case e:ZufaroException =>
                    JsUtils.showError(e.getMessage)
            }
        }


        val shareOp = {
            if (!bp.shared){

                SHtml.a(doShareProcess(), Text("share now"))

            }else
                NodeSeq.Empty
        }

        val sharedInfo = {
            bp.shared match {
                case true =>
                    val at = bp.sharedAt.map(x => "at " + new Date(x.getTime).toString).getOrElse("")
                    <div>{f"YES $at".trim} - <a href={s"/admin/business/${bus.id}/report/${bp.id}/share-detail"}>detail</a></div>
                case _ =>
                    "NO"
            }
        }


        <tr>
            <td>{bp.ts.toString}</td>
            <td>Rp. {bp.omzet},-</td>
            <td>Rp. {bp.profit},-</td>
            <td>{bp.info}</td>
            <td>
                <div>{sharedInfo}</div>
                <div>{shareOp}</div>
            </td>
            <td>

            </td>
        </tr>:Node
    }

    private def updateList(bus:Business) = {
        val reports = bus.getIncomeReport(0, 30)
        val ns = NodeSeq.fromSeq(reports.map( r => buildReportListItem(bus, r) ))
        SetHtml("ListRunning", ns)
    }

    def businessReportList:CssSel = {
        busO.map { bus =>
            val reports = bus.getIncomeReport(0, 30)
            "#List *" #> NodeSeq.fromSeq(reports.map( r => buildReportListItem(bus, r) ))
        }.getOrElse("*" #> NodeSeq.Empty)
    }

    def ops(in:NodeSeq):NodeSeq = {
        val _busO = this.busO
        lazy val doShareProcess = JsUtils.ajaxConfirm("Are you sure to process shares to investor?",
            Text("Process share now"), "Process share", Map("class" -> "btn btn-danger")){

            val bus = _busO.get

            bus.doShareProcess(ShareMethod(ShareMethod.MANUAL, Auth.getInitiator))

            updateList(bus) & JsUtils.showNotice("Success")
        }
        lazy val doShareProcessAll = JsUtils.ajaxConfirm("Are you sure to process all business shares to investor?",
            Text("Process share all now"), "Process share", Map("class" -> "btn btn-danger")){

            BusinessManager.getRunningBusinessList(0, 50).foreach { _bus =>
                _bus.doShareProcess(ShareMethod(ShareMethod.MANUAL, Auth.getInitiator))
            }

            JsUtils.showNotice("All business shares processed")
        }
        bind("in", in,
            "do-share-process" -> doShareProcess,
            "do-share-process-all" -> doShareProcessAll
        )
    }

    private def busProfId = S.param("busProfId").openOr("0").toLong

    def shareReportTitle:CssSel = {
        "h1 *" #> s"Profit Share #$busProfId"
    }

    private def buildShareReportListItem(shareReport:BusinessManager.ShareReport) = {
        <tr>
            <td>{shareReport.date}</td>
            <td>{shareReport.investorName} #{shareReport.investorId}</td>
            <td>{shareReport.amount.format(IDR)}</td>
            <td></td>
        </tr>
    }


    def shareReportList:CssSel = {
        val bus = busO.get
        val reports = bus.getShareReport(busProfId, 0, 50)
        "#List *" #> NodeSeq.fromSeq(reports.map(buildShareReportListItem))
    }

    def backToReportListButton:NodeSeq = {
        <a href={s"/admin/business/$busId/report"}>Back to report list</a>
    }

    def sharedIntoInvestorInfo:CssSel = {
        import scala.slick.driver.PostgresDriver.simple._
        "#Count *" #> {
            Zufaro.db.withSession { implicit sess =>
                val q = for {
                    b <- ProfitShareJournals if b.busId === busId && b.busProfId === busProfId
                    inv <- Investors if inv.id === b.invId
                } yield b.busId
                q.length.run.toString
            }
        }
    }


    /************************************************
      * ACCOUNT MUTATION
      ***********************************************/

    private def buildAccountMutationListItem(mut:BusinessAccountMutation) = {

        val credit = mut.kind match {
            case MutationKind.CREDIT => mut.amount.format(IDR)
            case _ => "-"
        }
        val debit = mut.kind match {
            case MutationKind.DEBIT => mut.amount.format(IDR)
            case _ => "-"
        }
        val initiator = mut.initiator.split('=').toList match {
            case "admin" :: AsLong(id) :: Nil =>
                UserManager.getById(id).map(_.name).getOrElse("-")
//            case "operator" :: AsLong(id) :: Nil =>
//                OperatorManager.getById(id).map(_.name).getOrElse("-")
            case _ =>
                "-"
        }

        <tr>
            <td>{mut.id}</td>
            <td>{mut.info}</td>
            <td>{credit}</td>
            <td>{debit}</td>
            <td>{initiator}</td>
            <td>{new Date(mut.ts.getTime)}</td>
        </tr>
    }


    def accountMutationList:CssSel = {
        val bus = busO.get
        val reports = bus.getAccountMutationReport(0, 50)
        "#MutationList *" #> NodeSeq.fromSeq(reports.map(buildAccountMutationListItem))
    }

}


object InvestorBusinessTab extends MtTabInterface {
    def defaultSelected: String = "running"

    def tmplDir: String = "admin/business"

    override def tabNames: Array[String] = Array("running", "project", "closed")

    override def preSendJs(tabName: String): JsCmd = {
        JsRaw(s"History.pushState(null,null,'/admin/business/$tabName');").cmd
    }

    lazy val rewrite:LiftRules.RewritePF = NamedPF(s"${getClass.getSimpleName}Rewrite"){
        case RewriteRequest(ParsePath("admin" :: "business" :: tabRe(tab) :: Nil, _, _, _), _, _) =>
            RewriteResponse("admin" :: "business" :: Nil, Map("tab" -> tab))
    }
}


