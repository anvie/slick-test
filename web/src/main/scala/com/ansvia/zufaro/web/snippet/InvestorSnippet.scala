package com.ansvia.zufaro.web.snippet

import net.liftweb._
import util._
import Helpers._
import http._
import scala.xml.NodeSeq
import net.liftweb.http.{RequestVar, SHtml}
import com.ansvia.zufaro.exception.ZufaroException
import com.ansvia.zufaro.web.Auth

/**
 * Author: robin
 * Date: 8/21/14
 * Time: 9:48 AM
 *
 */
object InvestorSnippet {

    private object userNameVar extends RequestVar("")
    private object passwordVar extends RequestVar("")

    def login(in:NodeSeq):NodeSeq = {

        def doLoginInternal() = {
            try {
                Auth.investorLogin(userNameVar, passwordVar)
                S.redirectTo("/investor")
            }catch{
                case e:ZufaroException =>
                    S.error(e.getMessage)
            }
        }

        bind("in", in,
            "user-name" -> SHtml.text(userNameVar, userNameVar(_), "class" -> "form-control", "id" -> "UserName"),
            "password" -> SHtml.password(passwordVar, passwordVar(_), "class" -> "form-control", "id" -> "Password"),
            "submit" -> //S.formGroup(3000){
//                SHtml.hidden(doLoginInternal) ++
                SHtml.submit("Login", doLoginInternal, "class" -> "btn btn-success")
            //}
        )
    }

}
