package com.atocash.common.fragments.inbox.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.InboxExpenseReimburseResponse
import com.atocash.utils.DateUtils
import com.atocash.utils.DateUtils.EXPENSE_REIMBURSE_FORMAT_NEW

class InboxExpenseVhVm(
    response: InboxExpenseReimburseResponse
) {

    val projectName = ObservableField<String>()
    val id = ObservableField<String>()
    val empName = ObservableField<String>()
    val requestDate = ObservableField<String>()
    val status = ObservableField<String>()

    val isChecked = ObservableField<Boolean>()

    init {
        id.set(response.expenseReimburseRequestId.toString())
        empName.set(response.employeeName)

        response.project?.let {
            projectName.set(it)
        } ?: run {
            projectName.set(response.businessType)
        }

        requestDate.set(response.requestDate?.let {
            DateUtils.parseIsoDate(
                it,
                EXPENSE_REIMBURSE_FORMAT_NEW
            )
        })
        status.set(response.approvalStatusType)
        isChecked.set(response.isChecked)
    }


}