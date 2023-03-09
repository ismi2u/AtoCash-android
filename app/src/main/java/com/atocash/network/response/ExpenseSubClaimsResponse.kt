package com.atocash.network.response

data class ExpenseSubClaimsResponse(
    var description: String? = null,
    var documentIds: String? = null,
    var invoiceDate: String? = null,
    var invoiceNo: String? = null,
    var location: String? = null,
    var vendor: String? = null,
    var tax: Int? = null,
    var taxAmount: Int? = null,
    var expenseReimbClaimAmount: Int? = null,
    var expenseTypeId: Int? = null
)