package com.ansvia.zufaro.web.api

import com.ansvia.zufaro.model.Tables._

/**
 * Author: robin
 * Date: 8/13/14
 * Time: 7:31 PM
 *
 */
case class AuthInfo(cred:Option[Credential], client:Option[ApiClient])
