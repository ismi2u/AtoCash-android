package com.atocash.activities.admin.common

import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.*
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageAdminViewModel :
    BaseViewModel<ManageAdminNavigator>() {

    fun onCreate() {
        getNavigator().onCreateItem()
    }

    fun onBack() {
        getNavigator().onBack()
    }

    fun getSubProjectsForDropDown(authToken: String): MutableLiveData<ArrayList<SubProjectsResponse>> {
        val statusResponse = MutableLiveData<ArrayList<SubProjectsResponse>>()
        RetrofitClient.instance.apiService.getSubProjectsForDropDown(authToken)
            .enqueue(object : Callback<ArrayList<SubProjectsResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<SubProjectsResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<ArrayList<SubProjectsResponse>>,
                    response: Response<ArrayList<SubProjectsResponse>>
                ) {
                    if (response.code() == 200) {
                        statusResponse.value = response.body()
                    } else {
                        statusResponse.value = ArrayList()
                    }
                }
            })
        return statusResponse
    }

    fun createTask(
        authToken: String,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val apiResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.postWorkTask(authToken, requestData).enqueue(
            object : Callback<BaseResponse> {
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    apiResponse.value = response
                }
            }
        )
        return apiResponse
    }

    fun updateTask(
        authToken: String,
        id: Int,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val apiResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.putWorkTask(authToken, id, requestData).enqueue(
            object : Callback<BaseResponse> {
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    apiResponse.value = response
                }
            }
        )
        return apiResponse
    }

   /*---------------*/

    fun getEmployeesForDropDown(authToken: String): MutableLiveData<ArrayList<ProjectManagerResponse>> {
        val statusResponse = MutableLiveData<ArrayList<ProjectManagerResponse>>()
        RetrofitClient.instance.apiService.getEmployeesForDropDown(authToken)
            .enqueue(object : Callback<ArrayList<ProjectManagerResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<ProjectManagerResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<ArrayList<ProjectManagerResponse>>,
                    response: Response<ArrayList<ProjectManagerResponse>>
                ) {
                    if (response.code() == 200) {
                        statusResponse.value = response.body()
                    } else {
                        statusResponse.value = ArrayList()
                    }
                }
            })
        return statusResponse
    }

    fun createUser(
        authToken: String,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val apiResponse = MutableLiveData<Response<BaseResponse>>()
//        RetrofitClient.instance.apiService.postUser(authToken, requestData).enqueue(
//            object : Callback<BaseResponse> {
//                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
//                    throwError(throwable = t)
//                }
//
//                override fun onResponse(
//                    call: Call<BaseResponse>,
//                    response: Response<BaseResponse>
//                ) {
//                    apiResponse.value = response
//                }
//            }
//        )
        return apiResponse
    }

    //todo change APi calls
    fun updateUser(
        authToken: String,
        id: Int,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val apiResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.putUser(authToken, id, requestData).enqueue(
            object : Callback<BaseResponse> {
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    apiResponse.value = response
                }
            }
        )
        return apiResponse
    }

    /*------------------------*/

    fun createUserRole(
        authToken: String,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val apiResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.postWorkTask(authToken, requestData).enqueue(
            object : Callback<BaseResponse> {
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    apiResponse.value = response
                }
            }
        )
        return apiResponse
    }

    fun updateUserRole(
        authToken: String,
        id: Int,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val apiResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.putWorkTask(authToken, id, requestData).enqueue(
            object : Callback<BaseResponse> {
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    apiResponse.value = response
                }
            }
        )
        return apiResponse
    }

}
