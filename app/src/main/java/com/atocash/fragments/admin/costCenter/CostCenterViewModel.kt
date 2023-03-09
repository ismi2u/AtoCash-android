package com.atocash.fragments.admin.costCenter

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.ApiResponse
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.CostCenterResponse
import com.atocash.utils.extensions.printLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CostCenterViewModel : BaseViewModel<CostCenterNavigator>() {

    var isLoading = ObservableField<Boolean>(true)

    var costCenterList = MutableLiveData<Response<ArrayList<CostCenterResponse>>>()

    fun getCostCenterList(auth: String): MutableLiveData<Response<ArrayList<CostCenterResponse>>> {
        RetrofitClient.instance.apiService.getCostCenters(auth).enqueue(object :
            Callback<ArrayList<CostCenterResponse>> {
            override fun onFailure(call: Call<ArrayList<CostCenterResponse>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<CostCenterResponse>>,
                response: Response<ArrayList<CostCenterResponse>>
            ) {

                costCenterList.value = response
            }
        })
        return costCenterList
    }

    fun deleteCostCenter(auth: String, item: CostCenterResponse) : MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.deleteCostCenter(auth, item.id).enqueue(
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