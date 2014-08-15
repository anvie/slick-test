package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import com.ansvia.commons.logging.Slf4jLogger


/**
 * Author: robin
 * Date: 8/3/14
 * Time: 10:40 PM
 *
 */
object Zufaro extends Slf4jLogger {

    import scala.slick.jdbc.meta._

    var jdbcUrl:String = "jdbc:h2:data/data"

    lazy val db = {
        val _db = Database.forURL(jdbcUrl, driver = "org.h2.Driver")

        _db.withSession { implicit sess =>
            if (MTable.getTables.list().isEmpty){
                info("first init db, no any tables exists, creating...")
                model.Tables.ddl.create
            }
        }
        _db
    }

    def init(){

    }

}

