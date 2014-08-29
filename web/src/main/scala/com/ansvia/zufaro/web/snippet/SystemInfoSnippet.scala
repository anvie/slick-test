package com.ansvia.zufaro.web.snippet

import net.liftweb._
import util._
import Helpers._
import com.ansvia.zufaro.web.WebConfig
import org.joda.time.DateTime

/**
 * Author: robin
 * Date: 8/29/14
 * Time: 9:32 PM
 *
 */
object SystemInfoSnippet {

    def info:CssSel = {
        ".web-version *" #> WebConfig.VERSION &
        ".year *" #> new DateTime().getYear.toString
    }
}
