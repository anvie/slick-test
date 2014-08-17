package com.ansvia.zufaro.web.snippet

import net.liftweb._
import util._
import http._
import Helpers._
import scala.xml.{Node, Text, NodeSeq}
import com.ansvia.zufaro.exception.{PermissionDeniedException, InvalidParameterException, ZufaroException}
import com.ansvia.zufaro.{Zufaro, BusinessManager}
import com.ansvia.zufaro.web.lib.MtTabInterface
import com.ansvia.zufaro.model.Tables._
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmd
import com.ansvia.zufaro.model.{UserRole, ShareMethod}
import com.ansvia.zufaro.web.Auth
import com.ansvia.zufaro.web.util.JsUtils
import net.liftweb.http.js.JsCmds.SetHtml
import java.util.Date

/**
 * Author: robin
 * Date: 8/15/14
 * Time: 5:15 PM
 *
 */
class AdminBusinessSnippet {

    import com.ansvia.zufaro.BusinessHelpers._

    private object nameVar extends RequestVar("")
    private object descVar extends RequestVar("")
    private object fundVar extends RequestVar(0.0)
    private object divInvestorVar extends RequestVar(0.0)
    private object stateVar extends RequestVar(BusinessManager.state.DRAFT)

    def addNew(in:NodeSeq):NodeSeq = {

        def doAddNew() = {
            try {

                if (nameVar.isEmpty)
                    throw InvalidParameterException("No name")
                if (descVar.isEmpty)
                    throw InvalidParameterException("No description")

                stateVar.is match {
                    case BusinessManager.state.DRAFT =>
                    case BusinessManager.state.PRODUCTION =>
                    case x =>
                        throw InvalidParameterException("Unknown business state " + x)
                }


                val bus = BusinessManager.create(nameVar, descVar, fundVar, divInvestorVar, stateVar)

                S.redirectTo("/admin/business/project", () => S.notice(s"Success added business `${bus.name}` with id `${bus.id}`"))

            }catch{
                case e:ZufaroException =>
                    S.error(e.getMessage)
            }
        }

        stateVar.setIsUnset(BusinessManager.state.DRAFT)

        bind("in", in,
            "name" -> SHtml.text(nameVar, nameVar(_), "class" -> "form-control", "id" -> "Name"),
            "description" -> SHtml.textarea(descVar, descVar(_), "class" -> "form-control", "id" -> "Desc"),
            "fund" -> SHtml.number(fundVar, fundVar(_), 0.0, 9999999.0, 1.0, "class" -> "form-control", "id" -> "Fund"),
            "div-investor" -> SHtml.number(divInvestorVar, divInvestorVar(_), 0.0, 100.0, 1.0, "class" -> "form-control", "id" -> "DivInvestor"),
            "submit" -> SHtml.submit("Add", doAddNew, "class" -> "btn btn-success")
        )
    }


    private def buildBusinessListItem(bus:BusinessRow, state:String):Node = {
        def updateList() = {
            val ns = NodeSeq.fromSeq(state match {
                case "running" =>
                    BusinessManager.getRunningBusinessList(0, 10).flatMap(b => buildBusinessListItem(b, state))
                case "project" =>
                    BusinessManager.getProjectBusinessList(0, 10).flatMap(b => buildBusinessListItem(b, state))
                case "closed" =>
                    BusinessManager.getClosedBusinessList(0, 10).flatMap(b => buildBusinessListItem(b, state))
            })
            new JsCmd {
                def toJsCmd: String = {
                    fixHtmlFunc("inline", ns){ nss =>
                        """$("#List").html(%s);""".format(nss)
                    }
                }
            }
        }
        
        val deleteInternal = () => {
            BusinessManager.delete(bus)
            updateList() & JsUtils.showNotice("Success")
        }
        val runInternal = () => {
            bus.makeProduction()
            updateList() & JsUtils.showNotice(s"${bus.name} is now production")
        }
        val closeInternal = () => {
            bus.close()
            updateList() & JsUtils.showNotice(s"${bus.name} is now closed")
        }
        val toProjectInternal = () => {
            bus.makeDraft()
            updateList() & JsUtils.showNotice(s"${bus.name} is now marked as project")
        }
        def stateChanger() = {
            <li class="divider"></li> ++
            (bus.state match {
                case BusinessManager.state.DRAFT =>
                    <li>{SHtml.a(runInternal, Text("Run"))}</li> ++
                    <li>{SHtml.a(closeInternal, Text("Close"))}</li>
                case BusinessManager.state.PRODUCTION =>
                    <li>{SHtml.a(toProjectInternal, Text("Make Project"))}</li> ++
                    <li>{SHtml.a(closeInternal, Text("Close"))}</li>
                case BusinessManager.state.CLOSED =>
                    <li>{SHtml.a(toProjectInternal, Text("Make Project"))}</li>
            })
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
        

        <tr>
            <td>{bus.id.toString}</td>
            <td>{bus.name}</td>
            <td>{bus.desc}</td>
            <td>{f"Rp.${bus.fund}%.02f,-"}</td>
            <td>{f"${bus.share}%.01f%%"}</td>
            {progress}
            <td>

                <div class="dropdown">
                    <a data-toggle="dropdown" href="#"><span class="glyphicon glyphicon-cog"></span></a>

                    <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
                        {reporter()}
                        {stateChanger()}
                        <li class="divider"></li>
                        <li>{SHtml.a(deleteInternal,Text("Delete"))}</li>
                    </ul>
                </div>


            </td>
        </tr>:Node
    }


    def businessList:CssSel = {
        S.attr("state").openOr("") match {
            case "running" =>
                val business = BusinessManager.getRunningBusinessList(0, 10)
                "#List *" #> NodeSeq.fromSeq(business.map { bus => buildBusinessListItem(bus, "running") })
            case "project" =>
                val business = BusinessManager.getProjectBusinessList(0, 30)
                "#List *" #> NodeSeq.fromSeq(business.map { bus => buildBusinessListItem(bus, "project") })
            case "closed" =>
                val business = BusinessManager.getClosedBusinessList(0, 30)
                "#List *" #> NodeSeq.fromSeq(business.map { bus => buildBusinessListItem(bus, "closed") })
        }
    }

    def reportTitle:NodeSeq = {
        <p>Business <strong>{BusinessManager.getById(S.param("busId").openOr("0").toLong).map(_.name).getOrElse("-")}</strong></p>
    }

    private def buildReportListItem(bus:BusinessRow, bp:BusinessProfitRow):Node = {

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
                    val at = new Date(bp.sharedAt.getTime)
                    f"YES at $at"
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

    private def updateList(bus:BusinessRow) = {
        val reports = bus.getReport(0, 30)
        val ns = NodeSeq.fromSeq(reports.map( r => buildReportListItem(bus, r) ))
        SetHtml("List", ns)
    }

    private def busO = BusinessManager.getById(S.param("busId").openOr("0").toLong)

    def businessReportList:CssSel = {
        busO.map { bus =>
            val reports = bus.getReport(0, 30)
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


}


object AdminBusinessTab extends MtTabInterface {
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

