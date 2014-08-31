package com.ansvia.zufaro.web.api

import com.ansvia.zufaro.web.Auth
import net.liftweb.http.S

/**
 * Author: robin
 * Date: 8/14/14
 * Time: 11:32 PM
 *
 */
object AuthRestApi extends ZufaroRestHelper {

    serve {
        case "logout" :: Nil Get req => {
            Auth.adminLogout()
            Auth.investorLogout()
            S.redirectTo("/", () => S.notice("You are successfully logged out"))
        }
    }
}
