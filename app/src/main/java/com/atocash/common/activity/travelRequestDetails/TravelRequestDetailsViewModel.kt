package com.atocash.common.activity.travelRequestDetails

import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.TimeLineItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TravelRequestDetailsViewModel: BaseViewModel<TravelRequestDetailNavigator>() {
    fun getApprovalFlowForRequest(auth: String, id: Int): MutableLiveData<Response<ArrayList<TimeLineItem>>> {
        val costCenterList = MutableLiveData<Response<ArrayList<TimeLineItem>>>()
        RetrofitClient.instance.apiService.approvalFlowForTravelRequest(auth, id).enqueue(object :
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
}