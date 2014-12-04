package com.ansvia.zufaro.web.snippet

import com.ansvia.commons.logging.Slf4jLogger
import com.ansvia.zufaro.UserManager
import com.ansvia.zufaro.exception.{InvalidParameterException, ZufaroException}
import com.ansvia.zufaro.model.Tables._
import com.ansvia.zufaro.model.{OperatorStatus, UserRole}
import com.ansvia.zufaro.web.lib.{HTML5HistoryHandler, MtTabInterface}
import com.ansvia.zufaro.web.util.JsUtils
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.http.{ParsePath, RewriteRequest, _}
import net.liftweb.util.Helpers._
import net.liftweb.util.{CssSel, NamedPF}

import scala.xml.{Node, NodeSeq, Text}


/**
 * Author: robin
 * Date: 8/25/14
 * Time: 6:59 PM
 *
 */
class AdminOperatorSnippet extends Slf4jLogger {

    import com.ansvia.zufaro.model.OperatorStatus._

    private object nameVar extends RequestVar("")
    private object passwordVar extends RequestVar("")
    private object verifyPasswordVar extends RequestVar("")
    private object emailVar extends RequestVar("")
    private object phoneVar extends RequestVar("")

    def addNewOperatorDialog(in:NodeSeq):NodeSeq = {

        val currentStatus = status

        def doCreateInternal() = {

            try {
                if (nameVar.isEmpty)
                    throw InvalidParameterException("No name")
                if (emailVar.isEmpty)
                    throw InvalidParameterException("Please enter email address")
                if (phoneVar.isEmpty)
                    throw InvalidParameterException("Please enter phone number")
                if (passwordVar.isEmpty)
                    throw InvalidParameterException("Please enter password")
                if (verifyPasswordVar.isEmpty)
                    throw InvalidParameterException("Please verify password")

                if (passwordVar.is != verifyPasswordVar.is)
                    throw InvalidParameterException("Password verification didn't match")


                val op = UserManager.create(nameVar, emailVar, phoneVar, passwordVar, UserRole.OPERATOR, "")

                info("operator created: " + op)

                updateList(currentStatus) & JsUtils.hideAllModal

            }
            catch {
                case e:ZufaroException =>
                    S.error(e.getMessage)
            }

        }



        SHtml.ajaxForm(bind("in", in,
            "name" -> SHtml.text(nameVar, nameVar(_), "class" -> "form-control", "id" -> "Name"),
            "email" -> SHtml.text(emailVar, emailVar(_), "class" -> "form-control", "id" -> "Email"),
            "phone" -> SHtml.text(phoneVar, phoneVar(_), "class" -> "form-control", "id" -> "Phone"),
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
        val operators = UserManager.getOperatorList(0, 50)
        val ns = NodeSeq.fromSeq(operators.map(op => buildOperatorListItem(op, state)))
        SetHtml("List", ns)
    }

    private def buildOperatorListItem(op:User, currentState:Int):Node = {

        import com.ansvia.zufaro.UserHelpers._

        val deleter = {
            JsUtils.ajaxConfirm("Are you sure to delete this investor? " +
                "This operation cannot be undone", Text("delete"), "Delete this investor"){

                UserManager.delete(op)

                updateList(currentState)
            }
        }

        val state = OperatorStatus.toStr(op.status)

        val activeInactive = op.status match {
            case ACTIVE =>
                SHtml.a(()=>{
                    op.setActive(state = false)
                    updateList(currentState)
                },Text("Suspend"))
            case SUSPENDED =>
                SHtml.a(()=>{
                    op.setActive(state = true)
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
        val operators = UserManager.getOperatorList(0, 50)
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

