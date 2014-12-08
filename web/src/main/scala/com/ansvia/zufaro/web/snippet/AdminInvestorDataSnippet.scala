package com.ansvia.zufaro.web.snippet


import com.ansvia.zufaro.exception.{InvalidParameterException, ZufaroException}
import com.ansvia.zufaro.model.Tables.{Investor, InvestorContact}
import com.ansvia.zufaro.model.{ContactKind, IdentityType, InvestorRole, MaritalStatus}
import com.ansvia.zufaro.{PasswordUtil, InvestorManager, SexType}
import net.liftweb.common.Full
import net.liftweb.http._
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import org.joda.time.format.DateTimeFormat

import scala.xml.{NodeSeq, Text}


/**
 * Author: Robin (r@nosql.asia)
 *
 */

class AdminInvestorDataSnippet {

    private lazy val invO = {
        S.param("invId").flatMap {
            case AsLong(id) =>
                InvestorManager.getById(id)
            case x =>
                throw new InvalidParameterException("invalid investor id: " + x)
        }
    }
    private lazy val contactKind = {
        S.param("contactKind").openOr("ktp")
    }

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
    private object motherNameVar extends RequestVar("")
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
    private object contactKindVar extends RequestVar("")

    private lazy val birthDateRegex = """(\d{2})/(\d{2})/(\d{4})""".r
    private val dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy")

    // @TODO(robin): test fungsional ini secara manual
    def updateDataBasic(in:NodeSeq):NodeSeq = {

        val _idType = S.attr("idType").openOr("ktp")

        def doUpdateInternal() = {

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
                if (motherNameVar.isEmpty)
                    throw InvalidParameterException("No mother name")
                if (passwordVar.isEmpty)
                    throw InvalidParameterException("Please enter password")
                if (verifyPasswordVar.isEmpty)
                    throw InvalidParameterException("Please verify password")


                if (passwordVar.is != verifyPasswordVar.is)
                    throw InvalidParameterException("Password verification didn't match")

                val inv = invO.openOrThrowException("no investor ref session")

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

//                val identityBasedOn = IdentityType.KTP_BASED

                val investor = Investor(0L, nameVar, fullNameVar, role, sex, nationVar, birthPlaceVar, birthDate,
                    religion, educationVar, titleFrontVar, titleBackVar, maritalStatus, motherNameVar, "")


                // apabila password berbeda dengan yang lama maka perlu update juga password-nya
                val newPassword = if (!PasswordUtil.isMatch(passwordVar, inv.passhash))
                    Some(passwordVar.is)
                else
                    None

                val idType = _idType match {
                    case "ktp" => IdentityType.KTP_BASED
                    case "passport" => IdentityType.PASSPORT_BASED
                }

                val invUpdated = InvestorManager.updateBasicInfo(investor.id, investor, newPassword, idType)

                S.redirectTo("/admin/investor/active-investor", () => S.notice(s"Investor created ${invUpdated.name} with id ${invUpdated.id}"))
            }
            catch {
                case e:ZufaroException =>
                    S.error(e.getMessage)
            }

        }


        val inv = invO.openOrThrowException("investor not exists")

        val sexTypes = Seq(("male", "MALE"), ("female", "FEMALE"))
        val roles = Seq(("owner", "OWNER"), ("operator", "OPERATOR"), ("supervisor", "SUPERVISOR"))
        val maritalTypes = Seq(("single", "SINGLE"), ("maried", "MARIED"))

        nameVar.setIsUnset(inv.name)
        fullNameVar.setIsUnset(inv.fullName)
        sexVar.setIsUnset(SexType.toStr(inv.sex).toLowerCase)
        nationVar.setIsUnset(inv.nation)
        birthPlaceVar.setIsUnset(inv.birthPlace)
        birthDateVar.setIsUnset(dateTimeFormatter.print(inv.birthDate.getTime))
        religionVar.setIsUnset(inv.religion)
        educationVar.setIsUnset(inv.education)
        titleFrontVar.setIsUnset(inv.titleFront)
        titleBackVar.setIsUnset(inv.titleBack)
        maritalStatusVar.setIsUnset(MaritalStatus.toStr(inv.maritalStatus).toLowerCase)
        motherNameVar.setIsUnset(inv.motherName)

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
            "mother-name" -> SHtml.text(motherNameVar, motherNameVar(_), "class" -> "form-control", "id" -> "MotherName"),
            "role" -> SHtml.select(roles, Full("owner"), roleVar(_), "class" -> "form-control"),
            //        "identity-based-on" -> SHtml.select(identityBasedOnTypes, Full(identityBasedOnVar.is), identityBasedOnVar(_), "class" -> "form-control", "id" -> "IdentityBasedOn"),
            "submit" -> SHtml.submit("Create", doUpdateInternal, "class" -> "btn btn-success")
        )
    }

    // @TODO(robin): test fungsional ini secara manual
    def updateDataContact(in:NodeSeq):NodeSeq = {

        def doUpdateInternal() = {

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
                if (motherNameVar.isEmpty)
                    throw InvalidParameterException("No mother name")
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

                //                val contactKind = contactKindVar.is match {
                //                    case "personal" => ContactKind.PERSONAL
                //                    case "emergency" => ContactKind.EMERGENCY
                //                }


                val investor = Investor(0L, nameVar, fullNameVar, role, sex, nationVar, birthPlaceVar, birthDate,
                    religion, educationVar, titleFrontVar, titleBackVar, maritalStatus, motherNameVar, "")

                val contact = InvestorContact(0L, addressVar, villageVar, districtVar, cityVar, provinceVar,
                    countryVar, postalCodeVar, emailVar, homePhoneVar, mobilePhoneVar, bbPinVar,
                    identityBasedOn, ContactKind.PERSONAL)

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
        //        val contactKind = Seq(("personal", "PERSONAL"), ("emergency", "EMERGENCY"))

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
            "mother-name" -> SHtml.text(motherNameVar, motherNameVar(_), "class" -> "form-control", "id" -> "MotherName"),
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
            "submit" -> SHtml.submit("Create", doUpdateInternal, "class" -> "btn btn-success")
        )
    }


    def pageTitle:NodeSeq = {
        invO.map { inv =>
            Text("Contacts: " + inv.name)
        }.getOrElse(NodeSeq.Empty)
    }

    def detailTitle:CssSel = {
        "Kind *" #> contactKind
    }


    def dataList:NodeSeq = {
        invO.map { inv =>
            <ul>
                <li><a href={"/admin/investor/%s/data/contacts/ktp".format(inv.id)}>Sesuai KTP</a></li>
                <li><a href={"/admin/investor/%s/contacts/tinggal".format(inv.id)}>Tempat tinggal saat ini</a></li>
                <li><a href={"/admin/investor/%s/contacts/darurat".format(inv.id)}>Darurat</a></li>
            </ul>
        }.getOrElse(NodeSeq.Empty)
    }



}

