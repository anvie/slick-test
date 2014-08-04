package com.ansvia.zufaro

import scala.slick.driver.H2Driver.simple._
import com.ansvia.zufaro.model.Tables._
import com.ansvia.zufaro.model.{UserRole, InvestorRole}

object Test {

    import Helpers._

    def main(args:Array[String]){

        com.ansvia.zufaro.Zufaro.db withSession { implicit session =>

            (Business.ddl ++ BusinessGroup.ddl ++ BusinessGroupLink.ddl ++ BusinessProfit.ddl ++
                  Investor.ddl ++ Invest.ddl ++ InvestorBalance.ddl ++
                  Operator.ddl ++
                  Credit.ddl).create

            InvestorManager.create("robin", InvestorRole.OWNER)
            InvestorManager.create("gondez", InvestorRole.OWNER)
            InvestorManager.create("temon", InvestorRole.OWNER)

            //                Investor.users.foreach { case (id, name, role) =>
            //                    println(s" * $id - $name ($role)")
            //                }

            println("Investors:")
            Investor.foreach { case InvestorRow(id, name, role) =>
                println(s" * $id - $name ($role)")
            }

            //                Business += BusinessRow(0, "Anu", "anu kae", , 70.0, 30.0)
            //                Business += BusinessRow(0, "Kae", "anu kae", 60.0, 40.0)

            val busAnu = BusinessManager.create("Anu", "anu kae", 200, 70.0, 30.0)
            val busKae = BusinessManager.create("Kae", "anu kae", 200, 50.0, 50.0)
            val busPulsa = BusinessManager.create("Pulsa", "pulsa", 500, 70, 30)

            val busGroup = BusinessManager.createGroup("all", "all business group")

            busGroup.addMembers(busAnu, busKae, busPulsa)

            println("\nBusiness: ")
            for (b <- Business){
                println(f"  + bisnis `${b.name}` has fund: Rp.${b.fund}%.02f")
            }

            println("\nBusiness Group: ")
            BusinessGroup.foreach { bg =>
                println("    * `" + bg.name + "` with members:")
                bg.getMembers(0, 10).sortBy(_.name).foreach { bus =>
                    println("      - " + bus.name)
                }
            }
            println("")

            val robin = InvestorManager.getByName("robin").get
            val gondez = InvestorManager.getByName("gondez").get
            val temon = InvestorManager.getByName("temon").get

            //                val busAnu = BusinessManager.getByName("Anu").get
            //                val busKae = BusinessManager.getByName("Kae").get

            robin.addBalance(100.0)
            gondez.addBalance(500.10)
            temon.addBalance(200.0)

            println(s"${robin.name} balance: ${robin.getBalance}")

            robin.invest(busAnu, 50.0)
            robin.invest(busKae, 10)
            robin.invest(busPulsa, 10)
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

            val op1 = OperatorManager.create("op1")

            busAnu.addProfit(100.0, op1, UserRole.OPERATOR)
            busKae.addProfit(50.0, op1, UserRole.OPERATOR)
            busPulsa.addProfit(100.0, op1, UserRole.OPERATOR)

            Business.foreach { bus =>
                if (bus.getProfit > 0)
                    println(f"  business ${bus.name}%s has profit amount of Rp.${bus.getProfit}%.02f")
            }


            Investor.foreach { investor =>
                println("\n")
                println(f"  ${investor.name}%s balance: Rp.${investor.getBalance}%.02f")
                println("  mutation:")
                Credit.filter(_.invId === investor.id).sortBy(_.ts.desc).foreach { case (credit) =>
                    println(f"     + credit : Rp.${credit.amount}%.02f ref: ${credit.ref.getOrElse("-")}%s [${credit.ts}]")
                }
            }


            //
            //
            //                println(f"  ${robin.name}%s balance: Rp.${robin.getBalance}%.02f")
            //                Credit.filter(_.invId === robin.id).foreach { case (credit) =>
            //                    println(f"     + credit : Rp.${credit.amount}%.02f ref: ${credit.ref}%s [${credit.ts}]")
            //                }

            println("\n")

        }

    }

}
