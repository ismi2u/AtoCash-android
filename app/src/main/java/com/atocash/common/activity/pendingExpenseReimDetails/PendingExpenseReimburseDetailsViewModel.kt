package com.atocash.common.activity.pendingExpenseReimDetails

import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.ExpenseReimburseListingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PendingExpenseReimburseDetailsViewModel:
    BaseViewModel<PendingExpenseReimburseDetailsNavigator>() {

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
}