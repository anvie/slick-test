package com.ansvia.zufaro.web.api

import net.liftweb.common.Box

/**
 * Author: robin
 * Date: 8/13/14
 * Time: 4:24 PM
 *
 */


case class Credential(id:Long, user:Box[{def id:Long}])
