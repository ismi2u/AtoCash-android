package com.atocash.network.response

data class PettyCashBalanceVsAdvancedResponse(
    var curCashBal: Float? = 0.00F,
    var maxCashAllowed: Int? = 0
) : BaseResponse()