package com.atocash.network.response

data class PettyCashBalanceResponse(
    var curCashBal: Float? = null,
    var maxCashAllowed: Int? = 0
) : BaseResponse()