package com.ansvia.zufaro.web.snippet

import scala.xml.NodeSeq
import com.ansvia.zufaro.web.Auth
import net.liftweb.http.S
import net.liftweb._
import util._
import Helpers._
import com.ansvia.zufaro.model.UserRole

/**
 * Author: robin
 * Date: 8/14/14
 * Time: 9:52 PM
 *
 */
object AuthSnippet {

    def isLoggedIn(in:NodeSeq):NodeSeq = {
        if (Auth.isLoggedIn_?(S.attr("who").openOr("admin"))){
            S.attr("isYes").openOr("") match {
                case "redirect" =>
                    S.redirectTo(S.attr("redirectTo").openOr("/"), ()=> S.error("Not Found"))
                case _ =>
                    in
            }
        } else {
            S.attr("isNot").openOr("") match {
                case "redirect" =>
                    S.redirectTo(S.attr("redirectTo").openOr("/"), ()=> S.error("Not Found"))
                case _ =>
                    NodeSeq.Empty
            }
        }
    }

    def isAnon(in:NodeSeq):NodeSeq = {
        if (!Auth.isLoggedIn_?(S.attr("who").openOr("admin")))
            in
        else
            NodeSeq.Empty
    }


    def loginInfo(in:NodeSeq):NodeSeq = {
        val who = S.attr("who").openOr("admin")
        val (name, role) =
            who match {
                case "admin" =>
                    (Auth.currentUser.is.map(_.name).getOrElse("unknown"), UserRole.toStr(UserRole.ADMIN))
//                case "operator" =>
//                    (Auth.currentOperator.is.map(_.name).getOrElse("unknown"), UserRole.toStr(UserRole.OPERATOR))
                case "investor" =>
                    (Auth.currentInvestor.is.map(_.name).getOrElse("unknown"), UserRole.toStr(UserRole.INVESTOR))
            }
        bind("in", in,
            "name" -> name,
            "role" -> role
        )
    }


}
