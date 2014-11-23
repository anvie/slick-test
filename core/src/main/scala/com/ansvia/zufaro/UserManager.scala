package com.ansvia.zufaro

import com.ansvia.zufaro.model.Tables._
import scala.slick.driver.PostgresDriver.simple._

/**
 * Author: robin
 * Date: 8/4/14
 * Time: 1:01 PM
 *
 */
object UserManager {

    object status {
        val ANY = 0 // used for query only
        val ACTIVE = 1
        val INACTIVE = 2
    }
    
    object roles {
        val ADMIN = 0
        val OPERATOR = 1
        val USER = 2
    }




    def create(name:String, email:String, phone:String, password:String, role:Int, abilities:String=""):User = {
        val passHash = PasswordUtil.hash(password)
        val opId = Zufaro.db.withSession { implicit sess =>
            (Users.map(s => (s.name, s.email, s.phone, s.passhash, s.role, s.status)) returning Users.map(_.id)) +=
                (name, email, phone, passHash, role, status.ACTIVE)
//                User(0L, name, email, phone, passHash,
//                now(), status.ACTIVE)
        }
        getById(opId).get
    }

    def getById(id:Long) = {
        Zufaro.db.withSession { implicit sess =>
            Users.filter(_.id === id).firstOption
        }
    }

    def getByName(name:String) = {
        Zufaro.db.withSession { implicit sess =>
            Users.filter(_.name === name).firstOption
        }
    }


    def getList(offset:Int, limit:Int):Seq[User] = {
        Zufaro.db.withSession { implicit sess =>
            Users.drop(offset).take(limit).run
        }
    }

    def delete(user:User){
        Zufaro.db.withTransaction { implicit sess =>
            Users.filter(_.id === user.id).delete
        }
    }


}

trait UserHelpers {
    import com.ansvia.zufaro.UserManager.status

    implicit class AdminWrapper(user:User){
        def setActive(s:Boolean){
            Zufaro.db.withTransaction { implicit sess =>
                val _status = s match {
                    case true => status.ACTIVE
                    case false => status.INACTIVE
                }
                Users.filter(_.id === user.id).map(_.status).update(_status)
            }
        }

    }
}

object UserHelpers extends UserHelpers


object SexType {
    val FEMALE = 1
    val MALE = 2
}

