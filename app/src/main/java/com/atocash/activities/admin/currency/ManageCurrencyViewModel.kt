package com.atocash.activities.admin.currency

import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.StatusDropDownResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ManageCurrencyViewModel :
    BaseViewModel<ManageCurrencyNavigator>() {

    fun onCreate() {
        getNavigator().onCreateCostCenter()
    }

    fun onBack() {
        getNavigator().onBack()
    }

    fun getStatusForDropDown(authToken: String): MutableLiveData<ArrayList<StatusDropDownResponse>> {
        val statusResponse = MutableLiveData<ArrayList<StatusDropDownResponse>>()
        RetrofitClient.instance.apiService.getStatusForDropDown(authToken)
            .enqueue(object : Callback<ArrayList<StatusDropDownResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<StatusDropDownResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<ArrayList<StatusDropDownResponse>>,
                    response: Response<ArrayList<StatusDropDownResponse>>
                ) {

                    if (response.isSuccessful) {

                    } else {

                    }

                    if (response.code() == 200) {
                        statusResponse.value = response.body()
                    } else {
                        parseStatusCodes(response)
                    }
                }
            })
        return statusResponse
    }

    private fun parseStatusCodes(response: Response<ArrayList<StatusDropDownResponse>>) {
        when (response.code()) {
            404 -> {
            }
            500 -> {
            }
            201 -> {

            }
            else -> {
            }
        }
    }

    fun createCurrency(
        authToken: String,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val costCenterResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.postCurrency(authToken, requestData).enqueue(
            object : Callback<BaseResponse> {
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    costCenterResponse.value = response
                }
            }
        )
        return costCenterResponse
    }

    fun updateCurrency(
        authToken: String,
        id: Int,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val costCenterResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.putCurrency(authToken, id, requestData).enqueue(
            object : Callback<BaseResponse> {
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    costCenterResponse.value = response
                }
            }
        )
        return costCenterResponse
    }
}
