package com.ansvia.zufaro.model

/**
 * Author: robin
 * Date: 8/4/14
 * Time: 12:09 AM
 *
 */
object UserRole {
    val ADMIN = 1
    val OPERATOR = 2
    val INVESTOR = 3
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

