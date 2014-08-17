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
  lazy val ddl = Admin.ddl ++ ApiClient.ddl ++ ApiClientAccess.ddl ++ Business.ddl ++ BusinessAccountMutation.ddl ++ BusinessGroup.ddl ++ BusinessGroupLink.ddl ++ BusinessProfit.ddl ++ Invest.ddl ++ Investor.ddl ++ InvestorBalance.ddl ++ Mutation.ddl ++ Operator.ddl ++ ProfitShareJournal.ddl ++ ProjectReport.ddl ++ ProjectWatcher.ddl
  
  /** Entity class storing rows of table Admin
   *  @param id Database column ID AutoInc, PrimaryKey
   *  @param name Database column NAME 
   *  @param passhash Database column PASSHASH  */
  case class AdminRow(id: Long, name: String, passhash: String)
  /** GetResult implicit for fetching AdminRow objects using plain SQL queries */
  implicit def GetResultAdminRow(implicit e0: GR[Long], e1: GR[String]): GR[AdminRow] = GR{
    prs => import prs._
    AdminRow.tupled((<<[Long], <<[String], <<[String]))
  }
  /** Table description of table ADMIN. Objects of this class serve as prototypes for rows in queries. */
  class Admin(tag: Tag) extends Table[AdminRow](tag, "ADMIN") {
    def * = (id, name, passhash) <> (AdminRow.tupled, AdminRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, passhash.?).shaped.<>({r=>import r._; _1.map(_=> AdminRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column NAME  */
    val name: Column[String] = column[String]("NAME")
    /** Database column PASSHASH  */
    val passhash: Column[String] = column[String]("PASSHASH")
  }
  /** Collection-like TableQuery object for table Admin */
  lazy val Admin = new TableQuery(tag => new Admin(tag))
  
  /** Entity class storing rows of table ApiClient
   *  @param id Database column ID AutoInc, PrimaryKey
   *  @param name Database column NAME 
   *  @param desc Database column DESC 
   *  @param creatorId Database column CREATOR_ID 
   *  @param creatorRole Database column CREATOR_ROLE 
   *  @param key Database column KEY 
   *  @param suspended Database column SUSPENDED  */
  case class ApiClientRow(id: Long, name: String, desc: String, creatorId: Long, creatorRole: Int, key: String, suspended: Boolean)
  /** GetResult implicit for fetching ApiClientRow objects using plain SQL queries */
  implicit def GetResultApiClientRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Int], e3: GR[Boolean]): GR[ApiClientRow] = GR{
    prs => import prs._
    ApiClientRow.tupled((<<[Long], <<[String], <<[String], <<[Long], <<[Int], <<[String], <<[Boolean]))
  }
  /** Table description of table API_CLIENT. Objects of this class serve as prototypes for rows in queries. */
  class ApiClient(tag: Tag) extends Table[ApiClientRow](tag, "API_CLIENT") {
    def * = (id, name, desc, creatorId, creatorRole, key, suspended) <> (ApiClientRow.tupled, ApiClientRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, desc.?, creatorId.?, creatorRole.?, key.?, suspended.?).shaped.<>({r=>import r._; _1.map(_=> ApiClientRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column NAME  */
    val name: Column[String] = column[String]("NAME")
    /** Database column DESC  */
    val desc: Column[String] = column[String]("DESC")
    /** Database column CREATOR_ID  */
    val creatorId: Column[Long] = column[Long]("CREATOR_ID")
    /** Database column CREATOR_ROLE  */
    val creatorRole: Column[Int] = column[Int]("CREATOR_ROLE")
    /** Database column KEY  */
    val key: Column[String] = column[String]("KEY")
    /** Database column SUSPENDED  */
    val suspended: Column[Boolean] = column[Boolean]("SUSPENDED")
  }
  /** Collection-like TableQuery object for table ApiClient */
  lazy val ApiClient = new TableQuery(tag => new ApiClient(tag))
  
  /** Entity class storing rows of table ApiClientAccess
   *  @param id Database column ID AutoInc, PrimaryKey
   *  @param apiClientId Database column API_CLIENT_ID 
   *  @param grant Database column GRANT 
   *  @param target Database column TARGET  */
  case class ApiClientAccessRow(id: Long, apiClientId: Long, grant: String, target: String)
  /** GetResult implicit for fetching ApiClientAccessRow objects using plain SQL queries */
  implicit def GetResultApiClientAccessRow(implicit e0: GR[Long], e1: GR[String]): GR[ApiClientAccessRow] = GR{
    prs => import prs._
    ApiClientAccessRow.tupled((<<[Long], <<[Long], <<[String], <<[String]))
  }
  /** Table description of table API_CLIENT_ACCESS. Objects of this class serve as prototypes for rows in queries. */
  class ApiClientAccess(tag: Tag) extends Table[ApiClientAccessRow](tag, "API_CLIENT_ACCESS") {
    def * = (id, apiClientId, grant, target) <> (ApiClientAccessRow.tupled, ApiClientAccessRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, apiClientId.?, grant.?, target.?).shaped.<>({r=>import r._; _1.map(_=> ApiClientAccessRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column API_CLIENT_ID  */
    val apiClientId: Column[Long] = column[Long]("API_CLIENT_ID")
    /** Database column GRANT  */
    val grant: Column[String] = column[String]("GRANT")
    /** Database column TARGET  */
    val target: Column[String] = column[String]("TARGET")
  }
  /** Collection-like TableQuery object for table ApiClientAccess */
  lazy val ApiClientAccess = new TableQuery(tag => new ApiClientAccess(tag))
  
  /** Entity class storing rows of table Business
   *  @param id Database column ID AutoInc, PrimaryKey
   *  @param name Database column NAME 
   *  @param desc Database column DESC 
   *  @param fund Database column FUND 
   *  @param share Database column SHARE 
   *  @param state Database column STATE 
   *  @param shareTime Database column SHARE_TIME 
   *  @param sharePeriod Database column SHARE_PERIOD 
   *  @param saving Database column SAVING 
   *  @param createdAt Database column CREATED_AT  */
  case class BusinessRow(id: Long, name: String, desc: String, fund: Double, share: Double, state: Int, shareTime: Int, sharePeriod: Int, saving: Double, createdAt: java.sql.Timestamp)
  /** GetResult implicit for fetching BusinessRow objects using plain SQL queries */
  implicit def GetResultBusinessRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Double], e3: GR[Int], e4: GR[java.sql.Timestamp]): GR[BusinessRow] = GR{
    prs => import prs._
    BusinessRow.tupled((<<[Long], <<[String], <<[String], <<[Double], <<[Double], <<[Int], <<[Int], <<[Int], <<[Double], <<[java.sql.Timestamp]))
  }
  /** Table description of table BUSINESS. Objects of this class serve as prototypes for rows in queries. */
  class Business(tag: Tag) extends Table[BusinessRow](tag, "BUSINESS") {
    def * = (id, name, desc, fund, share, state, shareTime, sharePeriod, saving, createdAt) <> (BusinessRow.tupled, BusinessRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, desc.?, fund.?, share.?, state.?, shareTime.?, sharePeriod.?, saving.?, createdAt.?).shaped.<>({r=>import r._; _1.map(_=> BusinessRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column NAME  */
    val name: Column[String] = column[String]("NAME")
    /** Database column DESC  */
    val desc: Column[String] = column[String]("DESC")
    /** Database column FUND  */
    val fund: Column[Double] = column[Double]("FUND")
    /** Database column SHARE  */
    val share: Column[Double] = column[Double]("SHARE")
    /** Database column STATE  */
    val state: Column[Int] = column[Int]("STATE")
    /** Database column SHARE_TIME  */
    val shareTime: Column[Int] = column[Int]("SHARE_TIME")
    /** Database column SHARE_PERIOD  */
    val sharePeriod: Column[Int] = column[Int]("SHARE_PERIOD")
    /** Database column SAVING  */
    val saving: Column[Double] = column[Double]("SAVING")
    /** Database column CREATED_AT  */
    val createdAt: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("CREATED_AT")
  }
  /** Collection-like TableQuery object for table Business */
  lazy val Business = new TableQuery(tag => new Business(tag))
  
  /** Entity class storing rows of table BusinessAccountMutation
   *  @param id Database column ID AutoInc, PrimaryKey
   *  @param busId Database column BUS_ID 
   *  @param kind Database column KIND 
   *  @param amount Database column AMOUNT 
   *  @param info Database column INFO 
   *  @param initiator Database column INITIATOR 
   *  @param ts Database column TS  */
  case class BusinessAccountMutationRow(id: Long, busId: Long, kind: Int, amount: Double, info: String, initiator: String, ts: java.sql.Timestamp)
  /** GetResult implicit for fetching BusinessAccountMutationRow objects using plain SQL queries */
  implicit def GetResultBusinessAccountMutationRow(implicit e0: GR[Long], e1: GR[Int], e2: GR[Double], e3: GR[String], e4: GR[java.sql.Timestamp]): GR[BusinessAccountMutationRow] = GR{
    prs => import prs._
    BusinessAccountMutationRow.tupled((<<[Long], <<[Long], <<[Int], <<[Double], <<[String], <<[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table BUSINESS_ACCOUNT_MUTATION. Objects of this class serve as prototypes for rows in queries. */
  class BusinessAccountMutation(tag: Tag) extends Table[BusinessAccountMutationRow](tag, "BUSINESS_ACCOUNT_MUTATION") {
    def * = (id, busId, kind, amount, info, initiator, ts) <> (BusinessAccountMutationRow.tupled, BusinessAccountMutationRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, busId.?, kind.?, amount.?, info.?, initiator.?, ts.?).shaped.<>({r=>import r._; _1.map(_=> BusinessAccountMutationRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column BUS_ID  */
    val busId: Column[Long] = column[Long]("BUS_ID")
    /** Database column KIND  */
    val kind: Column[Int] = column[Int]("KIND")
    /** Database column AMOUNT  */
    val amount: Column[Double] = column[Double]("AMOUNT")
    /** Database column INFO  */
    val info: Column[String] = column[String]("INFO")
    /** Database column INITIATOR  */
    val initiator: Column[String] = column[String]("INITIATOR")
    /** Database column TS  */
    val ts: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("TS")
  }
  /** Collection-like TableQuery object for table BusinessAccountMutation */
  lazy val BusinessAccountMutation = new TableQuery(tag => new BusinessAccountMutation(tag))
  
  /** Entity class storing rows of table BusinessGroup
   *  @param id Database column ID AutoInc
   *  @param name Database column NAME 
   *  @param desc Database column DESC  */
  case class BusinessGroupRow(id: Long, name: String, desc: String)
  /** GetResult implicit for fetching BusinessGroupRow objects using plain SQL queries */
  implicit def GetResultBusinessGroupRow(implicit e0: GR[Long], e1: GR[String]): GR[BusinessGroupRow] = GR{
    prs => import prs._
    BusinessGroupRow.tupled((<<[Long], <<[String], <<[String]))
  }
  /** Table description of table BUSINESS_GROUP. Objects of this class serve as prototypes for rows in queries. */
  class BusinessGroup(tag: Tag) extends Table[BusinessGroupRow](tag, "BUSINESS_GROUP") {
    def * = (id, name, desc) <> (BusinessGroupRow.tupled, BusinessGroupRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, desc.?).shaped.<>({r=>import r._; _1.map(_=> BusinessGroupRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc */
    val id: Column[Long] = column[Long]("ID", O.AutoInc)
    /** Database column NAME  */
    val name: Column[String] = column[String]("NAME")
    /** Database column DESC  */
    val desc: Column[String] = column[String]("DESC")
  }
  /** Collection-like TableQuery object for table BusinessGroup */
  lazy val BusinessGroup = new TableQuery(tag => new BusinessGroup(tag))
  
  /** Entity class storing rows of table BusinessGroupLink
   *  @param id Database column ID AutoInc
   *  @param busGroupId Database column BUS_GROUP_ID 
   *  @param busId Database column BUS_ID  */
  case class BusinessGroupLinkRow(id: Long, busGroupId: Long, busId: Long)
  /** GetResult implicit for fetching BusinessGroupLinkRow objects using plain SQL queries */
  implicit def GetResultBusinessGroupLinkRow(implicit e0: GR[Long]): GR[BusinessGroupLinkRow] = GR{
    prs => import prs._
    BusinessGroupLinkRow.tupled((<<[Long], <<[Long], <<[Long]))
  }
  /** Table description of table BUSINESS_GROUP_LINK. Objects of this class serve as prototypes for rows in queries. */
  class BusinessGroupLink(tag: Tag) extends Table[BusinessGroupLinkRow](tag, "BUSINESS_GROUP_LINK") {
    def * = (id, busGroupId, busId) <> (BusinessGroupLinkRow.tupled, BusinessGroupLinkRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, busGroupId.?, busId.?).shaped.<>({r=>import r._; _1.map(_=> BusinessGroupLinkRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc */
    val id: Column[Long] = column[Long]("ID", O.AutoInc)
    /** Database column BUS_GROUP_ID  */
    val busGroupId: Column[Long] = column[Long]("BUS_GROUP_ID")
    /** Database column BUS_ID  */
    val busId: Column[Long] = column[Long]("BUS_ID")
  }
  /** Collection-like TableQuery object for table BusinessGroupLink */
  lazy val BusinessGroupLink = new TableQuery(tag => new BusinessGroupLink(tag))
  
  /** Entity class storing rows of table BusinessProfit
   *  @param id Database column ID AutoInc, PrimaryKey
   *  @param busId Database column BUS_ID 
   *  @param omzet Database column OMZET 
   *  @param profit Database column PROFIT 
   *  @param ts Database column TS 
   *  @param mutatorId Database column MUTATOR_ID 
   *  @param mutatorRole Database column MUTATOR_ROLE 
   *  @param info Database column INFO 
   *  @param shared Database column SHARED Default(false)
   *  @param sharedAt Database column SHARED_AT  */
  case class BusinessProfitRow(id: Long, busId: Long, omzet: Double, profit: Double, ts: java.sql.Timestamp, mutatorId: Long, mutatorRole: Int, info: String, shared: Boolean = false, sharedAt: java.sql.Timestamp)
  /** GetResult implicit for fetching BusinessProfitRow objects using plain SQL queries */
  implicit def GetResultBusinessProfitRow(implicit e0: GR[Long], e1: GR[Double], e2: GR[java.sql.Timestamp], e3: GR[Int], e4: GR[String], e5: GR[Boolean]): GR[BusinessProfitRow] = GR{
    prs => import prs._
    BusinessProfitRow.tupled((<<[Long], <<[Long], <<[Double], <<[Double], <<[java.sql.Timestamp], <<[Long], <<[Int], <<[String], <<[Boolean], <<[java.sql.Timestamp]))
  }
  /** Table description of table BUSINESS_PROFIT. Objects of this class serve as prototypes for rows in queries. */
  class BusinessProfit(tag: Tag) extends Table[BusinessProfitRow](tag, "BUSINESS_PROFIT") {
    def * = (id, busId, omzet, profit, ts, mutatorId, mutatorRole, info, shared, sharedAt) <> (BusinessProfitRow.tupled, BusinessProfitRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, busId.?, omzet.?, profit.?, ts.?, mutatorId.?, mutatorRole.?, info.?, shared.?, sharedAt.?).shaped.<>({r=>import r._; _1.map(_=> BusinessProfitRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column BUS_ID  */
    val busId: Column[Long] = column[Long]("BUS_ID")
    /** Database column OMZET  */
    val omzet: Column[Double] = column[Double]("OMZET")
    /** Database column PROFIT  */
    val profit: Column[Double] = column[Double]("PROFIT")
    /** Database column TS  */
    val ts: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("TS")
    /** Database column MUTATOR_ID  */
    val mutatorId: Column[Long] = column[Long]("MUTATOR_ID")
    /** Database column MUTATOR_ROLE  */
    val mutatorRole: Column[Int] = column[Int]("MUTATOR_ROLE")
    /** Database column INFO  */
    val info: Column[String] = column[String]("INFO")
    /** Database column SHARED Default(false) */
    val shared: Column[Boolean] = column[Boolean]("SHARED", O.Default(false))
    /** Database column SHARED_AT  */
    val sharedAt: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("SHARED_AT")
  }
  /** Collection-like TableQuery object for table BusinessProfit */
  lazy val BusinessProfit = new TableQuery(tag => new BusinessProfit(tag))
  
  /** Entity class storing rows of table Invest
   *  @param id Database column ID AutoInc, PrimaryKey
   *  @param invId Database column INV_ID 
   *  @param busId Database column BUS_ID 
   *  @param amount Database column AMOUNT 
   *  @param busKind Database column BUS_KIND 
   *  @param ts Database column TS  */
  case class InvestRow(id: Long, invId: Long, busId: Long, amount: Double, busKind: Int, ts: java.sql.Timestamp)
  /** GetResult implicit for fetching InvestRow objects using plain SQL queries */
  implicit def GetResultInvestRow(implicit e0: GR[Long], e1: GR[Double], e2: GR[Int], e3: GR[java.sql.Timestamp]): GR[InvestRow] = GR{
    prs => import prs._
    InvestRow.tupled((<<[Long], <<[Long], <<[Long], <<[Double], <<[Int], <<[java.sql.Timestamp]))
  }
  /** Table description of table INVEST. Objects of this class serve as prototypes for rows in queries. */
  class Invest(tag: Tag) extends Table[InvestRow](tag, "INVEST") {
    def * = (id, invId, busId, amount, busKind, ts) <> (InvestRow.tupled, InvestRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, invId.?, busId.?, amount.?, busKind.?, ts.?).shaped.<>({r=>import r._; _1.map(_=> InvestRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column INV_ID  */
    val invId: Column[Long] = column[Long]("INV_ID")
    /** Database column BUS_ID  */
    val busId: Column[Long] = column[Long]("BUS_ID")
    /** Database column AMOUNT  */
    val amount: Column[Double] = column[Double]("AMOUNT")
    /** Database column BUS_KIND  */
    val busKind: Column[Int] = column[Int]("BUS_KIND")
    /** Database column TS  */
    val ts: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("TS")
  }
  /** Collection-like TableQuery object for table Invest */
  lazy val Invest = new TableQuery(tag => new Invest(tag))
  
  /** Entity class storing rows of table Investor
   *  @param id Database column ID AutoInc, PrimaryKey
   *  @param name Database column NAME 
   *  @param role Database column ROLE 
   *  @param passhash Database column PASSHASH 
   *  @param status Database column STATUS Default(1) */
  case class InvestorRow(id: Long, name: String, role: Int, passhash: String, status: Int = 1)
  /** GetResult implicit for fetching InvestorRow objects using plain SQL queries */
  implicit def GetResultInvestorRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[InvestorRow] = GR{
    prs => import prs._
    InvestorRow.tupled((<<[Long], <<[String], <<[Int], <<[String], <<[Int]))
  }
  /** Table description of table INVESTOR. Objects of this class serve as prototypes for rows in queries. */
  class Investor(tag: Tag) extends Table[InvestorRow](tag, "INVESTOR") {
    def * = (id, name, role, passhash, status) <> (InvestorRow.tupled, InvestorRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, role.?, passhash.?, status.?).shaped.<>({r=>import r._; _1.map(_=> InvestorRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column NAME  */
    val name: Column[String] = column[String]("NAME")
    /** Database column ROLE  */
    val role: Column[Int] = column[Int]("ROLE")
    /** Database column PASSHASH  */
    val passhash: Column[String] = column[String]("PASSHASH")
    /** Database column STATUS Default(1) */
    val status: Column[Int] = column[Int]("STATUS", O.Default(1))
  }
  /** Collection-like TableQuery object for table Investor */
  lazy val Investor = new TableQuery(tag => new Investor(tag))
  
  /** Entity class storing rows of table InvestorBalance
   *  @param id Database column ID AutoInc, PrimaryKey
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
    
    /** Database column ID AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column INV_ID  */
    val invId: Column[Long] = column[Long]("INV_ID")
    /** Database column AMOUNT  */
    val amount: Column[Double] = column[Double]("AMOUNT")
  }
  /** Collection-like TableQuery object for table InvestorBalance */
  lazy val InvestorBalance = new TableQuery(tag => new InvestorBalance(tag))
  
  /** Entity class storing rows of table Mutation
   *  @param id Database column ID AutoInc, PrimaryKey
   *  @param invId Database column INV_ID 
   *  @param kind Database column KIND 
   *  @param amount Database column AMOUNT 
   *  @param ref Database column REF 
   *  @param initiator Database column INITIATOR Default(None)
   *  @param ts Database column TS  */
  case class MutationRow(id: Long, invId: Long, kind: Int, amount: Double, ref: Option[String], initiator: Option[String] = None, ts: java.sql.Timestamp)
  /** GetResult implicit for fetching MutationRow objects using plain SQL queries */
  implicit def GetResultMutationRow(implicit e0: GR[Long], e1: GR[Int], e2: GR[Double], e3: GR[Option[String]], e4: GR[java.sql.Timestamp]): GR[MutationRow] = GR{
    prs => import prs._
    MutationRow.tupled((<<[Long], <<[Long], <<[Int], <<[Double], <<?[String], <<?[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table MUTATION. Objects of this class serve as prototypes for rows in queries. */
  class Mutation(tag: Tag) extends Table[MutationRow](tag, "MUTATION") {
    def * = (id, invId, kind, amount, ref, initiator, ts) <> (MutationRow.tupled, MutationRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, invId.?, kind.?, amount.?, ref, initiator, ts.?).shaped.<>({r=>import r._; _1.map(_=> MutationRow.tupled((_1.get, _2.get, _3.get, _4.get, _5, _6, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column INV_ID  */
    val invId: Column[Long] = column[Long]("INV_ID")
    /** Database column KIND  */
    val kind: Column[Int] = column[Int]("KIND")
    /** Database column AMOUNT  */
    val amount: Column[Double] = column[Double]("AMOUNT")
    /** Database column REF  */
    val ref: Column[Option[String]] = column[Option[String]]("REF")
    /** Database column INITIATOR Default(None) */
    val initiator: Column[Option[String]] = column[Option[String]]("INITIATOR", O.Default(None))
    /** Database column TS  */
    val ts: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("TS")
  }
  /** Collection-like TableQuery object for table Mutation */
  lazy val Mutation = new TableQuery(tag => new Mutation(tag))
  
  /** Entity class storing rows of table Operator
   *  @param id Database column ID AutoInc, PrimaryKey
   *  @param name Database column NAME 
   *  @param abilities Database column ABILITIES 
   *  @param passhash Database column PASSHASH  */
  case class OperatorRow(id: Long, name: String, abilities: String, passhash: String)
  /** GetResult implicit for fetching OperatorRow objects using plain SQL queries */
  implicit def GetResultOperatorRow(implicit e0: GR[Long], e1: GR[String]): GR[OperatorRow] = GR{
    prs => import prs._
    OperatorRow.tupled((<<[Long], <<[String], <<[String], <<[String]))
  }
  /** Table description of table OPERATOR. Objects of this class serve as prototypes for rows in queries. */
  class Operator(tag: Tag) extends Table[OperatorRow](tag, "OPERATOR") {
    def * = (id, name, abilities, passhash) <> (OperatorRow.tupled, OperatorRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, abilities.?, passhash.?).shaped.<>({r=>import r._; _1.map(_=> OperatorRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column NAME  */
    val name: Column[String] = column[String]("NAME")
    /** Database column ABILITIES  */
    val abilities: Column[String] = column[String]("ABILITIES")
    /** Database column PASSHASH  */
    val passhash: Column[String] = column[String]("PASSHASH")
  }
  /** Collection-like TableQuery object for table Operator */
  lazy val Operator = new TableQuery(tag => new Operator(tag))
  
  /** Entity class storing rows of table ProfitShareJournal
   *  @param busId Database column BUS_ID 
   *  @param busProfId Database column BUS_PROF_ID 
   *  @param invId Database column INV_ID 
   *  @param amount Database column AMOUNT 
   *  @param shareMethod Database column SHARE_METHOD Default(2)
   *  @param initiator Database column INITIATOR Default(None)
   *  @param ts Database column TS  */
  case class ProfitShareJournalRow(busId: Long, busProfId: Long, invId: Long, amount: Double, shareMethod: Int = 2, initiator: Option[String] = None, ts: java.sql.Timestamp)
  /** GetResult implicit for fetching ProfitShareJournalRow objects using plain SQL queries */
  implicit def GetResultProfitShareJournalRow(implicit e0: GR[Long], e1: GR[Double], e2: GR[Int], e3: GR[Option[String]], e4: GR[java.sql.Timestamp]): GR[ProfitShareJournalRow] = GR{
    prs => import prs._
    ProfitShareJournalRow.tupled((<<[Long], <<[Long], <<[Long], <<[Double], <<[Int], <<?[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table PROFIT_SHARE_JOURNAL. Objects of this class serve as prototypes for rows in queries. */
  class ProfitShareJournal(tag: Tag) extends Table[ProfitShareJournalRow](tag, "PROFIT_SHARE_JOURNAL") {
    def * = (busId, busProfId, invId, amount, shareMethod, initiator, ts) <> (ProfitShareJournalRow.tupled, ProfitShareJournalRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (busId.?, busProfId.?, invId.?, amount.?, shareMethod.?, initiator, ts.?).shaped.<>({r=>import r._; _1.map(_=> ProfitShareJournalRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column BUS_ID  */
    val busId: Column[Long] = column[Long]("BUS_ID")
    /** Database column BUS_PROF_ID  */
    val busProfId: Column[Long] = column[Long]("BUS_PROF_ID")
    /** Database column INV_ID  */
    val invId: Column[Long] = column[Long]("INV_ID")
    /** Database column AMOUNT  */
    val amount: Column[Double] = column[Double]("AMOUNT")
    /** Database column SHARE_METHOD Default(2) */
    val shareMethod: Column[Int] = column[Int]("SHARE_METHOD", O.Default(2))
    /** Database column INITIATOR Default(None) */
    val initiator: Column[Option[String]] = column[Option[String]]("INITIATOR", O.Default(None))
    /** Database column TS  */
    val ts: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("TS")
  }
  /** Collection-like TableQuery object for table ProfitShareJournal */
  lazy val ProfitShareJournal = new TableQuery(tag => new ProfitShareJournal(tag))
  
  /** Entity class storing rows of table ProjectReport
   *  @param id Database column ID AutoInc, PrimaryKey
   *  @param busId Database column BUS_ID 
   *  @param info Database column INFO 
   *  @param percentage Database column PERCENTAGE 
   *  @param initiator Database column INITIATOR 
   *  @param ts Database column TS  */
  case class ProjectReportRow(id: Long, busId: Long, info: String, percentage: Double, initiator: String, ts: java.sql.Timestamp)
  /** GetResult implicit for fetching ProjectReportRow objects using plain SQL queries */
  implicit def GetResultProjectReportRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Double], e3: GR[java.sql.Timestamp]): GR[ProjectReportRow] = GR{
    prs => import prs._
    ProjectReportRow.tupled((<<[Long], <<[Long], <<[String], <<[Double], <<[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table PROJECT_REPORT. Objects of this class serve as prototypes for rows in queries. */
  class ProjectReport(tag: Tag) extends Table[ProjectReportRow](tag, "PROJECT_REPORT") {
    def * = (id, busId, info, percentage, initiator, ts) <> (ProjectReportRow.tupled, ProjectReportRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, busId.?, info.?, percentage.?, initiator.?, ts.?).shaped.<>({r=>import r._; _1.map(_=> ProjectReportRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column BUS_ID  */
    val busId: Column[Long] = column[Long]("BUS_ID")
    /** Database column INFO  */
    val info: Column[String] = column[String]("INFO")
    /** Database column PERCENTAGE  */
    val percentage: Column[Double] = column[Double]("PERCENTAGE")
    /** Database column INITIATOR  */
    val initiator: Column[String] = column[String]("INITIATOR")
    /** Database column TS  */
    val ts: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("TS")
  }
  /** Collection-like TableQuery object for table ProjectReport */
  lazy val ProjectReport = new TableQuery(tag => new ProjectReport(tag))
  
  /** Entity class storing rows of table ProjectWatcher
   *  @param id Database column ID AutoInc
   *  @param invId Database column INV_ID 
   *  @param busId Database column BUS_ID  */
  case class ProjectWatcherRow(id: Long, invId: Long, busId: Long)
  /** GetResult implicit for fetching ProjectWatcherRow objects using plain SQL queries */
  implicit def GetResultProjectWatcherRow(implicit e0: GR[Long]): GR[ProjectWatcherRow] = GR{
    prs => import prs._
    ProjectWatcherRow.tupled((<<[Long], <<[Long], <<[Long]))
  }
  /** Table description of table PROJECT_WATCHER. Objects of this class serve as prototypes for rows in queries. */
  class ProjectWatcher(tag: Tag) extends Table[ProjectWatcherRow](tag, "PROJECT_WATCHER") {
    def * = (id, invId, busId) <> (ProjectWatcherRow.tupled, ProjectWatcherRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, invId.?, busId.?).shaped.<>({r=>import r._; _1.map(_=> ProjectWatcherRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc */
    val id: Column[Long] = column[Long]("ID", O.AutoInc)
    /** Database column INV_ID  */
    val invId: Column[Long] = column[Long]("INV_ID")
    /** Database column BUS_ID  */
    val busId: Column[Long] = column[Long]("BUS_ID")
  }
  /** Collection-like TableQuery object for table ProjectWatcher */
  lazy val ProjectWatcher = new TableQuery(tag => new ProjectWatcher(tag))
}