package com.atocash.activities.admin.projects

interface ManageProjectsNavigator {
    fun onCreateProject()
    fun onBack()
    fun updateUi(msg: String, isDone: Boolean)
}