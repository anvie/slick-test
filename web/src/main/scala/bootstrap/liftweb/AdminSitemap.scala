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

    lazy val AdminOnly = If(()=>Auth.currentAdmin.isDefined, "Not found")


    private def menu(path:String, caption:String, locs:Loc.LocParam[Unit]*) = {
        val _locs = List(AdminOnly, LocGroup(GROUP)) ++ locs
        Menu(Loc("Admin " + caption, path.split('/').toList, caption, _locs))
    }

    private lazy val adminSubMenu =
        /* ------------------ BUSINESS ----------------- */
        menu("admin/business", "Business") ::
        menu("admin/business/add", "Business Add", Hidden) ::
        menu("admin/business/report", "Business Report", Hidden) ::
        /* ------------------ USER ----------------- */
        menu("admin/user", "User") ::
        /* ------------------ API ----------------- */
        menu("admin/api", "API") ::
        menu("admin/api/add", "API Add", Hidden) ::
        Nil

    private lazy val _sitemap =
        Menu(Loc("Admin", List("admin"), "Admin"), adminSubMenu: _*) ::
        Nil

    lazy val sitemap = _sitemap

//    private val tabRe = "(running|project|closed)".r

    private lazy val internalRewrite:LiftRules.RewritePF = NamedPF("AdminRewrite2"){
        case RewriteRequest(ParsePath("admin" :: "business" :: AsLong(busId) :: "report" :: Nil, _, _, _), _, _) =>
            RewriteResponse("admin" :: "business" :: "report" :: Nil, Map("busId" -> busId.toString))

    }
    lazy val rewrite:LiftRules.RewritePF =
        com.ansvia.zufaro.web.snippet.AdminBusinessTab.rewrite orElse
            internalRewrite

}
