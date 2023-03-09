package com.atocash.fragments.admin.dashboard.travelReq

import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import com.atocash.adapter.MyPagerAdapter
import com.atocash.base.common.BaseViewModel
import com.atocash.fragments.admin.approval.group.ApprovalGroupFragment
import com.atocash.fragments.admin.approval.level.ApprovalLevelFragment
import com.atocash.fragments.admin.approval.roleMap.ApprovalRoleMapFragment
import com.atocash.fragments.admin.approval.status.ApprovalStatusFragment
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.CostCenterResponse
import com.atocash.network.response.PettyCashResponse
import com.atocash.network.response.TravelResponse
import com.atocash.utils.Keys
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminTravelRequestViewModel : BaseViewModel<AdminTravelRequestNavigator>() {

    var isLoading = ObservableField<Boolean>(true)

    fun onTravelRequest() {
        getNavigator().onTravelRequest()
    }

    fun deleteTravelRequest(auth: String, item: TravelResponse) : MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        item.id?.let {
            RetrofitClient.instance.apiService.deleteTravelRequest(auth, it).enqueue(
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
        }
        return responseData
    }

    fun getTravelRequest(auth: String): MutableLiveData<Response<ArrayList<TravelResponse>>> {
        val empId = getDataStorage().getString(Keys.UserData.ID).toString().toInt()
        val travelList = MutableLiveData<Response<ArrayList<TravelResponse>>>()
        RetrofitClient.instance.apiService.getTravelRequest(auth, empId).enqueue(object :
            Callback<ArrayList<TravelResponse>> {
            override fun onFailure(call: Call<ArrayList<TravelResponse>>, t: Throwable) {
                throwError(throwable = t)
            }

            override fun onResponse(
                call: Call<ArrayList<TravelResponse>>,
                response: Response<ArrayList<TravelResponse>>
            ) {

                travelList.value = response
            }
        })
        return travelList
    }
}