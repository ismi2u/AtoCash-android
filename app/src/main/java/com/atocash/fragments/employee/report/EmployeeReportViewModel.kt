package com.atocash.fragments.employee.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EmployeeReportViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is report Fragment"
    }
    val text: LiveData<String> = _text
}