package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import model.Tables._
import com.ansvia.util.idgen.TokenIdGenerator

// @TODO(robin): add this to unittest
/**
 * Author: robin
 * Date: 8/13/14
 * Time: 6:46 PM
 *
 */
object ApiClientManager {

    private object idgen extends TokenIdGenerator

    def create(name:String, desc:String, creatorId:Long, creatorRole:Int) = {
        val id = Zufaro.db.withTransaction { implicit sess =>
            (ApiClient returning ApiClient.map(_.id)) += ApiClientRow(0L, name, desc,
                creatorId, creatorRole, generateKey())
        }
        getById(id).get
    }

    def getById(id:Long) = {
        Zufaro.db.withSession { implicit sess =>
            ApiClient.where(_.id === id).firstOption
        }
    }

    def getByKey(key:String) = {
        Zufaro.db.withSession { implicit sess =>
            ApiClient.where(_.key === key).firstOption
        }
    }


    def generateKey() = idgen.nextId()

}

trait ApiClientHelpers {

    implicit class ApiClientWrapper(apiClient:ApiClientRow){

        /**
         * Regerenate API key.
         * @return
         */
        def regenerateKey() = {
            val newKey = ApiClientManager.generateKey()
            Zufaro.db.withTransaction { implicit sess =>
                ApiClient.where(_.id === apiClient.id).map(_.key).update(newKey)
            }
            newKey
        }

    }
}
