package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import model.Tables._
import org.apache.commons.codec.digest.DigestUtils

/**
 * Author: robin
 * Date: 8/4/14
 * Time: 1:01 PM
 *
 */
object OperatorManager {

    def create(name:String, password:String, abilities:String=""):OperatorRow = {
        val passhash = PasswordUtil.hash(password)
        val opId = Zufaro.db.withSession { implicit sess =>
            (Operator returning Operator.map(_.id)) += OperatorRow(0L, name, abilities, passhash)
        }
        getById(opId).get
    }

    def getById(id:Long) = {
        Zufaro.db.withSession { implicit sess =>
            Operator.filter(_.id === id).firstOption
        }
    }

}



