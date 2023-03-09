package com.atocash.fragments.employee.travelReq.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.EmployeeTravelRequestModel

class EmployeeTravelReqVhVm(
    model: EmployeeTravelRequestModel
) {
    val id = ObservableField<String>()
    val initiatorName = ObservableField<String>()
    val requestAmount = ObservableField<String>()
    val date = ObservableField<String>()
    val description = ObservableField<String>()
    val status = ObservableField<String>()

    val startDate = ObservableField<String>()
    val endDate = ObservableField<String>()

    init {
        description.set(model.description)
        startDate.set(model.startDate)
        endDate.set(model.endDate)
        requestAmount.set(model.requestAmount)
    }

}