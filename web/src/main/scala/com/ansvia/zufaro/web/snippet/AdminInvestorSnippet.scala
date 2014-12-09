package com.ansvia.zufaro.web.snippet

/**
 * Author: robin
 *
 */

import com.ansvia.zufaro.exception.{NotExistsException, InvalidParameterException, ZufaroException}
import com.ansvia.zufaro.model.Tables._
import com.ansvia.zufaro.model._
import com.ansvia.zufaro.web.lib.{HTML5HistoryHandler, MtTabInterface}
import com.ansvia.zufaro.web.util.JsUtils
import com.ansvia.zufaro.{InvestorManager, SexType, Zufaro}
import net.liftweb.common.Full
import net.liftweb.http._
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.util.Helpers._
import net.liftweb.util._
import org.joda.time.format.DateTimeFormat

import scala.slick.driver.PostgresDriver.simple._
import scala.xml.{Node, NodeSeq, Text}


class AdminInvestorSnippet {

    import com.ansvia.zufaro.InvestorHelpers._
    import com.ansvia.zufaro.ZufaroHelpers._

    private object nameVar extends RequestVar("")
    private object fullNameVar extends RequestVar("")
    private object sexVar extends RequestVar("")
    private object nationVar extends RequestVar("")
    private object birthPlaceVar extends RequestVar("")
    private object birthDateVar extends RequestVar("")
    private object religionVar extends RequestVar("")
    private object educationVar extends RequestVar("")
    private object titleFrontVar extends RequestVar("")
    private object titleBackVar extends RequestVar("")
    private object maritalStatusVar extends RequestVar("")
    private object maidenNameVar extends RequestVar("")
    private object passwordVar extends RequestVar("")
    private object verifyPasswordVar extends RequestVar("")
    private object roleVar extends RequestVar("")
    private object addressVar extends RequestVar("")
    private object cityVar extends RequestVar("")
    private object provinceVar extends RequestVar("")
    private object countryVar extends RequestVar("")
    private object postalCodeVar extends RequestVar("")
    private object emailVar extends RequestVar("")
    private object homePhoneVar extends RequestVar("")
    private object mobilePhoneVar extends RequestVar("")
    private object villageVar extends RequestVar("")
    private object districtVar extends RequestVar("")
//    private object identityBasedOnVar extends RequestVar("ktp")
    private object bbPinVar extends RequestVar("")
//    private object contactTypeVar extends RequestVar("")

    private lazy val birthDateRegex = """(\d{2})/(\d{2})/(\d{4})""".r
    private val dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy")

