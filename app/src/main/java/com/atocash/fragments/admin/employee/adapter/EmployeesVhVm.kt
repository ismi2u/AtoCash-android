package com.atocash.fragments.admin.employee.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.EmployeesResponse

class EmployeesVhVm(
    response: EmployeesResponse
) {

    val name = ObservableField<String>()
    val email = ObservableField<String>()
    val mobile = ObservableField<String>()
    val dateJoined = ObservableField<String>()
    val nationality = ObservableField<String>()

    init {
        name.set(response.firstName+" "+response.lastName)
        email.set(response.email)
        mobile.set(response.mobileNumber)
        dateJoined.set("Date Joined: " + response.doj)
        nationality.set("Nationality: " + response.nationality)
    }

}