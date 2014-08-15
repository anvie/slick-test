package com.ansvia.zufaro

import com.ansvia.zufaro.model.UserRole
import scala.slick.driver.H2Driver.simple._

/**
 * Author: robin
 * Date: 8/15/14
 * Time: 12:53 PM
 *
 */
class ApiClientSpec extends ZufaroTest {
    import model.Tables._
    import ApiClientHelpers._

    "ApiClientManager" should {
        "can create" in {
            val busId = 1
            val access = Seq(ApiClientManager.Access("add-profit", "bus=" + busId),
                ApiClientManager.Access("edit-info", "bus=" + busId))
            val name = genRandomString
            val desc = genRandomString
            val client = ApiClientManager.create(name, desc, 0L, UserRole.ADMIN, access)

            client must beAnInstanceOf[ApiClientRow]
            client.name must_== name
            client.desc must_== desc
            val rvAccesses = client.getAccesses.toSeq
            access.map { acc =>
                rvAccesses.map(_.target) must be contain(acc.target)
            }
            rvAccesses.length must_== 2
        }
        "suspended api client cannot be retrieved by default" in {
            val client = ApiClientManager.create(genRandomString, genRandomString, 0L, UserRole.ADMIN, Nil)

            client.suspended must beFalse

            client.setSuspended(true)

            ApiClientManager.getById(client.id) must beNone
            ApiClientManager.getById(client.id, true) must not beNone

        }
    }
}