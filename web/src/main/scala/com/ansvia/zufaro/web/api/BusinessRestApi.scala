package com.ansvia.zufaro.web.api

import net.liftweb._
import util._
import Helpers._
import json._
import JsonDSL._
import com.ansvia.zufaro.{BusinessHelpers, BusinessManager}
import net.liftweb.http.Req
import com.ansvia.zufaro.model.MutatorRole
import com.ansvia.zufaro.model.Tables._



case class WReq(req:Req, cred:Credential, client:ApiClientRow)



object BusinessRestApi extends ZufaroRestHelper {

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
