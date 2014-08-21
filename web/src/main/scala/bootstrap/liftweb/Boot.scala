package bootstrap.liftweb

import net.liftweb._
import net.liftweb.http.{RedirectResponse, Req, LiftRules}
import net.liftweb.sitemap.{SiteMap, Menu, Loc}
import com.ansvia.zufaro.web.api.{AuthRestApi, BusinessRestApi}
import com.ansvia.commons.logging.Slf4jLogger
import net.liftweb.common.{Box, Full}
import com.ansvia.zufaro.web.Auth

/**
 * Author: robin
 * Date: 8/10/14
 * Time: 2:05 PM
 *
 */
class Boot extends Slf4jLogger {

    def boot {

        LiftRules.addToPackages("com.ansvia.zufaro.web")

        val sitemapEntries = (Menu(Loc("Home", List("index"), "Home")) :: Nil) ++
            AdminSitemap.sitemap ++
            InvestorSitemap.sitemap ++
            WikiSitemap.sitemap


        val basicRules:List[PartialFunction[Box[Req],Loc.AnyLocParam]] = List({
            case Full(Req(path, _, _)) if path == ("index" :: Nil) && Auth.isLoggedIn_?("investor") =>
                Loc.EarlyResponse(()=> Full(RedirectResponse("/investor")))
        })

        LiftRules.setSiteMap(SiteMap(basicRules, sitemapEntries: _*))





        /************************************************
         * REWRITE
         ***********************************************/
        LiftRules.statelessRewrite.append(AdminSitemap.rewrite)
        LiftRules.statelessRewrite.append(InvestorSitemap.rewrite)





        /************************************************
         * API SETUP
         ***********************************************/
        LiftRules.dispatch.append(BusinessRestApi)
        LiftRules.dispatch.append(AuthRestApi)

        debug("boot completed")
    }

}
