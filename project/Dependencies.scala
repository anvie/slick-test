import sbt._

object Dependencies {
    val resolutionRepos = Seq(
        "typesafe repo"   at "http://repo.typesafe.com/typesafe/releases/",
        "glassfish repo"  at "http://download.java.net/maven/glassfish/",
        "spray repo"      at "http://repo.spray.cc/",
        "Ansvia release repo"     at "http://scala.repo.ansvia.com/releases/",
        "Ansvia snapshot repo" at "http://scala.repo.ansvia.com/nexus/content/repositories/snapshots",
        "Sonatype snapshots"     at "https://oss.sonatype.org/content/repositories/snapshots",
        "Sonatype releases"      at "https://oss.sonatype.org/content/repositories/releases"
    )

    def compile   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
    def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
    def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
    def runtime   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
    def container (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

    lazy val lift = {
        val liftVersion = "2.6-M4"
        Seq(
            "net.liftweb"       %% "lift-webkit" % liftVersion % "compile",
//            "net.liftmodules"   % "lift-jquery-module_2.10" % (liftVersion + "-2.3"),
            "net.liftmodules" %% ("lift-jquery-module_2.6") % "2.5",
            "org.eclipse.jetty" % "jetty-webapp"        % "8.1.7.v20120910"  % "container,test",
            "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container,test" artifacts Artifact("javax.servlet", "jar", "jar"),
            "ch.qos.logback" % "logback-classic" % "1.0.13",
            "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided"
        )
    }

    val ansviaCommons = "com.ansvia" %% "ansvia-commons" % "0.1.9"
    val ansviaIdGen = "com.ansvia" %% "ansvia-idgen" % "0.1.9"

    val specs2        = "org.specs2" %% "specs2" % "2.4"
    val logback       = "ch.qos.logback" % "logback-classic" % "1.0.9"
    lazy val slick    = "com.typesafe.slick" %% "slick" % "2.0.2"
    lazy val h2db     = "com.h2database" % "h2" % "1.2.140"

}
