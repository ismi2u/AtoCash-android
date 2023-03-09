package com.atocash.network.response

import java.math.BigDecimal

data class MaxLimitBalanceAndCashInHand(
    var maxLimit: BigDecimal? = null,
    var curBalance: BigDecimal? = null,
    var cashInHand: BigDecimal? = null,
    var pendingApprovalCA: BigDecimal? = null,
    var pendingApprovalER: BigDecimal? = null,
    var pendingSettlementCA: BigDecimal? = null,
    var pendingSettlementER: BigDecimal? = null,
    var adjustedAgainstCA: BigDecimal? = null,
    var walletBalLastUpdated: String? = null
): BaseResponse()
