package com.atocash.activities.admin.department

import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.CostCenterDropDownResponse
import com.atocash.network.response.StatusDropDownResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDepartmentViewModel :
    BaseViewModel<AdminDepartmentNavigator>() {

    fun onCreate() {
        getNavigator().onCreate()
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

    fun createDepartment(
        authToken: String,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val deptResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.postDepartment(authToken, requestData).enqueue(
            object : Callback<BaseResponse> {
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    deptResponse.value = response
                }
            }
        )
        return deptResponse
    }

    fun updateDepartment(
        authToken: String,
        id: Int,
        requestData: JsonObject
    ): MutableLiveData<Response<BaseResponse>> {
        val deptResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.putDepartment(authToken, id, requestData).enqueue(
            object : Callback<BaseResponse> {
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    deptResponse.value = response
                }
            }
        )
        return deptResponse
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

}
