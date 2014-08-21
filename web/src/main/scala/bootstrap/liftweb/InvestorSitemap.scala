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
object InvestorSitemap {

    private val GROUP = "investor"

//    lazy val AdminOnly = If(()=>Auth.isLoggedIn_?("admin"), "Not found")
    lazy val InvestorOnly = If(()=>Auth.isLoggedIn_?("investor"), "Not found")


    private def menu(path:String, caption:String, locs:Loc.LocParam[Unit]*) = {
        val _locs = List(InvestorOnly, LocGroup(GROUP)) ++ locs
        Menu(Loc("Investor " + caption, path.split('/').toList, caption, _locs))
    }

    private lazy val investorSubMenu =
        /* ----------------- DASHBOARD ----------------- */
        menu("investor/dashboard", "Dashboard") ::
        /* ------------------ BUSINESS ----------------- */
        menu("investor/business", "Business") ::
        menu("investor/business/add", "Business Add", Hidden) ::
        menu("investor/business/report", "Business Report", Hidden) ::
        menu("investor/business/project-report", "Project Report", Hidden) ::
        /* ------------------ Investor ----------------- */
        menu("investor/investor", "Investor") ::
        menu("investor/investor/add", "Investor Add", Hidden) ::
        menu("investor/investor/deposit", "Investor Deposit", Hidden) ::
        menu("investor/investor/business", "Investor Business", Hidden) ::
        Nil

    private lazy val _sitemap =
        Menu(Loc("Investor", List("investor"), "Investor", Hidden), investorSubMenu: _*) ::
        Nil

    lazy val sitemap = _sitemap

//    private val tabRe = "(running|project|closed)".r

    private lazy val internalRewrite:LiftRules.RewritePF = NamedPF("AdminRewrite2"){
        case RewriteRequest(ParsePath("investor" :: "business" :: AsLong(busId) :: "report" :: AsLong(busProfId) :: "share-detail" :: Nil, _, _, _), _, _) =>
            RewriteResponse("investor" :: "business" :: "report" :: "share-detail" :: Nil, Map("busId" -> busId.toString, "busProfId" -> busProfId.toString))

        case RewriteRequest(ParsePath("investor" :: "business" :: AsLong(busId) :: "report" :: Nil, _, _, _), _, _) =>
            RewriteResponse("investor" :: "business" :: "report" :: Nil, Map("busId" -> busId.toString))

        case RewriteRequest(ParsePath("investor" :: "business" :: AsLong(busId) :: "project-report" :: Nil, _, _, _), _, _) =>
            RewriteResponse("investor" :: "business" :: "project-report" :: Nil, Map("busId" -> busId.toString))

        case RewriteRequest(ParsePath("investor" :: "investor" :: AsLong(invId) :: "deposit" :: Nil, _, _, _), _, _) =>
            RewriteResponse("investor" :: "investor" :: "deposit" :: Nil, Map("invId" -> invId.toString))

        case RewriteRequest(ParsePath("investor" :: "investor" :: AsLong(invId) :: "business" :: Nil, _, _, _), _, _) =>
            RewriteResponse("investor" :: "investor" :: "business" :: Nil, Map("invId" -> invId.toString))

    }
    lazy val rewrite:LiftRules.RewritePF =
            internalRewrite

}
