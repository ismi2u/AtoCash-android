package com.atocash.fragments.employee.advance.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.EmployeeAdvanceModel

class EmployeeAdvanceVhVm(
    model: EmployeeAdvanceModel
) {
    val id = ObservableField<String>()
    val initiatorName = ObservableField<String>()
    val requestAmount = ObservableField<String>()
    val date = ObservableField<String>()
    val description = ObservableField<String>()
    val status = ObservableField<String>()

    init {
        id.set("Advance ID: "+model.id)
        initiatorName.set(model.initiatorName)
        requestAmount.set(model.requestAmount)
        date.set(model.date)
        description.set(model.description)
        status.set(model.status)
    }

}