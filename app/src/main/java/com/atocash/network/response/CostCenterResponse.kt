package com.atocash.network.response

class CostCenterResponse  (
    var id: Int = -1,
    var costCenterCode: String = "",
    var costCenterDesc: String = "",
    var statusType: String = "",
    var statusTypeId: Int = -1
) : BaseResponse()