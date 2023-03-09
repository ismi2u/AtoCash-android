package com.atocash.fragments.admin.costCenter.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.CostCenterResponse

class CostCenterVhVm(
    costCenterResponse: CostCenterResponse
) {
    val code = ObservableField<String>()
    val description = ObservableField<String>()

    init {
        code.set(costCenterResponse.costCenterCode)
        description.set(costCenterResponse.costCenterDesc)
    }

}