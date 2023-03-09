package com.atocash.activities.admin.empTypes

import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageEmployeeViewModel :
    BaseViewModel<ManageEmployeeNavigator>() {

    fun onCreate() {
        getNavigator().onCreateCostCenter()
    }

    fun onBack() {
        getNavigator().onBack()
    }

    fun createOrUpdateCostCenter(token: String, dataJson: JsonObject) {

    }

    fun createEmployementType(
        authToken: String,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val costCenterResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.postEmploymentTypes(authToken, requestData).enqueue(
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

    fun updateEmployementType(
        authToken: String,
        id: Int,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val costCenterResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.putEmploymentTypes(authToken, id, requestData).enqueue(
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

    fun createEmployeeType(
        authToken: String,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val costCenterResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.postEmployee(authToken, requestData).enqueue(
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

    fun updateEmployeeType(
        authToken: String,
        id: Int,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val costCenterResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.putEmployee(authToken, id, requestData).enqueue(
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
