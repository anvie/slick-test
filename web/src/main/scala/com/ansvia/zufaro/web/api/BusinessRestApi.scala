package com.ansvia.zufaro.web.api

import net.liftweb._
import util._
import Helpers._
import json._
import JsonDSL._
import net.liftweb.http.rest.RestHelper
import com.ansvia.zufaro.{ApiClientManager, BusinessHelpers, BusinessManager}
import net.liftweb.http.{LiftResponse, Req}
import com.ansvia.zufaro.model.MutatorRole
import net.liftweb.common.Box
import com.ansvia.zufaro.exception.PermissionDeniedException
import com.ansvia.zufaro.model.Tables._

/**
 * Author: robin
 * Date: 8/13/14
 * Time: 4:24 PM
 *
 */


case class Credential(id:Long, user:Box[{def id:Long}])
case class AuthInfo(cred:Option[Credential], client:Option[ApiClientRow])
case class WReq(req:Req, cred:Credential, client:ApiClientRow)

trait ZufaroRestHelper extends RestHelper {

    def authorized(req:Req)(func: (AuthInfo) => LiftResponse):LiftResponse = {
        val key = req.header("X-API-Key").openOr {
            req.param("api_key").openOr {
                throw PermissionDeniedException("No API key")
            }
        }

        val apiClient = ApiClientManager.getByKey(key)

        // @TODO(robin): make support credential
        func(AuthInfo(None, apiClient))
    }

    def success(json:JValue):JValue = {
        (("error" -> 0) ~ ("result" -> json)):JValue
    }

    def fail(msg:String, code:Int):JValue = {
        (("error" -> code) ~ ("info" -> msg)):JValue
    }


}

class BusinessRestApi extends ZufaroRestHelper {

    import BusinessHelpers._

    serve {
        case "api" :: "business" :: AsLong(busId) :: "report_profit" :: Nil Post req => authorized(req) { authInfo =>

            val amount:Double = req.param("amount").openOr("0.0").toDouble
            val mutator:{def id:Long} = new {
                def id = 0L
            }
            val mutationRole = MutatorRole.SYSTEM
            val info = req.param("info").asA[String].openOr("")

            val rv =
            BusinessManager.getById(busId).map { bus =>
                val busProfit = bus.addProfit(amount, mutator, mutationRole, info)

                success(
                    ("id" -> busProfit.id.toString) ~
                        ("business_id" -> busProfit.busId) ~
                        ("amount" -> busProfit.amount) ~
                        ("timestamp" -> busProfit.ts.toString) ~
                        ("info" -> busProfit.info)
                )

            }.getOrElse {
                fail("Cannot report profit", 901)
            }
            rv
        }
    }
}
