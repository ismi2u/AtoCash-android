package com.atocash.network.response

class InboxTravelRequestResponse(
    var approvalLevelId: Int? = null,
    var approvalGroupId: Int? = null,
    var approvalStatusType: String? = null,
    var approvalStatusTypeId: Int = 0,
    var approverActionDate: String? = null,
    var approverEmpId: String? = null,
    var businessType: String? = null,
    var businessTypeId: Int? = null,
    var businessUnit: String? = null,
    var businessUnitId: Int? = null,
    var comments: String?= null,
    var employeeId: Int?= null,
    var employeeName: String?= null,
    var id: Int?= null,
    var jobRole: String?= null,
    var jobRoleId: Int?= null,
    var projectId: Int?= null,
    var projectName: String?= null,
    var requestDate: String? = null,
    var travelApprovalRequestId: Int = 0,
    var travelStartDate: String = "",
    var travelEndDate: String = "",

    var isChecked: Boolean = false,
)