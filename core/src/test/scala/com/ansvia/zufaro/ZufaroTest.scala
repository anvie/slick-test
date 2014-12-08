package com.ansvia.zufaro

import com.ansvia.commons.logging.Slf4jLogger
import org.specs2.mutable.Specification

/**
 * Author: robin
 * Date: 8/15/14
 * Time: 12:54 PM
 *
 */

abstract class ZufaroTest extends Specification with ZufaroTestHelpers with Slf4jLogger {

  // comment out ini apabila ingin menggunakan H2
//    val dbTestFile = "/tmp/zufaro-data-test"
//
//    {   // clean up data file
//        val f = new File("/tmp")
//        if (f.exists()){
//            f.listFiles(new FilenameFilter {
//                def accept(dir: File, name: String): Boolean = name.startsWith("zufaro-data")
//            }).foreach { _f =>
//                println(s"deleting /tmp/${_f.getName} ...")
//                _f.delete()
//            }
//        }
//    }
//
//    Zufaro.jdbcUrl = s"jdbc:h2:$dbTestFile"
  // EOL ---------------------


  Zufaro.jdbcUrl = "jdbc:postgresql://localhost/zufaro_test?user=robin&password=123123"


}

