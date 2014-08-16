package com.ansvia.zufaro.web.util

/**
 * Author: robin
 * Date: 8/16/14
 * Time: 1:53 PM
 *
 */

import xml._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.S
import xml.Text
import net.liftweb.json.JsonAST
import net.liftweb.json.JsonAST.JValue

import net.liftweb.http.js.JE.JsRaw
import com.ansvia.zufaro.exception.ZufaroException


object JsUtils {

    def setByClass(className:String, content:String):JsCmd =
        setByClass(className, Text(content))

    /**
     * Set content by it class name.
     * @param className
     * @param content
     */
    def setByClass(className:String, content:NodeSeq):JsCmd =
        new JsCmd {
            def toJsCmd = {
                val cn = if(className.startsWith("."))
                    className
                else
                    "." + className
                fixHtmlCmdFunc("inline", content){ nss =>
                    """$("%s").html(%s);""".format(cn, nss)
                }
            }
        }

    /**
     * Set content by it class name and add class to the element.
     * @param className
     * @param content
     * @param classStyle
     */
    def setContentByClass(className:String, content:Text, classStyle:String):JsCmd =
        new JsCmd {
            def toJsCmd = """$(".%s").html("%s").removeClass().addClass("%s");""".format(className, content, classStyle)
        }

    /**
     * @deprecated use [[com.ansvia.zufaro.web.util.JsUtils.setByClass]] instead.
     * @param className
     * @param content
     * @param classStyle
     */
    case class SetByClass(className:String, content:Text, classStyle:String) extends JsCmd {
        def toJsCmd = {
            val classNamex = if(className.startsWith("."))
                className
            else
                "." + className
            if (classStyle.length > 0)
                """$("%s").text("%s").removeClass().addClass("%s");""".format(classNamex, content, classStyle)
            else
                """$("%s").text("%s");""".format(classNamex, content)
        }
    }

    /**
     * set class by css selector.
     * @param cssSelector css selector, eg: .notif, #SomeId.
     * @param className new class name.
     * @return
     */
    def setClass(cssSelector:String, className:String) = new JsCmd {
        def toJsCmd = """$("%s").attr("class", "%s");""".format(cssSelector, className)
    }

    /**
     * Create confirmation dialog before doing an ajax operation.
     * @param confirmationText confirmation text which show in dialog yes/no.
     * @param actionContent element content / button content.
     * @param elementTitle element title if any.
     * @param attrs additional attributes to element.
     * @param funcOp function to dispatch when user click yes/ok.
     * @return
     */
    def ajaxConfirm[T](confirmationText:String, actionContent:NodeSeq, elementTitle:String="",
                       attrs:Map[String,String]=Map.empty[String,String])(funcOp: => T):NodeSeq = {
        S.fmapFunc(()=> funcOp) { id =>

            var rv:Elem =
                if(elementTitle.length > 0)
                    <a href="javascript://" title={elementTitle}
                       onclick={"dg.Ajax.confirm('%s', '%s');".format(confirmationText, id)}>
                        {actionContent}
                    </a>
                else
                    <a href="javascript://"
                       onclick={"dg.Ajax.confirm('%s', '%s');".format(confirmationText, id)}>
                        {actionContent}
                    </a>

            for( (k, v) <- attrs ){
                rv = rv % Attribute(None, k, Seq(Text(v)), Null)
            }

            rv
        }
    }


    /**
     * Remove node/element by id.
     * @param nodeId id of element to remove.
     * @return
     */
    def removeNode(nodeId:String) = {
        new JsCmd {
            def toJsCmd = """$("#%s").remove();""".format(nodeId)
        }
    }

    /**
     * Remove node/element by css selector with fadeOut effect.
     * @param selector id of element to remove.
     * @return
     */
    def removeNodeFade(selector:String) = {
        new JsCmd {
            def toJsCmd =
                """$("%s").fadeOut(function(){
                  |     $(this).remove();
                  |});""".stripMargin.format(selector)
        }
    }

    /**
     * Remove node/element by id.
     * @param className class name of element to remove.
     * @return
     */
    def removeNodeByClass(className:String) = {
        new JsCmd {
            def toJsCmd = """$(".%s").remove();""".format(className)
        }
    }

