package com.ansvia.zufaro.web.snippet

/**
 * Author: robin
 * Date: 8/16/14
 * Time: 12:26 PM
 *
 */

import net.liftweb._
import util._
import http._
import Helpers._
import scala.xml.{Node, Text, NodeSeq}
import com.ansvia.zufaro.{Zufaro, InvestorManager}
import com.ansvia.zufaro.web.lib.{HTML5HistoryHandler, MtTabInterface}
import com.ansvia.zufaro.model.Tables._
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmd
import com.ansvia.zufaro.exception.{NotExistsException, ZufaroException, InvalidParameterException}
import net.liftweb.common.Full
import com.ansvia.zufaro.model.{UserRole, Initiator, InvestorRole}
import com.ansvia.zufaro.web.util.JsUtils
import net.liftweb.http.js.JsCmds.SetHtml
import com.ansvia.zufaro.web.Auth
import com.ansvia.zufaro.InvestorManager.{Contact, Address}
import scala.slick.driver.PostgresDriver.backend
import scala.slick.driver.PostgresDriver.simple._


class AdminInvestorSnippet {

    import com.ansvia.zufaro.InvestorHelpers._
    import com.ansvia.zufaro.ZufaroHelpers._

    private object nameVar extends RequestVar("")
    private object fullNameVar extends RequestVar("")
    private object passwordVar extends RequestVar("")
    private object verifyPasswordVar extends RequestVar("")
    private object roleVar extends RequestVar("")
    private object addressVar extends RequestVar("")
    private object cityVar extends RequestVar("")
    private object provinceVar extends RequestVar("")
    private object countryVar extends RequestVar("")
    private object postalCodeVar extends RequestVar("")
    private object emailVar extends RequestVar("")
    private object phone1Var extends RequestVar("")
    private object phone2Var extends RequestVar("")

    def addNew(in:NodeSeq):NodeSeq = {

        def doCreateInternal() = {

            try {
                if (nameVar.isEmpty)
                    throw InvalidParameterException("No name")
                if (passwordVar.isEmpty)
                    throw InvalidParameterException("Please enter password")
                if (verifyPasswordVar.isEmpty)
                    throw InvalidParameterException("Please verify password")
                if (emailVar.isEmpty)
                    throw InvalidParameterException("Please fill email")
                if (addressVar.isEmpty)
                    throw InvalidParameterException("Please fill address information")
                if (cityVar.isEmpty)
                    throw InvalidParameterException("Please fill address city information")
                if (provinceVar.isEmpty)
                    throw InvalidParameterException("Please fill address province information")
                if (countryVar.isEmpty)
                    throw InvalidParameterException("Please fill address country information")
                if (postalCodeVar.is == 0L)
                    throw InvalidParameterException("Please fill postal code information")
                if (phone1Var.isEmpty && phone2Var.isEmpty)
                    throw InvalidParameterException("Please fill at least 1 phone number")

                if (passwordVar.is != verifyPasswordVar.is)
                    throw InvalidParameterException("Password verification didn't match")

                val role = roleVar.is match {
                    case "owner" => InvestorRole.OWNER
                    case "operator" => InvestorRole.OPERATOR
                    case "supervisor" => InvestorRole.SUPERVISOR
                }

                val addr = Address(addressVar, cityVar, provinceVar, countryVar, postalCodeVar.is.toLong)
                addr.validate()
                val contact = Contact(addr, emailVar, phone1Var, phone2Var)

                val inv = InvestorManager.create(nameVar, fullNameVar, role, passwordVar, contact)

                S.redirectTo("/admin/investor/active-investor", () => S.notice(s"Investor created ${inv.name} with id ${inv.id}"))
            }
            catch {
                case e:ZufaroException =>
                    S.error(e.getMessage)
            }

        }


        val roles = Seq(("owner", "OWNER"), ("operator", "OPERATOR"), ("supervisor", "SUPERVISOR"))


        bind("in", in,
        "name" -> SHtml.text(nameVar, nameVar(_), "class" -> "form-control", "id" -> "Name"),
        "role" -> SHtml.select(roles, Full("owner"), roleVar(_), "class" -> "form-control"),
        "password" -> SHtml.password(passwordVar, passwordVar(_), "class" -> "form-control", "id" -> "Password"),
        "verify-password" -> SHtml.password(verifyPasswordVar, verifyPasswordVar(_), "class" -> "form-control", "id" -> "VerifyPassword"),
        "email" -> SHtml.text(emailVar, emailVar(_), "class" -> "form-control", "id" -> "Email"),
        "address" -> SHtml.textarea(addressVar, addressVar(_), "class" -> "form-control", "id" -> "Address"),
        "city" -> SHtml.text(cityVar, cityVar(_), "class" -> "form-control", "id" -> "City"),
        "province" -> SHtml.text(provinceVar, provinceVar(_), "class" -> "form-control", "id" -> "Province"),
        "country" -> SHtml.text(countryVar, countryVar(_), "class" -> "form-control", "id" -> "Country"),
        "postal-code" -> SHtml.text(postalCodeVar, postalCodeVar(_), "class" -> "form-control", "id" -> "PostalCode"),
        "phone1" -> SHtml.text(phone1Var, phone1Var(_), "class" -> "form-control", "id" -> "Phone1"),
        "phone2" -> SHtml.text(phone2Var, phone2Var(_), "class" -> "form-control", "id" -> "Phone2"),
        "submit" -> SHtml.submit("Create", doCreateInternal, "class" -> "btn btn-success")
        )
    }


