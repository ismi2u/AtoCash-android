package com.atocash.fragments.admin.approval.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.ApprovalBaseResponse
import com.atocash.network.response.CostCenterResponse

class ApprovalLevelVhVm(
    response: ApprovalBaseResponse
) {
    val code = ObservableField<String>()
    val description = ObservableField<String>()

    init {
        code.set(response.level.toString())
        description.set(response.levelDesc)
    }

}