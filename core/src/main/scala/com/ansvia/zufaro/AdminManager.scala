package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import model.Tables._

/**
 * Author: robin
 * Date: 8/4/14
 * Time: 1:01 PM
 *
 */
object AdminManager {

    def create(name:String, password:String, abilities:String=""):AdminRow = {
        val passHash = PasswordUtil.hash(password)
        val opId = Zufaro.db.withSession { implicit sess =>
            (Admin returning Admin.map(_.id)) += AdminRow(0L, name, passHash)
        }
        getById(opId).get
    }

    def getById(id:Long) = {
        Zufaro.db.withSession { implicit sess =>
            Admin.filter(_.id === id).firstOption
        }
    }

    def getByName(name:String) = {
        Zufaro.db.withSession { implicit sess =>
            Admin.filter(_.name === name).firstOption
        }
    }

}



