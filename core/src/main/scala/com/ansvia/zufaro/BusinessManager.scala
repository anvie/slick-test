package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import model.Tables._
import scala.collection.JavaConversions._

/**
 * Author: robin
 * Date: 8/3/14
 * Time: 10:39 PM
 *
 */

object BusinessManager {


    def create(name:String, desc:String) = {
        Zufaro.db.withSession { implicit sess =>
            (Business returning Business.map(_.id)) += BusinessRow(0L, name, desc, 0.0, 0.0)
        }
    }

    def getById(id:Long) = {
        Zufaro.db.withSession { implicit session =>
            Business.where(_.id === id).firstOption
        }
    }

    def getByName(name:String) = {
        Zufaro.db.withSession { implicit session =>
            Business.where(_.name === name).firstOption
        }
    }

}
