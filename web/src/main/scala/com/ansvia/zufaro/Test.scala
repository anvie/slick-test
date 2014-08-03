package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import com.ansvia.zufaro.model.Tables._

object Test {

    import UserManager.implicits._

    def main(args:Array[String]){

        com.ansvia.zufaro.Zufaro.db withSession {
            implicit session =>

                (Business.ddl ++ User.ddl ++ Invest.ddl).create

                User += UserRow(0, "robin", 0)
                User += UserRow(0, "gondez", 1)
                User += UserRow(0, "temon", 2)

//                User.users.foreach { case (id, name, role) =>
//                    println(s" * $id - $name ($role)")
//                }

                User.foreach { case UserRow(id, name, role) =>
                    println(s" * $id - $name ($role)")
                }

                Business += BusinessRow(0, "Anu", "anu kae", 70.0, 30.0)
                Business += BusinessRow(0, "Kae", "anu kae", 60.0, 40.0)

                val robin = UserManager.getByName("robin").get
                val gondez = UserManager.getByName("gondez").get

                val busAnu = BusinessManager.getByName("Anu").get
                val busKae = BusinessManager.getByName("Kae").get

                Invest += InvestRow(0, robin.id, busKae.id, 10)

                gondez.invest(busAnu, 100)
                gondez.invest(busKae, 300)

                val investors = for {
                    ((u, v), b) <- User.innerJoin(Invest).innerJoin(Business).on {
                        case ((_u, _v), _b) => _u.id === _v.userId && _b.id === _v.busId
                    }
                } yield (u.name, v.amount, b.name, v.id)


                println("\n--------------------------")

                investors.sortBy(_._1.asc).foreach { case (userName, investAmount, busName, investId) =>
//                    Business.filter(_.id === invest.busId).foreach { case (business) =>
                        println(s" * $userName invest Rp.${investAmount}jt in $busName ($investId)")
//                    }
                }


        }

    }

}
