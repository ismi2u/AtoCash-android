package com.atocash.common.activity.expenseReimburseListing.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.ExpenseReimburseListingResponse
import com.atocash.utils.DateUtils
import java.text.DecimalFormat

class ExpenseReimburseListingVhVm(
    model: ExpenseReimburseListingResponse,
    val isSubmitted: Boolean
) {

    val id = ObservableField<String>()
    val invoiceNo = ObservableField<String>()
    val invoiceDate = ObservableField<String>()
    val amount = ObservableField<String>()
    val expenseType = ObservableField<String>()

    init {
        id.set(model.id.toString())
        invoiceDate.set(DateUtils.getDate(model.invoiceDate.toString()))
        invoiceNo.set(model.invoiceNo)
        expenseType.set(model.expenseType)

        val decimalFormat = DecimalFormat("##.00")

        model.expenseReimbClaimAmount?.let { expenseAmt ->
            model.taxAmount?.let { taxAmt ->
                val amountWithTax: Double = expenseAmt.plus(taxAmt).toDouble()
                val taxableAmt = decimalFormat.format(amountWithTax)
                amount.set("Amount: $taxableAmt")
            }
        }
        model.expenseReimbClaimAmount?.let { expenseReimbClaimAmount ->
            model.taxAmount?.let { taxAmt ->
                val amountWithTax: Double = expenseReimbClaimAmount.plus(taxAmt).toDouble()
                val taxableAmt = decimalFormat.format(amountWithTax)
                amount.set("Amount: $taxableAmt")
            }
        }
    }

}