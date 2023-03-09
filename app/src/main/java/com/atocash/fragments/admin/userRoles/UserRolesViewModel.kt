package com.atocash.fragments.admin.userRoles

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.ApiResponse
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.UserRolesResponse
import com.atocash.network.response.UsersResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRolesViewModel : BaseViewModel<UserRolesNavigator>() {

    var isLoading = ObservableField<Boolean>()

    fun getRoles(auth: String): MutableLiveData<Response<ArrayList<UserRolesResponse>>> {
        val responseData = MutableLiveData<Response<ArrayList<UserRolesResponse>>>()
        RetrofitClient.instance.apiService.getUsersRoles(auth)
            .enqueue(object : Callback<ArrayList<UserRolesResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<UserRolesResponse>>,
                    t: Throwable
                ) {
                    throwError(t)
                }

                override fun onResponse(
                    call: Call<ArrayList<UserRolesResponse>>,
                    response: Response<ArrayList<UserRolesResponse>>
                ) {
                    responseData.value = response
                }

            })
        return responseData
    }

    fun deleteUserRoles(auth: String, item: UserRolesResponse) : MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.deleteUserRoles(auth, item.id).enqueue(
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