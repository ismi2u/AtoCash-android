package com.atocash.network.response

class ExpenseReimburseResponse  (
    var id: Int = 0,
    var employeeId: Int = 0,
    var currencyTypeId: Int = 0,
    var totalClaimAmount: Int = 0,
    var departmentId: Int = 0,
    var projectId: Int = 0,
    var subProjectId: Int = 0,
    var workTaskId: Int = 0,
    var approvalStatusTypeId: Int = 0,

    var showEditDelete: Boolean = false,

    var expenseReportTitle: String = "",
    var employeeName: String = "",
    var expReimReqDate: String = "",
    var department: String = "",
    var project: String = "",
    var subProject: String = "",
    var workTask: String = "",
    var approvalStatusType: String = "",
    var approvedDate: String = "",

    var expenseSubClaims : ArrayList<ExpenseSubClaims>? = null
)

data class ExpenseSubClaims(
    var id: Int = 0,
    var employeeId: Int = 0,
    var expenseReimbClaimAmount: Int = 0,
    var tax: Int = 0,
    var taxAmount: Int = 0,
    var currencyTypeId: Int = 0,
    var expenseTypeId: Int = 0,
    var departmentId: Int = 0,
    var projectId: Int = 0,
    var subProjectId: Int = 0,
    var workTaskId: Int = 0,
    var approvalStatusTypeId: Int = 0,

    var employeeName: String = "",
    var documentIDs: String = "",
    var expReimReqDate: String = "",
    var invoiceNo: String = "",
    var invoiceDate: String = "",
    var vendor: String = "",
    var location: String = "",
    var description: String = "",
    var currencyType: String = "",
    var expenseType: String = "",
    var department: String = "",
    var project: String = "",
    var subProject: String = "",
    var workTask: String = "",
    var approvalStatusType: String = "",
    var approvedDate: String = ""
)