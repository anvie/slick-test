package com.ansvia.zufaro.web.snippet

import scala.xml.NodeSeq
import net.liftweb.util.Props
import com.ansvia.util.idgen.RandomStringGenerator

/**
 * Author: robin
 * Date: 8/10/14
 * Time: 6:30 PM
 *
 */
object AssetsSnippet {

    lazy val hash = {
        (new RandomStringGenerator).nextId()
    }

    private lazy val postFixReplacer = "\\.(js|css)$".r

    private def asset(path:String):String = {
        if (Props.mode == Props.RunModes.Development /* || Props.mode == Props.RunModes.Test*/){
            path + "?" + hash
        }else{
            if (path.startsWith("/classpath")){
                path + "?" + hash
            }else{
                val ps = path.split("/")
                var i = 0
                var psConstruct = Array.empty[String]
                for (p <- ps){
                    i += 1
                    if (i == ps.length){
                        psConstruct :+= postFixReplacer.replaceFirstIn(p,".min.$1")
                    }else
                        psConstruct :+= p
                }
                psConstruct.reduceLeftOption(_ + "/" + _).getOrElse("") + "?" + hash
            }
        }
    }

    def js:NodeSeq = {
        <xml:group>
            {/* <script id="jquery" src={asset("/classpath/jquery.js")} type="text/javascript"></script> */ }
            <script id="jquery" src={asset("/assets/js/jquery-1.11.1.min.js")} type="text/javascript"></script>
            {/* <!--<script id="json" src={asset("/classpath/json.js")} type="text/javascript"></script>--> */ }
            {/* <script id="jsconf" type="text/javascript" src="/sys/conf"></script> */}
            <script type="text/javascript" src={asset("/assets/js/digaku2.js")}></script>
            <script id="BootstrapJs" src={asset("/assets/js/bootstrap.js")} type="text/javascript"></script>
        </xml:group>
    }

    def css:NodeSeq = {
        <xml:group>
            <link rel="stylesheet" type="text/css" href={asset("/assets/css/bootstrap.css")}></link>
        </xml:group>
    }


}
