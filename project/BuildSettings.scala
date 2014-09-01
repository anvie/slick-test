import sbt._
import Keys._
//import com.github.siasia.WebPlugin._
//import ls.Plugin._

object BuildSettings {

  lazy val basicSettings = seq(
    version               := "0.0.7-alpha",
    homepage              := Some(new URL("http://ansvia.com")),
    organization          := "com.ansvia.zufaro",
    organizationHomepage  := Some(new URL("http://ansvia.com")),
    description           := "",
    startYear             := Some(2012),
    //licenses              := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    scalaVersion          := "2.10.4",
    resolvers             ++= Dependencies.resolutionRepos,
    scalacOptions         := Seq("-deprecation", "-encoding", "utf8", "-language:experimental.macros")
  )

  lazy val moduleSettings = basicSettings ++ seq(
    // scaladoc settings
    (scalacOptions in doc) <++= (name, version).map { (n, v) => Seq("-doc-title", n, "-doc-version", v) },

    // publishing
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    crossPaths := false,
    publishMavenStyle := true

    // LS
    // (LsKeys.tags in LsKeys.lsync) := Seq("http", "server", "client", "async"),
    // (LsKeys.docsUrl in LsKeys.lsync) := Some(new URL("http://spray.github.com/spray/api/spray-can/")),
    // (externalResolvers in LsKeys.lsync) := Seq("spray repo" at "http://repo.spray.cc")
  )

  lazy val noPublishing = Seq(
    publish := (),
    publishLocal := ()
  )
  
  lazy val withPublishing = Seq(
      publishTo <<= version { (v:String) =>
            val ansviaRepo = "http://scala.repo.ansvia.com/nexus"
            if(v.trim.endsWith("SNAPSHOT"))
                Some("snapshots" at ansviaRepo + "/content/repositories/snapshots")
            else
                Some("releases" at ansviaRepo + "/content/repositories/releases")
      },

      credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),

      publishArtifact in Test := false,

      pomIncludeRepository := { _ => false },

      crossPaths := false,

      pomExtra := (
          <url>http://www.mindtalk.com/u/robin</url>
          <developers>
            <developer>
              <id>robin</id>
              <name>Robin Syihab</name>
              <url>http://www.mindtalk.com/u/robin</url>
            </developer>
          </developers>)
  )

}

