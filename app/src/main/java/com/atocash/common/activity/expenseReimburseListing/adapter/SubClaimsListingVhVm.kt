package com.atocash.common.activity.expenseReimburseListing.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.ExpenseReimburseListingResponse
import com.atocash.utils.DateUtils
import com.atocash.utils.extensions.printLog
import java.text.DecimalFormat

class SubClaimsListingVhVm(
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
    }

}