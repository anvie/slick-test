package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import model.Tables._
import org.apache.commons.codec.digest.DigestUtils
import com.ansvia.zufaro.model.OperatorStatus
import com.ansvia.zufaro.exception.ZufaroException

/**
 * Author: robin
 * Date: 8/4/14
 * Time: 1:01 PM
 *
 */
object OperatorManager {

    import OperatorStatus._

    def create(name:String, password:String, abilities:String=""):OperatorRow = {
        val passhash = PasswordUtil.hash(password)
        val opId = Zufaro.db.withSession { implicit sess =>
            (Operator returning Operator.map(_.id)) += OperatorRow(0L, name, abilities, passhash, ACTIVE)
        }
        getById(opId).get
    }

    def getById(id:Long) = {
        Zufaro.db.withSession { implicit sess =>
            Operator.filter(_.id === id).firstOption
        }
    }

    /**
     * Get list of operator
     * @param offset starting offset.
     * @param limit ends limit.
     * @param status status. see [[com.ansvia.zufaro.model.OperatorStatus]]
     * @return
     */
    def getList(offset:Int, limit:Int, status:Int):Seq[OperatorRow] = {
        Zufaro.db.withSession { implicit sess =>
            status match {
                case ANY =>
                    Operator.drop(offset).take(limit).run
                case ACTIVE | SUSPENDED =>
                    Operator.where(_.status === status).drop(offset).take(limit).run
                case x =>
                    throw new ZufaroException(s"Unknown status: $x", -1)
            }
        }
    }

    def delete(op:OperatorRow) = {
        Zufaro.db.withTransaction { implicit sess =>
            Operator.where(_.id === op.id).delete
        }
    }


}

trait OperatorHelpers {

    import OperatorStatus._

    implicit class OperatorWrapper(op:OperatorRow){
        def activate(){
            Zufaro.db.withTransaction { implicit sess =>
                Operator.where(_.id === op.id).map(_.status).update(ACTIVE)
            }
        }

        def suspend(){
            Zufaro.db.withTransaction { implicit sess =>
                Operator.where(_.id === op.id).map(_.status).update(SUSPENDED)
            }
        }

    }
}


object OperatorHelpers extends OperatorHelpers

