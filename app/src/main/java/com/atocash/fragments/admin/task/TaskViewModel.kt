package com.atocash.fragments.admin.task

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.ApiResponse
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.ProjectsResponse
import com.atocash.network.response.TasksResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskViewModel : BaseViewModel<TaskNavigator>() {

    var isLoading = ObservableField<Boolean>()

    fun getWorkTasks(auth: String): MutableLiveData<Response<ArrayList<TasksResponse>>> {
        val responseData = MutableLiveData<Response<ArrayList<TasksResponse>>>()
        RetrofitClient.instance.apiService.getWorkTasks(auth)
            .enqueue(object : Callback<ArrayList<TasksResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<TasksResponse>>,
                    t: Throwable
                ) {
                    throwError(t)
                }

                override fun onResponse(
                    call: Call<ArrayList<TasksResponse>>,
                    response: Response<ArrayList<TasksResponse>>
                ) {
                    responseData.value = response
                }

            })
        return responseData
    }

    fun deleteTasks(auth: String, item: TasksResponse) : MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.deleteWorkTask(auth, item.id).enqueue(
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