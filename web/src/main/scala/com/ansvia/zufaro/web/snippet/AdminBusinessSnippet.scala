package com.ansvia.zufaro.web.snippet

import net.liftweb._
import util._
import http._
import Helpers._
import scala.xml.NodeSeq
import net.liftweb.common.Full
import com.ansvia.zufaro.exception.{InvalidParameterException, ZufaroException}
import com.ansvia.zufaro.{ApiClientManager, BusinessManager}
import com.ansvia.zufaro.model.UserRole
import com.ansvia.zufaro.web.lib.MtTabInterface

/**
  * Author: robin
  * Date: 8/15/14
  * Time: 5:15 PM
  *
  */
class AdminBusinessSnippet {

     private object nameVar extends RequestVar("")
     private object descVar extends RequestVar("")
     private object grantVar extends RequestVar("")
     private object businessVar extends RequestVar("")
     private object fundVar extends RequestVar(0.0)
     private object divInvestorVar extends RequestVar(0.0)
     private object stateVar extends RequestVar(0)

     def addNew(in:NodeSeq):NodeSeq = {

         def doAddNew() = {
             try {

                 if (nameVar.isEmpty)
                     throw InvalidParameterException("No name")
                 if (descVar.isEmpty)
                     throw InvalidParameterException("No description")


                 val bus = BusinessManager.create(nameVar, descVar, fundVar, divInvestorVar, stateVar)


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
         "fund" -> SHtml.number(fundVar, fundVar(_), 0.0, 9999999.0, 1.0, "class" -> "form-control", "id" -> "Fund"),
         "div-investor" -> SHtml.number(divInvestorVar, divInvestorVar(_), 0.0, 100.0, 1.0, "class" -> "form-control", "id" -> "DivInvestor"),
         "grant" -> SHtml.select(grants, Full(grantVar), grantVar(_), "class" -> "form-control", "id" -> "Grant"),
         "business" -> SHtml.select(business, Full(businessVar), businessVar(_), "class" -> "form-control", "id" -> "Business"),
         "submit" -> SHtml.submit("Add", doAddNew, "class" -> "btn btn-success")
         )
     }

 }


class AdminBusinessTab extends MtTabInterface {
    def defaultSelected: String = "active-client"

    def tmplDir: String = "admin/api"

    override def tabNames: Array[String] = Array("active-client", "suspended-client")
}

