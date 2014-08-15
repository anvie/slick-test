package com.ansvia.zufaro.web.snippet

import net.liftweb._
import util._
import http._
import Helpers._
import com.ansvia.zufaro.web.lib.MtTabInterface
import scala.xml.NodeSeq
import net.liftweb.common.Full
import com.ansvia.zufaro.exception.ZufaroException

/**
 * Author: robin
 * Date: 8/15/14
 * Time: 5:15 PM
 *
 */
class AdminApiSnippet {

    private object nameVar extends RequestVar("")
    private object descVar extends RequestVar("")
    private object grantVar extends RequestVar("")

    def addNew(in:NodeSeq):NodeSeq = {

        def doAddNew() = {
            try {

                // @TODO(robin): code here

            }catch{
                case e:ZufaroException =>
                    S.error(e.getMessage)
            }
        }


        val grants = Seq(("all", "ALL"))


        bind("in", in,
        "name" -> SHtml.text(nameVar, nameVar(_), "class" -> "form-control", "id" -> "Name"),
        "name" -> SHtml.textarea(descVar, descVar(_), "class" -> "form-control", "id" -> "Desc"),
        "grant" -> SHtml.select(grants, Full(grantVar), grantVar(_), "class" -> "form-control", "id" -> "Grant"),
        "submit" -> SHtml.submit("Add", doAddNew, "class" -> "btn btn-success")
        )
    }

}

class AdminApiTab extends MtTabInterface {
    def defaultSelected: String = "active-client"

    def tmplDir: String = "admin/api"

    override def tabNames: Array[String] = Array("active-client", "suspended-client")
}
