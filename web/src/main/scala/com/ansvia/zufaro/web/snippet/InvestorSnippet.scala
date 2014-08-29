package com.ansvia.zufaro.web.snippet

import net.liftweb._
import util._
import Helpers._
import http._
import scala.xml.{Node, NodeSeq}
import net.liftweb.http.{RequestVar, SHtml}
import com.ansvia.zufaro.exception.ZufaroException
import com.ansvia.zufaro.web.Auth
import com.ansvia.zufaro.Activity
import com.ansvia.zufaro.Activity.ActivityStreamItem
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

/**
 * Author: robin
 * Date: 8/21/14
 * Time: 9:48 AM
 *
 */
object InvestorSnippet {

    private object userNameVar extends RequestVar("")
    private object passwordVar extends RequestVar("")

    def investorO = Auth.currentInvestor.is

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

    private val dateTimeFormat = DateTimeFormat.forPattern("dd MMM yyyy HH:mm:ss")
    
    private def buildActivityListItem(activity:ActivityStreamItem):Node = {

        val date = new DateTime(activity.ts).toString(dateTimeFormat)

        <tr>
            <td>{activity.activity}</td>
            <td>{activity.info}</td>
            <td>{date}</td>
        </tr>
    }


    def recentActivityList:CssSel = {
        val activities = Activity.getActivities(investorO.openOrThrowException("only for logged in investor"), 0, 5)
        "#ListRecentActivity *" #> NodeSeq.fromSeq(activities.map(a => buildActivityListItem(a)))
    }

}
