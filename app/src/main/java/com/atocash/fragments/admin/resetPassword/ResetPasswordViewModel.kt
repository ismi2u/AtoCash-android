package com.atocash.fragments.admin.resetPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ResetPasswordViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is reset password Fragment"
    }
    val text: LiveData<String> = _text
}