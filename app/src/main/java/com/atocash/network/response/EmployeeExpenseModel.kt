package com.atocash.network.response

data class EmployeeExpenseModel(
    var id: String = "",
    var initiatorName: String = "",
    var requestAmount: String = "",
    var date: String = "",
    var remark: String = "",
    var status: String = ""
)