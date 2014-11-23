package com.ansvia.zufaro

import scala.slick.driver.PostgresDriver.simple._
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
            val apiClientId = (ApiClients.map(s => (s.name, s.desc, s.creatorId, s.creatorRole, s.key, s.suspended))
                returning ApiClients.map(_.id)) +=
                (name, desc, creatorId, creatorRole, apiKey, false)

//            ApiClient(0L, name, desc,
//                creatorId, creatorRole, apiKey, suspended=false)

            debug(s"api client `$name` created with id `$apiClientId`, key: `$apiKey`")

            access.foreach { acc =>
                ApiClientAccesses.map(s => (s.apiClientId, s.grant, s.target)) +=
                    (apiClientId, acc.grant, acc.target)

//                    ApiClientAccess(0L, apiClientId, acc.grant, acc.target)

                debug(s"add grant to api client $name : ${acc.grant} -> ${acc.target}")
            }

            apiClientId
        }
        getById(id).get
    }

    def getById(id:Long, withSuspended:Boolean=false) = {
        Zufaro.db.withSession { implicit sess =>
            if (withSuspended)
                ApiClients.filter(_.id === id).firstOption
            else
                ApiClients.filter(a => a.id === id && a.suspended === false).firstOption
        }
    }

    def getByKey(key:String, withSuspended:Boolean=false) = {
        Zufaro.db.withSession { implicit sess =>
            if (withSuspended)
                ApiClients.filter(_.key === key).firstOption
            else
                ApiClients.filter(a => a.key === key && a.suspended === false).firstOption
        }
    }


    def generateKey() = {
        "API" + idgen.nextId() + idgen.nextId().substring(0, 5)
    }

    def getList(offset: Int, limit: Int):Seq[ApiClient] = {
        Zufaro.db.withSession { implicit sess =>
            ApiClients.drop(offset).take(limit).run
        }
    }

    def delete(client:ApiClient) = {
        Zufaro.db.withTransaction { implicit sess =>
            ApiClientAccesses.filter(_.apiClientId === client.id).delete
            ApiClients.filter(_.id === client.id).delete
        }
    }

}

trait ApiClientHelpers {

    implicit class ApiClientWrapper(apiClient:ApiClient){

        /**
         * Regerenate API key.
         * @return
         */
        def regenerateKey() = {
            val newKey = ApiClientManager.generateKey()
            Zufaro.db.withTransaction { implicit sess =>
                ApiClients.filter(_.id === apiClient.id).map(_.key).update(newKey)
            }
            newKey
        }

        def getAccesses = {
            Zufaro.db.withSession { implicit sess =>
                val access = for {
                    acc <- ApiClientAccesses if acc.apiClientId === apiClient.id
                } yield acc
                access.run
            }
        }

        def setSuspended(state:Boolean){
            Zufaro.db.withTransaction { implicit sess =>
                ApiClients.filter(_.id === apiClient.id).map(_.suspended).update(state)
            }
        }
        

    }
}

object ApiClientHelpers extends ApiClientHelpers