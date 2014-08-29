package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import scala.slick.driver.H2Driver.backend.SessionDef
import com.ansvia.zufaro.model.Tables._
import java.util.Date

/**
 * Author: robin
 * Date: 8/29/14
 * Time: 9:54 PM
 *
 */
object Activity {

    import TimestampHelpers._

    object kind {
        val ADMIN = 1
        val INVESTOR = 2
        val OPERATOR = 3
    }

    def publish(activity:String, info:String, investor:InvestorRow){
        Zufaro.db.withTransaction { implicit sess =>
            publish(activity, info, investor.id, kind.INVESTOR)
        }

    }

    def publish(activity:String, info:String, subscriberId:Long, subscriberKind:Int)(implicit sess:SessionDef){
        ActivityStream += ActivityStreamRow(0L, activity, info, subscriberId, subscriberKind, now())
    }

    case class ActivityStreamItem(id:Long, activity:String, info:String, ts:Date)

    def getActivities(investor:InvestorRow, offset:Int, limit:Int):Seq[ActivityStreamItem] = {
        Zufaro.db.withSession { implicit sess =>
            val q = for {
                as <- ActivityStream if as.subscriberId === investor.id && as.subscriberKind === kind.INVESTOR
            } yield (as.id, as.activity, as.info, as.ts)
            q.sortBy(_._3).drop(offset).take(limit)
                .run.map(a => ActivityStreamItem(a._1, a._2, a._3, new Date(a._4.getTime)))
        }
    }


}
