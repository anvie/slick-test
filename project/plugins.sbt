//addSbtPlugin("me.lessis" % "ls-sbt" % "0.1.1")

resolvers ++= Seq(
	"Ansvia repo" at "http://scala.repo.ansvia.com/releases"
//  "less is" at "http://repo.lessis.me",
//  "coda" at "http://repo.codahale.com"
)

//libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v + "-0.2.11"))

addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "0.9.0")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

//addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.0")

//addSbtPlugin("com.ansvia" % "onedir" % "0.6")

//addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.8.3")
