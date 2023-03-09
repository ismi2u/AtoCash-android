package com.atocash.fragments.admin.expense

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.ApiResponse
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.DepartmentResponse
import com.atocash.network.response.ExpenseTypeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExpenseTypesViewModel : BaseViewModel<ExpenseTypesNavigator>() {

    var isLoading = ObservableField<Boolean>()

    var expenseTypeResponse = MutableLiveData<ArrayList<ExpenseTypeResponse>>()

    fun getExpenseTypesList(auth: String) : MutableLiveData<ArrayList<ExpenseTypeResponse>> {
        RetrofitClient.instance.apiService.getExpenseTypes(auth).enqueue(object :
            Callback<ArrayList<ExpenseTypeResponse>> {
            override fun onFailure(call: Call<ArrayList<ExpenseTypeResponse>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<ExpenseTypeResponse>>,
                response: Response<ArrayList<ExpenseTypeResponse>>
            ) {
                if (response.code() == 401) {
                    expenseTypeResponse.value = ArrayList()
                } else if (response.code() == 200) {
                    expenseTypeResponse.value = response.body()
                }
            }
        })
        return expenseTypeResponse
    }

    fun deleteExpenseTypes(auth: String, item: ExpenseTypeResponse) : MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.deleteExpenseTypes(auth, item.id).enqueue(
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