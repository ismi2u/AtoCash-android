package com.atocash.database

import com.atocash.network.response.ExpenseRaisedForEmployeeResponse
import com.atocash.utils.extensions.printLog
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseHelperImpl(private val appDao: AppDao) : DatabaseHelper {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val mDisposable = CompositeDisposable()

    override suspend fun updateExpenseItem(pendingExpenseItem: ExpenseRaisedForEmployeeResponse): Long {
        return appDao.update(pendingExpenseItem).toLong()
    }

    override suspend fun saveExpenseItem(pendingExpenseItem: ExpenseRaisedForEmployeeResponse): Long {
        val id = appDao.saveExpenseItem(pendingExpenseItem)
        printLog("expense item saved id $id")
        return id
    }

    override suspend fun deletePendingExpense(employeeId: Int) {
        withContext(ioDispatcher) {
            appDao.deletePendingExpense(employeeId)
        }
    }

    override suspend fun deleteSinglePendingExpense(id: Int) {
        withContext(ioDispatcher) {
            appDao.deleteSinglePendingExpense(id)
        }
    }

    override suspend fun getPendingExpenses(employeeId: Int): List<ExpenseRaisedForEmployeeResponse> {
        val runningScope = Dispatchers.Default
        return withContext(ioDispatcher) {
            appDao.getPendingExpenses(employeeId)
        }
    }

    override suspend fun deleteAllPendingExpense() {
        withContext(ioDispatcher) {
            appDao.deleteAllPendingExpense()
        }
    }

}