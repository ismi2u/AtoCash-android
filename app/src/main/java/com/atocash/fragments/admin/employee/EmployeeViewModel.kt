package com.atocash.fragments.admin.employee

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.ApiResponse
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.CostCenterResponse
import com.atocash.network.response.EmployeesResponse
import com.atocash.network.response.JobRolesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeViewModel : BaseViewModel<EmployeeNavigator>() {

    var isLoading = ObservableField<Boolean>()

    fun getEmployees(auth: String): MutableLiveData<Response<ArrayList<EmployeesResponse>>> {
        val responseData = MutableLiveData<Response<ArrayList<EmployeesResponse>>>()
        RetrofitClient.instance.apiService.getEmployees(auth)
            .enqueue(object : Callback<ArrayList<EmployeesResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<EmployeesResponse>>,
                    t: Throwable
                ) {
                    throwError(t)
                }

                override fun onResponse(
                    call: Call<ArrayList<EmployeesResponse>>,
                    response: Response<ArrayList<EmployeesResponse>>
                ) {
                    responseData.value = response
                }

            })
        return responseData
    }

    fun deleteEmployee(auth: String, item: EmployeesResponse) : MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.deleteEmployee(auth, item.id).enqueue(
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