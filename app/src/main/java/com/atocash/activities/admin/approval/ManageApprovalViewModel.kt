package com.atocash.activities.admin.approval

import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.ApprovalBaseResponse
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.JobRolesDropDownResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageApprovalViewModel :
    BaseViewModel<ManageApprovalNavigator>() {


    fun onBack() {
        getNavigator().onBack()
    }

    fun createGroup(
        authToken: String,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val costCenterResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.postApprovalGroup(authToken, requestData).enqueue(
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

    fun updateGroup(
        authToken: String,
        id: Int,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val costCenterResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.putApprovalGroup(authToken, id, requestData).enqueue(
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

    fun createStatus(
        authToken: String,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val costCenterResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.postApprovalStatusType(authToken, requestData).enqueue(
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

    fun updateStatus(
        authToken: String,
        id: Int,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val costCenterResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.putApprovalStatusType(authToken, id, requestData).enqueue(
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

    fun createRoleMap(
        authToken: String,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val costCenterResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.postApprovalRoleMap(authToken, requestData).enqueue(
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

    fun updateRoleMap(
        authToken: String,
        id: Int,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val costCenterResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.putApprovalRoleMap(authToken, id, requestData).enqueue(
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

    fun createLevel(
        authToken: String,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val costCenterResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.postApprovalLevel(authToken, requestData).enqueue(
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

    fun updateLevel(
        authToken: String,
        id: Int,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val costCenterResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.putApprovalLevel(authToken, id, requestData).enqueue(
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

    fun getApprovalLevels(authToken: String): MutableLiveData<Response<ArrayList<ApprovalBaseResponse>>> {
        val statusResponse = MutableLiveData<Response<ArrayList<ApprovalBaseResponse>>>()
        RetrofitClient.instance.apiService.getApprovalLevels(authToken)
            .enqueue(object : Callback<ArrayList<ApprovalBaseResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<ApprovalBaseResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<ArrayList<ApprovalBaseResponse>>,
                    response: Response<ArrayList<ApprovalBaseResponse>>
                ) {
                    statusResponse.value = response
                }
            })
        return statusResponse
    }

}
