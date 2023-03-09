package com.atocash.activities.admin.jobRoles

import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.ApiResponse
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageJobRolesViewModel : BaseViewModel<ManageJobRolesNavigator>() {

    fun onBack() {  getNavigator().onBack() }
    fun onCreate() {  getNavigator().onCreate() }

    fun createJobRoles(
        authToken: String,
        requestData: JsonObject
    ): MutableLiveData<Response<ApiResponse<BaseResponse>>> {
        val costCenterResponse = MutableLiveData<Response<ApiResponse<BaseResponse>>>()
        RetrofitClient.instance.apiService.postJobRoles(authToken, requestData).enqueue(
            object : Callback<Response<ApiResponse<BaseResponse>>> {
                override fun onFailure(call: Call<Response<ApiResponse<BaseResponse>>>, t: Throwable) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<Response<ApiResponse<BaseResponse>>>,
                    response: Response<Response<ApiResponse<BaseResponse>>>
                ) {
                    if (response.code() == 201) {
                        costCenterResponse.value = response.body()
                    }
                }
            }
        )
        return costCenterResponse
    }

    fun updateJobRoles(
        authToken: String,
        id: Int,
        requestData: JsonObject
    ): MutableLiveData<Response<ApiResponse<BaseResponse>>> {
        val costCenterResponse = MutableLiveData<Response<ApiResponse<BaseResponse>>>()
        RetrofitClient.instance.apiService.putJobRoles(authToken, id, requestData).enqueue(
            object : Callback<Response<ApiResponse<BaseResponse>>> {
                override fun onFailure(call: Call<Response<ApiResponse<BaseResponse>>>, t: Throwable) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<Response<ApiResponse<BaseResponse>>>,
                    response: Response<Response<ApiResponse<BaseResponse>>>
                ) {
                    if (response.code() == 201) {
                        costCenterResponse.value = response.body()
                    }
                }
            }
        )
        return costCenterResponse
    }
}