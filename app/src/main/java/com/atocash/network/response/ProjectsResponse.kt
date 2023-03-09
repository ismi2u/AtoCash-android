package com.atocash.network.response

data class ProjectsResponse(
    var id: Int = 0,
    var costCenterId: Int = 0,
    var projectManagerId: Int = 0,
    var statusTypeId: Int = 0,
    var projectName: String = "",
    var costCenter: String = "",
    var projectManager: String = "",
    var projectDesc: String = "",
    var statusType: String = ""
) : BaseResponse()