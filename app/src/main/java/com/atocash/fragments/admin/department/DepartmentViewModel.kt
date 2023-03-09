package com.atocash.fragments.admin.department

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.ApiResponse
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.CostCenterResponse
import com.atocash.network.response.DepartmentResponse
import com.atocash.utils.extensions.printLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DepartmentViewModel : BaseViewModel<DepartmentNavigator>() {

    var isLoading = ObservableField<Boolean>(true)

    var departmentResponseList = MutableLiveData<ArrayList<DepartmentResponse>>()
    fun getDepartmentsList(auth: String) : MutableLiveData<ArrayList<DepartmentResponse>> {
        RetrofitClient.instance.apiService.getAllDepartmentsList(auth).enqueue(object : Callback<ArrayList<DepartmentResponse>> {

            override fun onFailure(call: Call<ArrayList<DepartmentResponse>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<DepartmentResponse>>,
                response: Response<ArrayList<DepartmentResponse>>
            ) {
                if(response.code() == 401) {
                    departmentResponseList.value = ArrayList()
                } else if(response.code() == 200) {
                    departmentResponseList.value = response.body()
                }
            }
        })
        return departmentResponseList
    }

    fun deleteDepartment(auth: String, item: DepartmentResponse) : MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.deleteDepartment(auth, item.id).enqueue(
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