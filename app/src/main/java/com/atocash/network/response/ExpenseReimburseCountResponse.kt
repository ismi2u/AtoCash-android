package com.atocash.network.response

data class ExpenseReimburseCountResponse(
    var totalCount: Int? = 0,
    var pendingCount: Int? = 0,
    var rejectedCount: Int? = 0,
    var approvedCount: Int? = 0
) : BaseResponse()