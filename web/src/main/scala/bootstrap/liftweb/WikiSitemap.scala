package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._
import net.liftweb.sitemap.{Menu, Loc}
import net.liftweb.sitemap.Loc.{Hidden, LocGroup}

/**
 * Author: robin
 * Date: 8/16/14
 * Time: 10:06 PM
 *
 */
object WikiSitemap {


    private def menu(path:String, caption:String, locs:Loc.LocParam[Unit]*) = {
        Menu(Loc("Admin " + caption, path.split('/').toList, caption, locs.toList))
    }

    lazy val sitemap =
        menu("wiki/api", "Wiki", Hidden) :: Nil

}
