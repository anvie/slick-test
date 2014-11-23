package com.ansvia.zufaro

import java.sql.Date

import scala.slick.driver.PostgresDriver.simple._
import com.ansvia.zufaro.model.Tables._
import com.ansvia.zufaro.model._
import java.util.UUID
import java.io.{FilenameFilter, File}
import com.ansvia.zufaro.model.Tables.Investor
import com.ansvia.zufaro.InvestorManager.{Address, Contact}


object Test extends ZufaroTestHelpers {

    import Helpers._
    import ZufaroHelpers._


    private def genPass = {
        UUID.randomUUID().toString
    }
    private def genContact = {
        val addr = Address(genRandomString,genRandomString,genRandomString,genRandomString,System.currentTimeMillis())
        val email = s"$genRandomString@hello.com"
        Contact(addr, email, genRandomString, genRandomString)
    }


    def main(args:Array[String]){

        com.ansvia.zufaro.Zufaro.db withSession { implicit session =>

//            (Business.ddl ++ BusinessGroup.ddl ++ BusinessGroupLink.ddl ++ BusinessProfit.ddl ++
//                  Investor.ddl ++ Invest.ddl ++ InvestorBalance.ddl ++
//                  Operator.ddl ++
//                  Credit.ddl).create
//
//            ddl.create

            val contacts = for (i <- 0 to 5) yield genContact

            val inv = Investor(0L,
                name = "robin",
                fullName = "robin",
                role = InvestorRole.OWNER,
                sex = SexType.MALE,
                nation = "Indonesia",
                birthPlace = "Wonosobo",
                birthDate = new Date(1988,01,01),
                religion = "-",
                education = "S1",
                titleFront = "Ir.",
                maritalStatus = 1,
                motherName = "",
                passhash = ""
            )

            InvestorManager.create(inv, genPass)
            InvestorManager.create(inv.copy(name = "gondez", fullName = "gondez"), genPass)
            InvestorManager.create(inv.copy(name = "temon", fullName = "temon"), genPass)
            val imam = InvestorManager.create(inv.copy(name = "imam", fullName = "imam"), genPass)

            //                Investor.users.foreach { case (id, name, role) =>
            //                    println(s" * $id - $name ($role)")
            //                }

            println("Investors:")
            Investors.foreach { case inv:Investor =>
                println(s" * ${inv.id} - ${inv.name} (${inv.role})")
            }

            //                Business += Business(0, "Anu", "anu kae", , 70.0, 30.0)
            //                Business += Business(0, "Kae", "anu kae", 60.0, 40.0)

            val busCucianMobil = BusinessManager.create("Cucian Mobil", "cucian mobil", "jasa", 200, 30.0, BusinessManager.state.PRODUCTION)
            val busLaundry = BusinessManager.create("Laundry", "laundry", "jasa", 200, 50.0, BusinessManager.state.PRODUCTION)
            val busPulsa = BusinessManager.create("Pulsa", "pulsa", "technology", 500, 30, BusinessManager.state.PRODUCTION)

            val busGroupAll = BusinessManager.createGroup("all", "all business group")

            busGroupAll.addMembers(busCucianMobil, busLaundry, busPulsa)

            println("\nBusiness: ")
            for (b <- Businesses){
                println(f"  + bisnis `${b.name}` has fund: Rp.${b.fund}%.02f")
            }

            println("\nBusiness Group: ")
            BusinessGroups.foreach { bg =>
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
            imam.addBalance(200.0)

            println("balance before invest:")
            Investors.foreach { inv =>
                println(f"   ${inv.name} balance: Rp.${inv.getBalance}%.02f")
            }

//            println(s"${robin.name} balance: ${robin.getBalance}")

            robin.invest(busCucianMobil, 50.0)
            robin.invest(busLaundry, 10)
            robin.invest(busPulsa, 10)
            gondez.invest(busCucianMobil, 100)
            gondez.invest(busLaundry, 300)
            temon.invest(busCucianMobil, 122)
            imam.invest(busGroupAll, 100)


//            println(s"${robin.name} balance after invest: ${robin.getBalance}")

            val investors = for {
                ((u, v), b) <- Investors.innerJoin(Invests).innerJoin(Businesses).on {
                    case ((_u, _v), _b) => _u.id === _v.investorId && _b.id === _v.businessId
                }
            } yield (u, v.amount, b.id, b.name, v.id, v.busKind)


            println("\n--------------------------")

            investors.sortBy(_._1.name.asc).foreach { case (investor, investAmount, busId, busName, investId, busKind) =>
                busKind match {
                    case BusinessKind.SINGLE =>
                        println(f" * ${investor.name}%s invest Rp.${investAmount}%sjt in $busName ($investId%d)")
                    case BusinessKind.GROUP =>
                        val busNameGroup = BusinessManager.getGroupById(busId).get.name
                        println(f" * ${investor.name}%s invest Rp.${investAmount}%sjt in $busNameGroup%s ($investId%d)")
                }
            }

            // remove test
            temon.removeInvestment(busCucianMobil)

            println("\nbalance after invest:")
            Investors.foreach { inv =>
                println(f"   ${inv.name} balance: Rp.${inv.getBalance}%.02f")
            }

            println("\n")

            val op1 = OperatorManager.create("op1", genPass)

            busCucianMobil.addProfit(300, 100.0, op1, UserRole.OPERATOR)
            busLaundry.addProfit(100, 50.0, op1, UserRole.OPERATOR)
            busPulsa.addProfit(250, 100.0, op1, UserRole.OPERATOR)

            // walaupun sudah add profit tapi belum otomatis ke-share profit-nya ke investor
            // harus eksekusi doShareProcess() dulu

            Businesses.foreach { bus =>
                if (bus.getProfit > 0)
                    println(f"  business ${bus.name}%s has profit amount of Rp.${bus.getProfit}%.02f")
            }

            // lakukan prosedur share ke semua investor
            val shareMethod = ShareMethod(ShareMethod.AUTO, NoInitiator)
            Businesses.foreach(_.doShareProcess(shareMethod))


            println("\n --------------- BUSINESS ACCOUNT -----------------\n")

            for (b <- Businesses){

                println(f" * ${b.name} - balance: ${b.saving format IDR}")

                val q = for {
                    fin <- BusinessAccountMutations if fin.busId === b.id
                } yield (fin.kind, fin.amount, fin.info)

                q.foreach { case (kind, amount, info) =>

                    val mutationKind = mutationStr(kind)

                    println(s"    - $mutationKind: ${amount format IDR} - $info")
                }

                println("")
            }


            println("\n --------------- INVESTOR ACCOUNT -----------------")

            Investors.foreach { investor =>
                println("\n")
                println(f"  ${investor.name}%s balance: Rp.${investor.getBalance}%.02f")
                println("  mutation:")
                Mutations.filter(_.invId === investor.id).sortBy(_.ts.desc)
                    .map(x => (x.kind, x.amount, x.ref, x.ts))
                    .foreach { case (kind, amount, ref, ts) =>
                        println(f"     - ${mutationStr(kind)} : Rp.${amount format IDR} ref: ${ref.getOrElse("-")}%s [$ts]")
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

    private def mutationStr(kind:Int) = kind match {
        case MutationKind.CREDIT => "credit"
        case MutationKind.DEBIT => "debit"
    }

}
