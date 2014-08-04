package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import com.ansvia.zufaro.model.Tables._


/**
 * Author: robin
 * Date: 8/3/14
 * Time: 10:40 PM
 *
 */
object Zufaro {

    val db = Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver")

    def init(){

    }

}