    def confirmDialog(ns:NodeSeq) = {
        new JsCmd {
            def toJsCmd = fixHtmlCmdFunc("inline",ns) { nss =>

                val reMatch = """id=\\"(F\w*)\\"""".r.findAllIn(nss).matchData

                if (!reMatch.hasNext)
                    throw new ZufaroException("Cannot spawn confirm dialog, not ajax form?\n" +
                        "JsUtils.confirmDialog only work for ajaxForm", -1)

                val formId = reMatch.next().group(1)

                """bootbox.confirm(%s, function(result){
                  |     if(result==true)
                  |         liftAjax.lift_ajaxHandler(jQuery("#%s").serialize(), null, null, "javascript");
                  |});""".stripMargin.format(nss, formId)
            }
        }
    }

    def genericModal(ns:NodeSeq, title:String) = {
        new JsCmd {
            def toJsCmd = fixHtmlCmdFunc("inline",ns) { nss =>
                """bootbox.dialog({message:%s, title:'%s'});""".stripMargin.format(nss, title)
            }
        }
    }

    def magnificModal(ns:NodeSeq) = {
        new JsCmd {
            def toJsCmd = fixHtmlCmdFunc("inline", ns) { nss =>
                """dg.Util.modalDialog(%s);""".format(nss)
            }
        }
    }

    /**
     * Digunakan untuk menutup semua modal dialog apabila ada.
     */
    object hideAllModal extends JsCmd {
        def toJsCmd = {
            """dg.Util.hideAllModal();""".stripMargin
        }
    }

    def modalDialog(ns:NodeSeq, className:String="") = {
        new JsCmd {
            def toJsCmd = fixHtmlCmdFunc("inline",ns) { nss =>
                """bootbox.dialog({message:%s, className:"modal %s"});""".format(nss, className)
            }
        }
    }

    def modalDialogIframe(url:String,afterClose:Option[JsCmd]=None) = {
        new JsCmd {
            def toJsCmd = {

                afterClose.map { acCmd =>
                    """
                      |dg.Util.modalIframe('%s', "", function(){
                      |   %s;
                      |});
                      |""".stripMargin.format(url, acCmd.toJsCmd)
                }.getOrElse {
                    """dg.Util.modalIframe('%s');""".format(url)
                }
            }
        }
    }

    def alertDialog(ns:NodeSeq) = {
        new JsCmd {
            def toJsCmd = fixHtmlCmdFunc("inline",ns) { nss =>

                val formId = """id=\\"(F\w*)\\"""".r.findAllIn(nss).matchData.next().group(1)

                """bootbox.alert(%s, function(result){
                  |     if(result==true)
                  |         liftAjax.lift_ajaxHandler(jQuery("#%s").serialize(), null, null, "javascript");
                  |});""".stripMargin.format(nss, formId)
            }
        }
    }


    /**
     * Show error alert dialog to client.
     * @param text alert text.
     * @return
     */
    def showError(text:String):JsCmd = {
        new JsCmd {
            def toJsCmd =
                """dg.Util.showError('%s');""".format(text)
        }
    }

    /**
     * Menampilkan notice dialog.
     * @param text
     * @return
     */
    def showNotice(text:String):JsCmd = {
        new JsCmd {
            def toJsCmd =
                """dg.Util.notice('%s');""".format(text)
        }
    }

    /**
     * Untuk operasi-operasi yang berkaitan dengan
     * notifikasi di client-side (browser).
     */
    object notif {

        /**
         * untuk menampilkan notifikasi notice.
         * @param title judul notifikasi
         * @param text isi notifikasi, bisa juga berupa html string.
         * @return
         */
        def notice(title:String, text:String):JsCmd = {
            new JsCmd {
                def toJsCmd = """dg.Notif.notice('%s','%s');""".format(title, text)
            }
        }

        /**
         * untuk menampilkan notifikasi notice.
         * @param title judul notifikasi
         * @param content isi notifikasi berupa NodeSeq.
         * @return
         */
        def notice(title:String, content:NodeSeq):JsCmd = {
            new JsCmd {
                def toJsCmd = fixHtmlCmdFunc("inline", content){ nss =>
                    """dg.Notif.notice('%s','%s');""".format(title, nss)
                }
            }
        }

        /**
         * untuk menampilkan notifikasi info.
         * @param title judul notifikasi
         * @param text isi notifikasi, bisa juga berupa html string.
         * @return
         */
        def info(title:String, text:String, onClick:Option[JsCmd]=None):JsCmd = {
            info(title, Text(text), onClick)
        }

