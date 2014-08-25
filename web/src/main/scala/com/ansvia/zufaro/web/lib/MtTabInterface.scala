package com.ansvia.zufaro.web.lib

/**
 * Author: robin
 * Date: 8/15/14
 * Time: 5:13 PM
 *
 */

import net.liftweb.http._
import xml.NodeSeq
import net.liftweb.util.Helpers._
import net.liftweb.http.js._
import JsCmds._
import net.liftweb.http.js.JE.{JsRaw, Str}
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.util.NamedPF


abstract class MtTabInterface extends DispatchSnippet {

    def defaultSelected:String

    /**
     * parameter key
     * untuk mengidentifikasi parameter
     * posisi tab saat ini.
     */
    val tabSelected:String = "tab"

    def tmplNamePrefix = "_chunk_"

    def render(in:NodeSeq):NodeSeq = {
        val contentId = S.attr("content_id") openOr "TabPane"
        val _tmplDir:String = S.attr("tmplDir") openOr tmplDir
        val addParams = S.attr("addParams") openOr ""
        val selected = (S.attr("selected") openOr defaultSelected).toLowerCase
        val redirNames = (S.attr("redirParams") openOr "").toLowerCase.split(",")

        val paramNames = S.request map { req =>
            req.params
        }
        val redirParams =
            paramNames.map(_.filter(z => redirNames.contains(z._1.toLowerCase))
                .map(x => "%s=%s".format(x._1,x._2.reduceLeftOption(_ + "," + _).getOrElse("")))
                .reduceLeftOption(_ + "&" + _).getOrElse("")).getOrElse("")

        var selectedTabFiltered = false

        def _loadTab(content:NodeSeq, title:String) = {

            val tabItemName = _tmplDir.replaceAll("\\W+","-").toLowerCase + "-tab-item"
            val tabItemTitleName = tabItemName + "-" + title.toLowerCase

            S.fmapFunc(()=>{

                Templates(_tmplDir.split("/").toList ++ List(tmplNamePrefix + title.toLowerCase)).
                    map { ns =>

                    new JsCmd {
                        def toJsCmd = SetHtml(contentId, ns).toJsCmd + ";" +
                            """
                              |$(".%s").removeClass("active");
                              |$(".%s").addClass("active");
                            """.stripMargin.trim.format(tabItemName, tabItemTitleName) +
                            receiveJs(title).toJsCmd
                    }

                } openOr Noop

            }){ ajaxId =>

                val html5State = getTabState

                val classNames =
                    if (selectedTabFiltered){
                        selectedTabFiltered = false
                        tabItemName + " " + tabItemTitleName + " active"
                    }else{
                        if (selected == title.toLowerCase)
                            if(html5State.length > 0)
                                if(html5State == title.toLowerCase)
                                    tabItemName + " " + tabItemTitleName + " active"
                                else
                                    tabItemName + " " + tabItemTitleName
                            else
                                tabItemName + " " + tabItemTitleName + " active"
                        else
                        if(html5State.length > 0)
                            if(html5State == title.toLowerCase)
                                tabItemName + " " + tabItemTitleName + " active"
                            else
                                tabItemName + " " + tabItemTitleName
                        else
                            tabItemName + " " + tabItemTitleName
                    }

                <li class={classNames}>
                    <a href="javascript://" onclick={SHtml.makeAjaxCall(Str(ajaxId + "=true&" +
                        addParams + "&" + redirParams)).toJsCmd + "; " +
                        preSendJs(title).toJsCmd + ";" +
                        "; return false;"}>{content}</a>
                </li>
            }
        }

        val tabStrList = tabNames

        selectedTabFiltered = tabStrList.map(_.toLowerCase).filterNot(tabFilter).contains(selected)

        val tabs = tabStrList map { tab =>
            if(tabFilter(tab)){
                FuncBindParam(tab.toLowerCase, (ns)=> _loadTab(ns, tab))
            }else{
                FuncBindParam(tab.toLowerCase, (ns)=> Nil)
            }
        }

        bind("tab", in,
            (tabs ++ List(FuncBindParam("tab-pane", tabPane))): _*
        )
    }

    private def tabPane(ns:NodeSeq):NodeSeq = {
        val selected = (S.attr("selected") openOr defaultSelected).toLowerCase
        val tab = S.param(tabSelected) openOr(selected)
        S.runTemplate((tmplDir.split("/").toList ++ List(tmplNamePrefix + tab))) openOr NodeSeq.Empty
    }

    def tmplDir:String

    def tabNames = S.attr("tabs") openOr "tab1,tab2" split(",")

    def tabFilter(tabName:String):Boolean = true

    def getTabState = S.param(tabSelected) openOr ""

    def receiveJs(tabName:String):JsCmd = new JsCmd {
        def toJsCmd = ""
    }

    def preSendJs(tabName:String):JsCmd = new JsCmd {
        def toJsCmd = ""
    }

    def dispatch = {
        case _ => render
    }


    protected lazy val tabRe = ("(" + tabNames.mkString("|") + ")").r


}

trait HTML5HistoryHandler {
    this: MtTabInterface =>

    protected def urlFormat(tabName:String):String = s"/$tmplDir/$tabName"

    override def preSendJs(tabName: String): JsCmd =
        JsRaw(s"History.pushState(null,null,'${urlFormat(tabName)}');").cmd
}


