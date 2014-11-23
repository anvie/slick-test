package com.ansvia.zufaro.web.snippet

import net.liftweb._
import http._
import util._
import Helpers._
import scala.xml.{Node, Text, NodeSeq}
import com.ansvia.zufaro.web.Auth
import com.ansvia.zufaro.exception.{PermissionDeniedException, UnimplementedException, InvalidParameterException, ZufaroException}
import net.liftweb.common.Full
import com.ansvia.zufaro.UserManager
import com.ansvia.zufaro.AdminHelpers._
import com.ansvia.commons.logging.Slf4jLogger
import com.ansvia.zufaro.model.Tables._
import net.liftweb.http.js.JsCmds.SetHtml
import com.ansvia.zufaro.web.util.JsUtils


/**
 * Author: robin
 * Date: 8/14/14
 * Time: 8:16 PM
 *
 */
class AdminAdminSnippet extends Slf4jLogger {


    private def buildListItem(admin:AdminRow):Node = {
        val elmId = "Admin-" + admin.id

        val status = admin.status match {
            case UserManager.status.ACTIVE => "ACTIVE"
            case UserManager.status.INACTIVE => "INACTIVE"
        }

        val activate = {
            SHtml.a(()=>{
                admin.setActive(true)
                updateList()
            },Text("Activate"))
        }
        val deactivate = {
            SHtml.a(()=>{
                try {
                    if (admin.name.toLowerCase.trim == "admin")
                        throw PermissionDeniedException("You cannot deactivate this admin account")
                    admin.setActive(false)
                    updateList()
                }catch{
                    case e:ZufaroException =>
                        JsUtils.showError(e.getMessage)
                }
            },Text("Deactivate"))
        }

        val activeDeactivate = {
            admin.status match {
                case UserManager.status.ACTIVE =>
                    deactivate
                case UserManager.status.INACTIVE =>
                    activate
            }
        }
        val deleteInternal = ()=> {
            try {
                if (admin.name.toLowerCase.trim == "admin")
                    throw PermissionDeniedException("You cannot delete this admin account")
                UserManager.delete(admin)
                updateList()
            }catch{
                case e:ZufaroException =>
                    JsUtils.showError(e.getMessage)
            }
        }

        val opMenu = {
            <div class="dropdown">
                <a data-toggle="dropdown" href="#"><span class="glyphicon glyphicon-cog"></span></a>

                <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
                    <li>{activeDeactivate}</li>
                    <li class="divider"></li>
                    <li>
                        {SHtml.a(deleteInternal,Text("Delete"))}
                    </li>
                </ul>
            </div>
        }

        <tr id={elmId}>
            <td>{admin.id}</td>
            <td>{admin.name}</td>
            <td>{status}</td>
            <td>{admin.createdAt}</td>
            <td>{opMenu}</td>
        </tr>
    }

    private def updateList() = {
        val admins = UserManager.getList(0, 30)
        SetHtml("List", NodeSeq.fromSeq(admins.map(buildListItem)))
    }


    def list:CssSel = {
        val admins = UserManager.getList(0, 30)
        "#List *" #> NodeSeq.fromSeq(admins.map(buildListItem))
    }


    private object nameVar extends RequestVar("")
    private object emailVar extends RequestVar("")
    private object phoneVar extends RequestVar("")
    private object passwordVar extends RequestVar("")
    private object passwordVerificationVar extends RequestVar("")

    def addAdminDialog(in:NodeSeq):NodeSeq = {

        def addAdminInternal() = {
            try {

                if (nameVar.isEmpty)
                    throw InvalidParameterException("No name")

                if (passwordVar.is != passwordVerificationVar.is)
                    throw InvalidParameterException("Password verification didn't match")

                UserManager.create(nameVar, emailVar, phoneVar, passwordVar)

                JsUtils.hideAllModal &
                updateList()
            }catch{
                case e:ZufaroException =>
                    JsUtils.showError(e.getMessage)
            }
        }


        SHtml.ajaxForm(
        bind("in", in,
        "name" -> SHtml.text(nameVar, nameVar(_), "class" -> "form-control"),
        "email" -> SHtml.text(emailVar, emailVar(_), "class" -> "form-control"),
        "phone" -> SHtml.text(phoneVar, phoneVar(_), "class" -> "form-control"),
        "password" -> SHtml.password(passwordVar, passwordVar(_), "class" -> "form-control"),
        "password-verification" -> SHtml.password(passwordVerificationVar, passwordVerificationVar(_), "class" -> "form-control"),
        "submit" -> S.formGroup(1000){
            SHtml.hidden(addAdminInternal) ++
            SHtml.submit("Add", addAdminInternal, "class" -> "btn btn-success")
        }
        )
        )
    }


    def addAdminButton:NodeSeq = {
        SHtml.a(()=>{

            val ns = S.runTemplate("admin" :: "admin" :: "_chunk_dialog-add-admin" :: Nil).openOr(NodeSeq.Empty)

            JsUtils.modalDialog(ns)

        },Text("+ Add Admin"))
    }


}
