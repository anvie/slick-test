package com.ansvia.zufaro.web.api

import net.liftweb._
import util._
import Helpers._
import json._
import JsonDSL._
import com.ansvia.zufaro.{BusinessHelpers, BusinessManager}
import com.ansvia.zufaro.model.MutatorRole
import com.ansvia.zufaro.exception.InvalidParameterException


object BusinessRestApi extends ZufaroRestHelper {

    import BusinessHelpers._

    serve {
        case "api" :: "business" :: AsLong(busId) :: "report" :: Nil Post req => authorized(req) {
            case AuthInfo(_, Some(apiClient)) =>

            val omzet:Double = req.param("omzet").openOr("0.0").toDouble
            val profit:Double = req.param("profit").openOr("0.0").toDouble

            // validate

            if (omzet < profit)
                throw InvalidParameterException("Invalid parameter omzet can't greater than profit")

            //        if (omzet == 0.0 && profit == 0.0)
            //            throw InvalidParameterException("omzet and profit is empty")


            val mutator:{def id:Long} = new {
                def id = 0L
            }
            val mutationRole = MutatorRole.SYSTEM
            val info = req.param("info").asA[String].openOr("")

            val rv =
                BusinessManager.getById(busId).map { bus =>
                    val busProfit = bus.addProfit(omzet, profit, mutator, mutationRole, info)

                    success(
                        ("id" -> busProfit.id.toString) ~
                            ("business_id" -> busProfit.busId) ~
                            ("omzet" -> busProfit.omzet) ~
                            ("profit" -> busProfit.profit) ~
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
