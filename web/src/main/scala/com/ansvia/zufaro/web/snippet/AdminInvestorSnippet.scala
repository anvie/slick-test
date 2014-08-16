package com.ansvia.zufaro.web.snippet

/**
 * Author: robin
 * Date: 8/16/14
 * Time: 12:26 PM
 *
 */

import net.liftweb._
import util._
import http._
import Helpers._
import scala.xml.{Text, NodeSeq}
import com.ansvia.zufaro.InvestorManager
import com.ansvia.zufaro.web.lib.MtTabInterface
import com.ansvia.zufaro.model.Tables._
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmd


class AdminInvestorSnippet {

    import com.ansvia.zufaro.InvestorHelpers._

    private def buildInvestorListItem(inv:InvestorRow) = {

        val businessNs = inv.getBusiness(0, 10).map { bus =>
            <li>{bus.name} #{bus.id}</li>
        }

        <tr>
            <td>{inv.id}</td>
            <td>{inv.name}</td>
            <td>{

                if (businessNs.length > 0){
                    <ul>{NodeSeq.fromSeq(businessNs)}</ul>
                }else
                    Text("-")

                }
            </td>
            <td>{inv.getBalance}</td>
        </tr>
    }


    def investorList:CssSel = {
        val investors = InvestorManager.getList(0, 50)
        "#List " #> NodeSeq.fromSeq(investors.map(buildInvestorListItem))
    }


}


object AdminInvestorTab extends MtTabInterface {
    def defaultSelected: String = "running"

    def tmplDir: String = "admin/investor"

    override def tabNames: Array[String] = Array("active", "suspended")

    override def preSendJs(tabName: String): JsCmd = {
        JsRaw(s"History.pushState(null,null,'/admin/investor/$tabName');").cmd
    }

    lazy val rewrite:LiftRules.RewritePF = NamedPF(s"${getClass.getSimpleName}Rewrite"){
        case RewriteRequest(ParsePath("admin" :: "investor" :: tabRe(tab) :: Nil, _, _, _), _, _) =>
            RewriteResponse("admin" :: "investor" :: Nil, Map("tab" -> tab))
    }
}


