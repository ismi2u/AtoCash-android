package com.atocash.activities.admin.costCenter

interface ManageCostCenterNavigator {
    fun onCreateCostCenter()
    fun onBack()
    fun showMsg(msg: String)
    abstract fun updateUi(msg: String, isDone: Boolean)

}