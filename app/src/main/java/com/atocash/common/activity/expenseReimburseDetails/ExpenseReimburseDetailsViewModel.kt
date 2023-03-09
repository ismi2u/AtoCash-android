package com.atocash.common.activity.expenseReimburseDetails

import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.ExpenseReimburseListingResponse
import com.atocash.network.response.TimeLineItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExpenseReimburseDetailsViewModel : BaseViewModel<ExpenseReimburseDetailsNavigator>() {

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

    fun getApprovalFlowForExpenseReimburseRequest(
        auth: String,
        id: Int
    ): MutableLiveData<Response<ArrayList<TimeLineItem>>> {
        val costCenterList = MutableLiveData<Response<ArrayList<TimeLineItem>>>()
        RetrofitClient.instance.apiService.approvalFlowForExpenseReimburseRequest(auth, id).enqueue(object :
            Callback<ArrayList<TimeLineItem>> {
            override fun onFailure(call: Call<ArrayList<TimeLineItem>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<TimeLineItem>>,
                response: Response<ArrayList<TimeLineItem>>
            ) {

                costCenterList.value = response
            }
        })
        return costCenterList
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
}