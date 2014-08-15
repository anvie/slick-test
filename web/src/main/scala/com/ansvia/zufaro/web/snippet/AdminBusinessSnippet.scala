package com.ansvia.zufaro.web.snippet

import net.liftweb._
import util._
import http._
import Helpers._
import scala.xml.{Text, NodeSeq}
import com.ansvia.zufaro.exception.{InvalidParameterException, ZufaroException}
import com.ansvia.zufaro.BusinessManager
import com.ansvia.zufaro.web.lib.MtTabInterface
import com.ansvia.zufaro.model.Tables._
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmd

/**
  * Author: robin
  * Date: 8/15/14
  * Time: 5:15 PM
  *
  */
class AdminBusinessSnippet {

     private object nameVar extends RequestVar("")
     private object descVar extends RequestVar("")
     private object fundVar extends RequestVar(0.0)
     private object divInvestorVar extends RequestVar(0.0)
     private object stateVar extends RequestVar(BusinessManager.state.DRAFT)

     def addNew(in:NodeSeq):NodeSeq = {

         def doAddNew() = {
             try {

                 if (nameVar.isEmpty)
                     throw InvalidParameterException("No name")
                 if (descVar.isEmpty)
                     throw InvalidParameterException("No description")

                 stateVar.is match {
                     case BusinessManager.state.DRAFT =>
                     case BusinessManager.state.PRODUCTION =>
                     case x =>
                         throw InvalidParameterException("Unknown business state " + x)
                 }


                 val bus = BusinessManager.create(nameVar, descVar, fundVar, divInvestorVar, stateVar)

                 S.redirectTo("/admin/business/project", () => S.notice(s"Success added business `${bus.name}` with id `${bus.id}`"))

             }catch{
                 case e:ZufaroException =>
                     S.error(e.getMessage)
             }
         }

         stateVar.setIsUnset(BusinessManager.state.DRAFT)

         bind("in", in,
         "name" -> SHtml.text(nameVar, nameVar(_), "class" -> "form-control", "id" -> "Name"),
         "description" -> SHtml.textarea(descVar, descVar(_), "class" -> "form-control", "id" -> "Desc"),
         "fund" -> SHtml.number(fundVar, fundVar(_), 0.0, 9999999.0, 1.0, "class" -> "form-control", "id" -> "Fund"),
         "div-investor" -> SHtml.number(divInvestorVar, divInvestorVar(_), 0.0, 100.0, 1.0, "class" -> "form-control", "id" -> "DivInvestor"),
         "submit" -> SHtml.submit("Add", doAddNew, "class" -> "btn btn-success")
         )
     }


    private def buildListItem(bus:BusinessRow, state:String):NodeSeq = {
        val deleteInternal = () => {

            BusinessManager.delete(bus)

            val ns = NodeSeq.fromSeq(state match {
                case "running" =>
                    BusinessManager.getRunningBusinessList(0, 10).flatMap(b => buildListItem(b, state))
                case "project" =>
                    BusinessManager.getProjectBusinessList(0, 10).flatMap(b => buildListItem(b, state))
            })
            val updater = new JsCmd {
                def toJsCmd: String = {
                    fixHtmlFunc("inline", ns){ nss =>
                        """$("#List").html(%s);""".format(nss)
                    }
                }
            }

            updater & JsRaw("alert('Success');").cmd
        }

        <tr>
            <td>{bus.id.toString}</td>
            <td>{bus.name}</td>
            <td>{bus.desc}</td>
            <td>{bus.fund}</td>
            <td>{bus.divideInvest}</td>
            <td>


                <div class="dropdown">
                    <a data-toggle="dropdown" href="#"><span class="glyphicon glyphicon-cog"></span></a>

                    <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
                        <li>
                            {SHtml.a(deleteInternal,Text("Delete"))}
                        </li>
                    </ul>
                </div>


            </td>
        </tr>
    }


    def businessList:CssSel = {
        S.attr("state").openOr("") match {
            case "running" =>
                val business = BusinessManager.getRunningBusinessList(0, 10)
                "#List *" #> business.map { client => buildListItem(client, "running") }
            case "project" =>
                val business = BusinessManager.getProjectBusinessList(0, 30)
                "#List *" #> business.map { client => buildListItem(client, "project") }
        }
    }    

 }


class AdminBusinessTab extends MtTabInterface {
    def defaultSelected: String = "running"

    def tmplDir: String = "admin/business"

    override def tabNames: Array[String] = Array("running", "project")

    override def preSendJs(tabName: String): JsCmd = {
        JsRaw(s"History.pushState(null,null,'/admin/business/$tabName');").cmd
    }
}

