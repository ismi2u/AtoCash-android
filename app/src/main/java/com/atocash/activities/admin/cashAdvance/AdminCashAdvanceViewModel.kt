package com.atocash.activities.admin.cashAdvance

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.*
import com.atocash.utils.Keys
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminCashAdvanceViewModel : BaseViewModel<AdminCashAdvanceNavigator>() {

    val isProjectEnabled = ObservableField<Boolean>()
    val isSubProjectEnabled = ObservableField<Boolean>()
    val isTaskEnabled = ObservableField<Boolean>()

    fun onCreate() {
        getNavigator().onCreateAdvanceRequest()
    }

    fun onBack() {
        getNavigator().onBack()
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

    fun getAssignedProjectsForDropDown(authToken: String): MutableLiveData<ArrayList<AssignedProjectsResponse>> {
        val projectResponse = MutableLiveData<ArrayList<AssignedProjectsResponse>>()
        val empId = getDataStorage().getString(Keys.UserData.ID).toString().toInt()
        RetrofitClient.instance.apiService.getProjectsAssignedForEmployee(authToken, empId)
            .enqueue(object : Callback<ArrayList<AssignedProjectsResponse>> {
                override fun onResponse(
                    call: Call<ArrayList<AssignedProjectsResponse>>,
                    response: Response<ArrayList<AssignedProjectsResponse>>
                ) {
                    projectResponse.value = response.body()
                }
                override fun onFailure(call: Call<ArrayList<AssignedProjectsResponse>>, t: Throwable) {
                    throwError(throwable = t)
                }

            })
        return projectResponse
    }

    fun getSubProjectsForDropDown(authToken: String, projectId: Int): MutableLiveData<ArrayList<SubProjectsResponse>> {
        val statusResponse = MutableLiveData<ArrayList<SubProjectsResponse>>()
        RetrofitClient.instance.apiService.getSubProjectsForProjects(authToken, projectId)
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

    fun getTasksForDropDown(authToken: String, subProjectId: Int): MutableLiveData<ArrayList<TasksResponse>> {
        val statusResponse = MutableLiveData<ArrayList<TasksResponse>>()
        RetrofitClient.instance.apiService.getWorkTasksForSubProject(authToken, subProjectId)
            .enqueue(object : Callback<ArrayList<TasksResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<TasksResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<ArrayList<TasksResponse>>,
                    response: Response<ArrayList<TasksResponse>>
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

    fun createCashAdvance(
        authToken: String,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val costCenterResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.postPettyCashRequest(authToken, requestData).enqueue(
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

    fun updateCashAdvance(
        authToken: String,
        id: Int,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val costCenterResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.putPettyCashRequest(authToken, id, requestData).enqueue(
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