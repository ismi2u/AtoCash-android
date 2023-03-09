package com.atocash.activities.admin.expenseTypes

interface ManageExpenseTypeNavigator {
    fun onCreateExpense()
    fun onBack()
    abstract fun updateUi(msg: String, isDone: Boolean)
}