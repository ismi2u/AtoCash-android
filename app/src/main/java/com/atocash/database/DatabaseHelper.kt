package com.atocash.database

import androidx.lifecycle.LiveData
import com.atocash.network.response.ExpenseRaisedForEmployeeResponse

interface DatabaseHelper {

    suspend fun saveExpenseItem(pendingExpenseItem: ExpenseRaisedForEmployeeResponse): Long

    suspend fun updateExpenseItem(pendingExpenseItem: ExpenseRaisedForEmployeeResponse): Long

    suspend fun deletePendingExpense(employeeId: Int)

    suspend fun deleteSinglePendingExpense(id: Int)

    suspend fun getPendingExpenses(employeeId: Int): List<ExpenseRaisedForEmployeeResponse>

    suspend fun deleteAllPendingExpense()
}