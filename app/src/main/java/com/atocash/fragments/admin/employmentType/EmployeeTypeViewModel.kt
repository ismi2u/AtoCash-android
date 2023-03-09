package com.atocash.fragments.admin.employmentType

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.ApiResponse
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.EmpTypeModel
import com.atocash.network.response.JobRolesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeTypeViewModel : BaseViewModel<EmployeeTypeNavigator>() {

    var isLoading = ObservableField<Boolean>()

    fun getEmployeeTypes(auth: String): MutableLiveData<Response<ArrayList<EmpTypeModel>>> {
        val responseData = MutableLiveData<Response<ArrayList<EmpTypeModel>>>()
        RetrofitClient.instance.apiService.getEmploymentTypes(auth)
            .enqueue(object : Callback<ArrayList<EmpTypeModel>> {
                override fun onFailure(
                    call: Call<ArrayList<EmpTypeModel>>,
                    t: Throwable
                ) {
                    throwError(t)
                }

                override fun onResponse(
                    call: Call<ArrayList<EmpTypeModel>>,
                    response: Response<ArrayList<EmpTypeModel>>
                ) {
                    responseData.value = response
                }

            })
        return responseData
    }

    fun deleteEmploymentType(auth: String, item: EmpTypeModel) : MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.deleteJobRoles(auth, item.id).enqueue(
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