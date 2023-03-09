package com.atocash.activities.employee.advance

import androidx.databinding.ObservableField
import com.atocash.base.common.BaseViewModel

class ManageEmpAdvanceViewModel : BaseViewModel<ManageEmpAdvanceNavigator>() {

    var isProjectEnabled = ObservableField<Boolean>(false)

    fun onBack() {  getNavigator().onBack() }
    fun onCreate() {  getNavigator().onCreate() }
}