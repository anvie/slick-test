package com.ansvia.zufaro.web.snippet

import net.liftweb._
import http._
import util._
import Helpers._
import scala.xml.{Text, NodeSeq}
import com.ansvia.zufaro.web.Auth
import com.ansvia.zufaro.exception.{UnimplementedException, InvalidParameterException, ZufaroException}
import net.liftweb.common.Full
import com.ansvia.zufaro.UserManager
import com.ansvia.commons.logging.Slf4jLogger
import com.ansvia.zufaro.web.util.JsUtils

/**
 * Author: robin
 * Date: 8/14/14
 * Time: 8:16 PM
 *
 */
class AdminSnippet extends Slf4jLogger {

    private object userNameVar extends RequestVar("")
    private object emailVar extends RequestVar("")
    private object phoneVar extends RequestVar("")
    private object passwordVar extends RequestVar("")
    private object kindVar extends RequestVar("")

    def login(in:NodeSeq):NodeSeq = {

        def doLogin() = {

            try {

                if (userNameVar.is.trim.length == 0)
                    throw InvalidParameterException("Please enter user name")
                if (passwordVar.is.trim.length == 0)
                    throw InvalidParameterException("Please enter password")


                kindVar.is match {
                    case "operator" =>
                        throw UnimplementedException("Not implemented yet for operator")
                    case "admin" =>
                        // apabila user name adalah admin dan apabila belum ada user dengan nama
                        // tersebut maka dianggap baru jalankan pertama, lakukan prosedur admin creation
                        if (userNameVar.is == "admin" && UserManager.getByName("admin") == None){
                            // create first
                            warn("first init detected, do admin creation")
                            UserManager.create(userNameVar.is, "", "", passwordVar.is, "")
                        }

                        Auth.adminLogin(userNameVar.is, passwordVar.is)
                        S.notice("Login success")
                    case x =>
                        throw InvalidParameterException(s"Unknown kind: $x")
                }

            }
            catch {
                case e:ZufaroException =>
                    S.error(e.getMessage)
            }
        }

        kindVar.setIsUnset("operator")

        val kinds = Seq(("admin", "ADMIN"), ("operator", "OPERATOR"))


        bind("in", in,
        "user-name" -> SHtml.text(userNameVar, userNameVar(_), "class" -> "form-control", "id" -> "UserName", "placeholder" -> "User name"),
        "password" -> SHtml.password(passwordVar, passwordVar(_), "class" -> "form-control", "id" -> "Password", "placeholder" -> "Password"),
        "kind" -> SHtml.select(kinds, Full(kindVar.is), kindVar(_), "class" -> "form-control", "id" -> "Kind", "placeholder" -> "kind"),
        "submit" -> SHtml.submit("Login", doLogin, "class" -> "btn btn-success")
        )
    }

    def surround:CssSel = {
        "#Main [data-lift]" #> {
            if (Auth.currentAdmin.is.isDefined)
                "Surround?with=admin;at=content"
            else
                "Surround?with=default;at=content"
        }
    }




}
