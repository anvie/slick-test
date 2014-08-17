package com.ansvia.zufaro.model

/**
 * Author: robin
 * Date: 8/16/14
 * Time: 1:50 PM
 *
 */
object ShareMethod {
    val AUTO = 1
    val MANUAL = 2
}

case class ShareMethod(method:Int, initiator:Initiator)
