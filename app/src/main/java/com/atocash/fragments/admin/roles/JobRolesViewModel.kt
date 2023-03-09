package com.atocash.fragments.admin.roles

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.ApiResponse
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.CostCenterResponse
import com.atocash.network.response.JobRolesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobRolesViewModel : BaseViewModel<JobRolesNavigator>() {

    val isLoading = ObservableField<Boolean>()

    fun getRoles(auth: String): MutableLiveData<Response<ArrayList<JobRolesResponse>>> {
        val responseData = MutableLiveData<Response<ArrayList<JobRolesResponse>>>()
        RetrofitClient.instance.apiService.getJobRoles(auth)
            .enqueue(object : Callback<ArrayList<JobRolesResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<JobRolesResponse>>,
                    t: Throwable
                ) {
                    throwError(t)
                }

                override fun onResponse(
                    call: Call<ArrayList<JobRolesResponse>>,
                    response: Response<ArrayList<JobRolesResponse>>
                ) {
                    responseData.value = response
                }

            })
        return responseData
    }

    fun deleteJobRoles(auth: String, item: JobRolesResponse) : MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.deleteJobRoles(auth, item.id).enqueue(
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