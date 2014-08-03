package com.ansvia.zufaro.model
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = scala.slick.driver.H2Driver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._
  import scala.slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{GetResult => GR}
  
  /** DDL for all tables. Call .create to execute. */
  lazy val ddl = Business.ddl ++ Invest.ddl ++ User.ddl
  
  /** Entity class storing rows of table Business
   *  @param id Database column ID 
   *  @param name Database column NAME 
   *  @param desc Database column DESC 
   *  @param divideSys Database column DIVIDE_SYS 
   *  @param divideInvest Database column DIVIDE_INVEST  */
  case class BusinessRow(id: Int, name: String, desc: String, divideSys: Double, divideInvest: Double)
  /** GetResult implicit for fetching BusinessRow objects using plain SQL queries */
  implicit def GetResultBusinessRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Double]): GR[BusinessRow] = GR{
    prs => import prs._
    BusinessRow.tupled((<<[Int], <<[String], <<[String], <<[Double], <<[Double]))
  }
  /** Table description of table BUSINESS. Objects of this class serve as prototypes for rows in queries. */
  class Business(tag: Tag) extends Table[BusinessRow](tag, "BUSINESS") {
    def * = (id, name, desc, divideSys, divideInvest) <> (BusinessRow.tupled, BusinessRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, desc.?, divideSys.?, divideInvest.?).shaped.<>({r=>import r._; _1.map(_=> BusinessRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID  */
    val id: Column[Int] = column[Int]("ID")
    /** Database column NAME  */
    val name: Column[String] = column[String]("NAME")
    /** Database column DESC  */
    val desc: Column[String] = column[String]("DESC")
    /** Database column DIVIDE_SYS  */
    val divideSys: Column[Double] = column[Double]("DIVIDE_SYS")
    /** Database column DIVIDE_INVEST  */
    val divideInvest: Column[Double] = column[Double]("DIVIDE_INVEST")
  }
  /** Collection-like TableQuery object for table Business */
  lazy val Business = new TableQuery(tag => new Business(tag))
  
  /** Entity class storing rows of table Invest
   *  @param id Database column ID AutoInc
   *  @param userId Database column USER_ID 
   *  @param busId Database column BUS_ID 
   *  @param amount Database column AMOUNT  */
  case class InvestRow(id: Long, userId: Int, busId: Int, amount: Int)
  /** GetResult implicit for fetching InvestRow objects using plain SQL queries */
  implicit def GetResultInvestRow(implicit e0: GR[Long], e1: GR[Int]): GR[InvestRow] = GR{
    prs => import prs._
    InvestRow.tupled((<<[Long], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table INVEST. Objects of this class serve as prototypes for rows in queries. */
  class Invest(tag: Tag) extends Table[InvestRow](tag, "INVEST") {
    def * = (id, userId, busId, amount) <> (InvestRow.tupled, InvestRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, userId.?, busId.?, amount.?).shaped.<>({r=>import r._; _1.map(_=> InvestRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc */
    val id: Column[Long] = column[Long]("ID", O.AutoInc)
    /** Database column USER_ID  */
    val userId: Column[Int] = column[Int]("USER_ID")
    /** Database column BUS_ID  */
    val busId: Column[Int] = column[Int]("BUS_ID")
    /** Database column AMOUNT  */
    val amount: Column[Int] = column[Int]("AMOUNT")
  }
  /** Collection-like TableQuery object for table Invest */
  lazy val Invest = new TableQuery(tag => new Invest(tag))
  
  /** Entity class storing rows of table User
   *  @param id Database column ID 
   *  @param name Database column NAME 
   *  @param role Database column ROLE  */
  case class UserRow(id: Int, name: String, role: Int)
  /** GetResult implicit for fetching UserRow objects using plain SQL queries */
  implicit def GetResultUserRow(implicit e0: GR[Int], e1: GR[String]): GR[UserRow] = GR{
    prs => import prs._
    UserRow.tupled((<<[Int], <<[String], <<[Int]))
  }
  /** Table description of table USER. Objects of this class serve as prototypes for rows in queries. */
  class User(tag: Tag) extends Table[UserRow](tag, "USER") {
    def * = (id, name, role) <> (UserRow.tupled, UserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, role.?).shaped.<>({r=>import r._; _1.map(_=> UserRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID  */
    val id: Column[Int] = column[Int]("ID")
    /** Database column NAME  */
    val name: Column[String] = column[String]("NAME")
    /** Database column ROLE  */
    val role: Column[Int] = column[Int]("ROLE")
  }
  /** Collection-like TableQuery object for table User */
  lazy val User = new TableQuery(tag => new User(tag))
}