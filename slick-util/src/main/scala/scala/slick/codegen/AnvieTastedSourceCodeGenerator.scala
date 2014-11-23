
package scala.slick.codegen

import scala.slick.model.ForeignKeyAction
import scala.slick.{model => m}

/**
 * Author: robin
 *
 */


/**
 * My custom slick code generator to fit my taste.
 * @param model Slick data model for which code should be generated.
 */
class AnvieTastedSourceCodeGenerator(model: m.Model)
    extends AbstractSourceCodeGenerator(model) with OutputHelpers{
    // "Tying the knot": making virtual classes concrete
    type Table = TableDef
    def Table = new TableDef(_)

    override def code = {
        "import scala.slick.model.ForeignKeyAction\n" +
            ( if(tables.exists(_.hlistEnabled)){
                "import scala.slick.collection.heterogenous._\n"+
                    "import scala.slick.collection.heterogenous.syntax._\n"
            } else ""
                ) +
            ( if(tables.exists(_.PlainSqlMapper.enabled)){
                "// NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.\n"+
                    "import scala.slick.jdbc.{GetResult => GR}\n"
            } else ""
                ) +
            "\n/** DDL for all tables. Call .create to execute. */\nlazy val ddl = " + tables.map(t => (jamak(t.TableValue.name.toString)) + ".ddl").mkString(" ++ ") +
            "\n\n" +
            tables.map(_.code.mkString("\n")).mkString("\n\n")
    }

    override def entityName: (String) => String = (dbName:String) => dbName.toCamelCase

    protected def jamak(str:String) = if (str.endsWith("s")) str + "es" else str + "s"


    class TableDef(model: m.Table) extends super.TableDef(model){
        // Using defs instead of (caching) lazy vals here to provide consitent interface to the user.
        // Performance should really not be critical in the code generator. Models shouldn't be huge.
        // Also lazy vals don't inherit docs from defs
        type EntityType     =     EntityTypeDef
        def  EntityType     = new EntityType{}
        type PlainSqlMapper =     PlainSqlMapperDef
        def  PlainSqlMapper = new PlainSqlMapper{}
        type TableClass     =     TableClassDef
        def  TableClass     = new TableClass{
            override def code = {
                val prns = parents.map(" with " + _).mkString("")
                val args = model.name.schema.map(n => s"""Some("$n")""") ++ Seq("\""+model.name.table+"\"")
                s"""
class ${name}Row(_tableTag: Tag) extends Table[$elementType](_tableTag, ${args.mkString(", ")})$prns {
  ${indent(body.map(_.mkString("\n")).mkString("\n\n"))}
}
        """.trim()
            }
        }
        type TableValue     =     TableValueDef
        def  TableValue     = new TableValue{
            override def code: String = s"lazy val ${jamak(name.toString)} = new TableQuery(tag => new ${TableClass.name}Row(tag))"
        }
        type Column         =     ColumnDef
        def  Column         = new Column(_)
        type PrimaryKey     =     PrimaryKeyDef
        def  PrimaryKey     = new PrimaryKey(_)
        type ForeignKey     =     MyForeignKeyDef
        def  ForeignKey     = new ForeignKey(_)
        type Index          =     IndexDef
        def  Index          = new Index(_)

        class MyForeignKeyDef(model: m.ForeignKey) extends super.ForeignKeyDef(model){
            override def code = {
                val pkTable = jamak(referencedTable.TableValue.name)
                val (pkColumns, fkColumns) = (referencedColumns, referencingColumns).zipped.map { (p, f) =>
                    val pk = s"r.${p.name}"
                    val fk = f.name
                    if(p.model.nullable && !f.model.nullable) (pk, s"Rep.Some($fk)")
                    else if(!p.model.nullable && f.model.nullable) (s"Rep.Some($pk)", fk)
                    else (pk, fk)
                }.unzip
                s"""lazy val $name = foreignKey("$dbName", ${compoundValue(fkColumns)}, $pkTable)(r => ${compoundValue(pkColumns)}, onUpdate=${onUpdate}, onDelete=${onDelete})"""
            }
        }


    }


}

/** A runnable class to execute the code generator without further setup */
object AnvieTastedSourceCodeGenerator {
    import scala.reflect.runtime.currentMirror
    import scala.slick.driver.JdbcProfile
    def main(args: Array[String]) = {
        args.toList match {
            case slickDriver :: jdbcDriver :: url :: outputFolder :: pkg :: tail if tail.size == 0 || tail.size == 2 => {
                val driver: JdbcProfile = {
                    val module = currentMirror.staticModule(slickDriver)
                    val reflectedModule = currentMirror.reflectModule(module)
                    val driver = reflectedModule.instance.asInstanceOf[JdbcProfile]
                    driver
                }
                val db = driver.simple.Database
                (tail match{
                    case user :: password :: Nil => db.forURL(url, driver = jdbcDriver, user=user, password=password)
                    case Nil => db.forURL(url, driver = jdbcDriver)
                    case _ => throw new Exception("This should never happen.")
                }).withSession{ implicit session =>
                    new AnvieTastedSourceCodeGenerator(driver.createModel()).writeToFile(slickDriver,outputFolder,pkg)
                }
            }
            case _ => {
                println("""
Usage:
  AnvieTastedSourceCodeGenerator.main(Array(slickDriver, jdbcDriver, url, outputFolder, pkg))
  AnvieTastedSourceCodeGenerator.main(Array(slickDriver, jdbcDriver, url, outputFolder, pkg, user, password))

slickDriver: Fully qualified name of Slick driver class, e.g. "scala.slick.driver.H2Driver"

jdbcDriver: Fully qualified name of jdbc driver class, e.g. "org.h2.Driver"

url: jdbc url, e.g. "jdbc:postgresql://localhost/test"

outputFolder: Place where the package folder structure should be put

pkg: Scala package the generated code should be places in

user: database connection user name

password: database connection password
                        """.trim
                )
            }
        }
    }
}


