package com.ansvia.zufaro

import org.apache.commons.codec.digest.DigestUtils

/**
 * Author: robin
 * Date: 8/14/14
 * Time: 8:03 PM
 *
 */
object PasswordUtil {

    private val SALT = "vRq_|ADm8`C7TMt);[TNbhMj>FqUj|79BNio+e['\\DW_uflFnzhz\"qzz\"u&2tbL"

    def hash(plainPassword:String) = {
        val salted = plainPassword.zip(SALT).flatMap(x => List(x._1, x._2)).mkString("")
//        println("salted: " + salted)
        DigestUtils.sha256Hex(salted)
    }

    def isMatch(plainPassword:String, theHash:String) = {
        hash(plainPassword) == theHash
    }


}
