import sbt._
import Keys._

// Author: Robin

object Build extends Build {
	import BuildSettings._
	import Dependencies._

  	// root
	lazy val root = Project("root", file("."))
		.aggregate(zufaroCore, zufaro)
		.settings(basicSettings: _*)
		.settings(noPublishing: _*)
		
	// modules

//	lazy val examples = Project("examples", file("examples"))
//		.settings(moduleSettings: _*)
//		.settings(libraryDependencies ++=
//			compile(ansviaCommons) ++
//			test(specs2) ++
//			runtime(logback)
//		)

  lazy val zufaroCore = Project("zufaro-core", file("core"))
    .settings(moduleSettings: _*)
    .settings(slickTask <<= slickCodeGenTask)
    .settings(libraryDependencies ++=
      compile(ansviaCommons, slick, h2db) ++
      test(specs2) ++
      runtime(logback)
  )

	lazy val zufaro = Project("zufaro", file("zufaro"))
		.settings(moduleSettings: _*)
        .settings(slickTask <<= slickCodeGenTask)
		.settings(libraryDependencies ++=
			compile(ansviaCommons, slick) ++
			test(specs2) ++
			runtime(logback)
		).dependsOn(zufaroCore)


    // code generation task
    lazy val slickTask = TaskKey[Seq[File]]("gen-tables")
    lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
        val outputDir = "core/src/main/scala"  //(dir / "slick").getPath // place generated files in sbt's managed sources folder
        val url = "jdbc:h2:mem:test1;INIT=runscript from 'core/src/main/sql/schema.sql'" // connection info for a pre-populated throw-away, in-memory db for this demo, which is freshly initialized on every run
        val jdbcDriver = "org.h2.Driver"
        val slickDriver = "scala.slick.driver.H2Driver"
        val pkg = "com.ansvia.zufaro.model"
        toError(r.run("scala.slick.model.codegen.SourceCodeGenerator", cp.files, Array(slickDriver, jdbcDriver, url, outputDir, pkg), s.log))
        val fname = outputDir + "/Tables.scala"
        Seq(file(fname))
    }
}