        /**
         * untuk menampilkan notifikasi info.
         * @param title judul notifikasi
         * @param content isi notifikasi, bisa juga berupa html string.
         * @return
         */
        def info(title:String, content:NodeSeq, onClick:Option[JsCmd]):JsCmd = {
            new JsCmd {
                def toJsCmd = {
                    onClick.map { oc =>
                        fixHtmlFunc("inline", content){ nss =>
                            """dg.Notif.info('%s', %s)
                              |.click(function(e){
                              |   %s;
                              |});
                            """.stripMargin.format(title, nss, oc.toJsCmd)
                        }
                    }.getOrElse {
                        fixHtmlFunc("inline", content){ nss =>
                            """dg.Notif.info('%s', %s);""".format(title, nss)
                        }
                    }
                }
            }
        }

    }

    /**
     * Untuk operasi-operasi yang berkaitan
     * dengan event di client-side (browser).
     */
    object event {

        /**
         * Untuk men-trigger browser event di current frame.
         * @param eventName
         * @param data
         * @return
         */
        def trigger(eventName:String, data:Option[JValue]=None) = {
            new JsCmd {
                def toJsCmd = {
                    import net.liftweb.json.Printer._

                    data.map { d =>
                        """$(document).trigger("%s", %s);
                        """.format(eventName, compact(JsonAST.render(d)))
                    }.getOrElse(
                        """$(document).trigger("%s", null);
                        """.format(eventName)
                    )
                }
            }
        }

        /**
         * Untuk men-trigger browser event dari iframe ke parent frame.
         * @param eventName
         * @param data
         * @return
         */
        def triggerParent(eventName:String, data:Option[JValue]=None) = {
            new JsCmd {
                def toJsCmd = {
                    import net.liftweb.json.Printer._
                    data.map { d =>
                        """parent.$(parent.document).trigger("%s", %s);
                        """.format(eventName, compact(JsonAST.render(d)))
                    }.getOrElse(
                        """parent.$(parent.document).trigger("%s", null);
                        """.format(eventName)
                    )
                }
            }
        }
    }

    /**
     * Untuk nge-refresh / reload halaman browser.
     * @return
     */
    def refreshPage() = {
        new JsCmd {
            def toJsCmd = {
                """window.location.reload();"""
            }
        }
    }

    /**
     * Untuk meredirect halaman mellaui Javascript.
     * @param url
     * @return
     */
    def redirectTo(url:String) = {
        new JsCmd {
            def toJsCmd: String = "window.location.href='" + url + "';"
        }
    }


    /**
     * Untuk nge-refresh / reload halaman browser
     * setelah beberapa milidetik.
     * @return
     */
    def refreshPageAfter(milis:Int) = {
        new JsCmd {
            def toJsCmd = {
                """setTimeout(function(){
                  | window.location.reload();
                  |},%s);""".stripMargin.format(milis)
            }
        }
    }

    /**
     * Untuk nge-redirect browser ke halaman lain
     * dilakukan di browser level (client side).
     * @param url url target.
     * @return
     */
    def goto(url:String) = {
        new JsCmd {
            def toJsCmd: String = "window.location.href='" + url + "';"
        }
    }

    val history = History


    object fixStream extends JsCmd {
        def toJsCmd: String = "dg.Util.fixStream();"
    }


    object Log {

        def info(msg:String) =
            JsRaw("log.info('" + msg + "')").cmd

        def warn(msg:String) =
            JsRaw("log.warn('" + msg + "')").cmd

        def error(msg:String) =
            JsRaw("log.error('" + msg + "')").cmd

        def print(msg:String) =
            JsRaw("log.print('" + msg + "')").cmd

    }
}



private[util] object History {

    def push(url:String, title:String="", data:String="") = {
        new JsCmd {
            def toJsCmd = {
                "dg.History.push('%s',%s,%s);".format(url,
                    if(title=="") "'null'" else "'%s'".format(title),
                    if(data=="") "'null'" else "'%s'".format(data)
                )
            }
        }
    }

    def showModal(url:String, embedUrl:String, currentUrl:String, dataClass:String="") = {
        new JsCmd {
            def toJsCmd = {
                """
                  |(function(){
                  |var data = {
                  |   loc: 'show-modal',
                  |   url: '%s',
                  |   embedUrl: '%s',
                  |   currentUrl: '%s',
                  |   dataClass: '%s'
                  |}
                  |dg.History.push('%s', null, data);
                  |})();
                """.stripMargin.format(url, embedUrl, currentUrl, dataClass, url)
            }
        }
    }

}

