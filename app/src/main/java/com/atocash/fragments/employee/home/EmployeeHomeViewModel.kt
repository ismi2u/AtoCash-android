package com.atocash.fragments.employee.home

import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.*
import com.atocash.network.response.dashboard.CashAdvanceCountResponseData
import com.atocash.utils.Keys
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeHomeViewModel : BaseViewModel<EmployeeHomeNavigator>() {

    fun loadPettyCashCount(token: String): MutableLiveData<Response<PettyCashCountResponse>> {
        val pettyCashCountResponse = MutableLiveData<Response<PettyCashCountResponse>>()
        val empId = getDataStorage().getString(Keys.UserData.ID).toString().toInt()
        RetrofitClient.instance.apiService.countPettyCashRequestRaisedByEmployee(token, empId)
            .enqueue(object : Callback<PettyCashCountResponse> {
                override fun onResponse(
                    call: Call<PettyCashCountResponse>,
                    response: Response<PettyCashCountResponse>
                ) {
                    pettyCashCountResponse.value = response
                }

                override fun onFailure(call: Call<PettyCashCountResponse>, t: Throwable) {
                    throwError(t)
                }
            })
        return pettyCashCountResponse
    }

    fun loadExpenseCount(token: String): MutableLiveData<Response<ExpenseReimburseCountResponse>> {
        val pettyCashCountResponse = MutableLiveData<Response<ExpenseReimburseCountResponse>>()
        val empId = getDataStorage().getString(Keys.UserData.ID).toString().toInt()
        RetrofitClient.instance.apiService.countExpenseRequestRaisedByEmployee(token, empId)
            .enqueue(object : Callback<ExpenseReimburseCountResponse> {
                override fun onResponse(
                    call: Call<ExpenseReimburseCountResponse>,
                    response: Response<ExpenseReimburseCountResponse>
                ) {
                    pettyCashCountResponse.value = response
                }

                override fun onFailure(call: Call<ExpenseReimburseCountResponse>, t: Throwable) {
                    throwError(t)
                }
            })
        return pettyCashCountResponse
    }

    fun loadTravelRequest(token: String): MutableLiveData<Response<ExpenseReimburseCountResponse>> {
        val pettyCashCountResponse = MutableLiveData<Response<ExpenseReimburseCountResponse>>()
        val empId = getDataStorage().getString(Keys.UserData.ID).toString().toInt()
        RetrofitClient.instance.apiService.countTravelRequestRaisedByEmployee(token, empId)
            .enqueue(object : Callback<ExpenseReimburseCountResponse> {
                override fun onResponse(
                    call: Call<ExpenseReimburseCountResponse>,
                    response: Response<ExpenseReimburseCountResponse>
                ) {
                    pettyCashCountResponse.value = response
                }

                override fun onFailure(call: Call<ExpenseReimburseCountResponse>, t: Throwable) {
                    throwError(t)
                }
            })
        return pettyCashCountResponse
    }

    fun loadPettyCashBalance(token: String): MutableLiveData<Response<PettyCashBalanceResponse>> {
        val pettyCashCountResponse = MutableLiveData<Response<PettyCashBalanceResponse>>()
        val empId = getDataStorage().getString(Keys.UserData.ID).toString().toInt()
        RetrofitClient.instance.apiService.getPettyCashBalance(token, empId)
            .enqueue(object : Callback<PettyCashBalanceResponse> {
                override fun onResponse(
                    call: Call<PettyCashBalanceResponse>,
                    response: Response<PettyCashBalanceResponse>
                ) {
                    pettyCashCountResponse.value = response
                }

                override fun onFailure(call: Call<PettyCashBalanceResponse>, t: Throwable) {
                    throwError(t)
                }
            })
        return pettyCashCountResponse
    }

    fun loadPettyCashBalanceVsAdvanced(token: String): MutableLiveData<Response<PettyCashBalanceVsAdvancedResponse>> {
        val pettyCashCountResponse = MutableLiveData<Response<PettyCashBalanceVsAdvancedResponse>>()
        val empId = getDataStorage().getString(Keys.UserData.ID).toString().toInt()
        RetrofitClient.instance.apiService.getPettyCashBalanceVsAdvanced(token, empId)
            .enqueue(object : Callback<PettyCashBalanceVsAdvancedResponse> {
                override fun onResponse(
                    call: Call<PettyCashBalanceVsAdvancedResponse>,
                    response: Response<PettyCashBalanceVsAdvancedResponse>
                ) {
                    pettyCashCountResponse.value = response
                }

                override fun onFailure(
                    call: Call<PettyCashBalanceVsAdvancedResponse>,
                    t: Throwable
                ) {
                    throwError(t)
                }
            })
        return pettyCashCountResponse
    }

    fun getEmployeeInfo(token: String): MutableLiveData<Response<EmployeesResponse>> {
        val empId = getDataStorage().getString(Keys.UserData.ID).toString().toInt()
        val responseData = MutableLiveData<Response<EmployeesResponse>>()
        RetrofitClient.instance.apiService.getEmployee(token, empId).enqueue(
            object : Callback<EmployeesResponse> {
                override fun onResponse(
                    call: Call<EmployeesResponse>,
                    response: Response<EmployeesResponse>
                ) {
                    responseData.value = response
                }

                override fun onFailure(call: Call<EmployeesResponse>, t: Throwable) {
                    throwError(t)
                }
            }
        )
        return responseData
    }

    fun getWalletDetails(token: String): MutableLiveData<Response<MaxLimitBalanceAndCashInHand>> {
        val responseData = MutableLiveData<Response<MaxLimitBalanceAndCashInHand>>()
        val empId = getDataStorage().getString(Keys.UserData.ID).toString().toInt()
        RetrofitClient.instance.apiService.getCurrentBalanceAndCashInHand(token, empId).enqueue(
            object : Callback<MaxLimitBalanceAndCashInHand> {
                override fun onResponse(
                    call: Call<MaxLimitBalanceAndCashInHand>,
                    response: Response<MaxLimitBalanceAndCashInHand>
                ) {
                    responseData.value = response
                }

                override fun onFailure(
                    call: Call<MaxLimitBalanceAndCashInHand>,
                    t: Throwable
                ) {
                    throwError(t)
                }

            }
        )
        return responseData
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

    fun getCashAdvances(token: String): MutableLiveData<Response<CashAdvanceCountResponseData>> {
        val empId = getDataStorage().getString(Keys.UserData.ID).toString().toInt()
        val expenseList = MutableLiveData<Response<CashAdvanceCountResponseData>>()
        RetrofitClient.instance.apiService.getCashAdvanceCount(token, empId).enqueue(object :
            Callback<CashAdvanceCountResponseData> {
            override fun onFailure(call: Call<CashAdvanceCountResponseData>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<CashAdvanceCountResponseData>,
                response: Response<CashAdvanceCountResponseData>
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
}