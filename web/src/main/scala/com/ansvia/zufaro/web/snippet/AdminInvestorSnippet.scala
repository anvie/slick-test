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
import scala.xml.{Node, Text, NodeSeq}
import com.ansvia.zufaro.InvestorManager
import com.ansvia.zufaro.web.lib.MtTabInterface
import com.ansvia.zufaro.model.Tables._
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmd
import com.ansvia.zufaro.exception.{NotExistsException, ZufaroException, InvalidParameterException}
import net.liftweb.common.Full
import com.ansvia.zufaro.model.{UserRole, Initiator, InvestorRole}
import com.ansvia.zufaro.web.util.JsUtils
import net.liftweb.http.js.JsCmds.SetHtml
import com.ansvia.zufaro.web.Auth


class AdminInvestorSnippet {

    import com.ansvia.zufaro.InvestorHelpers._

    private object nameVar extends RequestVar("")
    private object passwordVar extends RequestVar("")
    private object verifyPasswordVar extends RequestVar("")
    private object roleVar extends RequestVar("")

    def addNew(in:NodeSeq):NodeSeq = {

        def doCreateInternal() = {

            try {
                if (nameVar.isEmpty)
                    throw InvalidParameterException("No name")
                if (passwordVar.isEmpty)
                    throw InvalidParameterException("Please enter password")
                if (verifyPasswordVar.isEmpty)
                    throw InvalidParameterException("Please verify password")

                if (passwordVar.is != verifyPasswordVar.is)
                    throw InvalidParameterException("Password verification didn't match")

                val role = roleVar.is match {
                    case "owner" => InvestorRole.OWNER
                    case "operator" => InvestorRole.OPERATOR
                    case "supervisor" => InvestorRole.SUPERVISOR
                }

                val inv = InvestorManager.create(nameVar, role, passwordVar)

                S.redirectTo("/admin/investor/active-investor", () => S.notice(s"Investor created ${inv.name} with id ${inv.id}"))
            }
            catch {
                case e:ZufaroException =>
                    S.error(e.getMessage)
            }

        }


        val roles = Seq(("owner", "OWNER"), ("operator", "OPERATOR"), ("supervisor", "SUPERVISOR"))


        bind("in", in,
        "name" -> SHtml.text(nameVar, nameVar(_), "class" -> "form-control", "id" -> "Name"),
        "role" -> SHtml.select(roles, Full("owner"), roleVar(_), "class" -> "form-control"),
        "password" -> SHtml.password(passwordVar, passwordVar(_), "class" -> "form-control", "id" -> "Password"),
        "verify-password" -> SHtml.password(verifyPasswordVar, verifyPasswordVar(_), "class" -> "form-control", "id" -> "VerifyPassword"),
        "submit" -> SHtml.submit("Create", doCreateInternal, "class" -> "btn btn-success")
        )
    }



    private def buildInvestorListItem(inv:InvestorRow):Node = {

        def updater() = {
            val investors = InvestorManager.getList(0, 50)
            val ns = NodeSeq.fromSeq(investors.map(buildInvestorListItem))
            new JsCmd {
                def toJsCmd: String = {
                    fixHtmlFunc("inline", ns){ nss =>
                        SetHtml("List", ns)
                    }
                }
            }
        }

        val deleter = {
            JsUtils.ajaxConfirm("Are you sure to delete this investor? " +
                "This operation cannot be undone", Text("delete"), "Delete this investor"){

                InvestorManager.delete(inv)

                updater()
            }
        }


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
            <td>

                <div class="dropdown">
                    <a data-toggle="dropdown" href="#"><span class="glyphicon glyphicon-cog"></span></a>

                    <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
                        <li><a href={s"/admin/investor/${inv.id}/deposit"}>Deposit</a></li>
                        <li class="divider"></li>
                        <li>{deleter}</li>
                    </ul>
                </div>

            </td>
        </tr>
    }


    def investorList:CssSel = {
        val investors = InvestorManager.getList(0, 50)
        "#List *" #> NodeSeq.fromSeq(investors.map(buildInvestorListItem))
    }

    private def invO = {
        val id = S.param("invId").openOr("0").toLong
        InvestorManager.getById(id)
    }


}


object AdminInvestorTab extends MtTabInterface {
    def defaultSelected: String = "active-investor"

    def tmplDir: String = "admin/investor"

    override def tabNames: Array[String] = Array("active-investor", "suspended-investor")

    override def preSendJs(tabName: String): JsCmd = {
        JsRaw(s"History.pushState(null,null,'/admin/investor/$tabName');").cmd
    }

    lazy val rewrite:LiftRules.RewritePF = NamedPF(s"${getClass.getSimpleName}Rewrite"){
        case RewriteRequest(ParsePath("admin" :: "investor" :: tabRe(tab) :: Nil, _, _, _), _, _) =>
            RewriteResponse("admin" :: "investor" :: Nil, Map("tab" -> tab))
    }
}