    def addNew(in:NodeSeq):NodeSeq = {

        def doCreateInternal() = {

            try {
                if (nameVar.isEmpty)
                    throw InvalidParameterException("No name")
                if (fullNameVar.isEmpty)
                    throw InvalidParameterException("No full name")
                if (sexVar.isEmpty)
                    throw InvalidParameterException("No sex type")
                if (nationVar.isEmpty)
                    throw InvalidParameterException("No nation information")
                if (birthPlaceVar.isEmpty)
                    throw InvalidParameterException("No birth place information")
                if (birthDateVar.isEmpty)
                    throw InvalidParameterException("No birth date information")
                if (religionVar.isEmpty)
                    throw InvalidParameterException("No religion information")
                if (educationVar.isEmpty)
                    throw InvalidParameterException("No education")
                if (maritalStatusVar.isEmpty)
                    throw InvalidParameterException("No marital status information")
                if (maidenNameVar.isEmpty)
                    throw InvalidParameterException("No maiden name")
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
                if (postalCodeVar.isEmpty)
                    throw InvalidParameterException("Please fill postal code information")
                if (homePhoneVar.isEmpty && mobilePhoneVar.isEmpty)
                    throw InvalidParameterException("Please fill at least 1 phone number")

                if (passwordVar.is != verifyPasswordVar.is)
                    throw InvalidParameterException("Password verification didn't match")

                val role = roleVar.is match {
                    case "owner" => InvestorRole.OWNER
                    case "operator" => InvestorRole.OPERATOR
                    case "supervisor" => InvestorRole.SUPERVISOR
                }

                val sex = sexVar.is match {
                    case "male" => SexType.MALE
                    case "female" => SexType.FEMALE
                }
                val birthDate = birthDateVar.is match {
                    case birthDateRegex(day, month, year) =>

                        val dt = dateTimeFormatter.parseDateTime(birthDateVar)
                        new java.sql.Date(dt.getMillis)

                    case x =>
                        throw InvalidParameterException(s"Invalid birth date format: $x, please use this format: dd/MM/yyyy.")
                }
                val religion = religionVar.is

                val maritalStatus = maritalStatusVar.is match {
                    case "single" => MaritalStatus.SINGLE
                    case "maried" => MaritalStatus.MARIED
                }

//                val identityBasedOn = identityBasedOnVar.is match {
//                    case "ktp" => IdentityType.KTP_BASED
//                    case "passport" => IdentityType.PASSPORT_BASED
//                    case "current-live" => IdentityType.CURRENTLY_LIVE
//                }
                val identityBasedOn = IdentityType.KTP_BASED


                val investor = Investor(0L, nameVar, fullNameVar, role, sex, nationVar, birthPlaceVar, birthDate,
                    religion, educationVar, titleFrontVar, titleBackVar, maritalStatus, maidenNameVar, "")

                val contact = InvestorContact(0L, addressVar, villageVar, districtVar, cityVar, provinceVar,
                        countryVar, postalCodeVar, emailVar, homePhoneVar, mobilePhoneVar, bbPinVar,
                        identityBasedOn, ContactType.PERSONAL)

                val inv = InvestorManager.create(investor, fullNameVar, contact)

                S.redirectTo("/admin/investor/active-investor", () => S.notice(s"Investor created ${inv.name} with id ${inv.id}"))
            }
            catch {
                case e:ZufaroException =>
                    S.error(e.getMessage)
            }

        }


        val sexTypes = Seq(("male", "MALE"), ("female", "FEMALE"))
        val roles = Seq(("owner", "OWNER"), ("operator", "OPERATOR"), ("supervisor", "SUPERVISOR"))
        val maritalTypes = Seq(("single", "SINGLE"), ("maried", "MARIED"))
//        val identityBasedOnTypes = Seq(("ktp", "KTP"), ("passport", "PASSPORT"), ("current-live","CURRENT LIVE"))
//        val contactType = Seq(("personal", "PERSONAL"), ("emergency", "EMERGENCY"))

        sexVar.setIsUnset("male")

        bind("in", in,
        "name" -> SHtml.text(nameVar, nameVar(_), "class" -> "form-control", "id" -> "Name"),
        "full-name" -> SHtml.text(fullNameVar, fullNameVar(_), "class" -> "form-control", "id" -> "FullName"),
        "sex" -> SHtml.select(sexTypes, Full(sexVar.is), sexVar(_), "class" -> "form-control", "id" -> "Sex"),
        "nation" -> SHtml.text(nationVar, nationVar(_), "class" -> "form-control", "id" -> "Nation"),
        "birth-place" -> SHtml.text(birthPlaceVar, birthPlaceVar(_), "class" -> "form-control", "id" -> "BirthPlace"),
        "birth-date" -> SHtml.text(birthDateVar, birthDateVar(_), "class" -> "form-control", "id" -> "BirthDate"),
        "religion" -> SHtml.text(religionVar, religionVar(_), "class" -> "form-control", "id" -> "Religion"),
        "education" -> SHtml.text(educationVar, educationVar(_), "class" -> "form-control", "id" -> "Education"),
        "title-front" -> SHtml.text(titleFrontVar, titleFrontVar(_), "class" -> "form-control", "id" -> "TitleFront"),
        "title-back" -> SHtml.text(titleBackVar, titleBackVar(_), "class" -> "form-control", "id" -> "TitleBack"),
        "marital-status" -> SHtml.select(maritalTypes, Full(maritalStatusVar.is), maritalStatusVar(_), "class" -> "form-control", "id" -> "MaritalStatus"),
        "maiden-name" -> SHtml.text(maidenNameVar, maidenNameVar(_), "class" -> "form-control", "id" -> "MaidenName"),
        "role" -> SHtml.select(roles, Full("owner"), roleVar(_), "class" -> "form-control"),
        "password" -> SHtml.password(passwordVar, passwordVar(_), "class" -> "form-control", "id" -> "Password"),
        "verify-password" -> SHtml.password(verifyPasswordVar, verifyPasswordVar(_), "class" -> "form-control", "id" -> "VerifyPassword"),
        "email" -> SHtml.text(emailVar, emailVar(_), "class" -> "form-control", "id" -> "Email"),
        "address" -> SHtml.textarea(addressVar, addressVar(_), "class" -> "form-control", "id" -> "Address"),
        "city" -> SHtml.text(cityVar, cityVar(_), "class" -> "form-control", "id" -> "City"),
        "province" -> SHtml.text(provinceVar, provinceVar(_), "class" -> "form-control", "id" -> "Province"),
        "country" -> SHtml.text(countryVar, countryVar(_), "class" -> "form-control", "id" -> "Country"),
        "postal-code" -> SHtml.text(postalCodeVar, postalCodeVar(_), "class" -> "form-control", "id" -> "PostalCode"),
        "home-phone" -> SHtml.text(homePhoneVar, homePhoneVar(_), "class" -> "form-control", "id" -> "MobilePhone"),
        "mobile-phone" -> SHtml.text(mobilePhoneVar, mobilePhoneVar(_), "class" -> "form-control", "id" -> "HomePhone"),
        "bb-pin" -> SHtml.text(bbPinVar, bbPinVar(_), "class" -> "form-control", "id" -> "BBPin"),
        "village" -> SHtml.text(villageVar, villageVar(_), "class" -> "form-control", "id" -> "Village"),
        "district" -> SHtml.text(districtVar, districtVar(_), "class" -> "form-control", "id" -> "District"),
//        "identity-based-on" -> SHtml.select(identityBasedOnTypes, Full(identityBasedOnVar.is), identityBasedOnVar(_), "class" -> "form-control", "id" -> "IdentityBasedOn"),
        "submit" -> SHtml.submit("Create", doCreateInternal, "class" -> "btn btn-success")
        )
    }


