package com.ansvia.zufaro.web

import net.liftweb.http.SessionVar
import com.ansvia.zufaro.model.Tables._
import net.liftweb.common.{Full, Box, Empty}
import com.ansvia.zufaro.{PasswordUtil, AdminManager}
import com.ansvia.zufaro.exception.PermissionDeniedException

/**
 * Author: robin
 * Date: 8/14/14
 * Time: 8:18 PM
 *
 */
object Auth {


    object currentAdmin extends SessionVar[Box[AdminRow]](Empty)
    object currentOperator extends SessionVar[Box[OperatorRow]](Empty)



    def adminLogin(userName:String, password:String) = {
        AdminManager.getByName(userName).map { admin =>
            if (!PasswordUtil.isMatch(password, admin.passhash))
                throw PermissionDeniedException("Wrong user name or password")

            currentAdmin.set(Full(admin))
        }
    }

    def adminLogout(){
        currentAdmin.remove()
        currentOperator.remove()
    }

    def isLoggedIn_? = {
        currentAdmin.is.isDefined
    }
}
