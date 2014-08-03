package com.ansvia.zufaro.macros

import scala.reflect.macros.Context
import scala.language.experimental.macros

/**
* Author: robin
* Date: 8/4/14
* Time: 1:14 AM
*
*/



object RequireMacro {

    def require(cond:Boolean, msg:String) = macro requireImpl

    def requireImpl(c:Context)(cond:c.Expr[Boolean], msg:c.Expr[String]): c.Expr[Unit] = {
        import c.universe._
        reify {
            if (!cond.value)
                raise(msg)
            else ()
        }
    }

    def raise(msg:String) = {
        throw new Exception(msg)
    }
}


