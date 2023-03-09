package com.atocash.activities.admin.expenseTypes

import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.StatusDropDownResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageExpenseTypeViewModel : BaseViewModel<ManageExpenseTypeNavigator>() {

    fun onBack() {  getNavigator().onBack() }
    fun onCreate() {  getNavigator().onCreateExpense() }

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

    fun updateExpenseType(
        authToken: String,
        id: Int,
        requestData: JsonObject
    ): MutableLiveData<BaseResponse> {
        val costCenterResponse = MutableLiveData<BaseResponse>()
        RetrofitClient.instance.apiService.putExpenseTypes(authToken, id, requestData).enqueue(
            object : Callback<BaseResponse> {
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    getNavigator().updateUi(
                        if (response.code() == 200) "Expense Type updated successfully!" else "Expense Type not updated!",
                        response.code() == 200
                    )
                }
            }
        )
        return costCenterResponse
    }

    fun createExpenseType(
        authToken: String,
        requestData: JsonObject
    ): MutableLiveData<BaseResponse> {
        val costCenterResponse = MutableLiveData<BaseResponse>()
        RetrofitClient.instance.apiService.postExpenseTypes(authToken, requestData).enqueue(
            object : Callback<BaseResponse> {
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    getNavigator().updateUi(
                        if (response.code() == 201) "Expense Type created successfully!" else "Expense Type not created!",
                        response.code() == 201
                    )
                }
            }
        )
        return costCenterResponse
    }

}