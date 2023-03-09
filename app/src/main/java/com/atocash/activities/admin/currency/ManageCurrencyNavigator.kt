package com.atocash.activities.admin.currency

interface ManageCurrencyNavigator {
    fun onCreateCostCenter()
    fun onBack()
    fun showMsg(msg: String)
    abstract fun updateUi(msg: String, isDone: Boolean)

}