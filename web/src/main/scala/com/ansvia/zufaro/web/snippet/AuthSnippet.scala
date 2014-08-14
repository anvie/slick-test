package com.ansvia.zufaro.web.snippet

import scala.xml.NodeSeq
import com.ansvia.zufaro.web.Auth

/**
 * Author: robin
 * Date: 8/14/14
 * Time: 9:52 PM
 *
 */
object AuthSnippet {

    def isLoggedIn(in:NodeSeq):NodeSeq = {
        if (Auth.isLoggedIn_?)
            in
        else
            NodeSeq.Empty
    }

    def isAnon(in:NodeSeq):NodeSeq = {
        if (!Auth.isLoggedIn_?)
            in
        else
            NodeSeq.Empty
    }


}
