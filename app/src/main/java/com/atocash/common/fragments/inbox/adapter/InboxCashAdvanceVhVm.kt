package com.atocash.common.fragments.inbox.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.InboxCashAdvanceResponse
import com.atocash.utils.DateUtils

class InboxCashAdvanceVhVm(
    response: InboxCashAdvanceResponse
) {
    val empName = ObservableField<String>()
    val pettyCashRequestId = ObservableField<String>()
    val department = ObservableField<String>()
    val projectName = ObservableField<String>()
    val requestDate = ObservableField<String>()
    val status = ObservableField<String>()
    val isChecked = ObservableField<Boolean>()

    init {
        empName.set(response.employeeName)
        pettyCashRequestId.set(response.cashAdvanceRequestId.toString())

        response.projectName?.let {
            projectName.set(it)
        } ?: run {
            projectName.set(response.businessType)
        }

        requestDate.set(response.requestDate?.let {
            DateUtils.parseIsoDate(
                it,
                DateUtils.EXPENSE_REIMBURSE_FORMAT_NEW
            )
        })

        status.set(response.approvalStatusType)
        isChecked.set(response.isChecked)
    }

}