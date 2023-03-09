package com.atocash.network.response

import java.math.BigDecimal

class InboxExpenseReimburseResponse(
    var approvalGroupId: Int? = null,
    var approvalLevelId: Int? = null,
    var approvalStatusTypeId: Int? = null,
    var approvalStatusType: String? = null,
    var approvedDate: String? = null,
    var comments: String? = null,
    var currencyTypeId: Int? = null,
    var currencyType: String? = null,
    var department: String? = null,
    var departmentId: Int? = null,
    var employeeId: Int? = null,
    var employeeName: String? = null,
    var expReimReqDate: String? = null,
    var expenseReimburseRequestId: Int? = null,
    var id: Int? = null,
    var jobRole: String? = null,
    var jobRoleId: Int? = null,
    var projectId: Int? = null,
    var project: String? = null,

    var subProjectId: Int? = null,
    var workTaskId: Int? = null,
    var totalClaimAmount: BigDecimal? = null,
    var subProject: String? = null,
    var workTask: String? = null,
    var businessType: String? = null,
    var requestDate: String? = null,

    var isChecked: Boolean = false
)