package com.atocash.common.fragments.inbox

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.*
import com.atocash.utils.Keys
import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InboxViewModel : BaseViewModel<InboxNavigator>() {

    var isLoading = ObservableField<Boolean>()

    fun onApprove() {
        getNavigator().onApprove()
    }

    fun onReject() {
        getNavigator().onReject()
    }

    fun onShowDropDown() {
        getNavigator().showDropDown()
    }

    fun getExpenseReimburse(token: String): MutableLiveData<Response<ArrayList<InboxExpenseReimburseResponse>>> {
        val empId = getDataStorage().getString(Keys.UserData.ID).toString().toInt()
        val expenseList = MutableLiveData<Response<ArrayList<InboxExpenseReimburseResponse>>>()
        RetrofitClient.instance.apiService.getExpenseList(token, empId).enqueue(object :
            Callback<ArrayList<InboxExpenseReimburseResponse>> {
            override fun onFailure(
                call: Call<ArrayList<InboxExpenseReimburseResponse>>,
                t: Throwable
            ) {

            }

            override fun onResponse(
                call: Call<ArrayList<InboxExpenseReimburseResponse>>,
                response: Response<ArrayList<InboxExpenseReimburseResponse>>
            ) {

                expenseList.value = response
            }
        })
        return expenseList
    }

    fun getCashAdvances(token: String): MutableLiveData<Response<ArrayList<InboxCashAdvanceResponse>>> {
        val empId = getDataStorage().getString(Keys.UserData.ID).toString().toInt()
        val expenseList = MutableLiveData<Response<ArrayList<InboxCashAdvanceResponse>>>()
        RetrofitClient.instance.apiService.getCashAdvanceList(token, empId).enqueue(object :
            Callback<ArrayList<InboxCashAdvanceResponse>> {
            override fun onFailure(call: Call<ArrayList<InboxCashAdvanceResponse>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<InboxCashAdvanceResponse>>,
                response: Response<ArrayList<InboxCashAdvanceResponse>>
            ) {
                expenseList.value = response
            }
        })
        return expenseList
    }

    fun getTravelExpenses(token: String): MutableLiveData<Response<ArrayList<InboxTravelRequestResponse>>> {
        val empId = getDataStorage().getString(Keys.UserData.ID).toString().toInt()
        val expenseList = MutableLiveData<Response<ArrayList<InboxTravelRequestResponse>>>()
        RetrofitClient.instance.apiService.getTravelRequestList(token, empId).enqueue(object :
            Callback<ArrayList<InboxTravelRequestResponse>> {
            override fun onFailure(
                call: Call<ArrayList<InboxTravelRequestResponse>>,
                t: Throwable
            ) {

            }

            override fun onResponse(
                call: Call<ArrayList<InboxTravelRequestResponse>>,
                response: Response<ArrayList<InboxTravelRequestResponse>>
            ) {

                expenseList.value = response
            }
        })
        return expenseList
    }

    fun approveRejectCashAdvances(
        token: String,
        chosenCashAdvanceItems: JsonArray
    ): MutableLiveData<Response<BaseResponse>> {
        val baseResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.approveRejectCashAdvances(token, chosenCashAdvanceItems)
            .enqueue(object : Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    baseResponse.value = response
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(t)
                }
            })
        return baseResponse
    }

    fun approveRejectExpense(
        token: String,
        chosenExpenseList: JsonArray
    ): MutableLiveData<Response<BaseResponse>> {
        val baseResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.approveRejectExpenseReimburse(token, chosenExpenseList)
            .enqueue(object : Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    baseResponse.value = response
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(t)
                }
            })
        return baseResponse
    }

    fun approveRejectTravelRequest(
        token: String,
        chosenTravelItems: JsonArray
    ): MutableLiveData<Response<BaseResponse>> {
        val baseResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.approveRejectTravelExpenses(token, chosenTravelItems)
            .enqueue(object : Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    baseResponse.value = response
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(t)
                }
            })
        return baseResponse
    }

    fun getCashDetails(token: String, id: Int): MutableLiveData<Response<PettyCashResponse>> {
        val baseResponse = MutableLiveData<Response<PettyCashResponse>>()
        RetrofitClient.instance.apiService.getPettyCashRequest(token, id)
            .enqueue(object : Callback<PettyCashResponse> {
                override fun onResponse(
                    call: Call<PettyCashResponse>,
                    response: Response<PettyCashResponse>
                ) {
                    baseResponse.value = response
                }

                override fun onFailure(call: Call<PettyCashResponse>, t: Throwable) {
                    throwError(t)
                }
            })
        return baseResponse
    }

    fun getExpenseDetails(token: String, id: Int): MutableLiveData<Response<InboxExpenseReimburseResponse>> {
        val baseResponse = MutableLiveData<Response<InboxExpenseReimburseResponse>>()
        RetrofitClient.instance.apiService.getExpenseReimburseRequest(token, id)
            .enqueue(object : Callback<InboxExpenseReimburseResponse> {
                override fun onResponse(
                    call: Call<InboxExpenseReimburseResponse>,
                    response: Response<InboxExpenseReimburseResponse>
                ) {
                    baseResponse.value = response
                }

                override fun onFailure(call: Call<InboxExpenseReimburseResponse>, t: Throwable) {
                    throwError(t)
                }
            })
        return baseResponse
    }

    fun getTravelRequestDetails(token: String, id: Int): MutableLiveData<Response<TravelResponse>> {
        val baseResponse = MutableLiveData<Response<TravelResponse>>()
        RetrofitClient.instance.apiService.getTravelRequestDetail(token, id).enqueue(
            object : Callback<TravelResponse> {
                override fun onResponse(
                    call: Call<TravelResponse>,
                    response: Response<TravelResponse>
                ) {
                    baseResponse.value = response
                }

                override fun onFailure(call: Call<TravelResponse>, t: Throwable) {
                    throwError(throwable = t)
                }
            }
        )
        return baseResponse
    }
}