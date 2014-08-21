package com.ansvia.zufaro.model

import com.ansvia.zufaro.exception.ZufaroException

/**
 * Author: robin
 * Date: 8/4/14
 * Time: 12:09 AM
 *
 */
object UserRole {
    val SYSTEM = 0
    val ADMIN = 1
    val OPERATOR = 2
    val INVESTOR = 3

    def toStr(role:Int) = role match {
        case SYSTEM => "SYSTEM"
        case ADMIN => "ADMIN"
        case OPERATOR => "OPERATOR"
        case INVESTOR => "INVESTOR"
        case x =>
            throw new ZufaroException(s"Unknown role code $role", -1)
    }

}

object InvestorRole {
    /**
     * if the investor is owner of the investor.
     */
    val OWNER = 1

    /**
     * if the investor is has an representative operator.
     */
    val OPERATOR = 2

    /**
     * if the investor has a supervisor.
     */
    val SUPERVISOR = 3
}

object MutatorRole {
    val SYSTEM = 0
    val ADMIN = 1
    val OPERATOR = 2
}


