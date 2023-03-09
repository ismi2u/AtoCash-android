package com.atocash.common.activity.expenseReimburseListing

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.atocash.base.common.BaseViewModel
import com.atocash.database.DatabaseHelperImpl
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.ExpenseRaisedForEmployeeResponse
import com.atocash.network.response.ExpenseReimburseListingResponse
import com.atocash.network.response.ExpenseReimburseRequestDto
import com.atocash.utils.extensions.printLog
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExpenseReimburseListingViewModel : BaseViewModel<ExpenseReimburseListingNavigator>() {

    var isLoading = ObservableField<Boolean>()

    fun onSave() {
        getNavigator().onSave()
    }

    fun onSubmit() {
        getNavigator().onSubmit()
    }

    fun onUpdateExpenseMethod() {
        getNavigator().onUpdate()
    }

    fun onAddNew() {
        getNavigator().onAddNew()
    }

    val savedId = MutableLiveData<Long>()
    fun saveToLocalDb(
        dbHelper: DatabaseHelperImpl,
        expenseData: ExpenseRaisedForEmployeeResponse
    ) {
        viewModelScope.launch {
            val id = dbHelper.saveExpenseItem(expenseData)
            savedId.value = id
//            getNavigator().onSaveCompleted()
        }
    }

    fun submitExpenseReimburse(
        token: String,
        jsonData: ExpenseReimburseRequestDto
    ): MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.postExpenseReimburse(token, jsonData).enqueue(
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

    fun getExpenseSubClaims(
        token: String,
        id: Int?
    ): MutableLiveData<Response<ArrayList<ExpenseReimburseListingResponse>>> {
        val responseData = MutableLiveData<Response<ArrayList<ExpenseReimburseListingResponse>>>()
        id?.let {
            RetrofitClient.instance.apiService.getExpenseSubClaims(token, it).enqueue(
                object : Callback<ArrayList<ExpenseReimburseListingResponse>> {
                    override fun onFailure(call: Call<ArrayList<ExpenseReimburseListingResponse>>, t: Throwable) {
                        throwError(throwable = t)
                    }

                    override fun onResponse(
                        call: Call<ArrayList<ExpenseReimburseListingResponse>>,
                        response: Response<ArrayList<ExpenseReimburseListingResponse>>
                    ) {
                        responseData.value = response
                    }

                }
            )
        }
        return responseData
    }

    fun getExpenseSubClaimDetails(token: String, item: ExpenseReimburseListingResponse): MutableLiveData<Response<ArrayList<String>>> {
        val responseData = MutableLiveData<Response<ArrayList<String>>>()
        item.id?.let {
            RetrofitClient.instance.apiService.getExpenseSubClaimsDocs(token, it).enqueue(
                object : Callback<ArrayList<String>> {
                    override fun onFailure(call: Call<ArrayList<String>>, t: Throwable) {
                        throwError(throwable = t)
                    }

                    override fun onResponse(
                        call: Call<ArrayList<String>>,
                        response: Response<ArrayList<String>>
                    ) {
                        responseData.value = response
                    }
                }
            )
        }
        return responseData
    }

    val updatedId = MutableLiveData<Long>()
    fun updateRecord(dbHelper: DatabaseHelperImpl, response: ExpenseRaisedForEmployeeResponse) {
        viewModelScope.launch {
            val id = dbHelper.updateExpenseItem(pendingExpenseItem = response)
            updatedId.value = id
        }
    }
}