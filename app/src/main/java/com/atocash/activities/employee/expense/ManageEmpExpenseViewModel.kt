package com.atocash.activities.employee.expense

import com.atocash.base.common.BaseViewModel

class ManageEmpExpenseViewModel : BaseViewModel<ManageEmpExpenseNavigator>() {

    fun onBack() {  getNavigator().onBack() }
    fun onCreate() {  getNavigator().onCreate() }
}