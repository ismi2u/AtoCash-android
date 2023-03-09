package com.atocash.network.response

import androidx.room.Entity
import java.math.BigDecimal

@Entity
data class ExpenseReimburseListingResponse(
    var id: Int? = null,

    var employeeName: String? = null,
    var expenseCategory: String? = null,
    var expStrtDate: String? = null,
    var expEndDate: String? = null,
    var employeeId: BigDecimal? = null,
    var expenseReimburseRequestId: Int? = null,
    var expenseCategoryId: Int? = null,
    var expNoOfDays: Long? = null,
    var isVAT: Boolean? = false,
    var isStoreReq: Boolean? = false,
    var taxNo: String? = null,
    var expenseReimbClaimAmount: Float? = null,
    var documentIDs: String? = null,
    var requestDate: String? = null,
    var invoiceNo: String? = null,
    var invoiceDate: String? = null,
    var vendor: String? = null,
    var additionalVendor: String? = null,
    var tax: Float? = null,
    var taxAmount: Float? = null,
    var vendorId: Int? = null,
    var currencyTypeId: Int? = null,
    var expenseTypeId: Int? = null,
    var location: String? = null,
    var description: String? = null,
    var currencyType: String? = null,
    var expenseType: String? = null,
    var businessTypeId: Int? = null,
    var businessType: String? = null,
    var businessUnitId: Int? = null,
    var businessUnit: String? = null,
    var projectId: Int? = null,
    var projectName: String? = null,
    var subProjectId: Int? = null,
    var subProjectName: String? = null,
    var workTaskId: Int? = null,
    var workTaskName: String? = null,
    var costCenterId: BigDecimal? = null,
    var costCenter: String? = null,
    var approvalStatusType: String? = null,
    var approvalStatusTypeId: Int? = null,
    var approverActionDate: String? = null,
) : Cloneable {
    override fun clone(): ExpenseReimburseListingResponse =
        super.clone() as ExpenseReimburseListingResponse
}

class ExpenseReimburseRequestDto(
    var businessTypeId: Int? = null,
    var businessUnitId: Int? = null,
    var employeeId: String? = null,
    var currencyTypeId: Int? = null,
    var projectId: Int? = null,
    var subProjectId: Int? = null,
    var workTaskId: Int? = null,
    var expenseReportTitle: String? = null,
    var expenseSubClaims: ArrayList<ExpenseSubClaimDto>? = null,
)

class ExpenseSubClaimDto(

    var invoiceNo: String? = null,
    var invoiceDate: String? = null,
    var location: String? = null,
    var vendorId: Int? = null,
    var additionalVendor: String? = null,
    var description: String? = null,
    var expenseTypeId: Int? = null,
    var tax: Float? = null,
    var expenseReimbClaimAmount: Float? = null,
    var taxAmount: Float? = null,
    var documentIds: String? = null,

    var expenseCategoryId: Int? = null,
    var isVAT: Boolean? = null,
    var expStrtDate: String? = null,
    var expEndDate: String? = null,
    var expNoOfDays: Long? = null,
    var taxNo: String? = null,
    var NoOfDaysDate: ArrayList<String>? = null,
)

data class Documents(
    var id: Int? = null,
    var actualFileName: String? = null,
    var filePath: String? = null
)

data class ExpenseSubClaimResDto(
    var id: Int? = null,
    var employeeName: String? = null
)