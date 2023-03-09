package com.atocash.base.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atocash.common.fragments.inbox.InboxFragment
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.*
import com.atocash.utils.DataStorage
import com.atocash.utils.extensions.printLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

/**
 * Created by geniuS on 12/2/2019.
 */
abstract class BaseViewModel<N> : ViewModel() {

    private lateinit var dataStorage: DataStorage
    private lateinit var mNavigator: WeakReference<N>

    fun setDataStoreAndNavigator(dataStorage: DataStorage, navigator: N) {
        this.dataStorage = dataStorage
        mNavigator = WeakReference(navigator)
    }

    fun getDataStorage(): DataStorage {
        return dataStorage
    }

    fun getNavigator(): N {
        return mNavigator.get()!!
    }

    fun throwError(throwable: Throwable?) {
        printLog(throwable?.message.toString())
    }

    fun getApprovalStatus(auth: String): MutableLiveData<ArrayList<ApprovalBaseResponse>> {
        val costCenterList = MutableLiveData<ArrayList<ApprovalBaseResponse>>()
        RetrofitClient.instance.apiService.getApprovalStatusTypes(auth).enqueue(object :
            Callback<ArrayList<ApprovalBaseResponse>> {
            override fun onFailure(call: Call<ArrayList<ApprovalBaseResponse>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<ApprovalBaseResponse>>,
                response: Response<ArrayList<ApprovalBaseResponse>>
            ) {
                if (response.code() == 200) {
                    costCenterList.value = response.body()
                } else {
                    val errorResponse = response.body() as BaseResponse
                    printLog("Error: ${errorResponse.message}")
                }
            }
        })
        return costCenterList
    }

    fun getStatusForDropDowN(authToken: String): MutableLiveData<Response<ArrayList<StatusDropDownResponse>>> {
        val statusResponse = MutableLiveData<Response<ArrayList<StatusDropDownResponse>>>()
        RetrofitClient.instance.apiService.getStatusForDropDown(authToken)
            .enqueue(object : Callback<ArrayList<StatusDropDownResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<StatusDropDownResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<ArrayList<StatusDropDownResponse>>,
                    response: Response<ArrayList<StatusDropDownResponse>>
                ) {
                    statusResponse.value = response
                }
            })
        return statusResponse
    }

    fun getCurrencyTypesForDropDown(authToken: String): MutableLiveData<Response<ArrayList<CurrencyDropDownResponse>>> {
        val statusResponse = MutableLiveData<Response<ArrayList<CurrencyDropDownResponse>>>()
        RetrofitClient.instance.apiService.getCurrencyTypesForDropDown(authToken)
            .enqueue(object : Callback<ArrayList<CurrencyDropDownResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<CurrencyDropDownResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<ArrayList<CurrencyDropDownResponse>>,
                    response: Response<ArrayList<CurrencyDropDownResponse>>
                ) {
                    statusResponse.value = response
                }
            })
        return statusResponse
    }

    fun getBusinessTypesForDropDown(authToken: String): MutableLiveData<Response<ArrayList<BusinessTypesForDropDownResponseItem>>> {
        val statusResponse = MutableLiveData<Response<ArrayList<BusinessTypesForDropDownResponseItem>>>()
        RetrofitClient.instance.apiService.getBusinessTypesForDropDown(authToken)
            .enqueue(object : Callback<ArrayList<BusinessTypesForDropDownResponseItem>> {
                override fun onFailure(
                    call: Call<ArrayList<BusinessTypesForDropDownResponseItem>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<ArrayList<BusinessTypesForDropDownResponseItem>>,
                    response: Response<ArrayList<BusinessTypesForDropDownResponseItem>>
                ) {
                    statusResponse.value = response
                }
            })
        return statusResponse
    }

    fun getApprovalGroupsForDropDown(authToken: String): MutableLiveData<Response<ArrayList<ApprovalGroupDropDownResponse>>> {
        val statusResponse = MutableLiveData<Response<ArrayList<ApprovalGroupDropDownResponse>>>()
        RetrofitClient.instance.apiService.getApprovalGroupsForDropDown(authToken)
            .enqueue(object : Callback<ArrayList<ApprovalGroupDropDownResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<ApprovalGroupDropDownResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<ArrayList<ApprovalGroupDropDownResponse>>,
                    response: Response<ArrayList<ApprovalGroupDropDownResponse>>
                ) {
                    statusResponse.value = response
                }
            })
        return statusResponse
    }

    fun getJobRolesForDropDown(authToken: String): MutableLiveData<Response<ArrayList<JobRolesDropDownResponse>>> {
        val statusResponse = MutableLiveData<Response<ArrayList<JobRolesDropDownResponse>>>()
        RetrofitClient.instance.apiService.getJobRolesForDropDown(authToken)
            .enqueue(object : Callback<ArrayList<JobRolesDropDownResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<JobRolesDropDownResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<ArrayList<JobRolesDropDownResponse>>,
                    response: Response<ArrayList<JobRolesDropDownResponse>>
                ) {
                    statusResponse.value = response
                }
            })
        return statusResponse
    }

    fun getDepartmentsForDropDown(authToken: String): MutableLiveData<Response<ArrayList<DepartmentDropDownResponse>>> {
        val statusResponse = MutableLiveData<Response<ArrayList<DepartmentDropDownResponse>>>()
        RetrofitClient.instance.apiService.getDepartmentsForDropDown(authToken)
            .enqueue(object : Callback<ArrayList<DepartmentDropDownResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<DepartmentDropDownResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<ArrayList<DepartmentDropDownResponse>>,
                    response: Response<ArrayList<DepartmentDropDownResponse>>
                ) {
                    statusResponse.value = response
                }
            })
        return statusResponse
    }

    fun getEmploymentTypesForDropDown(authToken: String): MutableLiveData<Response<ArrayList<EmpTypesDropDownResponse>>> {
        val statusResponse = MutableLiveData<Response<ArrayList<EmpTypesDropDownResponse>>>()
        RetrofitClient.instance.apiService.getEmploymentTypesForDropDown(authToken)
            .enqueue(object : Callback<ArrayList<EmpTypesDropDownResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<EmpTypesDropDownResponse>>,
                    t: Throwable
                ) {
                    throwError(throwable = t)
                }

                override fun onResponse(
                    call: Call<ArrayList<EmpTypesDropDownResponse>>,
                    response: Response<ArrayList<EmpTypesDropDownResponse>>
                ) {
                    statusResponse.value = response
                }
            })
        return statusResponse
    }

    fun loadApprovalTypes(token: String): MutableLiveData<Response<ArrayList<InboxFragment.ApprovalStatusTypes>>> {
        val approvalTypes = MutableLiveData<Response<ArrayList<InboxFragment.ApprovalStatusTypes>>>()
        RetrofitClient.instance.apiService.getApprovalStatusTypesOnly(token).enqueue(object :
            Callback<ArrayList<InboxFragment.ApprovalStatusTypes>> {
            override fun onFailure(
                call: Call<ArrayList<InboxFragment.ApprovalStatusTypes>>,
                t: Throwable
            ) {

            }

            override fun onResponse(
                call: Call<ArrayList<InboxFragment.ApprovalStatusTypes>>,
                response: Response<ArrayList<InboxFragment.ApprovalStatusTypes>>
            ) {
                approvalTypes.value = response
            }
        })
        return approvalTypes
    }

}