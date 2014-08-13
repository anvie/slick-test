package com.ansvia.zufaro.web.api

import net.liftweb._
import util._
import Helpers._
import json._
import JsonDSL._
import net.liftweb.http.rest.RestHelper
import com.ansvia.zufaro.{BusinessHelpers, BusinessManager}
import net.liftweb.http.{JsonResponse, LiftResponse, Req, S}
import com.ansvia.zufaro.model.MutatorRole
import net.liftweb.common.{Empty, Box}
import com.ansvia.zufaro.exception.{ZufaroException, PermissionDeniedException}

/**
 * Author: robin
 * Date: 8/13/14
 * Time: 4:24 PM
 *
 */


case class Credential(id:Long, user:Box[{def id:Long}])
case class ZufaroClient(id:Long, name:String)
case class AuthInfo(cred:Credential, client:ZufaroClient)
case class WReq(req:Req, cred:Credential, client:ZufaroClient)

trait ZufaroRestHelper extends RestHelper {

//
//    serve {
//        case req =>
//            // @TODO(robin): fix this
//            val cred = Credential(0L, Empty)
//            val client = ZufaroClient(0L, "fix me")
//            this.accept(WReq(req, cred, client))
//    }
//
//    def accept(handler:PartialFunction[WReq, () => Box[LiftResponse]]){
//
//    }

//    override def apply(in: Req): () => Box[LiftResponse] = {
//
//        try {
//            val key = in.header("X-API-Key").openOr {
//                in.param("api_key").openOr("")
//            }
//
//            if (key == "")
//                throw PermissionDeniedException("No API key")
//
////
////            val newReq = new Req(in.path,
////                in.contextPath, in.requestType, in.contentType, in.request,
////                in.nanoStart, in.nanoEnd, in.stateless_?, in.paramCalculator, in.addlParams)
//
//            super.apply(in)
//        }
//        catch {
//            case e:ZufaroException =>
//                JsonResponse(("error" -> e.getMessage) ~ ("code" -> e.code):JValue)
//        }
//    }


    def authorized(req:Req)(func: (AuthInfo) => Box[LiftResponse]) = {
        val key = req.header("X-API-Key").openOr {
            req.param("api_key").openOr {
                throw PermissionDeniedException("No API key")
            }
        }
        // @TODO(robin): fix this
        func(AuthInfo(null, null))
    }


}

class BusinessRestApi extends ZufaroRestHelper {

    import BusinessHelpers._

    serve {
        case "api" :: "business" :: AsLong(busId) :: "report_profit" :: Nil Post req => authorized(req) {

            val amount:Double = req.param("amount").openOr("0.0").toDouble
            val mutator:{def id:Long} = new {
                def id = 0L
            }
            val mutationRole = MutatorRole.SYSTEM
            val info = req.param("info").asA[String].openOr("")

            BusinessManager.getById(busId).map { bus =>
                bus.addProfit(amount, mutator, mutationRole, info)
            }
        }
    }
}
