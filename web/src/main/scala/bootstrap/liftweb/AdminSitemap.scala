package bootstrap.liftweb

import net.liftweb._
import http._
import sitemap._
import Loc._
import util._
import Helpers._
import com.ansvia.zufaro.web.Auth
import net.liftweb.util.NamedPF

/**
 * Author: robin
 * Date: 8/14/14
 * Time: 4:35 PM
 *
 */
object AdminSitemap {

    private val GROUP = "admin"

    lazy val AdminOnly = If(()=>Auth.isLoggedIn_?("admin"), "Not found")


    private def menu(path:String, caption:String, locs:Loc.LocParam[Unit]*) = {
        val _locs = List(AdminOnly, LocGroup(GROUP)) ++ locs
        Menu(Loc("Admin " + caption, path.split('/').toList, caption, _locs))
    }

    private lazy val adminSubMenu =
        /* ------------------ BUSINESS ----------------- */
        menu("admin/business", "Business") ::
        menu("admin/business/add", "Business Add", Hidden) ::
        menu("admin/business/report", "Business Report", Hidden) ::
        menu("admin/business/report/share-detail", "Share Detail", Hidden) ::
        menu("admin/business/project-report", "Project Report", Hidden) ::
        /* ------------------ Investor ----------------- */
        menu("admin/investor", "Investor") ::
        menu("admin/investor/add", "Investor Add", Hidden) ::
        menu("admin/investor/deposit", "Investor Deposit", Hidden) ::
        menu("admin/investor/business", "Investor Business", Hidden) ::
        menu("admin/investor/data", "Investor Data", Hidden) ::
        menu("admin/investor/data/contacts/detail", "Investor Data Contacts Detail", Hidden) ::
        /* ------------------ Admin ----------------- */
        menu("admin/admin", "Admin", AdminOnly) ::
        menu("admin/operator", "Operator", AdminOnly) ::
        /* ------------------ API ----------------- */
        menu("admin/api", "API") ::
        menu("admin/api/add", "API Add", Hidden) ::
        Nil

    private lazy val _sitemap =
        Menu(Loc("Admin", List("admin"), "Admin"), adminSubMenu: _*) ::
        Nil

    lazy val sitemap = _sitemap

    private val contactTypeRe = "(personal|emergency)".r

    private lazy val internalRewrite:LiftRules.RewritePF = NamedPF("AdminRewrite2"){
        case RewriteRequest(ParsePath("admin" :: "business" :: AsLong(busId) :: "report" :: AsLong(busProfId) :: "share-detail" :: Nil, _, _, _), _, _) =>
            RewriteResponse("admin" :: "business" :: "report" :: "share-detail" :: Nil, Map("busId" -> busId.toString, "busProfId" -> busProfId.toString))

        case RewriteRequest(ParsePath("admin" :: "business" :: AsLong(busId) :: "report" :: Nil, _, _, _), _, _) =>
            RewriteResponse("admin" :: "business" :: "report" :: Nil, Map("busId" -> busId.toString))

        case RewriteRequest(ParsePath("admin" :: "business" :: AsLong(busId) :: "project-report" :: Nil, _, _, _), _, _) =>
            RewriteResponse("admin" :: "business" :: "project-report" :: Nil, Map("busId" -> busId.toString))

        case RewriteRequest(ParsePath("admin" :: "investor" :: AsLong(invId) :: "deposit" :: Nil, _, _, _), _, _) =>
            RewriteResponse("admin" :: "investor" :: "deposit" :: Nil, Map("invId" -> invId.toString))

        case RewriteRequest(ParsePath("admin" :: "investor" :: AsLong(invId) :: "business" :: Nil, _, _, _), _, _) =>
            RewriteResponse("admin" :: "investor" :: "business" :: Nil, Map("invId" -> invId.toString))
//
//        case RewriteRequest(ParsePath("admin" :: "investor" :: AsLong(invId) :: "contacts" :: Nil, _, _, _), _, _) =>
//            RewriteResponse("admin" :: "investor" :: "data" :: "contacts" :: Nil, Map("invId" -> invId.toString))

        case RewriteRequest(ParsePath("admin" :: "investor" :: AsLong(invId) :: "data" :: "contacts" :: contactTypeRe(_type) :: Nil, _, _, _), _, _) =>
            RewriteResponse("admin" :: "investor" :: "data" :: "contacts" :: "detail" :: Nil, Map("invId" -> invId.toString, "contactType" -> _type))

        case RewriteRequest(ParsePath("admin" :: "investor" :: AsLong(invId) :: "data" :: Nil, _, _, _), _, _) =>
            RewriteResponse("admin" :: "investor" :: "data" :: Nil, Map("invId" -> invId.toString))

    }
    lazy val rewrite:LiftRules.RewritePF =
        com.ansvia.zufaro.web.snippet.AdminBusinessTab.rewrite orElse
        com.ansvia.zufaro.web.snippet.AdminInvestorTab.rewrite orElse
        com.ansvia.zufaro.web.snippet.AdminOperatorTab.rewrite orElse
            internalRewrite

}
