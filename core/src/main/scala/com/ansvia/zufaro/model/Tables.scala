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
  lazy val ddl = Admin.ddl ++ Business.ddl ++ BusinessProfit.ddl ++ Credit.ddl ++ Debit.ddl ++ Invest.ddl ++ Investor.ddl ++ InvestorBalance.ddl ++ Operator.ddl
  
  /** Entity class storing rows of table Admin
   *  @param id Database column ID AutoInc
   *  @param name Database column NAME  */
  case class AdminRow(id: Long, name: String)
  /** GetResult implicit for fetching AdminRow objects using plain SQL queries */
  implicit def GetResultAdminRow(implicit e0: GR[Long], e1: GR[String]): GR[AdminRow] = GR{
    prs => import prs._
    AdminRow.tupled((<<[Long], <<[String]))
  }
  /** Table description of table ADMIN. Objects of this class serve as prototypes for rows in queries. */
  class Admin(tag: Tag) extends Table[AdminRow](tag, "ADMIN") {
    def * = (id, name) <> (AdminRow.tupled, AdminRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?).shaped.<>({r=>import r._; _1.map(_=> AdminRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc */
    val id: Column[Long] = column[Long]("ID", O.AutoInc)
    /** Database column NAME  */
    val name: Column[String] = column[String]("NAME")
  }
  /** Collection-like TableQuery object for table Admin */
  lazy val Admin = new TableQuery(tag => new Admin(tag))
  
  /** Entity class storing rows of table Business
   *  @param id Database column ID AutoInc
   *  @param name Database column NAME 
   *  @param desc Database column DESC 
   *  @param divideSys Database column DIVIDE_SYS 
   *  @param divideInvest Database column DIVIDE_INVEST  */
  case class BusinessRow(id: Long, name: String, desc: String, divideSys: Double, divideInvest: Double)
  /** GetResult implicit for fetching BusinessRow objects using plain SQL queries */
  implicit def GetResultBusinessRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Double]): GR[BusinessRow] = GR{
    prs => import prs._
    BusinessRow.tupled((<<[Long], <<[String], <<[String], <<[Double], <<[Double]))
  }
  /** Table description of table BUSINESS. Objects of this class serve as prototypes for rows in queries. */
  class Business(tag: Tag) extends Table[BusinessRow](tag, "BUSINESS") {
    def * = (id, name, desc, divideSys, divideInvest) <> (BusinessRow.tupled, BusinessRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, desc.?, divideSys.?, divideInvest.?).shaped.<>({r=>import r._; _1.map(_=> BusinessRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc */
    val id: Column[Long] = column[Long]("ID", O.AutoInc)
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
  
  /** Entity class storing rows of table BusinessProfit
   *  @param id Database column ID AutoInc
   *  @param busId Database column BUS_ID 
   *  @param amount Database column AMOUNT 
   *  @param ts Database column TS  */
  case class BusinessProfitRow(id: Long, busId: Long, amount: Double, ts: Option[java.sql.Timestamp])
  /** GetResult implicit for fetching BusinessProfitRow objects using plain SQL queries */
  implicit def GetResultBusinessProfitRow(implicit e0: GR[Long], e1: GR[Double], e2: GR[Option[java.sql.Timestamp]]): GR[BusinessProfitRow] = GR{
    prs => import prs._
    BusinessProfitRow.tupled((<<[Long], <<[Long], <<[Double], <<?[java.sql.Timestamp]))
  }
  /** Table description of table BUSINESS_PROFIT. Objects of this class serve as prototypes for rows in queries. */
  class BusinessProfit(tag: Tag) extends Table[BusinessProfitRow](tag, "BUSINESS_PROFIT") {
    def * = (id, busId, amount, ts) <> (BusinessProfitRow.tupled, BusinessProfitRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, busId.?, amount.?, ts).shaped.<>({r=>import r._; _1.map(_=> BusinessProfitRow.tupled((_1.get, _2.get, _3.get, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc */
    val id: Column[Long] = column[Long]("ID", O.AutoInc)
    /** Database column BUS_ID  */
    val busId: Column[Long] = column[Long]("BUS_ID")
    /** Database column AMOUNT  */
    val amount: Column[Double] = column[Double]("AMOUNT")
    /** Database column TS  */
    val ts: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("TS")
  }
  /** Collection-like TableQuery object for table BusinessProfit */
  lazy val BusinessProfit = new TableQuery(tag => new BusinessProfit(tag))
  
  /** Entity class storing rows of table Credit
   *  @param id Database column ID AutoInc
   *  @param invId Database column INV_ID 
   *  @param amount Database column AMOUNT 
   *  @param ref Database column REF  */
  case class CreditRow(id: Long, invId: Long, amount: Double, ref: Option[String])
  /** GetResult implicit for fetching CreditRow objects using plain SQL queries */
  implicit def GetResultCreditRow(implicit e0: GR[Long], e1: GR[Double], e2: GR[Option[String]]): GR[CreditRow] = GR{
    prs => import prs._
    CreditRow.tupled((<<[Long], <<[Long], <<[Double], <<?[String]))
  }
  /** Table description of table CREDIT. Objects of this class serve as prototypes for rows in queries. */
  class Credit(tag: Tag) extends Table[CreditRow](tag, "CREDIT") {
    def * = (id, invId, amount, ref) <> (CreditRow.tupled, CreditRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, invId.?, amount.?, ref).shaped.<>({r=>import r._; _1.map(_=> CreditRow.tupled((_1.get, _2.get, _3.get, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc */
    val id: Column[Long] = column[Long]("ID", O.AutoInc)
    /** Database column INV_ID  */
    val invId: Column[Long] = column[Long]("INV_ID")
    /** Database column AMOUNT  */
    val amount: Column[Double] = column[Double]("AMOUNT")
    /** Database column REF  */
    val ref: Column[Option[String]] = column[Option[String]]("REF")
  }
  /** Collection-like TableQuery object for table Credit */
  lazy val Credit = new TableQuery(tag => new Credit(tag))
  
  /** Entity class storing rows of table Debit
   *  @param id Database column ID AutoInc
   *  @param invId Database column INV_ID 
   *  @param amount Database column AMOUNT 
   *  @param ref Database column REF  */
  case class DebitRow(id: Long, invId: Long, amount: Double, ref: Option[String])
  /** GetResult implicit for fetching DebitRow objects using plain SQL queries */
  implicit def GetResultDebitRow(implicit e0: GR[Long], e1: GR[Double], e2: GR[Option[String]]): GR[DebitRow] = GR{
    prs => import prs._
    DebitRow.tupled((<<[Long], <<[Long], <<[Double], <<?[String]))
  }
  /** Table description of table DEBIT. Objects of this class serve as prototypes for rows in queries. */
  class Debit(tag: Tag) extends Table[DebitRow](tag, "DEBIT") {
    def * = (id, invId, amount, ref) <> (DebitRow.tupled, DebitRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, invId.?, amount.?, ref).shaped.<>({r=>import r._; _1.map(_=> DebitRow.tupled((_1.get, _2.get, _3.get, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc */
    val id: Column[Long] = column[Long]("ID", O.AutoInc)
    /** Database column INV_ID  */
    val invId: Column[Long] = column[Long]("INV_ID")
    /** Database column AMOUNT  */
    val amount: Column[Double] = column[Double]("AMOUNT")
    /** Database column REF  */
    val ref: Column[Option[String]] = column[Option[String]]("REF")
  }
  /** Collection-like TableQuery object for table Debit */
  lazy val Debit = new TableQuery(tag => new Debit(tag))
  
  /** Entity class storing rows of table Invest
   *  @param id Database column ID AutoInc
   *  @param invId Database column INV_ID 
   *  @param busId Database column BUS_ID 
   *  @param amount Database column AMOUNT  */
  case class InvestRow(id: Long, invId: Long, busId: Long, amount: Double)
  /** GetResult implicit for fetching InvestRow objects using plain SQL queries */
  implicit def GetResultInvestRow(implicit e0: GR[Long], e1: GR[Double]): GR[InvestRow] = GR{
    prs => import prs._
    InvestRow.tupled((<<[Long], <<[Long], <<[Long], <<[Double]))
  }
  /** Table description of table INVEST. Objects of this class serve as prototypes for rows in queries. */
  class Invest(tag: Tag) extends Table[InvestRow](tag, "INVEST") {
    def * = (id, invId, busId, amount) <> (InvestRow.tupled, InvestRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, invId.?, busId.?, amount.?).shaped.<>({r=>import r._; _1.map(_=> InvestRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc */
    val id: Column[Long] = column[Long]("ID", O.AutoInc)
    /** Database column INV_ID  */
    val invId: Column[Long] = column[Long]("INV_ID")
    /** Database column BUS_ID  */
    val busId: Column[Long] = column[Long]("BUS_ID")
    /** Database column AMOUNT  */
    val amount: Column[Double] = column[Double]("AMOUNT")
  }
  /** Collection-like TableQuery object for table Invest */
  lazy val Invest = new TableQuery(tag => new Invest(tag))
  
  /** Entity class storing rows of table Investor
   *  @param id Database column ID AutoInc
   *  @param name Database column NAME 
   *  @param role Database column ROLE  */
  case class InvestorRow(id: Long, name: String, role: Int)
  /** GetResult implicit for fetching InvestorRow objects using plain SQL queries */
  implicit def GetResultInvestorRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[InvestorRow] = GR{
    prs => import prs._
    InvestorRow.tupled((<<[Long], <<[String], <<[Int]))
  }
  /** Table description of table INVESTOR. Objects of this class serve as prototypes for rows in queries. */
  class Investor(tag: Tag) extends Table[InvestorRow](tag, "INVESTOR") {
    def * = (id, name, role) <> (InvestorRow.tupled, InvestorRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, role.?).shaped.<>({r=>import r._; _1.map(_=> InvestorRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc */
    val id: Column[Long] = column[Long]("ID", O.AutoInc)
    /** Database column NAME  */
    val name: Column[String] = column[String]("NAME")
    /** Database column ROLE  */
    val role: Column[Int] = column[Int]("ROLE")
  }
  /** Collection-like TableQuery object for table Investor */
  lazy val Investor = new TableQuery(tag => new Investor(tag))
  
  /** Entity class storing rows of table InvestorBalance
   *  @param id Database column ID AutoInc
   *  @param invId Database column INV_ID 
   *  @param amount Database column AMOUNT  */
  case class InvestorBalanceRow(id: Long, invId: Long, amount: Double)
  /** GetResult implicit for fetching InvestorBalanceRow objects using plain SQL queries */
  implicit def GetResultInvestorBalanceRow(implicit e0: GR[Long], e1: GR[Double]): GR[InvestorBalanceRow] = GR{
    prs => import prs._
    InvestorBalanceRow.tupled((<<[Long], <<[Long], <<[Double]))
  }
  /** Table description of table INVESTOR_BALANCE. Objects of this class serve as prototypes for rows in queries. */
  class InvestorBalance(tag: Tag) extends Table[InvestorBalanceRow](tag, "INVESTOR_BALANCE") {
    def * = (id, invId, amount) <> (InvestorBalanceRow.tupled, InvestorBalanceRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, invId.?, amount.?).shaped.<>({r=>import r._; _1.map(_=> InvestorBalanceRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc */
    val id: Column[Long] = column[Long]("ID", O.AutoInc)
    /** Database column INV_ID  */
    val invId: Column[Long] = column[Long]("INV_ID")
    /** Database column AMOUNT  */
    val amount: Column[Double] = column[Double]("AMOUNT")
  }
  /** Collection-like TableQuery object for table InvestorBalance */
  lazy val InvestorBalance = new TableQuery(tag => new InvestorBalance(tag))
  
  /** Entity class storing rows of table Operator
   *  @param id Database column ID AutoInc
   *  @param name Database column NAME 
   *  @param abilities Database column ABILITIES  */
  case class OperatorRow(id: Long, name: String, abilities: String)
  /** GetResult implicit for fetching OperatorRow objects using plain SQL queries */
  implicit def GetResultOperatorRow(implicit e0: GR[Long], e1: GR[String]): GR[OperatorRow] = GR{
    prs => import prs._
    OperatorRow.tupled((<<[Long], <<[String], <<[String]))
  }
  /** Table description of table OPERATOR. Objects of this class serve as prototypes for rows in queries. */
  class Operator(tag: Tag) extends Table[OperatorRow](tag, "OPERATOR") {
    def * = (id, name, abilities) <> (OperatorRow.tupled, OperatorRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, abilities.?).shaped.<>({r=>import r._; _1.map(_=> OperatorRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc */
    val id: Column[Long] = column[Long]("ID", O.AutoInc)
    /** Database column NAME  */
    val name: Column[String] = column[String]("NAME")
    /** Database column ABILITIES  */
    val abilities: Column[String] = column[String]("ABILITIES")
  }
  /** Collection-like TableQuery object for table Operator */
  lazy val Operator = new TableQuery(tag => new Operator(tag))
}