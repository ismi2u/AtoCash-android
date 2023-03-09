package com.atocash.fragments.admin.dashboard.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.PettyCashResponse
import com.atocash.utils.DateUtils
import java.util.*

class AdminCashAdvanceVhVm(
    response: PettyCashResponse
) {
    val amount = ObservableField<String>()
    val description = ObservableField<String>()
    val requestDate = ObservableField<String>()
    val canShowOptions = ObservableField<Boolean>()
    val projectOrDeptName = ObservableField<String>()
    val status = ObservableField<String>()

    init {
        val amountStr = "Request Amount: ${response.cashAdvanceAmount}"
        amount.set(amountStr)

        description.set(response.cashAdvanceRequestDesc)

        status.set(response.approvalStatusType)

        requestDate.set(response.requestDate?.let { DateUtils.parseIsoDate(it) })

        projectOrDeptName.set(response.businessType)

        canShowOptions.set(
            response.approvalStatusType.toString().toLowerCase(Locale.getDefault()) == "pending"
        )
    }

}