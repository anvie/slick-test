import sbt._

object Dependencies {
    val resolutionRepos = Seq(
        "typesafe repo"   at "http://repo.typesafe.com/typesafe/releases/",
        "glassfish repo"  at "http://download.java.net/maven/glassfish/",
        "spray repo"      at "http://repo.spray.cc/",
        "Ansvia repo"     at "http://scala.repo.ansvia.com/releases/",
        "Ansvia snapshot repo" at "http://scala.repo.ansvia.com/nexus/content/repositories/snapshots",
        "Sonatype snapshots"     at "https://oss.sonatype.org/content/repositories/snapshots",
        "Sonatype releases"      at "https://oss.sonatype.org/content/repositories/releases"
    )

    def compile   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
    def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
    def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
    def runtime   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
    def container (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

    val ansviaCommons = "com.ansvia" % "ansvia-commons" % "0.1.8-20140421-SNAPSHOT"
    val specs2        = "org.specs2" %% "specs2" % "2.4"
    val logback       = "ch.qos.logback" % "logback-classic" % "1.0.9"
    lazy val slick    = "com.typesafe.slick" %% "slick" % "2.0.2"
    lazy val h2db     = "com.h2database" % "h2" % "1.2.140"

}
