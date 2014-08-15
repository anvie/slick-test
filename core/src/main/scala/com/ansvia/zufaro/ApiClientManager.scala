package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import model.Tables._
import com.ansvia.util.idgen.TokenIdGenerator
import com.ansvia.commons.logging.Slf4jLogger

// @TODO(robin): add this to unittest
/**
 * Author: robin
 * Date: 8/13/14
 * Time: 6:46 PM
 *
 */
object ApiClientManager extends Slf4jLogger {

    private object idgen extends TokenIdGenerator

    case class Access(grant:String, target:String)

    def create(name:String, desc:String, creatorId:Long, creatorRole:Int, access:Seq[Access]) = {
        debug(s"creating api client `$name`")

        val id = Zufaro.db.withTransaction { implicit sess =>

            val apiKey = generateKey()
            val apiClientId = (ApiClient returning ApiClient.map(_.id)) += ApiClientRow(0L, name, desc,
                creatorId, creatorRole, apiKey, suspended=false)

            debug(s"api client `$name` created with id `$apiClientId`, key: `$apiKey`")

            access.foreach { acc =>
                ApiClientAccess += ApiClientAccessRow(0L, apiClientId, acc.grant, acc.target)
                debug(s"add grant to api client $name : ${acc.grant} -> ${acc.target}")
            }

            apiClientId
        }
        getById(id).get
    }

    def getById(id:Long, withSuspended:Boolean=false) = {
        Zufaro.db.withSession { implicit sess =>
            if (withSuspended)
                ApiClient.where(_.id === id).firstOption
            else
                ApiClient.where(a => a.id === id && a.suspended === false).firstOption
        }
    }

    def getByKey(key:String, withSuspended:Boolean=false) = {
        Zufaro.db.withSession { implicit sess =>
            if (withSuspended)
                ApiClient.where(_.key === key).firstOption
            else
                ApiClient.where(a => a.key === key && a.suspended === false).firstOption
        }
    }


    def generateKey() = {
        "API" + idgen.nextId() + idgen.nextId().substring(0, 5)
    }

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

        def getAccesses = {
            Zufaro.db.withSession { implicit sess =>
                val access = for {
                    acc <- ApiClientAccess if acc.apiClientId === apiClient.id
                } yield acc
                access.run
            }
        }

        def setSuspended(state:Boolean){
            Zufaro.db.withTransaction { implicit sess =>
                ApiClient.where(_.id === apiClient.id).map(_.suspended).update(state)
            }
        }
        

    }
}

object ApiClientHelpers extends ApiClientHelpers