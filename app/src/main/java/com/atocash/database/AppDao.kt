package com.atocash.database

import androidx.room.*
import com.atocash.network.response.ExpenseRaisedForEmployeeResponse
import io.reactivex.Completable

@Dao
interface AppDao {

    /*save a new expense with sub claims*/
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveExpenseItem(pendingExpenseItem: ExpenseRaisedForEmployeeResponse): Long

    /*update an existing sub claim*/
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(pendingExpenseItem: ExpenseRaisedForEmployeeResponse): Int

    /*Delete an expense with sub claims*/
    @Query("DELETE FROM ExpenseRaisedForEmployeeResponse WHERE employeeId = :employeeId")
    suspend fun deletePendingExpense(employeeId: Int)

    @Query("DELETE FROM ExpenseRaisedForEmployeeResponse WHERE id = :id")
    suspend fun deleteSinglePendingExpense(id: Int)

    /*Delete all items*/
    @Query("DELETE FROM ExpenseRaisedForEmployeeResponse")
    suspend fun deleteAllPendingExpense()

    /*Retrieve all sub claims when another login is matched*/
    @Query("SELECT * FROM ExpenseRaisedForEmployeeResponse WHERE employeeId = :employeeId")
    fun getPendingExpenses(employeeId: Int): List<ExpenseRaisedForEmployeeResponse>
}