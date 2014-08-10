package bootstrap.liftweb

import net.liftweb._
import net.liftweb.http.LiftRules
import net.liftweb.sitemap.{SiteMap, Menu, Loc}

/**
 * Author: robin
 * Date: 8/10/14
 * Time: 2:05 PM
 *
 */
class Boot {

    def boot {

        LiftRules.addToPackages("com.ansvia.zufaro.web")

        val sitemapEntries = Menu(Loc("Home", List("index"), "Home")) :: Nil

        LiftRules.setSiteMap(SiteMap(sitemapEntries: _*))

    }

}
