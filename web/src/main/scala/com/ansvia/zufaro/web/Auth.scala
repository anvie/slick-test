package com.ansvia.zufaro.web

import net.liftweb.http.SessionVar
import com.ansvia.zufaro.model.Tables._
import net.liftweb.common.{Full, Box, Empty}
import com.ansvia.zufaro.{InvestorManager, PasswordUtil, UserManager}
import com.ansvia.zufaro.exception.PermissionDeniedException
import com.ansvia.zufaro.model.{Initiator, UserRole}

/**
 * Author: robin
 * Date: 8/14/14
 * Time: 8:18 PM
 *
 */
object Auth {


    object currentAdmin extends SessionVar[Box[AdminRow]](Empty)
    object currentOperator extends SessionVar[Box[OperatorRow]](Empty)
    object currentInvestor extends SessionVar[Box[Investor]](Empty)



    def adminLogin(userName:String, password:String) = {
        UserManager.getByName(userName).map { admin =>
            if (!PasswordUtil.isMatch(password, admin.passhash))
                throw PermissionDeniedException("Wrong user name or password")

            currentAdmin.set(Full(admin))
        }
    }

    def adminLogout(){
        currentAdmin.remove()
        currentOperator.remove()
    }

    def investorLogin(userName:String, password:String) = {
        InvestorManager.getByName(userName).map { investor =>

            if (!PasswordUtil.isMatch(password, investor.passhash))
                throw PermissionDeniedException("Wrong user name or password")

            currentInvestor.set(Full(investor))
        }
    }

    def investorLogout(){
        currentInvestor.remove()
    }

    def isLoggedIn_?(who:String) = who match {
        case "admin" =>
            currentAdmin.is.isDefined
        case "operator" =>
            currentOperator.is.isDefined
        case "investor" =>
            currentInvestor.is.isDefined
        case "any" =>
            currentAdmin.is.isDefined ||
                currentOperator.is.isDefined ||
                currentInvestor.is.isDefined
    }


    def getInitiator = {
        val (initiator, initiatorRole) = {
            Auth.currentAdmin.map {
                admin =>
                    (admin.id, UserRole.ADMIN)
            }.getOrElse {
                Auth.currentOperator.map {
                    op =>
                        (op.id, UserRole.OPERATOR)
                }.getOrElse {
                    throw PermissionDeniedException("Unauthorized")
                }
            }
        }
        Initiator(initiator, initiatorRole)
    }

}
