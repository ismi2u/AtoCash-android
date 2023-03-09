package com.atocash.common.fragments.report

import androidx.databinding.ObservableField
import com.atocash.base.common.BaseViewModel

class ReportViewModel: BaseViewModel<ReportNavigator>() {

    var isLoading = ObservableField<Boolean>()

    fun onShowDropDown() {
        getNavigator().showDropDown()
    }
}