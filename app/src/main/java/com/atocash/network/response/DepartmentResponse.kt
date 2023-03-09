package com.atocash.network.response

data class DepartmentResponse(
    var id: Int = -1,
    var deptCode: String? = null,
    var deptName: String? = null,
    var costCenter: String? = null,
    var statusType: String? = null,
    var costCenterId: Int = -1,
    var statusTypeId: Int = -1
)