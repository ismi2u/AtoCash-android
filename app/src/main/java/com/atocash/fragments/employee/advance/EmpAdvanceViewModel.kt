package com.atocash.fragments.employee.advance

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.PettyCashResponse
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmpAdvanceViewModel : BaseViewModel<EmpAdvanceNavigator>() {

    var isLoading = ObservableField<Boolean>()

    val responseList = MutableLiveData<Response<ArrayList<PettyCashResponse>>>()

    fun getCashAdvanceRequest(auth: String): MutableLiveData<Response<ArrayList<PettyCashResponse>>> {
        val empId = getDataStorage().getString(Keys.UserData.ID).toString().toInt()
        RetrofitClient.instance.apiService.getPettyCashRequestRaisedForEmployee(auth, empId).enqueue(object :
            Callback<ArrayList<PettyCashResponse>> {
            override fun onFailure(call: Call<ArrayList<PettyCashResponse>>, t: Throwable) {
                throwError(t)

            }

            override fun onResponse(
                call: Call<ArrayList<PettyCashResponse>>,
                response: Response<ArrayList<PettyCashResponse>>
            ) {
                printLog("petty cash reponse loaded")
                responseList.value = response
            }
        })
        return responseList
    }

    fun deleteCashAdvance(auth: String, item: PettyCashResponse) : MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.deletePettyCashRequest(auth, item.id.toString().toInt()).enqueue(
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