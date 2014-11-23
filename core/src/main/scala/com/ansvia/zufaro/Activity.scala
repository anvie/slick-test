package com.ansvia.zufaro

import java.util.Date

import com.ansvia.zufaro.model.Tables._

import scala.slick.driver.PostgresDriver.backend.SessionDef
import scala.slick.driver.PostgresDriver.simple._

/**
 * Author: robin
 * Date: 8/29/14
 * Time: 9:54 PM
 *
 */
object Activity {

    object kind {
        val ADMIN = 1
        val INVESTOR = 2
        val OPERATOR = 3
    }

    def publish(activity:String, info:String, investor:Investor){
        Zufaro.db.withTransaction { implicit sess =>
            publish(activity, info, investor.id, kind.INVESTOR)
        }

    }

    def publish(activity:String, info:String, subscriberId:Long, subscriberKind:Int)(implicit sess:SessionDef){
        ActivityStreams.map(s => (s.activity, s.info, s.subscriberId, s.subscriberKind)) +=
            (activity, info, subscriberId, subscriberKind)
//        += ActivityStreamRow(0L, activity, info, subscriberId, subscriberKind, now())
    }

    case class ActivityStreamItem(id:Long, activity:String, info:String, ts:Date)

    def getActivities(investor:Investor, offset:Int, limit:Int):Seq[ActivityStreamItem] = {
        Zufaro.db.withSession { implicit sess =>
            val q = for {
                as <- ActivityStreams if as.subscriberId === investor.id && as.subscriberKind === kind.INVESTOR
            } yield (as.id, as.activity, as.info, as.ts)
            q.sortBy(_._4.desc).drop(offset).take(limit)
                .run.map(a => ActivityStreamItem(a._1, a._2, a._3, new Date(a._4.getTime)))
        }
    }


}
