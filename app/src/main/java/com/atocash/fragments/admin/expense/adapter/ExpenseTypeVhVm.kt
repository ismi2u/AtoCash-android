package com.atocash.fragments.admin.expense.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.ExpenseTypeResponse

class ExpenseTypeVhVm(
    response: ExpenseTypeResponse
) {
    val name = ObservableField<String>()
    val desc = ObservableField<String>()

    init {
        name.set(response.expenseTypeName)
        desc.set(response.expenseTypeDesc)
    }

}