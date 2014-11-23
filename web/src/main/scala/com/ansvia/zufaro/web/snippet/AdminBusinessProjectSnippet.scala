package com.ansvia.zufaro.web.snippet

import net.liftweb._
import util._
import Helpers._
import com.ansvia.zufaro.model.Tables._
import net.liftweb.http.{RequestVar, SHtml, S}
import com.ansvia.zufaro.{BusinessHelpers, BusinessManager}
import java.util.Date
import scala.xml.{Node, Text, NodeSeq}
import com.ansvia.zufaro.web.util.JsUtils
import com.ansvia.zufaro.model.Initiator
import com.ansvia.zufaro.web.Auth
import com.ansvia.zufaro.exception.ZufaroException
import net.liftweb.http.js.JsCmds.SetHtml

/**
 * Author: robin
 * Date: 8/17/14
 * Time: 11:51 AM
 *
 */
class AdminBusinessProjectSnippet {

    import BusinessHelpers._

    private def busId = S.param("busId").openOr(S.attr("busId").openOr("0")).toLong
    private def busO = {
        BusinessManager.getById(busId)
    }


    def reportInfo:CssSel = {
        val bus = busO.get
        "#Name *" #> bus.name &
        "#Desc *" #> bus.desc &
        "#Created *" #> {
            new Date(bus.createdAt.getTime).toString
        }
    }


    private def buildListItem(rep:ProjectReportRow) = {

        val time = new Date(rep.ts.getTime)

        <tr>
            <td>{time}</td>
            <td>{f"${rep.percentage}%.02f%%"}</td>
            <td>{rep.info}</td>
            <td></td>
        </tr>:Node
    }


    def reportList:CssSel = {
        val reports = busO.get.getProjectReports(0, 50)
        "#List *" #> NodeSeq.fromSeq(reports.map(buildListItem))
    }

    private def updateReportList(bus:Business) = {
        val reports = bus.getProjectReports(0, 50)
        SetHtml("List", NodeSeq.fromSeq(reports.map(buildListItem)))
    }


    def reportOps(in:NodeSeq):NodeSeq = {

        val busId = this.busId

        def writeReportInternal = () => {
            val ns = S.runTemplate("admin" :: "business" :: "_chunk_dialog-report-project" :: Nil).openOr(NodeSeq.Empty)

            val ns2 = ("#Dialog [class]" #> s"lift:AdminBusinessProjectSnippet.dialogReportProject?busId=$busId").apply(ns)

            JsUtils.modalDialog(ns2)
        }

        bind("in", in,
        "write-report" -> SHtml.a(writeReportInternal, Text("Write report"), "class" -> "btn btn-default")
        )
    }

    private object percentageVar extends RequestVar[Double](0.0)
    private object infoVar extends RequestVar("")

    def dialogReportProject(in:NodeSeq):NodeSeq = {

        val bus = busO.get

        def doReportInternal() = {
            try {
                bus.writeProjectReport(infoVar, percentageVar, Auth.getInitiator)

                JsUtils.hideAllModal & updateReportList(bus)
            }
            catch {
                case e:ZufaroException =>
                    JsUtils.showError(e.getMessage)
                case e:Exception =>
                    e.printStackTrace()
                    JsUtils.showError("Oops, something went wrong!")
            }
        }

        SHtml.ajaxForm(bind("in", in,
        "percentage" -> SHtml.number(percentageVar, percentageVar(_), 0, 100.0, 0.1, "class" -> "form-control"),
        "info" -> SHtml.textarea(infoVar, infoVar(_), "class" -> "form-control"),
        "submit" -> S.formGroup(100){
            SHtml.hidden(doReportInternal) ++ SHtml.submit("Report!", doReportInternal, "class" -> "btn btn-success")
        }
        ))
    }


}




