package scala.slick.codegen

object ModelGenerator {
    import scala.reflect.runtime.currentMirror
    import scala.slick.driver.JdbcProfile

    private val slickDriver = "scala.slick.driver.PostgresDriver"
    private val outputDir = "core/src/main/scala"
    private val jdbcDriver = "org.postgresql.Driver"
    private val pkg = "com.ansvia.zufaro.model"

    def main(args: Array[String]) {

        if (args.length < 2){
            println("Usage: ModelGenerator [USER] [PASSWORD]")
            return
        }

        val user = args(0)
        val password = args(1)

        val driver: JdbcProfile = {
            val module = currentMirror.staticModule(slickDriver)
            val reflectedModule = currentMirror.reflectModule(module)
            val driver = reflectedModule.instance.asInstanceOf[JdbcProfile]
            driver
        }
        val url = "jdbc:postgresql://localhost/zufaro"

        val db = driver.simple.Database.forURL(url, driver = jdbcDriver, user=user, password=password)
        db.withSession{ implicit session =>
            new AnvieTastedSourceCodeGenerator(driver.createModel()).writeToFile(slickDriver, outputDir, pkg)
        }
    }
}
