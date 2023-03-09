package com.atocash.fragments.admin.assignProject

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.ProjectManagerResponse
import com.atocash.network.response.ProjectsResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssignProjectViewModel : BaseViewModel<AssignProjectNavigator>() {

    var isLoading = ObservableField<Boolean>()

    fun onAssign() {
        getNavigator().onAssignProject()
    }

    fun assignProject(
        authToken: String,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val apiResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.assignProject(authToken, requestData).enqueue(
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

    fun getProjectsForDropDown(authToken: String): MutableLiveData<java.util.ArrayList<ProjectsResponse>> {
        val statusResponse = MutableLiveData<java.util.ArrayList<ProjectsResponse>>()
        RetrofitClient.instance.apiService.getProjectsForDropDown(authToken)
            .enqueue(object : Callback<java.util.ArrayList<ProjectsResponse>> {
                override fun onFailure(
                    call: Call<java.util.ArrayList<ProjectsResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<java.util.ArrayList<ProjectsResponse>>,
                    response: Response<java.util.ArrayList<ProjectsResponse>>
                ) {
                    if (response.code() == 200) {
                        statusResponse.value = response.body()
                    } else {
                        statusResponse.value = java.util.ArrayList()
                    }
                }
            })
        return statusResponse
    }
}