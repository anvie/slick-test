package bootstrap.liftweb

import net.liftweb._
import http._
import sitemap._
import Loc._

/**
 * Author: robin
 * Date: 8/14/14
 * Time: 4:35 PM
 *
 */
object AdminSitemap {

    private val GROUP = "admin"

    private lazy val _sitemap =
        Menu(Loc("Admin", List("admin"), "Admin")) ::
        Menu(Loc("Admin Business", List("admin", "business"), "Business", LocGroup(GROUP))) ::
        Menu(Loc("Admin User", List("admin", "user"), "User", LocGroup(GROUP))) ::
        Menu(Loc("Admin Api", List("admin", "api"), "API", LocGroup(GROUP))) ::
        Menu(Loc("Admin Api Add", List("admin", "api", "add"), "API Add", LocGroup(GROUP), Hidden)) ::
        Nil

    lazy val sitemap = _sitemap
}
