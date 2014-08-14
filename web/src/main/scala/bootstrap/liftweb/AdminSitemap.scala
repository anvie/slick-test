package bootstrap.liftweb

import net.liftweb._
import sitemap._

/**
 * Author: robin
 * Date: 8/14/14
 * Time: 4:35 PM
 *
 */
object AdminSitemap {

    lazy val sitemap =
        Menu(Loc("Admin", List("admin"), "Admin")) ::
        Menu(Loc("Admin Business", List("admin", "business"), "Admin Business")) ::
        Nil
}
