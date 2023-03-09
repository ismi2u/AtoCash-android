package com.atocash.network.response

data class EmployeeTravelRequestModel(
    var id: String = "",
    var initiatorName: String = "",
    var requestAmount: String = "",
    var date: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var remark: String = "",
    var status: String = "",
    var description: String = "",
    var isProjectEnabled: Boolean = false,
    var projectId: String = "",
    var projectName: String = "",
    var taskId: String = "",
    var taskName: String = "",
    var subProjectName: String = "",
    var subProjectId: String = ""
)