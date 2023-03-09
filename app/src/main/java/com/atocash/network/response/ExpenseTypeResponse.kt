package com.atocash.network.response

data class ExpenseTypeResponse(
    var id: Int = 0,
    var statusTypeId: Int = 0,
    var expenseTypeName: String = "",
    var expenseTypeDesc: String = "",
    var statusType: String = ""
)

data class ExpenseTypeNonProjectResponse(
    var id: Int = 0,
    var expenseCategoryName: String = ""
)

data class VatPercentageResponse(
    val id: Int = 0,
    val vatPercentage: Int = 0
)