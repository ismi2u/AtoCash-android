package com.atocash.common.fragments.expenseReimburse.submitted

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.ExpenseRaisedForEmployeeResponse
import com.atocash.utils.Keys
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubmittedExpenseReimburseViewModel : BaseViewModel<SubmittedExpenseReimburseNavigator>() {

    var isLoading = ObservableField<Boolean>(true)

    fun getExpenseReimburseList(authToken: String): MutableLiveData<Response<ArrayList<ExpenseRaisedForEmployeeResponse>>> {
        val projectResponse =
            MutableLiveData<Response<ArrayList<ExpenseRaisedForEmployeeResponse>>>()
        val empId = getDataStorage().getString(Keys.UserData.ID).toString().toInt()
        RetrofitClient.instance.apiService.getExpenseReimburseRaisedForEmployee(authToken, empId)
            .enqueue(object : Callback<ArrayList<ExpenseRaisedForEmployeeResponse>> {
                override fun onResponse(
                    call: Call<ArrayList<ExpenseRaisedForEmployeeResponse>>,
                    response: Response<ArrayList<ExpenseRaisedForEmployeeResponse>>
                ) {
                    projectResponse.value = response
                }

                override fun onFailure(
                    call: Call<ArrayList<ExpenseRaisedForEmployeeResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

            })
        return projectResponse
    }

    fun deleteRequest(authToken: String, item: ExpenseRaisedForEmployeeResponse)
            : MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        item.id?.let {
            RetrofitClient.instance.apiService.deleteExpenseReimburseRequest(
                authToken,
                it
            ).enqueue(
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
        }
        return responseData
    }
}