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
    var id = 0L
    var role = UserRole.SYSTEM
    def apply(id:Long, role:Int) = {
        this.id = id
        this.role = role
        this
    }

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
object NoInitiator extends Initiator {
    override def toString: String = ""
}