    import InvestorManager.status

    private def buildInvestorListItem(invWithContact:InvestorWithContact):Node = {

        val inv = invWithContact.inv

        def updater() = {

            val investors = Zufaro.db.withSession { implicit sess =>
                //            Investors.filter(_.status === status.ACTIVE).drop(0).take(50).run
                val q = InvestorContacts join Investors.filter(_.status === status.ACTIVE) on (_.investorId === _.id)
                q.run.map(a => InvestorWithContact(a._2, a._1))
            }

            val ns = NodeSeq.fromSeq(investors.map(buildInvestorListItem))
            SetHtml("List", ns)
        }

        val deleter = {
            JsUtils.ajaxConfirm("Are you sure to delete this investor? " +
                "This operation cannot be undone", Text("delete"), "Delete this investor"){

                InvestorManager.delete(inv)

                updater()
            }
        }


        val businessNs = inv.getBusiness(0, 10).map { bus =>
            <li>{bus.name} #{bus.id}</li>
        }

        val invContact = invWithContact.contact

        val contact = {
            <td>
                {invContact.city} - <a href="javascript://" onclick={s"showAddressDetail('${inv.id}');"}>show detail</a>
                <div id={s"AddressDetail-${inv.id}"} class="hidden">
                    {
                    <strong>Address:</strong><br />
                    <p>{invContact.address}</p>
                    <p>City: {invContact.city}<br />
                    Province: {invContact.province}<br />
                    Country: {invContact.country}<br />
                    Postal code: {invContact.postalCode}<br />
                    </p>
                    <hr />
                    <p><strong>Phone:</strong><br />
                        <ul>
                            <li>home: {invContact.homePhone}</li>
                            <li>mobile: {invContact.mobilePhone}</li>
                        </ul>
                    </p>
                    <p><strong>BB PIN: {invContact.bbPin}</strong>
                    </p>
                    <hr />
                    <p><strong>Email: </strong><br />
                        {invContact.email}
                    </p>
                    }
                </div>
            </td>
        }

        <tr>
            <td>{inv.id}</td>
            <td>{inv.name}</td>
            {contact}
            <td>{
                if (businessNs.length > 0){
                    <ul>{NodeSeq.fromSeq(businessNs)}</ul>
                }else
                    Text("-")
                }
            </td>
            <td>{inv.getBalance.format(IDR)}</td>
            <td>

                <div class="dropdown">
                    <a data-toggle="dropdown" href="#"><span class="glyphicon glyphicon-cog"></span></a>

                    <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
                        <li><a href={s"/admin/investor/${inv.id}/deposit"}>Account</a></li>
                        <li><a href={s"/admin/investor/${inv.id}/business"}>Investment</a></li>
                        <li class="divider"></li>
                        <li>{deleter}</li>
                    </ul>
                </div>

            </td>
        </tr>
    }

    case class InvestorWithContact(inv:Investor, contact:InvestorContact)

    def investorList:CssSel = {
//        val investors = InvestorManager.getList(0, 50)

        val investors = Zufaro.db.withSession { implicit sess =>
//            Investors.filter(_.status === status.ACTIVE).drop(0).take(50).run
            val q = InvestorContacts join Investors.filter(_.status === status.ACTIVE) on (_.investorId === _.id)
            q.run.map(a => InvestorWithContact(a._2, a._1))
        }

        "#List *" #> NodeSeq.fromSeq(investors.map(buildInvestorListItem))
    }

//    private def invO = {
//        val id = S.param("invId").openOr("0").toLong
//        InvestorManager.getById(id)
//    }


}


object AdminInvestorTab extends MtTabInterface with HTML5HistoryHandler {
    def defaultSelected: String = "active-investor"

    def tmplDir: String = "admin/investor"

    override def tabNames: Array[String] = Array("active-investor", "suspended-investor")

//    override def preSendJs(tabName: String): JsCmd = {
//        JsRaw(s"History.pushState(null,null,'/admin/investor/$tabName');").cmd
//    }

    lazy val rewrite:LiftRules.RewritePF = NamedPF(s"${getClass.getSimpleName}Rewrite"){
        case RewriteRequest(ParsePath("admin" :: "investor" :: tabRe(tab) :: Nil, _, _, _), _, _) =>
            RewriteResponse("admin" :: "investor" :: Nil, Map("tab" -> tab))
    }
}