    import com.ansvia.zufaro.InvestorManager.status


    case class InvestorWithContact(inv:Investor, contact:InvestorContact)


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
                {invContact.city} - <a href={"/admin/investor/%s/data".format(inv.id)}>show detail</a>
            </td>
        }

        val changePassword = {
            SHtml.a(()=>{
                val ns = S.runTemplate("admin" :: "investor" :: "dialog" :: "_chunk_dialog-change-password" :: Nil).openOr(NodeSeq.Empty)
                JsUtils.modalDialog(("#Dialog [class]" #> s"lift:AdminInvestorSnippet.dialogChangePassword?invId=${inv.id}").apply(ns))
            }, Text("Change password"))
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
                        <li>{changePassword}</li>
                        <li class="divider"></li>
                        <li>{deleter}</li>
                    </ul>
                </div>

            </td>
        </tr>
    }

    def investorList:CssSel = {
        val investors = Zufaro.db.withSession { implicit sess =>
            val q = InvestorContacts join Investors.filter(_.status === status.ACTIVE) on (_.investorId === _.id)
            q.run.map(a => InvestorWithContact(a._2, a._1))
        }

        "#List *" #> NodeSeq.fromSeq(investors.map(buildInvestorListItem))
    }


    def dialogChangePassword(in:NodeSeq):NodeSeq = {

        val inv = InvestorManager.getById(S.attr("invId").openOrThrowException("no invId").toLong).getOrElse {
            throw NotExistsException("No investor account exists")
        }

        def updateInternal = () => {
            try {

                if (passwordVar.isEmpty)
                    throw InvalidParameterException("Please fill the new password")
                if (verifyPasswordVar.isEmpty)
                    throw InvalidParameterException("Please repeat your password")
                if (passwordVar.is != verifyPasswordVar.is)
                    throw InvalidParameterException("Your password and verification didn't match")

                InvestorManager.updatePassword(inv.id, passwordVar.is)

                JsUtils.hideAllModal & JsUtils.showNotice("Investor password updated successfully")
            }catch{
                case e:ZufaroException =>
                    JsUtils.showError(e.getMessage)
            }
        }

        SHtml.ajaxForm(bind("in",in,
            "password" -> SHtml.password(passwordVar, passwordVar(_)),
            "password-verify" -> SHtml.password(verifyPasswordVar, verifyPasswordVar(_)),
            "submit" -> S.formGroup(1000){
                SHtml.hidden(updateInternal) ++
                SHtml.submit("Update", updateInternal)
            }
        ))
    }


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


