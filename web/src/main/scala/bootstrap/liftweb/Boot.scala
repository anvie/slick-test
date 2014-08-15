package bootstrap.liftweb

import net.liftweb._
import net.liftweb.http.LiftRules
import net.liftweb.sitemap.{SiteMap, Menu, Loc}
import com.ansvia.zufaro.web.api.{AuthRestApi, BusinessRestApi}
import com.ansvia.commons.logging.Slf4jLogger

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
            AdminSitemap.sitemap

        LiftRules.setSiteMap(SiteMap(sitemapEntries: _*))





        /************************************************
         * REWRITE
         ***********************************************/
        LiftRules.statelessRewrite.append(AdminSitemap.rewrite)





        /************************************************
         * API SETUP
         ***********************************************/
        LiftRules.dispatch.append(BusinessRestApi)
        LiftRules.dispatch.append(AuthRestApi)

        debug("boot completed")
    }

}
