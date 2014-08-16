package com.ansvia.zufaro.web.snippet

import net.liftweb._
import util._
import http._
import Helpers._
import net.liftweb.util.CssSel
import scala.xml.{Text, NodeSeq}
import com.ansvia.zufaro.InvestorManager
import com.ansvia.zufaro.model.{Initiator, UserRole, MutationKind}
import com.ansvia.zufaro.web.util.JsUtils
import com.ansvia.zufaro.exception.{ZufaroException, NotExistsException, InvalidParameterException}
import com.ansvia.zufaro.web.Auth
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.http.js.JsCmd

/**
 * Author: robin
 * Date: 8/16/14
 * Time: 6:46 PM
 *
 */
class AdminInvestorDepositSnippet {

    import com.ansvia.zufaro.model.Tables._
    import com.ansvia.zufaro.InvestorHelpers._

    private def invO = {
        val id = S.param("invId").openOr("0").toLong
        InvestorManager.getById(id)
    }


    def depositPageTitle:NodeSeq = {
        try {
            <div>Account:
                <strong>{invO.map(_.name).getOrElse("unknown")}</strong>
            </div>
        }
        catch {
            case e:NumberFormatException =>
                S.redirectTo("/admin/investor", () => S.error("Not found"))
        }
    }

    def balanceInfo:CssSel = {
        val inv = invO.get
        "#Amount *" #> f"Rp.${inv.getBalance}%.02f,-"
    }


