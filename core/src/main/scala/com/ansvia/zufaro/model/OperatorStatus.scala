package com.ansvia.zufaro.model

/**
 * Author: robin
 * Date: 8/25/14
 * Time: 7:08 PM
 *
 */
object OperatorStatus {

    val ANY = 0
    val ACTIVE = 1
    val SUSPENDED = 2

    def toStr(state:Int) = state match {
        case ACTIVE => "active"
        case SUSPENDED => "suspended"
    }

}
