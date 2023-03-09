package com.atocash.common.fragments.expenseReimburse.pending

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.atocash.base.common.BaseViewModel
import com.atocash.database.DatabaseHelperImpl
import com.atocash.network.response.ExpenseRaisedForEmployeeResponse
import kotlinx.coroutines.launch

class PendingExpenseReimburseViewModel :
    BaseViewModel<PendingExpenseReimburseNavigator>() {

    var items = MutableLiveData<List<ExpenseRaisedForEmployeeResponse>>()
    fun getPendingExpenses(
        dbHelper: DatabaseHelperImpl,
        employeeId: Int
    ) {
        viewModelScope.launch {
            items.value = dbHelper.getPendingExpenses(employeeId)
        }
    }

    fun deleteItem(dbHelper: DatabaseHelperImpl, id: Int?) {
        id?.let {
            viewModelScope.launch {
                dbHelper.deleteSinglePendingExpense(id)
            }
        }
    }

    var isLoading = ObservableField<Boolean>(true)

}