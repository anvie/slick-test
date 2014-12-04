package com.ansvia.zufaro.web.snippet

import com.ansvia.zufaro.web.Auth
import net.liftweb.util.Helpers._
import net.liftweb.util._

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
            if (Auth.isLoggedIn_?("any")){
                "#WelcomeName *" #> {
                    Auth.currentUser.is.map { admin =>
                        admin.name
                    }.getOrElse {
                        Auth.currentInvestor.is.map { investor =>
                            investor.name
                        }.getOrElse("unknown")
                    }
                }
            }else
                "*" #> NodeSeq.Empty
        }
    }
}
