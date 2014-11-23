package com.ansvia.zufaro.model
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = scala.slick.driver.PostgresDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._
  import scala.slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{GetResult => GR}
  
  /** DDL for all tables. Call .create to execute. */
  lazy val ddl = ActivityStreams.ddl ++ ApiClients.ddl ++ ApiClientAccesses.ddl ++ Businesses.ddl ++ BusinessAccountMutations.ddl ++ BusinessAssets.ddl ++ BusinessContacts.ddl ++ BusinessGroups.ddl ++ BusinessGroupLinks.ddl ++ BusinessProfits.ddl ++ Employees.ddl ++ Investors.ddl ++ InvestorBalances.ddl ++ InvestorContacts.ddl ++ InvestorHeirs.ddl ++ InvestorJobs.ddl ++ InvestorLegalIdentitys.ddl ++ InvestorOtherContacts.ddl ++ InvestorStocks.ddl ++ Mutations.ddl ++ ProfitShareJournals.ddl ++ ProjectReports.ddl ++ ProjectWatchers.ddl ++ Users.ddl
  
  /** Entity class storing rows of table ActivityStream
   *  @param id Database column ID DBType(bigserial), AutoInc, PrimaryKey
   *  @param activity Database column ACTIVITY DBType(varchar), Length(160,true)
   *  @param info Database column INFO DBType(varchar), Length(2147483647,true)
   *  @param subscriberId Database column SUBSCRIBER_ID DBType(int8)
   *  @param subscriberKind Database column SUBSCRIBER_KIND DBType(int4)
   *  @param ts Database column TS DBType(timestamp) */
  case class ActivityStream(id: Long, activity: String, info: String, subscriberId: Long, subscriberKind: Int, ts: java.sql.Timestamp)
  /** GetResult implicit for fetching ActivityStream objects using plain SQL queries */
  implicit def GetResultActivityStream(implicit e0: GR[Long], e1: GR[String], e2: GR[Int], e3: GR[java.sql.Timestamp]): GR[ActivityStream] = GR{
    prs => import prs._
    ActivityStream.tupled((<<[Long], <<[String], <<[String], <<[Long], <<[Int], <<[java.sql.Timestamp]))
  }
  /** Table description of table activity_stream. Objects of this class serve as prototypes for rows in queries. */
  class ActivityStreamRow(_tableTag: Tag) extends Table[ActivityStream](_tableTag, "activity_stream") {
    def * = (id, activity, info, subscriberId, subscriberKind, ts) <> (ActivityStream.tupled, ActivityStream.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, activity.?, info.?, subscriberId.?, subscriberKind.?, ts.?).shaped.<>({r=>import r._; _1.map(_=> ActivityStream.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(bigserial), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column ACTIVITY DBType(varchar), Length(160,true) */
    val activity: Column[String] = column[String]("ACTIVITY", O.Length(160,varying=true))
    /** Database column INFO DBType(varchar), Length(2147483647,true) */
    val info: Column[String] = column[String]("INFO", O.Length(2147483647,varying=true))
    /** Database column SUBSCRIBER_ID DBType(int8) */
    val subscriberId: Column[Long] = column[Long]("SUBSCRIBER_ID")
    /** Database column SUBSCRIBER_KIND DBType(int4) */
    val subscriberKind: Column[Int] = column[Int]("SUBSCRIBER_KIND")
    /** Database column TS DBType(timestamp) */
    val ts: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("TS")
  }
  /** Collection-like TableQuery object for table ActivityStream */
  lazy val ActivityStreams = new TableQuery(tag => new ActivityStreamRow(tag))
  
  /** Entity class storing rows of table ApiClient
   *  @param id Database column ID DBType(bigserial), AutoInc, PrimaryKey
   *  @param name Database column NAME DBType(varchar), Length(2147483647,true)
   *  @param desc Database column DESC DBType(varchar), Length(2147483647,true)
   *  @param creatorId Database column CREATOR_ID DBType(int8)
   *  @param creatorRole Database column CREATOR_ROLE DBType(int4)
   *  @param key Database column KEY DBType(varchar), Length(2147483647,true)
   *  @param suspended Database column SUSPENDED DBType(bool) */
  case class ApiClient(id: Long, name: String, desc: String, creatorId: Long, creatorRole: Int, key: String, suspended: Boolean)
  /** GetResult implicit for fetching ApiClient objects using plain SQL queries */
  implicit def GetResultApiClient(implicit e0: GR[Long], e1: GR[String], e2: GR[Int], e3: GR[Boolean]): GR[ApiClient] = GR{
    prs => import prs._
    ApiClient.tupled((<<[Long], <<[String], <<[String], <<[Long], <<[Int], <<[String], <<[Boolean]))
  }
  /** Table description of table api_client. Objects of this class serve as prototypes for rows in queries. */
  class ApiClientRow(_tableTag: Tag) extends Table[ApiClient](_tableTag, "api_client") {
    def * = (id, name, desc, creatorId, creatorRole, key, suspended) <> (ApiClient.tupled, ApiClient.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, desc.?, creatorId.?, creatorRole.?, key.?, suspended.?).shaped.<>({r=>import r._; _1.map(_=> ApiClient.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(bigserial), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column NAME DBType(varchar), Length(2147483647,true) */
    val name: Column[String] = column[String]("NAME", O.Length(2147483647,varying=true))
    /** Database column DESC DBType(varchar), Length(2147483647,true) */
    val desc: Column[String] = column[String]("DESC", O.Length(2147483647,varying=true))
    /** Database column CREATOR_ID DBType(int8) */
    val creatorId: Column[Long] = column[Long]("CREATOR_ID")
    /** Database column CREATOR_ROLE DBType(int4) */
    val creatorRole: Column[Int] = column[Int]("CREATOR_ROLE")
    /** Database column KEY DBType(varchar), Length(2147483647,true) */
    val key: Column[String] = column[String]("KEY", O.Length(2147483647,varying=true))
    /** Database column SUSPENDED DBType(bool) */
    val suspended: Column[Boolean] = column[Boolean]("SUSPENDED")
  }
  /** Collection-like TableQuery object for table ApiClient */
  lazy val ApiClients = new TableQuery(tag => new ApiClientRow(tag))
  
  /** Entity class storing rows of table ApiClientAccess
   *  @param id Database column ID DBType(bigserial), AutoInc, PrimaryKey
   *  @param apiClientId Database column API_CLIENT_ID DBType(int8)
   *  @param grant Database column GRANT DBType(varchar), Length(2147483647,true)
   *  @param target Database column TARGET DBType(varchar), Length(2147483647,true) */
  case class ApiClientAccess(id: Long, apiClientId: Long, grant: String, target: String)
  /** GetResult implicit for fetching ApiClientAccess objects using plain SQL queries */
  implicit def GetResultApiClientAccess(implicit e0: GR[Long], e1: GR[String]): GR[ApiClientAccess] = GR{
    prs => import prs._
    ApiClientAccess.tupled((<<[Long], <<[Long], <<[String], <<[String]))
  }
  /** Table description of table api_client_access. Objects of this class serve as prototypes for rows in queries. */
  class ApiClientAccessRow(_tableTag: Tag) extends Table[ApiClientAccess](_tableTag, "api_client_access") {
    def * = (id, apiClientId, grant, target) <> (ApiClientAccess.tupled, ApiClientAccess.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, apiClientId.?, grant.?, target.?).shaped.<>({r=>import r._; _1.map(_=> ApiClientAccess.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(bigserial), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column API_CLIENT_ID DBType(int8) */
    val apiClientId: Column[Long] = column[Long]("API_CLIENT_ID")
    /** Database column GRANT DBType(varchar), Length(2147483647,true) */
    val grant: Column[String] = column[String]("GRANT", O.Length(2147483647,varying=true))
    /** Database column TARGET DBType(varchar), Length(2147483647,true) */
    val target: Column[String] = column[String]("TARGET", O.Length(2147483647,varying=true))
  }
  /** Collection-like TableQuery object for table ApiClientAccess */
  lazy val ApiClientAccesses = new TableQuery(tag => new ApiClientAccessRow(tag))
  
  /** Entity class storing rows of table Business
   *  @param id Database column ID DBType(bigserial), AutoInc, PrimaryKey
   *  @param name Database column NAME DBType(varchar), Length(2147483647,true)
   *  @param desc Database column DESC DBType(varchar), Length(2147483647,true)
   *  @param tags Database column TAGS DBType(varchar), Length(2147483647,true)
   *  @param fund Database column FUND DBType(float8)
   *  @param share Database column SHARE DBType(float8)
   *  @param state Database column STATE DBType(int4)
   *  @param shareTime Database column SHARE_TIME DBType(int4)
   *  @param sharePeriod Database column SHARE_PERIOD DBType(int4)
   *  @param saving Database column SAVING DBType(float8)
   *  @param createdAt Database column CREATED_AT DBType(timestamp) */
  case class Business(id: Long, name: String, desc: String, tags: String, fund: Double, share: Double, state: Int, shareTime: Int, sharePeriod: Int, saving: Double, createdAt: java.sql.Timestamp)
  /** GetResult implicit for fetching Business objects using plain SQL queries */
  implicit def GetResultBusiness(implicit e0: GR[Long], e1: GR[String], e2: GR[Double], e3: GR[Int], e4: GR[java.sql.Timestamp]): GR[Business] = GR{
    prs => import prs._
    Business.tupled((<<[Long], <<[String], <<[String], <<[String], <<[Double], <<[Double], <<[Int], <<[Int], <<[Int], <<[Double], <<[java.sql.Timestamp]))
  }
  /** Table description of table business. Objects of this class serve as prototypes for rows in queries. */
  class BusinessRow(_tableTag: Tag) extends Table[Business](_tableTag, "business") {
    def * = (id, name, desc, tags, fund, share, state, shareTime, sharePeriod, saving, createdAt) <> (Business.tupled, Business.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, desc.?, tags.?, fund.?, share.?, state.?, shareTime.?, sharePeriod.?, saving.?, createdAt.?).shaped.<>({r=>import r._; _1.map(_=> Business.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(bigserial), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column NAME DBType(varchar), Length(2147483647,true) */
    val name: Column[String] = column[String]("NAME", O.Length(2147483647,varying=true))
    /** Database column DESC DBType(varchar), Length(2147483647,true) */
    val desc: Column[String] = column[String]("DESC", O.Length(2147483647,varying=true))
    /** Database column TAGS DBType(varchar), Length(2147483647,true) */
    val tags: Column[String] = column[String]("TAGS", O.Length(2147483647,varying=true))
    /** Database column FUND DBType(float8) */
    val fund: Column[Double] = column[Double]("FUND")
    /** Database column SHARE DBType(float8) */
    val share: Column[Double] = column[Double]("SHARE")
    /** Database column STATE DBType(int4) */
    val state: Column[Int] = column[Int]("STATE")
    /** Database column SHARE_TIME DBType(int4) */
    val shareTime: Column[Int] = column[Int]("SHARE_TIME")
    /** Database column SHARE_PERIOD DBType(int4) */
    val sharePeriod: Column[Int] = column[Int]("SHARE_PERIOD")
    /** Database column SAVING DBType(float8) */
    val saving: Column[Double] = column[Double]("SAVING")
    /** Database column CREATED_AT DBType(timestamp) */
    val createdAt: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("CREATED_AT")
  }
  /** Collection-like TableQuery object for table Business */
  lazy val Businesses = new TableQuery(tag => new BusinessRow(tag))
  
  /** Entity class storing rows of table BusinessAccountMutation
   *  @param id Database column ID DBType(bigserial), AutoInc, PrimaryKey
   *  @param busId Database column BUS_ID DBType(int8)
   *  @param kind Database column KIND DBType(int4)
   *  @param amount Database column AMOUNT DBType(float8)
   *  @param info Database column INFO DBType(varchar), Length(500,true)
   *  @param initiator Database column INITIATOR DBType(varchar), Length(100,true)
   *  @param ts Database column TS DBType(timestamp) */
  case class BusinessAccountMutation(id: Long, busId: Long, kind: Int, amount: Double, info: String, initiator: String, ts: java.sql.Timestamp)
  /** GetResult implicit for fetching BusinessAccountMutation objects using plain SQL queries */
  implicit def GetResultBusinessAccountMutation(implicit e0: GR[Long], e1: GR[Int], e2: GR[Double], e3: GR[String], e4: GR[java.sql.Timestamp]): GR[BusinessAccountMutation] = GR{
    prs => import prs._
    BusinessAccountMutation.tupled((<<[Long], <<[Long], <<[Int], <<[Double], <<[String], <<[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table business_account_mutation. Objects of this class serve as prototypes for rows in queries. */
  class BusinessAccountMutationRow(_tableTag: Tag) extends Table[BusinessAccountMutation](_tableTag, "business_account_mutation") {
    def * = (id, busId, kind, amount, info, initiator, ts) <> (BusinessAccountMutation.tupled, BusinessAccountMutation.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, busId.?, kind.?, amount.?, info.?, initiator.?, ts.?).shaped.<>({r=>import r._; _1.map(_=> BusinessAccountMutation.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(bigserial), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column BUS_ID DBType(int8) */
    val busId: Column[Long] = column[Long]("BUS_ID")
    /** Database column KIND DBType(int4) */
    val kind: Column[Int] = column[Int]("KIND")
    /** Database column AMOUNT DBType(float8) */
    val amount: Column[Double] = column[Double]("AMOUNT")
    /** Database column INFO DBType(varchar), Length(500,true) */
    val info: Column[String] = column[String]("INFO", O.Length(500,varying=true))
    /** Database column INITIATOR DBType(varchar), Length(100,true) */
    val initiator: Column[String] = column[String]("INITIATOR", O.Length(100,varying=true))
    /** Database column TS DBType(timestamp) */
    val ts: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("TS")
  }
  /** Collection-like TableQuery object for table BusinessAccountMutation */
  lazy val BusinessAccountMutations = new TableQuery(tag => new BusinessAccountMutationRow(tag))
  
  /** Entity class storing rows of table BusinessAsset
   *  @param id Database column ID DBType(bigserial), AutoInc, PrimaryKey
   *  @param busId Database column BUS_ID DBType(int8)
   *  @param content Database column CONTENT DBType(varchar), Length(2147483647,true)
   *  @param kind Database column KIND DBType(int4)
   *  @param creator Database column CREATOR DBType(int8)
   *  @param creatorRole Database column CREATOR_ROLE DBType(int4)
   *  @param ts Database column TS DBType(timestamp)
   *  @param lastUpdated Database column LAST_UPDATED DBType(timestamp) */
  case class BusinessAsset(id: Long, busId: Long, content: String, kind: Int, creator: Long, creatorRole: Int, ts: java.sql.Timestamp, lastUpdated: java.sql.Timestamp)
  /** GetResult implicit for fetching BusinessAsset objects using plain SQL queries */
  implicit def GetResultBusinessAsset(implicit e0: GR[Long], e1: GR[String], e2: GR[Int], e3: GR[java.sql.Timestamp]): GR[BusinessAsset] = GR{
    prs => import prs._
    BusinessAsset.tupled((<<[Long], <<[Long], <<[String], <<[Int], <<[Long], <<[Int], <<[java.sql.Timestamp], <<[java.sql.Timestamp]))
  }
  /** Table description of table business_asset. Objects of this class serve as prototypes for rows in queries. */
  class BusinessAssetRow(_tableTag: Tag) extends Table[BusinessAsset](_tableTag, "business_asset") {
    def * = (id, busId, content, kind, creator, creatorRole, ts, lastUpdated) <> (BusinessAsset.tupled, BusinessAsset.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, busId.?, content.?, kind.?, creator.?, creatorRole.?, ts.?, lastUpdated.?).shaped.<>({r=>import r._; _1.map(_=> BusinessAsset.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(bigserial), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column BUS_ID DBType(int8) */
    val busId: Column[Long] = column[Long]("BUS_ID")
    /** Database column CONTENT DBType(varchar), Length(2147483647,true) */
    val content: Column[String] = column[String]("CONTENT", O.Length(2147483647,varying=true))
    /** Database column KIND DBType(int4) */
    val kind: Column[Int] = column[Int]("KIND")
    /** Database column CREATOR DBType(int8) */
    val creator: Column[Long] = column[Long]("CREATOR")
    /** Database column CREATOR_ROLE DBType(int4) */
    val creatorRole: Column[Int] = column[Int]("CREATOR_ROLE")
    /** Database column TS DBType(timestamp) */
    val ts: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("TS")
    /** Database column LAST_UPDATED DBType(timestamp) */
    val lastUpdated: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("LAST_UPDATED")
  }
  /** Collection-like TableQuery object for table BusinessAsset */
  lazy val BusinessAssets = new TableQuery(tag => new BusinessAssetRow(tag))
  
  /** Entity class storing rows of table BusinessContact
   *  @param investorJobId Database column INVESTOR_JOB_ID DBType(int8)
   *  @param address Database column ADDRESS DBType(varchar), Length(2147483647,true)
   *  @param village Database column VILLAGE DBType(varchar), Length(2147483647,true)
   *  @param district Database column DISTRICT DBType(varchar), Length(2147483647,true)
   *  @param city Database column CITY DBType(varchar), Length(2147483647,true)
   *  @param province Database column PROVINCE DBType(varchar), Length(2147483647,true)
   *  @param country Database column COUNTRY DBType(varchar), Length(2147483647,true)
   *  @param postalCode Database column POSTAL_CODE DBType(varchar), Length(100,true)
   *  @param email Database column EMAIL DBType(varchar), Length(2147483647,true)
   *  @param officePhone Database column OFFICE_PHONE DBType(varchar), Length(120,true)
   *  @param mobilePhone Database column MOBILE_PHONE DBType(varchar), Length(120,true)
   *  @param bbPin Database column BB_PIN DBType(varchar), Length(20,true) */
  case class BusinessContact(investorJobId: Long, address: String, village: String, district: String, city: String, province: String, country: String, postalCode: String, email: String, officePhone: String, mobilePhone: String, bbPin: String)
  /** GetResult implicit for fetching BusinessContact objects using plain SQL queries */
  implicit def GetResultBusinessContact(implicit e0: GR[Long], e1: GR[String]): GR[BusinessContact] = GR{
    prs => import prs._
    BusinessContact.tupled((<<[Long], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table business_contact. Objects of this class serve as prototypes for rows in queries. */
  class BusinessContactRow(_tableTag: Tag) extends Table[BusinessContact](_tableTag, "business_contact") {
    def * = (investorJobId, address, village, district, city, province, country, postalCode, email, officePhone, mobilePhone, bbPin) <> (BusinessContact.tupled, BusinessContact.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (investorJobId.?, address.?, village.?, district.?, city.?, province.?, country.?, postalCode.?, email.?, officePhone.?, mobilePhone.?, bbPin.?).shaped.<>({r=>import r._; _1.map(_=> BusinessContact.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column INVESTOR_JOB_ID DBType(int8) */
    val investorJobId: Column[Long] = column[Long]("INVESTOR_JOB_ID")
    /** Database column ADDRESS DBType(varchar), Length(2147483647,true) */
    val address: Column[String] = column[String]("ADDRESS", O.Length(2147483647,varying=true))
    /** Database column VILLAGE DBType(varchar), Length(2147483647,true) */
    val village: Column[String] = column[String]("VILLAGE", O.Length(2147483647,varying=true))
    /** Database column DISTRICT DBType(varchar), Length(2147483647,true) */
    val district: Column[String] = column[String]("DISTRICT", O.Length(2147483647,varying=true))
    /** Database column CITY DBType(varchar), Length(2147483647,true) */
    val city: Column[String] = column[String]("CITY", O.Length(2147483647,varying=true))
    /** Database column PROVINCE DBType(varchar), Length(2147483647,true) */
    val province: Column[String] = column[String]("PROVINCE", O.Length(2147483647,varying=true))
    /** Database column COUNTRY DBType(varchar), Length(2147483647,true) */
    val country: Column[String] = column[String]("COUNTRY", O.Length(2147483647,varying=true))
    /** Database column POSTAL_CODE DBType(varchar), Length(100,true) */
    val postalCode: Column[String] = column[String]("POSTAL_CODE", O.Length(100,varying=true))
    /** Database column EMAIL DBType(varchar), Length(2147483647,true) */
    val email: Column[String] = column[String]("EMAIL", O.Length(2147483647,varying=true))
    /** Database column OFFICE_PHONE DBType(varchar), Length(120,true) */
    val officePhone: Column[String] = column[String]("OFFICE_PHONE", O.Length(120,varying=true))
    /** Database column MOBILE_PHONE DBType(varchar), Length(120,true) */
    val mobilePhone: Column[String] = column[String]("MOBILE_PHONE", O.Length(120,varying=true))
    /** Database column BB_PIN DBType(varchar), Length(20,true) */
    val bbPin: Column[String] = column[String]("BB_PIN", O.Length(20,varying=true))
    
    /** Foreign key referencing InvestorJob (database name business_contact_INVESTOR_JOB_ID_fkey) */
    lazy val investorJobFk = foreignKey("business_contact_INVESTOR_JOB_ID_fkey", investorJobId, InvestorJobs)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table BusinessContact */
  lazy val BusinessContacts = new TableQuery(tag => new BusinessContactRow(tag))
  
  /** Entity class storing rows of table BusinessGroup
   *  @param id Database column ID DBType(int8), PrimaryKey
   *  @param name Database column NAME DBType(varchar), Length(2147483647,true)
   *  @param desc Database column DESC DBType(varchar), Length(2147483647,true) */
  case class BusinessGroup(id: Long, name: String, desc: String)
  /** GetResult implicit for fetching BusinessGroup objects using plain SQL queries */
  implicit def GetResultBusinessGroup(implicit e0: GR[Long], e1: GR[String]): GR[BusinessGroup] = GR{
    prs => import prs._
    BusinessGroup.tupled((<<[Long], <<[String], <<[String]))
  }
  /** Table description of table business_group. Objects of this class serve as prototypes for rows in queries. */
  class BusinessGroupRow(_tableTag: Tag) extends Table[BusinessGroup](_tableTag, "business_group") {
    def * = (id, name, desc) <> (BusinessGroup.tupled, BusinessGroup.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, desc.?).shaped.<>({r=>import r._; _1.map(_=> BusinessGroup.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(int8), PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.PrimaryKey)
    /** Database column NAME DBType(varchar), Length(2147483647,true) */
    val name: Column[String] = column[String]("NAME", O.Length(2147483647,varying=true))
    /** Database column DESC DBType(varchar), Length(2147483647,true) */
    val desc: Column[String] = column[String]("DESC", O.Length(2147483647,varying=true))
  }
  /** Collection-like TableQuery object for table BusinessGroup */
  lazy val BusinessGroups = new TableQuery(tag => new BusinessGroupRow(tag))
  
  /** Entity class storing rows of table BusinessGroupLink
   *  @param id Database column ID DBType(int8), PrimaryKey
   *  @param busGroupId Database column BUS_GROUP_ID DBType(int8)
   *  @param busId Database column BUS_ID DBType(int8) */
  case class BusinessGroupLink(id: Long, busGroupId: Long, busId: Long)
  /** GetResult implicit for fetching BusinessGroupLink objects using plain SQL queries */
  implicit def GetResultBusinessGroupLink(implicit e0: GR[Long]): GR[BusinessGroupLink] = GR{
    prs => import prs._
    BusinessGroupLink.tupled((<<[Long], <<[Long], <<[Long]))
  }
  /** Table description of table business_group_link. Objects of this class serve as prototypes for rows in queries. */
  class BusinessGroupLinkRow(_tableTag: Tag) extends Table[BusinessGroupLink](_tableTag, "business_group_link") {
    def * = (id, busGroupId, busId) <> (BusinessGroupLink.tupled, BusinessGroupLink.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, busGroupId.?, busId.?).shaped.<>({r=>import r._; _1.map(_=> BusinessGroupLink.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(int8), PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.PrimaryKey)
    /** Database column BUS_GROUP_ID DBType(int8) */
    val busGroupId: Column[Long] = column[Long]("BUS_GROUP_ID")
    /** Database column BUS_ID DBType(int8) */
    val busId: Column[Long] = column[Long]("BUS_ID")
  }
  /** Collection-like TableQuery object for table BusinessGroupLink */
  lazy val BusinessGroupLinks = new TableQuery(tag => new BusinessGroupLinkRow(tag))
  
  /** Entity class storing rows of table BusinessProfit
   *  @param id Database column ID DBType(bigserial), AutoInc, PrimaryKey
   *  @param busId Database column BUS_ID DBType(int8)
   *  @param omzet Database column OMZET DBType(float8)
   *  @param profit Database column PROFIT DBType(float8)
   *  @param ts Database column TS DBType(timestamp)
   *  @param mutatorId Database column MUTATOR_ID DBType(int8)
   *  @param mutatorRole Database column MUTATOR_ROLE DBType(int4)
   *  @param info Database column INFO DBType(varchar), Length(2147483647,true)
   *  @param shared Database column SHARED DBType(bool), Default(false)
   *  @param sharedAt Database column SHARED_AT DBType(timestamp)
   *  @param status Database column STATUS DBType(int4), Default(0) */
  case class BusinessProfit(id: Long, busId: Long, omzet: Double, profit: Double, ts: java.sql.Timestamp, mutatorId: Long, mutatorRole: Int, info: String, shared: Boolean = false, sharedAt: java.sql.Timestamp, status: Int = 0)
  /** GetResult implicit for fetching BusinessProfit objects using plain SQL queries */
  implicit def GetResultBusinessProfit(implicit e0: GR[Long], e1: GR[Double], e2: GR[java.sql.Timestamp], e3: GR[Int], e4: GR[String], e5: GR[Boolean]): GR[BusinessProfit] = GR{
    prs => import prs._
    BusinessProfit.tupled((<<[Long], <<[Long], <<[Double], <<[Double], <<[java.sql.Timestamp], <<[Long], <<[Int], <<[String], <<[Boolean], <<[java.sql.Timestamp], <<[Int]))
  }
  /** Table description of table business_profit. Objects of this class serve as prototypes for rows in queries. */
  class BusinessProfitRow(_tableTag: Tag) extends Table[BusinessProfit](_tableTag, "business_profit") {
    def * = (id, busId, omzet, profit, ts, mutatorId, mutatorRole, info, shared, sharedAt, status) <> (BusinessProfit.tupled, BusinessProfit.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, busId.?, omzet.?, profit.?, ts.?, mutatorId.?, mutatorRole.?, info.?, shared.?, sharedAt.?, status.?).shaped.<>({r=>import r._; _1.map(_=> BusinessProfit.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(bigserial), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column BUS_ID DBType(int8) */
    val busId: Column[Long] = column[Long]("BUS_ID")
    /** Database column OMZET DBType(float8) */
    val omzet: Column[Double] = column[Double]("OMZET")
    /** Database column PROFIT DBType(float8) */
    val profit: Column[Double] = column[Double]("PROFIT")
    /** Database column TS DBType(timestamp) */
    val ts: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("TS")
    /** Database column MUTATOR_ID DBType(int8) */
    val mutatorId: Column[Long] = column[Long]("MUTATOR_ID")
    /** Database column MUTATOR_ROLE DBType(int4) */
    val mutatorRole: Column[Int] = column[Int]("MUTATOR_ROLE")
    /** Database column INFO DBType(varchar), Length(2147483647,true) */
    val info: Column[String] = column[String]("INFO", O.Length(2147483647,varying=true))
    /** Database column SHARED DBType(bool), Default(false) */
    val shared: Column[Boolean] = column[Boolean]("SHARED", O.Default(false))
    /** Database column SHARED_AT DBType(timestamp) */
    val sharedAt: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("SHARED_AT")
    /** Database column STATUS DBType(int4), Default(0) */
    val status: Column[Int] = column[Int]("STATUS", O.Default(0))
  }
  /** Collection-like TableQuery object for table BusinessProfit */
  lazy val BusinessProfits = new TableQuery(tag => new BusinessProfitRow(tag))
  
  /** Entity class storing rows of table Employee
   *  @param id Database column ID DBType(bigserial), AutoInc, PrimaryKey
   *  @param busId Database column BUS_ID DBType(int8)
   *  @param name Database column NAME DBType(varchar), Length(100,true)
   *  @param jobDesc Database column JOB_DESC DBType(varchar), Length(160,true)
   *  @param salary Database column SALARY DBType(float8)
   *  @param status Database column STATUS DBType(int4) */
  case class Employee(id: Long, busId: Long, name: String, jobDesc: String, salary: Double, status: Int)
  /** GetResult implicit for fetching Employee objects using plain SQL queries */
  implicit def GetResultEmployee(implicit e0: GR[Long], e1: GR[String], e2: GR[Double], e3: GR[Int]): GR[Employee] = GR{
    prs => import prs._
    Employee.tupled((<<[Long], <<[Long], <<[String], <<[String], <<[Double], <<[Int]))
  }
  /** Table description of table employee. Objects of this class serve as prototypes for rows in queries. */
  class EmployeeRow(_tableTag: Tag) extends Table[Employee](_tableTag, "employee") {
    def * = (id, busId, name, jobDesc, salary, status) <> (Employee.tupled, Employee.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, busId.?, name.?, jobDesc.?, salary.?, status.?).shaped.<>({r=>import r._; _1.map(_=> Employee.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(bigserial), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column BUS_ID DBType(int8) */
    val busId: Column[Long] = column[Long]("BUS_ID")
    /** Database column NAME DBType(varchar), Length(100,true) */
    val name: Column[String] = column[String]("NAME", O.Length(100,varying=true))
    /** Database column JOB_DESC DBType(varchar), Length(160,true) */
    val jobDesc: Column[String] = column[String]("JOB_DESC", O.Length(160,varying=true))
    /** Database column SALARY DBType(float8) */
    val salary: Column[Double] = column[Double]("SALARY")
    /** Database column STATUS DBType(int4) */
    val status: Column[Int] = column[Int]("STATUS")
  }
  /** Collection-like TableQuery object for table Employee */
  lazy val Employees = new TableQuery(tag => new EmployeeRow(tag))
  
  /** Entity class storing rows of table Investor
   *  @param id Database column ID DBType(bigserial), AutoInc, PrimaryKey
   *  @param name Database column NAME DBType(varchar), Length(2147483647,true)
   *  @param fullName Database column FULL_NAME DBType(varchar), Length(160,true)
   *  @param role Database column ROLE DBType(int4)
   *  @param sex Database column SEX DBType(int4)
   *  @param nation Database column NATION DBType(varchar), Length(100,true)
   *  @param birthPlace Database column BIRTH_PLACE DBType(varchar), Length(160,true)
   *  @param birthDate Database column BIRTH_DATE DBType(date)
   *  @param religion Database column RELIGION DBType(varchar), Length(100,true)
   *  @param education Database column EDUCATION DBType(varchar), Length(100,true)
   *  @param titleFront Database column TITLE_FRONT DBType(varchar), Length(20,true), Default()
   *  @param titleBack Database column TITLE_BACK DBType(varchar), Length(20,true), Default()
   *  @param maritalStatus Database column MARITAL_STATUS DBType(int4)
   *  @param motherName Database column MOTHER_NAME DBType(varchar), Length(160,true)
   *  @param passhash Database column PASSHASH DBType(varchar), Length(2147483647,true)
   *  @param status Database column STATUS DBType(int4), Default(1) */
  case class Investor(id: Long, name: String, fullName: String, role: Int, sex: Int, nation: String, birthPlace: String, birthDate: java.sql.Date, religion: String, education: String, titleFront: String = "", titleBack: String = "", maritalStatus: Int, motherName: String, passhash: String, status: Int = 1)
  /** GetResult implicit for fetching Investor objects using plain SQL queries */
  implicit def GetResultInvestor(implicit e0: GR[Long], e1: GR[String], e2: GR[Int], e3: GR[java.sql.Date]): GR[Investor] = GR{
    prs => import prs._
    Investor.tupled((<<[Long], <<[String], <<[String], <<[Int], <<[Int], <<[String], <<[String], <<[java.sql.Date], <<[String], <<[String], <<[String], <<[String], <<[Int], <<[String], <<[String], <<[Int]))
  }
  /** Table description of table investor. Objects of this class serve as prototypes for rows in queries. */
  class InvestorRow(_tableTag: Tag) extends Table[Investor](_tableTag, "investor") {
    def * = (id, name, fullName, role, sex, nation, birthPlace, birthDate, religion, education, titleFront, titleBack, maritalStatus, motherName, passhash, status) <> (Investor.tupled, Investor.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, fullName.?, role.?, sex.?, nation.?, birthPlace.?, birthDate.?, religion.?, education.?, titleFront.?, titleBack.?, maritalStatus.?, motherName.?, passhash.?, status.?).shaped.<>({r=>import r._; _1.map(_=> Investor.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(bigserial), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column NAME DBType(varchar), Length(2147483647,true) */
    val name: Column[String] = column[String]("NAME", O.Length(2147483647,varying=true))
    /** Database column FULL_NAME DBType(varchar), Length(160,true) */
    val fullName: Column[String] = column[String]("FULL_NAME", O.Length(160,varying=true))
    /** Database column ROLE DBType(int4) */
    val role: Column[Int] = column[Int]("ROLE")
    /** Database column SEX DBType(int4) */
    val sex: Column[Int] = column[Int]("SEX")
    /** Database column NATION DBType(varchar), Length(100,true) */
    val nation: Column[String] = column[String]("NATION", O.Length(100,varying=true))
    /** Database column BIRTH_PLACE DBType(varchar), Length(160,true) */
    val birthPlace: Column[String] = column[String]("BIRTH_PLACE", O.Length(160,varying=true))
    /** Database column BIRTH_DATE DBType(date) */
    val birthDate: Column[java.sql.Date] = column[java.sql.Date]("BIRTH_DATE")
    /** Database column RELIGION DBType(varchar), Length(100,true) */
    val religion: Column[String] = column[String]("RELIGION", O.Length(100,varying=true))
    /** Database column EDUCATION DBType(varchar), Length(100,true) */
    val education: Column[String] = column[String]("EDUCATION", O.Length(100,varying=true))
    /** Database column TITLE_FRONT DBType(varchar), Length(20,true), Default() */
    val titleFront: Column[String] = column[String]("TITLE_FRONT", O.Length(20,varying=true), O.Default(""))
    /** Database column TITLE_BACK DBType(varchar), Length(20,true), Default() */
    val titleBack: Column[String] = column[String]("TITLE_BACK", O.Length(20,varying=true), O.Default(""))
    /** Database column MARITAL_STATUS DBType(int4) */
    val maritalStatus: Column[Int] = column[Int]("MARITAL_STATUS")
    /** Database column MOTHER_NAME DBType(varchar), Length(160,true) */
    val motherName: Column[String] = column[String]("MOTHER_NAME", O.Length(160,varying=true))
    /** Database column PASSHASH DBType(varchar), Length(2147483647,true) */
    val passhash: Column[String] = column[String]("PASSHASH", O.Length(2147483647,varying=true))
    /** Database column STATUS DBType(int4), Default(1) */
    val status: Column[Int] = column[Int]("STATUS", O.Default(1))
    
    /** Uniqueness Index over (name) (database name investor_NAME_key) */
    val index1 = index("investor_NAME_key", name, unique=true)
  }
  /** Collection-like TableQuery object for table Investor */
  lazy val Investors = new TableQuery(tag => new InvestorRow(tag))
  
  /** Entity class storing rows of table InvestorBalance
   *  @param id Database column ID DBType(bigserial), AutoInc, PrimaryKey
   *  @param invId Database column INV_ID DBType(int8)
   *  @param amount Database column AMOUNT DBType(float8) */
  case class InvestorBalance(id: Long, invId: Long, amount: Double)
  /** GetResult implicit for fetching InvestorBalance objects using plain SQL queries */
  implicit def GetResultInvestorBalance(implicit e0: GR[Long], e1: GR[Double]): GR[InvestorBalance] = GR{
    prs => import prs._
    InvestorBalance.tupled((<<[Long], <<[Long], <<[Double]))
  }
  /** Table description of table investor_balance. Objects of this class serve as prototypes for rows in queries. */
  class InvestorBalanceRow(_tableTag: Tag) extends Table[InvestorBalance](_tableTag, "investor_balance") {
    def * = (id, invId, amount) <> (InvestorBalance.tupled, InvestorBalance.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, invId.?, amount.?).shaped.<>({r=>import r._; _1.map(_=> InvestorBalance.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(bigserial), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column INV_ID DBType(int8) */
    val invId: Column[Long] = column[Long]("INV_ID")
    /** Database column AMOUNT DBType(float8) */
    val amount: Column[Double] = column[Double]("AMOUNT")
  }
  /** Collection-like TableQuery object for table InvestorBalance */
  lazy val InvestorBalances = new TableQuery(tag => new InvestorBalanceRow(tag))
  
  /** Entity class storing rows of table InvestorContact
   *  @param investorId Database column INVESTOR_ID DBType(int8)
   *  @param address Database column ADDRESS DBType(varchar), Length(2147483647,true)
   *  @param village Database column VILLAGE DBType(varchar), Length(2147483647,true)
   *  @param district Database column DISTRICT DBType(varchar), Length(2147483647,true)
   *  @param city Database column CITY DBType(varchar), Length(2147483647,true)
   *  @param province Database column PROVINCE DBType(varchar), Length(2147483647,true)
   *  @param country Database column COUNTRY DBType(varchar), Length(2147483647,true)
   *  @param postalCode Database column POSTAL_CODE DBType(varchar), Length(100,true)
   *  @param email Database column EMAIL DBType(varchar), Length(2147483647,true)
   *  @param homePhone Database column HOME_PHONE DBType(varchar), Length(120,true)
   *  @param mobilePhone Database column MOBILE_PHONE DBType(varchar), Length(120,true)
   *  @param bbPin Database column BB_PIN DBType(varchar), Length(20,true)
   *  @param identityBasedOn Database column IDENTITY_BASED_ON DBType(int4)
   *  @param kind Database column kind DBType(int4) */
  case class InvestorContact(investorId: Long, address: String, village: String, district: String, city: String, province: String, country: String, postalCode: String, email: String, homePhone: String, mobilePhone: String, bbPin: String, identityBasedOn: Int, kind: Int)
  /** GetResult implicit for fetching InvestorContact objects using plain SQL queries */
  implicit def GetResultInvestorContact(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[InvestorContact] = GR{
    prs => import prs._
    InvestorContact.tupled((<<[Long], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[Int], <<[Int]))
  }
  /** Table description of table investor_contact. Objects of this class serve as prototypes for rows in queries. */
  class InvestorContactRow(_tableTag: Tag) extends Table[InvestorContact](_tableTag, "investor_contact") {
    def * = (investorId, address, village, district, city, province, country, postalCode, email, homePhone, mobilePhone, bbPin, identityBasedOn, kind) <> (InvestorContact.tupled, InvestorContact.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (investorId.?, address.?, village.?, district.?, city.?, province.?, country.?, postalCode.?, email.?, homePhone.?, mobilePhone.?, bbPin.?, identityBasedOn.?, kind.?).shaped.<>({r=>import r._; _1.map(_=> InvestorContact.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column INVESTOR_ID DBType(int8) */
    val investorId: Column[Long] = column[Long]("INVESTOR_ID")
    /** Database column ADDRESS DBType(varchar), Length(2147483647,true) */
    val address: Column[String] = column[String]("ADDRESS", O.Length(2147483647,varying=true))
    /** Database column VILLAGE DBType(varchar), Length(2147483647,true) */
    val village: Column[String] = column[String]("VILLAGE", O.Length(2147483647,varying=true))
    /** Database column DISTRICT DBType(varchar), Length(2147483647,true) */
    val district: Column[String] = column[String]("DISTRICT", O.Length(2147483647,varying=true))
    /** Database column CITY DBType(varchar), Length(2147483647,true) */
    val city: Column[String] = column[String]("CITY", O.Length(2147483647,varying=true))
    /** Database column PROVINCE DBType(varchar), Length(2147483647,true) */
    val province: Column[String] = column[String]("PROVINCE", O.Length(2147483647,varying=true))
    /** Database column COUNTRY DBType(varchar), Length(2147483647,true) */
    val country: Column[String] = column[String]("COUNTRY", O.Length(2147483647,varying=true))
    /** Database column POSTAL_CODE DBType(varchar), Length(100,true) */
    val postalCode: Column[String] = column[String]("POSTAL_CODE", O.Length(100,varying=true))
    /** Database column EMAIL DBType(varchar), Length(2147483647,true) */
    val email: Column[String] = column[String]("EMAIL", O.Length(2147483647,varying=true))
    /** Database column HOME_PHONE DBType(varchar), Length(120,true) */
    val homePhone: Column[String] = column[String]("HOME_PHONE", O.Length(120,varying=true))
    /** Database column MOBILE_PHONE DBType(varchar), Length(120,true) */
    val mobilePhone: Column[String] = column[String]("MOBILE_PHONE", O.Length(120,varying=true))
    /** Database column BB_PIN DBType(varchar), Length(20,true) */
    val bbPin: Column[String] = column[String]("BB_PIN", O.Length(20,varying=true))
    /** Database column IDENTITY_BASED_ON DBType(int4) */
    val identityBasedOn: Column[Int] = column[Int]("IDENTITY_BASED_ON")
    /** Database column kind DBType(int4) */
    val kind: Column[Int] = column[Int]("kind")
    
    /** Foreign key referencing Investor (database name investor_contact_INVESTOR_ID_fkey) */
    lazy val investorFk = foreignKey("investor_contact_INVESTOR_ID_fkey", investorId, Investors)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table InvestorContact */
  lazy val InvestorContacts = new TableQuery(tag => new InvestorContactRow(tag))
  
  /** Entity class storing rows of table InvestorHeir
   *  @param investorId Database column INVESTOR_ID DBType(int8)
   *  @param fullName Database column FULL_NAME DBType(varchar), Length(160,true)
   *  @param relationship Database column RELATIONSHIP DBType(varchar), Length(100,true) */
  case class InvestorHeir(investorId: Long, fullName: String, relationship: String)
  /** GetResult implicit for fetching InvestorHeir objects using plain SQL queries */
  implicit def GetResultInvestorHeir(implicit e0: GR[Long], e1: GR[String]): GR[InvestorHeir] = GR{
    prs => import prs._
    InvestorHeir.tupled((<<[Long], <<[String], <<[String]))
  }
  /** Table description of table investor_heir. Objects of this class serve as prototypes for rows in queries. */
  class InvestorHeirRow(_tableTag: Tag) extends Table[InvestorHeir](_tableTag, "investor_heir") {
    def * = (investorId, fullName, relationship) <> (InvestorHeir.tupled, InvestorHeir.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (investorId.?, fullName.?, relationship.?).shaped.<>({r=>import r._; _1.map(_=> InvestorHeir.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column INVESTOR_ID DBType(int8) */
    val investorId: Column[Long] = column[Long]("INVESTOR_ID")
    /** Database column FULL_NAME DBType(varchar), Length(160,true) */
    val fullName: Column[String] = column[String]("FULL_NAME", O.Length(160,varying=true))
    /** Database column RELATIONSHIP DBType(varchar), Length(100,true) */
    val relationship: Column[String] = column[String]("RELATIONSHIP", O.Length(100,varying=true))
    
    /** Foreign key referencing Investor (database name investor_heir_INVESTOR_ID_fkey) */
    lazy val investorFk = foreignKey("investor_heir_INVESTOR_ID_fkey", investorId, Investors)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table InvestorHeir */
  lazy val InvestorHeirs = new TableQuery(tag => new InvestorHeirRow(tag))
  
  /** Entity class storing rows of table InvestorJob
   *  @param id Database column ID DBType(bigserial), AutoInc, PrimaryKey
   *  @param investorId Database column INVESTOR_ID DBType(int8)
   *  @param kind Database column kind DBType(varchar), Length(2147483647,true)
   *  @param entityName Database column entity_name DBType(varchar), Length(2147483647,true)
   *  @param businessLine Database column business_line DBType(varchar), Length(2147483647,true) */
  case class InvestorJob(id: Long, investorId: Long, kind: String, entityName: String, businessLine: String)
  /** GetResult implicit for fetching InvestorJob objects using plain SQL queries */
  implicit def GetResultInvestorJob(implicit e0: GR[Long], e1: GR[String]): GR[InvestorJob] = GR{
    prs => import prs._
    InvestorJob.tupled((<<[Long], <<[Long], <<[String], <<[String], <<[String]))
  }
  /** Table description of table investor_job. Objects of this class serve as prototypes for rows in queries. */
  class InvestorJobRow(_tableTag: Tag) extends Table[InvestorJob](_tableTag, "investor_job") {
    def * = (id, investorId, kind, entityName, businessLine) <> (InvestorJob.tupled, InvestorJob.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, investorId.?, kind.?, entityName.?, businessLine.?).shaped.<>({r=>import r._; _1.map(_=> InvestorJob.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(bigserial), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column INVESTOR_ID DBType(int8) */
    val investorId: Column[Long] = column[Long]("INVESTOR_ID")
    /** Database column kind DBType(varchar), Length(2147483647,true) */
    val kind: Column[String] = column[String]("kind", O.Length(2147483647,varying=true))
    /** Database column entity_name DBType(varchar), Length(2147483647,true) */
    val entityName: Column[String] = column[String]("entity_name", O.Length(2147483647,varying=true))
    /** Database column business_line DBType(varchar), Length(2147483647,true) */
    val businessLine: Column[String] = column[String]("business_line", O.Length(2147483647,varying=true))
    
    /** Foreign key referencing Investor (database name investor_job_INVESTOR_ID_fkey) */
    lazy val investorFk = foreignKey("investor_job_INVESTOR_ID_fkey", investorId, Investors)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table InvestorJob */
  lazy val InvestorJobs = new TableQuery(tag => new InvestorJobRow(tag))
  
  /** Entity class storing rows of table InvestorLegalIdentity
   *  @param investorId Database column INVESTOR_ID DBType(int8)
   *  @param kind Database column KIND DBType(varchar), Length(100,true)
   *  @param identity Database column IDENTITY DBType(varchar), Length(2147483647,true)
   *  @param validUntil Database column VALID_UNTIL DBType(date), Default(None) */
  case class InvestorLegalIdentity(investorId: Long, kind: String, identity: String, validUntil: Option[java.sql.Date] = None)
  /** GetResult implicit for fetching InvestorLegalIdentity objects using plain SQL queries */
  implicit def GetResultInvestorLegalIdentity(implicit e0: GR[Long], e1: GR[String], e2: GR[Option[java.sql.Date]]): GR[InvestorLegalIdentity] = GR{
    prs => import prs._
    InvestorLegalIdentity.tupled((<<[Long], <<[String], <<[String], <<?[java.sql.Date]))
  }
  /** Table description of table investor_legal_identity. Objects of this class serve as prototypes for rows in queries. */
  class InvestorLegalIdentityRow(_tableTag: Tag) extends Table[InvestorLegalIdentity](_tableTag, "investor_legal_identity") {
    def * = (investorId, kind, identity, validUntil) <> (InvestorLegalIdentity.tupled, InvestorLegalIdentity.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (investorId.?, kind.?, identity.?, validUntil).shaped.<>({r=>import r._; _1.map(_=> InvestorLegalIdentity.tupled((_1.get, _2.get, _3.get, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column INVESTOR_ID DBType(int8) */
    val investorId: Column[Long] = column[Long]("INVESTOR_ID")
    /** Database column KIND DBType(varchar), Length(100,true) */
    val kind: Column[String] = column[String]("KIND", O.Length(100,varying=true))
    /** Database column IDENTITY DBType(varchar), Length(2147483647,true) */
    val identity: Column[String] = column[String]("IDENTITY", O.Length(2147483647,varying=true))
    /** Database column VALID_UNTIL DBType(date), Default(None) */
    val validUntil: Column[Option[java.sql.Date]] = column[Option[java.sql.Date]]("VALID_UNTIL", O.Default(None))
    
    /** Foreign key referencing Investor (database name investor_legal_identity_INVESTOR_ID_fkey) */
    lazy val investorFk = foreignKey("investor_legal_identity_INVESTOR_ID_fkey", investorId, Investors)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table InvestorLegalIdentity */
  lazy val InvestorLegalIdentitys = new TableQuery(tag => new InvestorLegalIdentityRow(tag))
  
  /** Entity class storing rows of table InvestorOtherContact
   *  @param investorId Database column INVESTOR_ID DBType(int8)
   *  @param kind Database column KIND DBType(varchar), Length(100,true)
   *  @param identity Database column IDENTITY DBType(varchar), Length(2147483647,true) */
  case class InvestorOtherContact(investorId: Long, kind: String, identity: String)
  /** GetResult implicit for fetching InvestorOtherContact objects using plain SQL queries */
  implicit def GetResultInvestorOtherContact(implicit e0: GR[Long], e1: GR[String]): GR[InvestorOtherContact] = GR{
    prs => import prs._
    InvestorOtherContact.tupled((<<[Long], <<[String], <<[String]))
  }
  /** Table description of table investor_other_contact. Objects of this class serve as prototypes for rows in queries. */
  class InvestorOtherContactRow(_tableTag: Tag) extends Table[InvestorOtherContact](_tableTag, "investor_other_contact") {
    def * = (investorId, kind, identity) <> (InvestorOtherContact.tupled, InvestorOtherContact.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (investorId.?, kind.?, identity.?).shaped.<>({r=>import r._; _1.map(_=> InvestorOtherContact.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column INVESTOR_ID DBType(int8) */
    val investorId: Column[Long] = column[Long]("INVESTOR_ID")
    /** Database column KIND DBType(varchar), Length(100,true) */
    val kind: Column[String] = column[String]("KIND", O.Length(100,varying=true))
    /** Database column IDENTITY DBType(varchar), Length(2147483647,true) */
    val identity: Column[String] = column[String]("IDENTITY", O.Length(2147483647,varying=true))
    
    /** Foreign key referencing Investor (database name investor_other_contact_INVESTOR_ID_fkey) */
    lazy val investorFk = foreignKey("investor_other_contact_INVESTOR_ID_fkey", investorId, Investors)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table InvestorOtherContact */
  lazy val InvestorOtherContacts = new TableQuery(tag => new InvestorOtherContactRow(tag))
  
  /** Entity class storing rows of table InvestorStock
   *  @param id Database column id DBType(bigserial), AutoInc, PrimaryKey
   *  @param investorOwnerId Database column investor_owner_id DBType(int8)
   *  @param businessId Database column business_id DBType(int8), Default(1)
   *  @param price Database column price DBType(float8)
   *  @param buyPurpose Database column buy_purpose DBType(varchar), Length(2147483647,true)
   *  @param fundSource Database column fund_source DBType(varchar), Length(2147483647,true)
   *  @param creationTime Database column creation_time DBType(timestamp)
   *  @param processed Database column processed DBType(bool), Default(false)
   *  @param processedByUserId Database column processed_by_user_id DBType(int8), Default(1)
   *  @param accepted Database column accepted DBType(bool), Default(false)
   *  @param acceptedByUserId Database column accepted_by_user_id DBType(int8), Default(1) */
  case class InvestorStock(id: Long, investorOwnerId: Long, businessId: Long = 1L, price: Double, buyPurpose: String, fundSource: String, creationTime: java.sql.Timestamp, processed: Boolean = false, processedByUserId: Long = 1L, accepted: Boolean = false, acceptedByUserId: Long = 1L)
  /** GetResult implicit for fetching InvestorStock objects using plain SQL queries */
  implicit def GetResultInvestorStock(implicit e0: GR[Long], e1: GR[Double], e2: GR[String], e3: GR[java.sql.Timestamp], e4: GR[Boolean]): GR[InvestorStock] = GR{
    prs => import prs._
    InvestorStock.tupled((<<[Long], <<[Long], <<[Long], <<[Double], <<[String], <<[String], <<[java.sql.Timestamp], <<[Boolean], <<[Long], <<[Boolean], <<[Long]))
  }
  /** Table description of table investor_stock. Objects of this class serve as prototypes for rows in queries. */
  class InvestorStockRow(_tableTag: Tag) extends Table[InvestorStock](_tableTag, "investor_stock") {
    def * = (id, investorOwnerId, businessId, price, buyPurpose, fundSource, creationTime, processed, processedByUserId, accepted, acceptedByUserId) <> (InvestorStock.tupled, InvestorStock.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, investorOwnerId.?, businessId.?, price.?, buyPurpose.?, fundSource.?, creationTime.?, processed.?, processedByUserId.?, accepted.?, acceptedByUserId.?).shaped.<>({r=>import r._; _1.map(_=> InvestorStock.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id DBType(bigserial), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column investor_owner_id DBType(int8) */
    val investorOwnerId: Column[Long] = column[Long]("investor_owner_id")
    /** Database column business_id DBType(int8), Default(1) */
    val businessId: Column[Long] = column[Long]("business_id", O.Default(1L))
    /** Database column price DBType(float8) */
    val price: Column[Double] = column[Double]("price")
    /** Database column buy_purpose DBType(varchar), Length(2147483647,true) */
    val buyPurpose: Column[String] = column[String]("buy_purpose", O.Length(2147483647,varying=true))
    /** Database column fund_source DBType(varchar), Length(2147483647,true) */
    val fundSource: Column[String] = column[String]("fund_source", O.Length(2147483647,varying=true))
    /** Database column creation_time DBType(timestamp) */
    val creationTime: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("creation_time")
    /** Database column processed DBType(bool), Default(false) */
    val processed: Column[Boolean] = column[Boolean]("processed", O.Default(false))
    /** Database column processed_by_user_id DBType(int8), Default(1) */
    val processedByUserId: Column[Long] = column[Long]("processed_by_user_id", O.Default(1L))
    /** Database column accepted DBType(bool), Default(false) */
    val accepted: Column[Boolean] = column[Boolean]("accepted", O.Default(false))
    /** Database column accepted_by_user_id DBType(int8), Default(1) */
    val acceptedByUserId: Column[Long] = column[Long]("accepted_by_user_id", O.Default(1L))
    
    /** Foreign key referencing Business (database name investor_stock_business_id_fkey) */
    lazy val businessFk = foreignKey("investor_stock_business_id_fkey", businessId, Businesses)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.SetDefault)
    /** Foreign key referencing Investor (database name investor_stock_investor_owner_id_fkey) */
    lazy val investorFk = foreignKey("investor_stock_investor_owner_id_fkey", investorOwnerId, Investors)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    /** Foreign key referencing User (database name investor_stock_accepted_by_user_id_fkey) */
    lazy val userFk3 = foreignKey("investor_stock_accepted_by_user_id_fkey", acceptedByUserId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.SetDefault)
    /** Foreign key referencing User (database name investor_stock_processed_by_user_id_fkey) */
    lazy val userFk4 = foreignKey("investor_stock_processed_by_user_id_fkey", processedByUserId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.SetDefault)
  }
  /** Collection-like TableQuery object for table InvestorStock */
  lazy val InvestorStocks = new TableQuery(tag => new InvestorStockRow(tag))
  
  /** Entity class storing rows of table Mutation
   *  @param id Database column ID DBType(bigserial), AutoInc, PrimaryKey
   *  @param invId Database column INV_ID DBType(int8)
   *  @param kind Database column KIND DBType(int4)
   *  @param amount Database column AMOUNT DBType(float8)
   *  @param ref Database column REF DBType(varchar), Length(2147483647,true), Default(None)
   *  @param initiator Database column INITIATOR DBType(varchar), Length(100,true), Default(Some())
   *  @param ts Database column TS DBType(timestamp) */
  case class Mutation(id: Long, invId: Long, kind: Int, amount: Double, ref: Option[String] = None, initiator: Option[String] = Some(""), ts: java.sql.Timestamp)
  /** GetResult implicit for fetching Mutation objects using plain SQL queries */
  implicit def GetResultMutation(implicit e0: GR[Long], e1: GR[Int], e2: GR[Double], e3: GR[Option[String]], e4: GR[java.sql.Timestamp]): GR[Mutation] = GR{
    prs => import prs._
    Mutation.tupled((<<[Long], <<[Long], <<[Int], <<[Double], <<?[String], <<?[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table mutation. Objects of this class serve as prototypes for rows in queries. */
  class MutationRow(_tableTag: Tag) extends Table[Mutation](_tableTag, "mutation") {
    def * = (id, invId, kind, amount, ref, initiator, ts) <> (Mutation.tupled, Mutation.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, invId.?, kind.?, amount.?, ref, initiator, ts.?).shaped.<>({r=>import r._; _1.map(_=> Mutation.tupled((_1.get, _2.get, _3.get, _4.get, _5, _6, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(bigserial), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column INV_ID DBType(int8) */
    val invId: Column[Long] = column[Long]("INV_ID")
    /** Database column KIND DBType(int4) */
    val kind: Column[Int] = column[Int]("KIND")
    /** Database column AMOUNT DBType(float8) */
    val amount: Column[Double] = column[Double]("AMOUNT")
    /** Database column REF DBType(varchar), Length(2147483647,true), Default(None) */
    val ref: Column[Option[String]] = column[Option[String]]("REF", O.Length(2147483647,varying=true), O.Default(None))
    /** Database column INITIATOR DBType(varchar), Length(100,true), Default(Some()) */
    val initiator: Column[Option[String]] = column[Option[String]]("INITIATOR", O.Length(100,varying=true), O.Default(Some("")))
    /** Database column TS DBType(timestamp) */
    val ts: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("TS")
  }
  /** Collection-like TableQuery object for table Mutation */
  lazy val Mutations = new TableQuery(tag => new MutationRow(tag))
  
  /** Entity class storing rows of table ProfitShareJournal
   *  @param busId Database column BUS_ID DBType(int8)
   *  @param busProfId Database column BUS_PROF_ID DBType(int8)
   *  @param invId Database column INV_ID DBType(int8)
   *  @param amount Database column AMOUNT DBType(float8)
   *  @param shareMethod Database column SHARE_METHOD DBType(int4), Default(2)
   *  @param initiator Database column INITIATOR DBType(varchar), Length(100,true), Default(Some())
   *  @param ts Database column TS DBType(timestamp) */
  case class ProfitShareJournal(busId: Long, busProfId: Long, invId: Long, amount: Double, shareMethod: Int = 2, initiator: Option[String] = Some(""), ts: java.sql.Timestamp)
  /** GetResult implicit for fetching ProfitShareJournal objects using plain SQL queries */
  implicit def GetResultProfitShareJournal(implicit e0: GR[Long], e1: GR[Double], e2: GR[Int], e3: GR[Option[String]], e4: GR[java.sql.Timestamp]): GR[ProfitShareJournal] = GR{
    prs => import prs._
    ProfitShareJournal.tupled((<<[Long], <<[Long], <<[Long], <<[Double], <<[Int], <<?[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table profit_share_journal. Objects of this class serve as prototypes for rows in queries. */
  class ProfitShareJournalRow(_tableTag: Tag) extends Table[ProfitShareJournal](_tableTag, "profit_share_journal") {
    def * = (busId, busProfId, invId, amount, shareMethod, initiator, ts) <> (ProfitShareJournal.tupled, ProfitShareJournal.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (busId.?, busProfId.?, invId.?, amount.?, shareMethod.?, initiator, ts.?).shaped.<>({r=>import r._; _1.map(_=> ProfitShareJournal.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column BUS_ID DBType(int8) */
    val busId: Column[Long] = column[Long]("BUS_ID")
    /** Database column BUS_PROF_ID DBType(int8) */
    val busProfId: Column[Long] = column[Long]("BUS_PROF_ID")
    /** Database column INV_ID DBType(int8) */
    val invId: Column[Long] = column[Long]("INV_ID")
    /** Database column AMOUNT DBType(float8) */
    val amount: Column[Double] = column[Double]("AMOUNT")
    /** Database column SHARE_METHOD DBType(int4), Default(2) */
    val shareMethod: Column[Int] = column[Int]("SHARE_METHOD", O.Default(2))
    /** Database column INITIATOR DBType(varchar), Length(100,true), Default(Some()) */
    val initiator: Column[Option[String]] = column[Option[String]]("INITIATOR", O.Length(100,varying=true), O.Default(Some("")))
    /** Database column TS DBType(timestamp) */
    val ts: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("TS")
  }
  /** Collection-like TableQuery object for table ProfitShareJournal */
  lazy val ProfitShareJournals = new TableQuery(tag => new ProfitShareJournalRow(tag))
  
  /** Entity class storing rows of table ProjectReport
   *  @param id Database column ID DBType(bigserial), AutoInc, PrimaryKey
   *  @param busId Database column BUS_ID DBType(int8)
   *  @param info Database column INFO DBType(varchar), Length(2147483647,true)
   *  @param percentage Database column PERCENTAGE DBType(float8)
   *  @param initiator Database column INITIATOR DBType(varchar), Length(2147483647,true)
   *  @param ts Database column TS DBType(timestamp) */
  case class ProjectReport(id: Long, busId: Long, info: String, percentage: Double, initiator: String, ts: java.sql.Timestamp)
  /** GetResult implicit for fetching ProjectReport objects using plain SQL queries */
  implicit def GetResultProjectReport(implicit e0: GR[Long], e1: GR[String], e2: GR[Double], e3: GR[java.sql.Timestamp]): GR[ProjectReport] = GR{
    prs => import prs._
    ProjectReport.tupled((<<[Long], <<[Long], <<[String], <<[Double], <<[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table project_report. Objects of this class serve as prototypes for rows in queries. */
  class ProjectReportRow(_tableTag: Tag) extends Table[ProjectReport](_tableTag, "project_report") {
    def * = (id, busId, info, percentage, initiator, ts) <> (ProjectReport.tupled, ProjectReport.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, busId.?, info.?, percentage.?, initiator.?, ts.?).shaped.<>({r=>import r._; _1.map(_=> ProjectReport.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(bigserial), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column BUS_ID DBType(int8) */
    val busId: Column[Long] = column[Long]("BUS_ID")
    /** Database column INFO DBType(varchar), Length(2147483647,true) */
    val info: Column[String] = column[String]("INFO", O.Length(2147483647,varying=true))
    /** Database column PERCENTAGE DBType(float8) */
    val percentage: Column[Double] = column[Double]("PERCENTAGE")
    /** Database column INITIATOR DBType(varchar), Length(2147483647,true) */
    val initiator: Column[String] = column[String]("INITIATOR", O.Length(2147483647,varying=true))
    /** Database column TS DBType(timestamp) */
    val ts: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("TS")
  }
  /** Collection-like TableQuery object for table ProjectReport */
  lazy val ProjectReports = new TableQuery(tag => new ProjectReportRow(tag))
  
  /** Entity class storing rows of table ProjectWatcher
   *  @param id Database column ID DBType(bigserial), AutoInc, PrimaryKey
   *  @param invId Database column INV_ID DBType(int8)
   *  @param busId Database column BUS_ID DBType(int8) */
  case class ProjectWatcher(id: Long, invId: Long, busId: Long)
  /** GetResult implicit for fetching ProjectWatcher objects using plain SQL queries */
  implicit def GetResultProjectWatcher(implicit e0: GR[Long]): GR[ProjectWatcher] = GR{
    prs => import prs._
    ProjectWatcher.tupled((<<[Long], <<[Long], <<[Long]))
  }
  /** Table description of table project_watcher. Objects of this class serve as prototypes for rows in queries. */
  class ProjectWatcherRow(_tableTag: Tag) extends Table[ProjectWatcher](_tableTag, "project_watcher") {
    def * = (id, invId, busId) <> (ProjectWatcher.tupled, ProjectWatcher.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, invId.?, busId.?).shaped.<>({r=>import r._; _1.map(_=> ProjectWatcher.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(bigserial), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column INV_ID DBType(int8) */
    val invId: Column[Long] = column[Long]("INV_ID")
    /** Database column BUS_ID DBType(int8) */
    val busId: Column[Long] = column[Long]("BUS_ID")
  }
  /** Collection-like TableQuery object for table ProjectWatcher */
  lazy val ProjectWatchers = new TableQuery(tag => new ProjectWatcherRow(tag))
  
  /** Entity class storing rows of table User
   *  @param id Database column ID DBType(bigserial), AutoInc, PrimaryKey
   *  @param name Database column NAME DBType(varchar), Length(2147483647,true)
   *  @param email Database column EMAIL DBType(varchar), Length(2147483647,true)
   *  @param phone Database column PHONE DBType(varchar), Length(2147483647,true)
   *  @param passhash Database column PASSHASH DBType(varchar), Length(2147483647,true)
   *  @param createdAt Database column CREATED_AT DBType(timestamp)
   *  @param status Database column STATUS DBType(int4)
   *  @param role Database column ROLE DBType(int4) */
  case class User(id: Long, name: String, email: String, phone: String, passhash: String, createdAt: java.sql.Timestamp, status: Int, role: Int)
  /** GetResult implicit for fetching User objects using plain SQL queries */
  implicit def GetResultUser(implicit e0: GR[Long], e1: GR[String], e2: GR[java.sql.Timestamp], e3: GR[Int]): GR[User] = GR{
    prs => import prs._
    User.tupled((<<[Long], <<[String], <<[String], <<[String], <<[String], <<[java.sql.Timestamp], <<[Int], <<[Int]))
  }
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class UserRow(_tableTag: Tag) extends Table[User](_tableTag, "user") {
    def * = (id, name, email, phone, passhash, createdAt, status, role) <> (User.tupled, User.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, email.?, phone.?, passhash.?, createdAt.?, status.?, role.?).shaped.<>({r=>import r._; _1.map(_=> User.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID DBType(bigserial), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column NAME DBType(varchar), Length(2147483647,true) */
    val name: Column[String] = column[String]("NAME", O.Length(2147483647,varying=true))
    /** Database column EMAIL DBType(varchar), Length(2147483647,true) */
    val email: Column[String] = column[String]("EMAIL", O.Length(2147483647,varying=true))
    /** Database column PHONE DBType(varchar), Length(2147483647,true) */
    val phone: Column[String] = column[String]("PHONE", O.Length(2147483647,varying=true))
    /** Database column PASSHASH DBType(varchar), Length(2147483647,true) */
    val passhash: Column[String] = column[String]("PASSHASH", O.Length(2147483647,varying=true))
    /** Database column CREATED_AT DBType(timestamp) */
    val createdAt: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("CREATED_AT")
    /** Database column STATUS DBType(int4) */
    val status: Column[Int] = column[Int]("STATUS")
    /** Database column ROLE DBType(int4) */
    val role: Column[Int] = column[Int]("ROLE")
    
    /** Uniqueness Index over (name) (database name user_NAME_key) */
    val index1 = index("user_NAME_key", name, unique=true)
  }
  /** Collection-like TableQuery object for table User */
  lazy val Users = new TableQuery(tag => new UserRow(tag))
}