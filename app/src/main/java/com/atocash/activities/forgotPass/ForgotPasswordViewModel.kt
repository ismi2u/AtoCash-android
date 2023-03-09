package com.atocash.activities.forgotPass

import com.atocash.base.common.BaseViewModel

class ForgotPasswordViewModel :
    BaseViewModel<ForgotPasswordNavigator>() {

    fun onBack() {
        getNavigator().onBackClick()
    }

    fun onSubmit() {
        getNavigator().onSubmit()
    }
}