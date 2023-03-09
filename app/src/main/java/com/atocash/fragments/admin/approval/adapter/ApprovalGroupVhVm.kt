package com.atocash.fragments.admin.approval.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.ApprovalBaseResponse

class ApprovalGroupVhVm(
    response: ApprovalBaseResponse
) {
    val code = ObservableField<String>()
    val description = ObservableField<String>()

    init {
        code.set(response.approvalGroupCode)
        description.set(response.approvalGroupDesc)
    }

}