package com.atocash.common.fragments.expenseReimburse.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.ExpenseRaisedForEmployeeResponse

class ExpenseReimburseVhVm(
    model: ExpenseRaisedForEmployeeResponse,
    val isSubmitted: Boolean
) {
    val id = ObservableField<String>()
    val businessType = ObservableField<String>()
    val expenseName = ObservableField<String>()
    val status = ObservableField<String>()
    val claimAmount = ObservableField<String>()
    val canShowOptions = ObservableField<Boolean>()

    init {
        id.set(model.id.toString())

        model.businessType?.let {
            businessType.set(it)
        } ?: run {
            businessType.set(model.projectName)
        }

        expenseName.set(model.expenseReportTitle)
        claimAmount.set("Claim Amount: ${model.totalClaimAmount}")
        status.set(model.approvalStatusType)
        canShowOptions.set(model.showEditDelete)
    }

}