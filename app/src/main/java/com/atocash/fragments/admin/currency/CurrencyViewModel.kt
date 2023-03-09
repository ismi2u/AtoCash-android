package com.atocash.fragments.admin.currency

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.ApiResponse
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.CostCenterResponse
import com.atocash.network.response.CurrencyResponse
import com.atocash.utils.extensions.printLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrencyViewModel : BaseViewModel<CurrencyNavigator>() {

    var isLoading = ObservableField<Boolean>(true)


    fun getCurrencyList(auth: String): MutableLiveData<Response<ArrayList<CurrencyResponse>>> {
        val costCenterList = MutableLiveData<Response<ArrayList<CurrencyResponse>>>()
        RetrofitClient.instance.apiService.getCurrencyList(auth).enqueue(object :
            Callback<ArrayList<CurrencyResponse>> {
            override fun onFailure(call: Call<ArrayList<CurrencyResponse>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<CurrencyResponse>>,
                response: Response<ArrayList<CurrencyResponse>>
            ) {

                costCenterList.value = response
            }
        })
        return costCenterList
    }

    fun deleteCurrency(auth: String, item: CurrencyResponse) : MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.deleteCurrency(auth, item.id).enqueue(
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