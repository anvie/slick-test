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

    import ZufaroHelpers._
    import TimestampHelpers._

    object status {
        val ANY = 0 // used for query only
        val ACTIVE = 1
        val INACTIVE = 2
    }


    def create(name:String, email:String, phone:String, password:String, abilities:String=""):AdminRow = {
        val passHash = PasswordUtil.hash(password)
        val opId = Zufaro.db.withSession { implicit sess =>
            (Admin returning Admin.map(_.id)) += AdminRow(0L, name, email, phone, passHash,
                now(), status.ACTIVE)
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


    def getList(offset:Int, limit:Int):Seq[AdminRow] = {
        Zufaro.db.withSession { implicit sess =>
            Admin.drop(offset).take(limit).run
        }
    }

    def delete(admin:AdminRow){
        Zufaro.db.withTransaction { implicit sess =>
            Admin.where(_.id === admin.id).delete
        }
    }


}

trait AdminHelpers {
    import AdminManager.status

    implicit class AdminWrapper(admin:AdminRow){
        def setActive(s:Boolean){
            Zufaro.db.withTransaction { implicit sess =>
                val _status = s match {
                    case true => status.ACTIVE
                    case false => status.INACTIVE
                }
                Admin.where(_.id === admin.id).map(_.status).update(_status)
            }
        }

    }
}

object AdminHelpers extends AdminHelpers
