package com.ansvia.zufaro.model

/**
 * Author: robin (r@nosql.asia)
 *
 */
object ContactType {

    val PERSONAL = 1
    val EMERGENCY = 2
}

object IdentityType {
    val KTP_BASED = 1
    val PASSPORT_BASED = 2
    val CURRENTLY_LIVE = 3
}

object MaritalStatus {
    val SINGLE = 1
    val MARIED = 2

    def toStr(code:Int) = code match {
        case SINGLE => "SINGLE"
        case MARIED => "MARIED"
    }

}
