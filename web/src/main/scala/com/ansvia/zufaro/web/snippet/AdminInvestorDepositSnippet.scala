package com.ansvia.zufaro.web.snippet

import net.liftweb._
import util._
import http._
import Helpers._
import net.liftweb.util.CssSel
import scala.xml.{Text, NodeSeq}
import com.ansvia.zufaro.InvestorManager
import com.ansvia.zufaro.model.MutationKind

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


    def mutationList:CssSel = {
        val mutations = invO.get.getDepositMutations(0, 30)
        "#DepositList" #> NodeSeq.fromSeq(mutations.map(buildJournalListItem))
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
            <td>{mut.ref}</td>
            <td>{debitInfo}</td>
            <td>{creditInfo}</td>
        </tr>
    }

}
