package com.atocash.fragments.admin.subProject

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.ApiResponse
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.ProjectsResponse
import com.atocash.network.response.SubProjectsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubProjectViewModel :
    BaseViewModel<SubProjectNavigator>() {

    var isLoading = ObservableField<Boolean>()

    fun getProjectList(auth: String): MutableLiveData<Response<ArrayList<SubProjectsResponse>>> {
        val responseData = MutableLiveData<Response<ArrayList<SubProjectsResponse>>>()
        RetrofitClient.instance.apiService.getSubProjects(auth)
            .enqueue(object : Callback<ArrayList<SubProjectsResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<SubProjectsResponse>>,
                    t: Throwable
                ) {
                    throwError(t)
                }

                override fun onResponse(
                    call: Call<ArrayList<SubProjectsResponse>>,
                    response: Response<ArrayList<SubProjectsResponse>>
                ) {
                    responseData.value = response
                }

            })
        return responseData
    }

    fun deleteSubProject(auth: String, item: SubProjectsResponse) : MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.deleteSubProject(auth, item.id).enqueue(
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