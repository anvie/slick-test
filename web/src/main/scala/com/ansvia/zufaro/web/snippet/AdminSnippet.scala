package com.ansvia.zufaro.web.snippet

import net.liftweb._
import http._
import util._
import Helpers._
import scala.xml.NodeSeq
import com.ansvia.zufaro.web.Auth
import com.ansvia.zufaro.exception.{InvalidParameterException, ZufaroException}

/**
 * Author: robin
 * Date: 8/14/14
 * Time: 8:16 PM
 *
 */
class AdminSnippet {

    private object userNameVar extends RequestVar("")
    private object passwordVar extends RequestVar("")

    def login(in:NodeSeq):NodeSeq = {

        def doLogin() = {

            try {

                if (userNameVar.is.trim.length == 0)
                    throw InvalidParameterException("Please enter user name")
                if (passwordVar.is.trim.length == 0)
                    throw InvalidParameterException("Please enter password")

                Auth.adminLogin(userNameVar.is, passwordVar.is)
            }
            catch {
                case e:ZufaroException =>
                    S.error(e.getMessage)
            }
        }


        bind("in", in,
        "user-name" -> SHtml.text("", userNameVar(_), "class" -> "form-control"),
        "password" -> SHtml.password("", passwordVar(_), "class" -> "form-control"),
        "submit" -> SHtml.submit("Login", doLogin, "class" -> "btn btn-success")
        )
    }

}
