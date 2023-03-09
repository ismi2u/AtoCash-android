package com.atocash.activities.admin.department

interface AdminDepartmentNavigator {
    fun onCreate()
    fun onBack()
    fun showMsg(msg: String, isDone: Boolean)

}