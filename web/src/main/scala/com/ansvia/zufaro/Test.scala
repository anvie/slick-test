package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import com.ansvia.zufaro.model.Tables._
import com.ansvia.zufaro.model.InvestorRole

object Test {

    import Helpers._

    def main(args:Array[String]){

        com.ansvia.zufaro.Zufaro.db withSession {
            implicit session =>

                (Business.ddl ++ Investor.ddl ++ Invest.ddl ++ InvestorBalance.ddl).create

                InvestorManager.create("robin", InvestorRole.OWNER)
                InvestorManager.create("gondez", InvestorRole.OWNER)
                InvestorManager.create("temon", InvestorRole.OWNER)

                //                Investor.users.foreach { case (id, name, role) =>
                //                    println(s" * $id - $name ($role)")
                //                }

                Investor.foreach { case InvestorRow(id, name, role) =>
                    println(s" * $id - $name ($role)")
                }

                Business += BusinessRow(0, "Anu", "anu kae", 70.0, 30.0)
                Business += BusinessRow(0, "Kae", "anu kae", 60.0, 40.0)

                val robin = InvestorManager.getByName("robin").get
                val gondez = InvestorManager.getByName("gondez").get
                val temon = InvestorManager.getByName("temon").get

                val busAnu = BusinessManager.getByName("Anu").get
                val busKae = BusinessManager.getByName("Kae").get

                robin.addBalance(20.0)
                gondez.addBalance(500.10)
                temon.addBalance(200.0)

                println(s"${robin.name} balance: ${robin.getBalance}")

                robin.invest(busKae, 10)
                gondez.invest(busAnu, 100)
                gondez.invest(busKae, 300)
                temon.invest(busAnu, 122)

                println(s"${robin.name} balance after invest: ${robin.getBalance}")

                val investors = for {
                    ((u, v), b) <- Investor.innerJoin(Invest).innerJoin(Business).on {
                        case ((_u, _v), _b) => _u.id === _v.invId && _b.id === _v.busId
                    }
                } yield (u, v.amount, b.name, v.id)


                println("\n--------------------------")

                investors.sortBy(_._1.name.asc).foreach { case (investor, investAmount, busName, investId) =>
                    println(f" * ${investor.name}%s invest Rp.${investAmount}%sjt in $busName ($investId%d) --- balance: ${investor.getBalance}%.02f")
                }

                // remove test
                temon.rmInvest(busAnu)

                // after update
                println("\n-------------------------- after update")

                investors.sortBy(_._1.name.asc).foreach { case (investor, investAmount, busName, investId) =>
                    println(f" * ${investor.name}%s invest Rp.${investAmount}%sjt in $busName ($investId%d) --- balance: ${investor.getBalance}%.02f")
                }

                println("\n")

        }

    }

}
