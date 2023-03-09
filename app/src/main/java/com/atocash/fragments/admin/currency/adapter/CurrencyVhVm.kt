package com.atocash.fragments.admin.currency.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.CostCenterResponse
import com.atocash.network.response.CurrencyResponse

class CurrencyVhVm(
    costCenterResponse: CurrencyResponse
) {
    val code = ObservableField<String>()
    val description = ObservableField<String>()

    init {
        code.set(costCenterResponse.currencyCode)
        description.set(costCenterResponse.currencyName)
    }

}