package com.ansvia.zufaro.web.snippet

import net.liftweb._
import util._
import http._
import Helpers._
import scala.xml.{Text, Node, NodeSeq}
import net.liftweb.http.S
import com.ansvia.zufaro.{BusinessManager, InvestorManager}
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.SetHtml
import com.ansvia.zufaro.web.util.JsUtils
import net.liftweb.util.CssSel
import net.liftweb.common.Full
import com.ansvia.zufaro.exception.{ZufaroException, NotExistsException, InvalidParameterException}

/**
 * Author: robin
 * Date: 8/16/14
 * Time: 7:58 PM
 *
 */
class AdminInvestorBusinessSnippet {


    import com.ansvia.zufaro.model.Tables._
    import com.ansvia.zufaro.InvestorHelpers._

    private def invO = {
        val id = S.param("invId").openOr("0").toLong
        InvestorManager.getById(id)
    }



    def pageTitle:NodeSeq = {
        val name = invO.map(_.name).getOrElse("")
        <h3>Account: {name}</h3>
    }

    def ops(in:NodeSeq):NodeSeq = {
        val invId = S.param("invId").openOr("0").toLong
        def addBusiness() = {

            val ns = S.runTemplate("admin" :: "investor" :: "_chunk_dialog-add-business" :: Nil).openOr(NodeSeq.Empty)

            val ns2 = ("#Dialog [class]" #> s"lift:AdminInvestorBusinessSnippet.dialogAddBusiness?invId=$invId").apply(ns)

            JsUtils.modalDialog(ns2)
        }

        bind("in", in,
        "add-business" -> SHtml.a(()=>addBusiness(),Text("Add business"), "class" -> "btn btn-success")
        )
    }

    private object businessVar extends RequestVar("")
    private object investAmountVar extends RequestVar[Double](0.0)

    def dialogAddBusiness(in:NodeSeq):NodeSeq = {

        val inv = {
            val id = S.attr("invId").openOr("0").toLong
            InvestorManager.getById(id).get
        }

        def doAddBusiness() = {
            try {
                if (investAmountVar.is < 0.1)
                    throw InvalidParameterException("Invalid amount")

                val business = BusinessManager.getById(businessVar.is.toLong).getOrElse {
                    throw NotExistsException("No business with id " + businessVar.is)
                }

                inv.invest(business, investAmountVar)

                JsUtils.hideAllModal &
                JsUtils.showNotice("Success") &
                updateList(inv)
            }
            catch {
                case e:ZufaroException =>
                    JsUtils.showError(e.getMessage)
            }
        }


        val businessList = Seq(("0","-")) ++
            BusinessManager.getList(0, 50, BusinessManager.state.ANY).map(bus => (bus.id.toString, bus.name))

        SHtml.ajaxForm(
        bind("in", in,
        "business-select" -> SHtml.select(businessList, Full("0"), businessVar(_), "class" -> "form-control"),
        "amount" -> SHtml.number(investAmountVar, investAmountVar(_), 0.0, 999999, 0.1, "class" -> "form-control"),
        "submit" -> S.formGroup(1000){
            SHtml.hidden(doAddBusiness) ++ SHtml.submit("Add", doAddBusiness, "class" -> "btn btn-success")
        }
        )
        )
    }




    private def buildListItem(inv:InvestorRow, bus:BusinessRow):Node = {

        def updater() = {
            val business = invO.get.getBusiness(0, 30)
            val ns = NodeSeq.fromSeq(business.map(b => buildListItem(inv, b)))
            new JsCmd {
                def toJsCmd: String = {
                    fixHtmlFunc("inline", ns){ nss =>
                        SetHtml("List", ns)
                    }
                }
            }
        }

        val remover = {
            JsUtils.ajaxConfirm("Are you sure to remove this business from your investment? " +
                "You may lost your share and this operation cannot be undone!", 
                Text("remove"), "Remove business from this investment"){

                inv.removeInvestment(bus)

                updater()
            }
        }
        val status = {
            bus.state match {
                case BusinessManager.state.DRAFT => "project"
                case BusinessManager.state.PRODUCTION => "running"
                case BusinessManager.state.CLOSED => "closed"
                case _ => "unknown"
            }
        }
        val shareAmount = {
            val s = inv.getShare(bus)
            f"Rp.$s%.02f,-"
        }
        
        <tr>
            <td>{bus.id}</td>
            <td>{bus.name}</td>
            <td>{bus.desc}</td>
            <td>{shareAmount}</td>
            <td>{status}</td>
            <td>
                <div class="dropdown">
                    <a data-toggle="dropdown" href="#"><span class="glyphicon glyphicon-cog"></span></a>

                    <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
                        <li class="divider"></li>
                        <li>{remover}</li>
                    </ul>
                </div>

            </td>
        </tr>
    }


    def list:CssSel = {
        val business = invO.get.getBusiness(0, 30)
        "#List *" #> NodeSeq.fromSeq(business.map(b => buildListItem(invO.get, b)))
    }


    def updateList(inv:InvestorRow) = {
        val business = inv.getBusiness(0, 30)
        SetHtml("List", NodeSeq.fromSeq(business.map(b => buildListItem(inv, b))))
    }

}
