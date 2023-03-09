package com.atocash.network.response

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.atocash.database.converter.SubClaimsConverter

@Entity
class ExpenseRaisedForEmployeeResponse(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    var employeeId: Int? = null,
    var currencyTypeId: Int? = null,
    var totalClaimAmount: Float? = null,
    var departmentId: Int? = null,
    var approvalStatusTypeId: Int? = null,
    var projectId: Int? = null,
    var subProjectId: Int? = null,
    var workTaskId: Int? = null,

    var workTaskName: String = "",
    var projectName: String? = null,
    var subProjectName: String = "",
    var expenseReportTitle: String = "",
    var businessType: String? = null,
    var employeeName: String = "",
    var departmentName: String? = null,
    var approvalStatusType: String = "",
    var expReimReqDate: String = "",
    var showEditDelete: Boolean = false,

    var currency: String = "",

    @TypeConverters(SubClaimsConverter::class)
    var expensesSubClaims: List<ExpenseReimburseListingResponse>? = null
)