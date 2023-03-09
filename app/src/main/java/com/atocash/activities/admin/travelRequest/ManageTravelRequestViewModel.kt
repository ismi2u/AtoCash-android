package com.atocash.activities.admin.travelRequest

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.request.TravelRequestDto
import com.atocash.network.response.AssignedProjectsResponse
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.SubProjectsResponse
import com.atocash.network.response.TasksResponse
import com.atocash.utils.Keys
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageTravelRequestViewModel :
    BaseViewModel<ManageTravelRequestNavigator>() {

    var isProjectEnabled = ObservableField<Boolean>(false)
    var isSubProjectEnabled = ObservableField<Boolean>(false)
    var isTaskEnabled = ObservableField<Boolean>(false)

    fun onBack() {
        getNavigator().onBack()
    }

    fun onCreate() {
        getNavigator().onCreateTravelReq()
    }

    fun onStartDateClicked() {
        getNavigator().onStartDateClick()
    }

    fun onEndDateClicked() {
        getNavigator().onEndDateClick()
    }

    fun editTravelRequest(
        token: String,
        id: Int?,
        travelRequestDto: ManageTravelRequestActivity.TravelRequestDto
    ): MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        id?.let {
            RetrofitClient.instance.apiService.putTravelRequest(token, it, travelRequestDto)
                .enqueue(object :
                    Callback<BaseResponse> {
                    override fun onResponse(
                        call: Call<BaseResponse>,
                        response: Response<BaseResponse>
                    ) {
                        responseData.value = response
                    }

                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        throwError(throwable = t)
                    }
                })
        }
        return responseData
    }

    fun createTravelRequest(
        token: String,
        travelRequestDto: TravelRequestDto
    ): MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.postTravelRequest(token, travelRequestDto)
            .enqueue(object :
                Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    responseData.value = response
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(throwable = t)
                }
            })
        return responseData
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

                override fun onFailure(
                    call: Call<ArrayList<AssignedProjectsResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

            })
        return projectResponse
    }

    fun getSubProjectsForDropDown(
        authToken: String,
        projectId: Int
    ): MutableLiveData<ArrayList<SubProjectsResponse>> {
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

    fun getTasksForDropDown(
        authToken: String,
        subProjectId: Int
    ): MutableLiveData<ArrayList<TasksResponse>> {
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
}
