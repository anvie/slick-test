package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import com.ansvia.zufaro.model.Tables._

object Zufaro {

    val db = Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver")

    def main(args:Array[String]){

        db withSession {
            implicit session =>

                (Business.ddl ++ User.ddl ++ Invest.ddl).create

                User += UserRow(1, "robin", 0)
                User += UserRow(2, "gondez", 1)
                User += UserRow(3, "temon", 2)

//                User.users.foreach { case (id, name, role) =>
//                    println(s" * $id - $name ($role)")
//                }

                User.foreach { case UserRow(id, name, role) =>
                    println(s" * $id - $name ($role)")
                }

                Business += BusinessRow(4, "Anu", "anu kae", 70.0, 30.0)
                Business += BusinessRow(5, "Kae", "anu kae", 60.0, 40.0)

                Invest += InvestRow(0, 2, 4, 100)
                Invest += InvestRow(0, 2, 5, 150)
                Invest += InvestRow(0, 1, 5, 10)
                Invest += InvestRow(0, 3, 4, 7)

                val investors = for {
                    ((u, v), b) <- User.innerJoin(Invest).innerJoin(Business).on {
                        case ((_u, _v), _b) => _u.id === _v.userId && _b.id === _v.busId
                    }
                } yield (u.name, v.amount, b.name, v.id)


                println("\n--------------------------")

                investors.sortBy(_._1.asc).foreach { case (userName, investAmount, busName, investId) =>
//                    Business.filter(_.id === invest.busId).foreach { case (business) =>
                        println(s" * ${userName} invest Rp.${investAmount}jt in ${busName} (${investId})")
//                    }
                }


        }

    }

}
