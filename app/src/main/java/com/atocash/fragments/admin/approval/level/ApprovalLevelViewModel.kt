package com.atocash.fragments.admin.approval.level

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.ApprovalBaseResponse
import com.atocash.network.response.BaseResponse
import com.atocash.utils.extensions.printLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApprovalLevelViewModel : BaseViewModel<ApprovalLevelNavigator>() {

    var isLoading = ObservableField<Boolean>()

    fun getApprovalLevels(auth: String): MutableLiveData<ArrayList<ApprovalBaseResponse>> {
        val costCenterList = MutableLiveData<ArrayList<ApprovalBaseResponse>>()
        RetrofitClient.instance.apiService.getApprovalLevels(auth).enqueue(object :
            Callback<ArrayList<ApprovalBaseResponse>> {
            override fun onFailure(call: Call<ArrayList<ApprovalBaseResponse>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<ApprovalBaseResponse>>,
                response: Response<ArrayList<ApprovalBaseResponse>>
            ) {
                if(response.code() == 200) {
                    costCenterList.value = response.body()
                } else {
                    val errorResponse = response.body() as BaseResponse
                    printLog("Error: ${errorResponse.message}")
                }
            }
        })
        return costCenterList
    }

    fun deleteApprovalLevel(auth: String, item: ApprovalBaseResponse) : MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.deleteApprovalLevel(auth, item.id).enqueue(
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