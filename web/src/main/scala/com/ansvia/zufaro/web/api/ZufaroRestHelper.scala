package com.ansvia.zufaro.web.api

import net.liftweb._
import util._
import Helpers._
import json._
import JsonDSL._
import http._
import net.liftweb.http.rest.RestHelper
import com.ansvia.zufaro.exception.PermissionDeniedException
import com.ansvia.zufaro.ApiClientManager

/**
 * Author: robin
 * Date: 8/13/14
 * Time: 7:31 PM
 *
 */
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
