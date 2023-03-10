package com.atocash.network.response

class InboxCashAdvanceResponse(
    var approvalGroupId: Int?= null,
    var approvalLevelId: Int?= null,
    var approvalStatusType: String? = null,
    var approvalStatusTypeId: Int? = null,
    var approverActionDate: String? = null,
    var approverEmpId: Int? = null,
    var businessType: String? = null,
    var businessTypeId: String? = null,
    var businessUnit: String? = null,
    var businessUnitId: String? = null,
    var cashAdvanceRequestId: Int?= null,
    var claimAmount: Int?= null,
    var comments: String = "",
    var employeeId: Int = 0,
    var employeeName: String? = null,
    var id: Int?= null,
    var jobRole: String?= null,
    var jobRoleId: Int?= null,
    var projectId: String? = null,
    var projectName: String?= null,
    var subProjectId: String?= null,
    var subProjectName: String?= null,
    var workTask: String?= null,
    var workTaskId: String?= null,
    var requestDate: String?= null,
    var isChecked: Boolean= false,
)