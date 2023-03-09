package com.atocash.activities.login

interface LoginNavigator {
    fun onForgotPasswordClick()
    fun onLoginClick()
    abstract fun onApiError(message: String)
}