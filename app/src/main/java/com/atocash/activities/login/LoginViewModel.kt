package com.atocash.activities.login

import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.ApiResponse
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.request.LoginRequest
import com.atocash.network.response.LoginResponse
import com.atocash.utils.extensions.printLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel :
    BaseViewModel<LoginNavigator>() {

    fun onForgotPassword() {
        getNavigator().onForgotPasswordClick()
    }

    fun onLogin() {
        getNavigator().onLoginClick()
    }

    val loginError = MutableLiveData<String>()

    fun doLogin(loginRequest: LoginRequest): MutableLiveData<Response<LoginResponse>> {
        val loginResponse = MutableLiveData<Response<LoginResponse>>()
        RetrofitClient.instance.apiService.doLogin(loginRequest)
            .enqueue(object : Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    throwError(t)
                    printLog("Error on login ${t.localizedMessage}")
                    loginError.value = t.message
                }

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    loginResponse.value = response
                }
            })
        return loginResponse
    }
}
