package com.ansvia.zufaro.web.snippet

import net.liftweb._
import util._
import Helpers._
import CSSHelpers._
import com.ansvia.zufaro.Zufaro
import com.ansvia.zufaro.web.Auth
import scala.xml.NodeSeq

/**
 * Author: robin
 * Date: 8/14/14
 * Time: 10:04 PM
 *
 */
class NavbarSnippet {

    def welcome:CssSel = {
        "*" #> {
            if (Auth.isLoggedIn_?){
                "#WelcomeName *" #> {
                    Auth.currentAdmin.is.map { admin =>
                        admin.name
                    }.getOrElse {
                        "Unknown"
                    }
                }
            }else
                "*" #> NodeSeq.Empty
        }
    }
}
