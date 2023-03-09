package com.atocash.fragments.admin.assignRoles

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.JobRolesDropDownResponse
import com.atocash.network.response.UserRolesResponse
import com.atocash.network.response.UsersResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssignRolesViewModel : BaseViewModel<AssignRolesNavigator>() {

    var isLoading = ObservableField<Boolean>()

    fun onAssign() {
        getNavigator().onAssignProject()
    }

    fun getUsers(authToken: String): MutableLiveData<Response<ArrayList<UsersResponse>>> {
        val statusResponse = MutableLiveData<Response<ArrayList<UsersResponse>>>()
        RetrofitClient.instance.apiService.getUsers(authToken)
            .enqueue(object : Callback<ArrayList<UsersResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<UsersResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<ArrayList<UsersResponse>>,
                    response: Response<ArrayList<UsersResponse>>
                ) {
                    statusResponse.value = response
                }
            })
        return statusResponse
    }

    fun getRoles(authToken: String): MutableLiveData<Response<ArrayList<UserRolesResponse>>> {
        val statusResponse = MutableLiveData<Response<ArrayList<UserRolesResponse>>>()
        RetrofitClient.instance.apiService.getUsersRoles(authToken)
            .enqueue(object : Callback<ArrayList<UserRolesResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<UserRolesResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<ArrayList<UserRolesResponse>>,
                    response: Response<ArrayList<UserRolesResponse>>
                ) {
                    statusResponse.value = response
                }
            })
        return statusResponse
    }

    fun assignRoles(token: String, rolesJson: JsonObject) : MutableLiveData<Response<BaseResponse>> {
        val apiResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.assignRoles(token, rolesJson).enqueue(
            object : Callback<BaseResponse> {
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    apiResponse.value = response
                }
            }
        )
        return apiResponse
    }

}