    def balanceOp(in:NodeSeq):NodeSeq = {

        val invId = S.param("invId").openOr("0").toLong

        def creditBalance = () => {

            val ns = S.runTemplate("admin" :: "investor" :: "_chunk_dialog-credit-balance" :: Nil)
                .openOr(NodeSeq.Empty)

            val ns2 = ("#Dialog [class]" #> s"lift:AdminInvestorDepositSnippet.creditBalanceDialog?invId=$invId").apply(ns)

            JsUtils.modalDialog(ns2)
        }

        def debitBalance = () => {

            val ns = S.runTemplate("admin" :: "investor" :: "_chunk_dialog-debit-balance" :: Nil)
                .openOr(NodeSeq.Empty)

            val ns2 = ("#Dialog [class]" #> s"lift:AdminInvestorDepositSnippet.debitBalanceDialog?invId=$invId").apply(ns)

            JsUtils.modalDialog(ns2)
        }


        bind("in", in,
            "credit" -> SHtml.a(creditBalance, Text("Credit"), "class" -> "btn btn-default"),
            "debit" -> SHtml.a(debitBalance, Text("Debit"), "class" -> "btn btn-default")
        )
    }


    def creditBalanceDialog(in:NodeSeq):NodeSeq = {
        val invId = S.attr("invId").openOr("0").toLong
        var amount:Double = 0.0
        var info = ""

        // @TODO(robin): add throttle here to prevent multiple call
        def creditBalanceInternal() = {
            try {
                if (amount == 0.0)
                    throw InvalidParameterException("No balance")

                val inv = InvestorManager.getById(invId).getOrElse {
                    throw NotExistsException(s"No investor with id $invId")
                }
                val (initId, initRole) = {
                    Auth.currentAdmin.map {
                        admin =>
                            (admin.id, UserRole.ADMIN)
                    }.getOrElse {
                        Auth.currentOperator.map {
                            op =>
                                (op.id, UserRole.OPERATOR)
                        }.openOrThrowException("Not authorized")
                    }
                }
                inv.addBalance(amount, Some(info), Initiator(initId, initRole))

                JsUtils.hideAllModal &
                    SetHtml("Amount", Text(f"Rp.${inv.getBalance}%.02f,-")) &
                    updateMutationList(inv)
            }
            catch {
                case e:ZufaroException =>
                    JsUtils.showError(e.getMessage)
            }
        }

        SHtml.ajaxForm(bind("in", in,
            "balance" -> SHtml.number(amount, (x:Double) => amount = x, 0.0, 100000.0, 1.0, "class" -> "form-control"),
            "info" -> SHtml.textarea(info, info = _, "class" -> "form-control"),
            "submit" -> S.formGroup(1000){
                SHtml.hidden(creditBalanceInternal) ++
                    SHtml.submit("Send", creditBalanceInternal, "class" -> "btn btn-success")
            }
        ))
    }


    private def updateMutationList(inv:InvestorRow) = {
        val mutations = inv.getDepositMutations(0, 30)
        val ns = NodeSeq.fromSeq(mutations.map(buildJournalListItem)) ++ totalMutationNs(mutations)
        SetHtml("MutationList", ns)
    }

    def debitBalanceDialog(in:NodeSeq):NodeSeq = {
        val invId = S.attr("invId").openOr("0").toLong
        var amount:Double = 0.0
        var info = ""

        // @TODO(robin): add throttle here to prevent multiple call
        def debitBalanceInternal() = {
            try {
                if (amount == 0.0)
                    throw InvalidParameterException("No balance")

                val inv = InvestorManager.getById(invId).getOrElse {
                    throw NotExistsException(s"No investor with id $invId")
                }
                val (initId, initRole) = {
                    Auth.currentAdmin.map {
                        admin =>
                            (admin.id, UserRole.ADMIN)
                    }.getOrElse {
                        Auth.currentOperator.map {
                            op =>
                                (op.id, UserRole.OPERATOR)
                        }.openOrThrowException("Not authorized")
                    }
                }
                inv.subBalance(amount, Some(info), Initiator(initId, initRole))

                JsUtils.hideAllModal &
                    SetHtml("Amount", Text(f"Rp.${inv.getBalance}%.02f,-")) &
                    updateMutationList(inv)
            }
            catch {
                case e:ZufaroException =>
                    JsUtils.showError(e.getMessage)
            }
        }

        SHtml.ajaxForm(bind("in", in,
            "balance" -> SHtml.number(amount, (x:Double) => amount = x, 0.0, 100000.0, 1.0, "class" -> "form-control"),
            "info" -> SHtml.textarea(info, info = _, "class" -> "form-control"),
            "submit" -> S.formGroup(1000){
                SHtml.hidden(debitBalanceInternal) ++
                    SHtml.submit("Send", debitBalanceInternal, "class" -> "btn btn-success")
            }
        ))
    }


    private def totalMutationNs(mutations:Seq[MutationRow]) = {
        val totalCredit = mutations.filter(_.kind==MutationKind.CREDIT).map(_.amount).sum
        val totalDebit = mutations.filter(_.kind==MutationKind.DEBIT).map(_.amount).sum
        <tr>
            <td colspan="2"></td>
            <td><strong>{f"Rp.$totalCredit%.02f,-"}</strong></td>
            <td><strong>{f"Rp.$totalDebit%.02f,-"}</strong></td>
            <td colspan="2"></td>
        </tr>:NodeSeq
    }


    def mutationList:CssSel = {
        val mutations = invO.get.getDepositMutations(0, 30)
        "#MutationList *" #> (NodeSeq.fromSeq(mutations.map(buildJournalListItem)) ++ totalMutationNs(mutations))
    }

    private def buildJournalListItem(mut:MutationRow) = {
        val debitInfo = {
            if (mut.kind == MutationKind.DEBIT)
                Text(f"Rp.${mut.amount}%.02f,-")
            else
                Text("-")
        }
        val creditInfo = {
            if (mut.kind == MutationKind.CREDIT)
                Text(f"Rp.${mut.amount}%.02f,-")
            else
                Text("-")
        }
        <tr>
            <td>{mut.id}</td>
            <td>{mut.ref.getOrElse("")}</td>
            <td>{creditInfo}</td>
            <td>{debitInfo}</td>
            <td>{mut.ts.toString}</td>
        </tr>
    }

}
