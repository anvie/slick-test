//package com.ansvia.zufaro.model
//
//import scala.slick.driver.PostgresDriver.simple._
//import scala.slick.model.QualifiedName
//import scala.slick.lifted.ProvenShape
//
///**
// * Author: robin
// * Date: 8/3/14
// * Time: 3:25 PM
// *
// */
//
//
//class User(tag:Tag) extends Table[(Int, String, Int)](tag, "USERS"){
//
//    def id = column[Int]("ID", O.PrimaryKey)
//    def name = column[String]("NAME")
//    def role = column[Int]("ROLE")
//
//    def * : ProvenShape[(Int, String, Int)] = (id, name, role)
//}
//
//object Users {
//    val users = TableQuery[User]
//}
//
//
//class Business(tag:Tag) extends Table[(Int, String, String)](tag, "BUSINESS"){
//
//    def id = column[Int]("ID", O.PrimaryKey)
//    def name = column[String]("NAME")
//    def desc = column[String]("DESC")
//
//    def * : ProvenShape[(Int, String, String)] = (id, name, desc)
//}
//
