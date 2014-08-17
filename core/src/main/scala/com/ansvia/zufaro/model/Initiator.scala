package com.ansvia.zufaro.model

import com.ansvia.zufaro.exception.InvalidParameterException

/**
 * Author: robin
 * Date: 8/16/14
 * Time: 5:49 PM
 *
 */
trait Initiator
object Initiator extends Initiator {

    case class HasInitiator(id:Long, role:Long) extends Initiator {
        override def toString() = {
            role match {
                case UserRole.SYSTEM => "system"
                case UserRole.ADMIN => "admin=" + this.id
                case UserRole.OPERATOR => "operator=" + this.id
                case UserRole.INVESTOR => "investor=" + this.id
                case x =>
                    throw InvalidParameterException("Unknown role type " + role)
            }
        }
    }

    def apply(id:Long, role:Int) = {
        HasInitiator(id, role)
    }

    def system = HasInitiator(0L, UserRole.SYSTEM)

}
object NoInitiator extends Initiator {
    override def toString: String = ""

}


