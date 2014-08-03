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
object UserManager {


    def create(name:String) = {

    }

    def getById(id:Long) = {
        Zufaro.db.withSession { implicit session =>
            User.where(_.id === id).firstOption
        }
    }

    def getByName(name:String) = {
        Zufaro.db.withSession { implicit session =>
            User.where(_.name === name).firstOption
        }
    }

    object implicits {
        implicit class userWrapper(user:UserRow){
            def invest(business:BusinessRow, amount:Double) = {
                Zufaro.db.withTransaction { implicit sess =>
                    Invest += InvestRow(0, user.id, business.id, amount)
                }
            }

        }
    }
}

