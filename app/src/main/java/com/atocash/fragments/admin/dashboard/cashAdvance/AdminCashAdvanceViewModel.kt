package com.atocash.fragments.admin.dashboard.cashAdvance

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
import com.atocash.utils.Keys
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminCashAdvanceViewModel : BaseViewModel<AdminCashAdvanceNavigator>() {

    var isLoading = ObservableField<Boolean>(false)

    fun onAddCashAdvance() {
        getNavigator().onAddCashAdvance()
    }

    fun getCashAdvanceRequest(auth: String): MutableLiveData<Response<ArrayList<PettyCashResponse>>> {
        val empId = getDataStorage().getString(Keys.UserData.ID).toString().toInt()
        val costCenterList = MutableLiveData<Response<ArrayList<PettyCashResponse>>>()
        RetrofitClient.instance.apiService.getPettyCashRequestRaisedForEmployee(auth, empId).enqueue(object :
            Callback<ArrayList<PettyCashResponse>> {
            override fun onFailure(call: Call<ArrayList<PettyCashResponse>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<PettyCashResponse>>,
                response: Response<ArrayList<PettyCashResponse>>
            ) {

                costCenterList.value = response
            }
        })
        return costCenterList
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