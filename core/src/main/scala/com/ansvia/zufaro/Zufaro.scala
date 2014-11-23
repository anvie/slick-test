package com.ansvia.zufaro

import scala.slick.driver.PostgresDriver.simple._
import com.ansvia.commons.logging.Slf4jLogger
import java.sql.Timestamp
import java.util.Date


/**
 * Author: robin
 * Date: 8/3/14
 * Time: 10:40 PM
 *
 */
object Zufaro extends Slf4jLogger {

    import scala.slick.jdbc.meta._

    var jdbcUrl:String = "jdbc:postgresql://localhost/zufaro?user=robin&password=123123"

    lazy val db = {
        val _db = Database.forURL(jdbcUrl, driver = "org.postgresql.Driver")

        _db.withSession { implicit sess =>
            if (MTable.getTables.list.isEmpty){
                info("first init db, no any tables exists, creating...")
                model.Tables.ddl.create
            }
        }
        _db
    }

    def init(){

    }

}

object TimestampHelpers {
    def now() = new Timestamp(new Date().getTime)
}

object ZufaroHelpers {

    val IDR = 1
    val USD = 2
    val SGD = 3

    implicit class currencyHelper(d:Double){
        def format(currency:Int) = {
            currency match {
                case IDR =>
                    f"Rp.$d%.02f,-"
                case USD =>
                    f"$$ $d%.01f USD"
                case SGD =>
                    f"$$ $d%.01f SGD"
            }
        }

    }
}

