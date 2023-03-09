package com.atocash.network.localDb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.atocash.database.converter.PendingExpenseConverter
import com.atocash.database.converter.SubClaimsConverter
import com.atocash.network.response.ExpenseRaisedForEmployeeResponse

@Entity
data class PendingExpenseReimbursement(
    @PrimaryKey
    @ColumnInfo(name = "userId")
    var userId: Int? = null,

    @TypeConverters(PendingExpenseConverter::class)
    var expensesSubClaims: List<ExpenseRaisedForEmployeeResponse>? = null
)