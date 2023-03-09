package com.atocash.network.response

data class EmployeesResponse(
    var id: Int = 0,
    var employmentTypeId: Int = 0,
    var departmentId: Int = 0,
    var roleId: Int = 0,
    var approvalGroupId: Int = 0,
    var currencyTypeId: Int = 0,
    var statusTypeId: Int = 0,
    var code: String = "",
    var firstName: String = "",
    var middleName: String = "",
    var lastName: String = "",
    var empCode: String = "",
    var bankAccount: String = "",
    var bankCardNo: String = "",
    var nationalID: String = "",
    var passportNo: String = "",
    var taxNumber: String = "",
    var nationality: String = "",
    var dob: String = "",
    var doj: String = "",
    var gender: String = "",
    var email: String = "",
    var mobileNumber: String = ""
)