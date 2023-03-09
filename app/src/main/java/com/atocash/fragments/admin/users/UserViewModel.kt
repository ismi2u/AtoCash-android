package com.atocash.fragments.admin.users

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.ApiResponse
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.BaseResponse
import com.atocash.network.response.TasksResponse
import com.atocash.network.response.UsersResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : BaseViewModel<UserNavigator>() {

    var isLoading = ObservableField<Boolean>()

    fun getUsers(auth: String): MutableLiveData<Response<ArrayList<UsersResponse>>> {
        val responseData = MutableLiveData<Response<ArrayList<UsersResponse>>>()
        RetrofitClient.instance.apiService.getUsers(auth)
            .enqueue(object : Callback<ArrayList<UsersResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<UsersResponse>>,
                    t: Throwable
                ) {
                    throwError(t)
                }

                override fun onResponse(
                    call: Call<ArrayList<UsersResponse>>,
                    response: Response<ArrayList<UsersResponse>>
                ) {
                    responseData.value = response
                }

            })
        return responseData
    }

    fun deleteUsers(auth: String, item: UsersResponse) : MutableLiveData<Response<BaseResponse>> {
        val responseData = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.deleteUser(auth, item.id).enqueue(
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