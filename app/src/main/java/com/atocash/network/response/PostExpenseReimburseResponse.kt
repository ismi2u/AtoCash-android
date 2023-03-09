package com.atocash.network.response

import androidx.room.Entity

@Entity
data class PostExpenseReimburseResponse(
    val currencyTypeId: Int? = null,
    val employeeId: String? = null,
    val expenseReportTitle: String? = null,
    val projectId: String? = null,
    val subProjectId: String? = null,
    val workTaskId: String? = null,
    val expenseSubClaims: ArrayList<ExpenseSubClaimsResponse>? = null
)