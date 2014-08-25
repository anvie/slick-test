package com.ansvia.zufaro.web.snippet

import net.liftweb._
import util._
import Helpers._
import common._
import com.ansvia.zufaro.web.lib.{HTML5HistoryHandler, MtTabInterface}
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http._
import net.liftweb.util.{CssSel, NamedPF}
import scala.util.matching.Regex
import com.ansvia.zufaro.{OperatorHelpers, OperatorManager}
import scala.xml.{Node, NodeSeq, Text}
import net.liftweb.http.RewriteRequest
import net.liftweb.http.ParsePath
import com.ansvia.zufaro.web.util.JsUtils
import net.liftweb.http.js.JsCmds.SetHtml
import com.ansvia.zufaro.exception.{ZufaroException, InvalidParameterException}
import com.ansvia.zufaro.model.Tables._
import com.ansvia.zufaro.model.OperatorStatus


/**
 * Author: robin
 * Date: 8/25/14
 * Time: 6:59 PM
 *
 */
class AdminOperatorSnippet {

    import com.ansvia.zufaro.model.OperatorStatus._
    import OperatorHelpers._

    private object nameVar extends RequestVar("")
    private object passwordVar extends RequestVar("")
    private object verifyPasswordVar extends RequestVar("")

    def addNewOperatorDialog(in:NodeSeq):NodeSeq = {

        val currentStatus = status

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


                val op = OperatorManager.create(nameVar, passwordVar)

                updateList(currentStatus) & JsUtils.hideAllModal

//                S.redirectTo("/admin/operator/active-operator", () => S.notice(s"Operator created ${op.name} with id ${op.id}"))
            }
            catch {
                case e:ZufaroException =>
                    S.error(e.getMessage)
            }

        }



        SHtml.ajaxForm(bind("in", in,
            "name" -> SHtml.text(nameVar, nameVar(_), "class" -> "form-control", "id" -> "Name"),
            "password" -> SHtml.password(passwordVar, passwordVar(_), "class" -> "form-control", "id" -> "Password"),
            "password-verification" -> SHtml.password(verifyPasswordVar, verifyPasswordVar(_), "class" -> "form-control", "id" -> "VerifyPassword"),
            "submit" -> S.formGroup(1000){
                SHtml.hidden(doCreateInternal) ++
                SHtml.submit("Create", doCreateInternal, "class" -> "btn btn-success")
            }
        ))
    }


    def addOperatorButton:NodeSeq = {
        SHtml.a(()=>{
            val ns = S.runTemplate("admin" :: "operator" :: "_chunk_dialog-add-operator" :: Nil).openOr(NodeSeq.Empty)
            val ns2 = ("#Dialog [class]" #> s"lift:AdminOperatorSnippet.addNewOperatorDialog?status=active").apply(ns)
            JsUtils.modalDialog(ns2)
        },Text("+ Add operator"))
    }



    private def updateList(state:Int) = {
        val operators = OperatorManager.getList(0, 50, state)
        val ns = NodeSeq.fromSeq(operators.map(op => buildOperatorListItem(op, state)))
        SetHtml("List", ns)
    }

    private def buildOperatorListItem(op:OperatorRow, currentState:Int):Node = {

        val deleter = {
            JsUtils.ajaxConfirm("Are you sure to delete this investor? " +
                "This operation cannot be undone", Text("delete"), "Delete this investor"){

                OperatorManager.delete(op)

                updateList(currentState)
            }
        }

        val state = OperatorStatus.toStr(op.status)

        val activeInactive = op.status match {
            case ACTIVE =>
                SHtml.a(()=>{
                    op.suspend()
                    updateList(currentState)
                },Text("Suspend"))
            case SUSPENDED =>
                SHtml.a(()=>{
                    op.activate()
                    updateList(currentState)
                },Text("Activate"))
        }


        <tr>
            <td>{op.id}</td>
            <td>{op.name}</td>
            <td>{state}</td>
            <td>

                <div class="dropdown">
                    <a data-toggle="dropdown" href="#"><span class="glyphicon glyphicon-cog"></span></a>

                    <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
                        <li>{activeInactive}</li>
                        <li class="divider"></li>
                        <li>{deleter}</li>
                    </ul>
                </div>

            </td>
        </tr>
    }

    private def status = S.attr("status").openOr("") match {
        case "active" => ACTIVE
        case "suspended" => SUSPENDED
    }

    def operatorList:CssSel = {
        val operators = OperatorManager.getList(0, 50, status)
        "#List *" #> NodeSeq.fromSeq(operators.map(op => buildOperatorListItem(op, status)))
    }
}


object AdminOperatorTab extends MtTabInterface with HTML5HistoryHandler {
    def defaultSelected: String = "active-operator"

    def tmplDir: String = "admin/operator"

    override def tabNames: Array[String] = Array("active-operator", "suspended-operator")

//    override def preSendJs(tabName: String): JsCmd = {
//        JsRaw(s"History.pushState(null,null,'/admin/operator/$tabName');").cmd
//    }

    lazy val rewrite:LiftRules.RewritePF = NamedPF(s"${getClass.getSimpleName}Rewrite"){
        case RewriteRequest(ParsePath("admin" :: "operator" :: tabRe(tab) :: Nil, _, _, _), _, _) =>
            RewriteResponse("admin" :: "operator" :: Nil, Map("tab" -> tab))
    }
}

