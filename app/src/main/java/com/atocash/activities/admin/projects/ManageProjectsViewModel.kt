package com.atocash.activities.admin.projects

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.*
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageProjectsViewModel :
    BaseViewModel<ManageProjectsNavigator>() {

    var isProject = ObservableField<Boolean>()

    fun onCreate() {
        getNavigator().onCreateProject()
    }

    fun onBack() {
        getNavigator().onBack()
    }

    fun getCostCenterForDropDown(authToken: String): MutableLiveData<ArrayList<CostCenterDropDownResponse>> {
        val statusResponse = MutableLiveData<ArrayList<CostCenterDropDownResponse>>()
        RetrofitClient.instance.apiService.getCostCenterForDropDown(authToken)
            .enqueue(object : Callback<ArrayList<CostCenterDropDownResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<CostCenterDropDownResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<ArrayList<CostCenterDropDownResponse>>,
                    response: Response<ArrayList<CostCenterDropDownResponse>>
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

    fun getProjectsManagersForDropDown(authToken: String): MutableLiveData<ArrayList<ProjectManagerResponse>> {
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

    fun getProjectsForDropDown(authToken: String): MutableLiveData<ArrayList<ProjectsResponse>> {
        val statusResponse = MutableLiveData<ArrayList<ProjectsResponse>>()
        RetrofitClient.instance.apiService.getProjectsForDropDown(authToken)
            .enqueue(object : Callback<ArrayList<ProjectsResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<ProjectsResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<ArrayList<ProjectsResponse>>,
                    response: Response<ArrayList<ProjectsResponse>>
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
                    if (response.code() == 200) {
                        statusResponse.value = response.body()
                    }
                }
            })
        return statusResponse
    }

    fun createProject(
        authToken: String,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val apiResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.postProject(authToken, requestData).enqueue(
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

    fun updateProject(
        authToken: String,
        id: Int,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val apiResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.putProject(authToken, id, requestData).enqueue(
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

    fun createSubProject(
        authToken: String,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val apiResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.postSubProject(authToken, requestData).enqueue(
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

    fun updateSubProject(
        authToken: String,
        id: Int,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val apiResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.putSubProject(authToken, id, requestData).enqueue(
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
