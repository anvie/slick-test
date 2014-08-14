package com.ansvia.zufaro

import org.specs2.mutable.Specification

/**
 * Author: robin
 * Date: 8/14/14
 * Time: 8:04 PM
 *
 */
class PasswordUtilSpec extends Specification {

    "PasswordUtil" should {
        "hash plain password correctly" in {
            val pw = "this_is_secure_password_ever"
            val rv = PasswordUtil.hash(pw)
            rv must not equalTo pw
        }
        "can match pass with hash" in {
            val pw = "this is pass"
            val h = PasswordUtil.hash(pw)
            PasswordUtil.isMatch(pw, h) must beTrue
        }
        "can't match invalid pass hash" in {
            val h = PasswordUtil.hash("this another pass")
            PasswordUtil.isMatch("123123", h) must beFalse
        }
    }
}

