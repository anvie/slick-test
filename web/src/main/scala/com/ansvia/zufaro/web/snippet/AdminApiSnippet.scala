package com.ansvia.zufaro.web.snippet

import com.ansvia.zufaro.web.lib.MtTabInterface

/**
 * Author: robin
 * Date: 8/15/14
 * Time: 5:15 PM
 *
 */
class AdminApiSnippet {

}

class AdminApiTab extends MtTabInterface {
    def defaultSelected: String = "active-client"

    def tmplDir: String = "admin/api"

    override def tabNames: Array[String] = Array("active-client", "suspended-client")
}
