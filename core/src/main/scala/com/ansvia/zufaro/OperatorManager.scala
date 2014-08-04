package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import model.Tables._

/**
 * Author: robin
 * Date: 8/4/14
 * Time: 1:01 PM
 *
 */
object OperatorManager {

    def create(name:String, abilities:String=""):OperatorRow = {
        val opId = Zufaro.db.withSession { implicit sess =>
            (Operator returning Operator.map(_.id)) += OperatorRow(0L, name, abilities)
        }
        getById(opId)
    }

    def getById(id:Long) = {
        Zufaro.db.withSession { implicit sess =>
            Operator.filter(_.id === id).first()
        }
    }

}
