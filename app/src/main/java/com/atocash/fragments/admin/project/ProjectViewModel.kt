package com.atocash.fragments.admin.project

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.ApiResponse
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.ProjectsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectViewModel : BaseViewModel<ProjectNavigator>() {

    var isLoading = ObservableField<Boolean>()

    fun getProject(auth: String, id: Int): MutableLiveData<Response<ProjectsResponse>> {
        val responseData = MutableLiveData<Response<ProjectsResponse>>()
        RetrofitClient.instance.apiService.getProject(auth, id)
            .enqueue(object : Callback<ProjectsResponse> {
                override fun onFailure(
                    call: Call<ProjectsResponse>,
                    t: Throwable
                ) {
                    throwError(t)
                }

                override fun onResponse(
                    call: Call<ProjectsResponse>,
                    response: Response<ProjectsResponse>
                ) {
                    responseData.value = response
                }

            })
        return responseData
    }

    fun getProjectList(auth: String): MutableLiveData<Response<ArrayList<ProjectsResponse>>> {
        val responseData = MutableLiveData<Response<ArrayList<ProjectsResponse>>>()
        RetrofitClient.instance.apiService.getProjects(auth)
            .enqueue(object : Callback<ArrayList<ProjectsResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<ProjectsResponse>>,
                    t: Throwable
                ) {
                    throwError(t)
                }

                override fun onResponse(
                    call: Call<ArrayList<ProjectsResponse>>,
                    response: Response<ArrayList<ProjectsResponse>>
                ) {
                    responseData.value = response
                }

            })
        return responseData
    }

    fun deleteProject(auth: String, item: ProjectsResponse) : MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.deleteProject(auth, item.id).enqueue(
            object : Callback<BaseResponse> {
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(throwable = t)
                }
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    responseData.value = response
                }

            }
        )
        return responseData
    }

}