package com.ansvia.zufaro.web.snippet

import net.liftweb._
import util._
import http._
import Helpers._
import com.ansvia.zufaro.web.lib.MtTabInterface
import scala.xml.NodeSeq
import net.liftweb.common.Full
import com.ansvia.zufaro.exception.{InvalidParameterException, ZufaroException}
import com.ansvia.zufaro.{ApiClientManager, BusinessManager}
import com.ansvia.zufaro.model.UserRole

/**
 * Author: robin
 * Date: 8/15/14
 * Time: 5:15 PM
 *
 */
class AdminApiSnippet {

    private object nameVar extends RequestVar("")
    private object descVar extends RequestVar("")
    private object grantVar extends RequestVar("")
    private object businessVar extends RequestVar("")

    def addNew(in:NodeSeq):NodeSeq = {

        def doAddNew() = {
            try {

                if (nameVar.isEmpty)
                    throw InvalidParameterException("No name")
                if (descVar.isEmpty)
                    throw InvalidParameterException("No description")


//                val bus = BusinessManager.create(nameVar, descVar, fundVar, divInvestorVar, stateVar)

                val grant = grantVar.is match {
                    case "all" => "all"
                    case x =>
                        throw InvalidParameterException("Unknown grant type: " + x)
                }

                val accesses = BusinessManager.getById(businessVar.is.toLong)
                    .map(b => ApiClientManager.Access(grant, "bus=" + b.id)).toSeq

                val apiClient = ApiClientManager.create(nameVar, descVar, 0L, UserRole.ADMIN, accesses)

                S.notice(s"API client created ${apiClient.name} with id ${apiClient.id}")

            }catch{
                case e:ZufaroException =>
                    S.error(e.getMessage)
            }
        }


        val grants = Seq(("all", "ALL"))
        val business = BusinessManager.getBusiness(0, 100).toSeq.map(bus => (bus.id.toString, bus.name))


        bind("in", in,
        "name" -> SHtml.text(nameVar, nameVar(_), "class" -> "form-control", "id" -> "Name"),
        "description" -> SHtml.textarea(descVar, descVar(_), "class" -> "form-control", "id" -> "Desc"),
        "grant" -> SHtml.select(grants, Full(grantVar), grantVar(_), "class" -> "form-control", "id" -> "Grant"),
        "business" -> SHtml.select(business, Full(businessVar), businessVar(_), "class" -> "form-control", "id" -> "Business"),
        "submit" -> SHtml.submit("Add", doAddNew, "class" -> "btn btn-success")
        )
    }

}

class AdminApiTab extends MtTabInterface {
    def defaultSelected: String = "active-client"

    def tmplDir: String = "admin/api"

    override def tabNames: Array[String] = Array("active-client", "suspended-client")
}
