package com.ansvia.zufaro.model

/**
 * Author: robin
 * Date: 8/16/14
 * Time: 7:02 PM
 *
 */
object MutationKind {

    val ACTIVITY = 0
    val CREDIT = 1
    val DEBIT = 2

    def str(k:Int) = k match {
        case ACTIVITY => "ACTIVITY"
        case CREDIT => "CREDIT"
        case DEBIT => "DEBIT"
    }

}
