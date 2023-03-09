package com.atocash.network.request

data class CostCenterRequest(
    var id: Int = -1,
    var statusTypeId: Int = -1,
    var costCenterCode: String = "",
    var costCenterDesc: String = "",
    var statusType: String = ""
)