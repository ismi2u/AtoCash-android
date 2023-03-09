package com.atocash.common.activity.expenseReimburse

import androidx.lifecycle.MutableLiveData
import com.atocash.base.common.BaseViewModel
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.*
import com.google.gson.JsonArray
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageExpenseReimbursementViewModel :
    BaseViewModel<ManageExpenseReimbursementNavigator>() {

    fun onBack() {
        getNavigator().onBack()
    }

    fun onCreate() {
        getNavigator().onAdd()
    }

    fun getExpenseTypes(token: String): MutableLiveData<Response<ArrayList<ExpenseTypeResponse>>> {
        val expenseList = MutableLiveData<Response<ArrayList<ExpenseTypeResponse>>>()
        RetrofitClient.instance.apiService.getExpenseTypes(token).enqueue(object :
            Callback<ArrayList<ExpenseTypeResponse>> {
            override fun onFailure(
                call: Call<ArrayList<ExpenseTypeResponse>>,
                t: Throwable
            ) {
                throwError(t)
            }

            override fun onResponse(
                call: Call<ArrayList<ExpenseTypeResponse>>,
                response: Response<ArrayList<ExpenseTypeResponse>>
            ) {
                expenseList.value = response
            }
        })
        return expenseList
    }

    fun getExpenseTypesNonProject(
        token: String,
        expenseFor: String
    ): MutableLiveData<Response<ArrayList<ExpenseTypeNonProjectResponse>>> {
        val expenseList = MutableLiveData<Response<ArrayList<ExpenseTypeNonProjectResponse>>>()
        val isBussCategory = expenseFor.lowercase() == "business area"
        RetrofitClient.instance.apiService.getExpenseTypesForNonProject(token)
            .enqueue(object :
                Callback<ArrayList<ExpenseTypeNonProjectResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<ExpenseTypeNonProjectResponse>>,
                    t: Throwable
                ) {
                    throwError(t)
                }

                override fun onResponse(
                    call: Call<ArrayList<ExpenseTypeNonProjectResponse>>,
                    response: Response<ArrayList<ExpenseTypeNonProjectResponse>>
                ) {
                    expenseList.value = response
                }
            })
        return expenseList
    }

    fun getExpenseTypesNonProjectCategoryId(
        token: String,
        id: Int
    ): MutableLiveData<Response<ArrayList<ExpenseTypeResponse>>> {
        val expenseList = MutableLiveData<Response<ArrayList<ExpenseTypeResponse>>>()
        RetrofitClient.instance.apiService.getExpenseTypesForNonProjectCategoryId(token, id)
            .enqueue(object :
                Callback<ArrayList<ExpenseTypeResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<ExpenseTypeResponse>>,
                    t: Throwable
                ) {
                    throwError(t)
                }

                override fun onResponse(
                    call: Call<ArrayList<ExpenseTypeResponse>>,
                    response: Response<ArrayList<ExpenseTypeResponse>>
                ) {
                    expenseList.value = response
                }
            })
        return expenseList
    }

    fun getVatPercentage(
        token: String,
    ): MutableLiveData<Response<VatPercentageResponse>> {
        val vatPercentage = MutableLiveData<Response<VatPercentageResponse>>()
        RetrofitClient.instance.apiService.getVatPercentage(token)
            .enqueue(object : Callback<VatPercentageResponse> {
                override fun onFailure(
                    call: Call<VatPercentageResponse>,
                    t: Throwable
                ) {
                    throwError(t)
                }

                override fun onResponse(
                    call: Call<VatPercentageResponse>,
                    response: Response<VatPercentageResponse>
                ) {
                    vatPercentage.value = response
                }
            })
        return vatPercentage
    }

    fun approveCashRequest(
        token: String,
        chosenCashAdvanceItems: JsonArray
    ): MutableLiveData<Response<BaseResponse>> {
        val baseResponse = MutableLiveData<Response<BaseResponse>>()
        RetrofitClient.instance.apiService.approveRejectCashAdvances(token, chosenCashAdvanceItems)
            .enqueue(object : Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    baseResponse.value = response
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    throwError(t)
                }
            })
        return baseResponse
    }

    fun postDocuments(
        token: String,
        files: ArrayList<Documents>
    ): MutableLiveData<Response<ArrayList<PostDocumentsResponse>>> {
        val baseResponse = MutableLiveData<Response<ArrayList<PostDocumentsResponse>>>()

        val multipartFileBody = ArrayList<MultipartBody.Part>()
        files.forEachIndexed { index, pickedMediaFile ->
            multipartFileBody.add(
                RetrofitClient.getPartFromFile(
                    pickedMediaFile.filePath.toString(),
                    "Documents"
                )
            )
        }

        RetrofitClient.instance.apiService.postDocuments(token, multipartFileBody)
            .enqueue(object : Callback<ArrayList<PostDocumentsResponse>> {
                override fun onResponse(
                    call: Call<ArrayList<PostDocumentsResponse>>,
                    response: Response<ArrayList<PostDocumentsResponse>>
                ) {
                    baseResponse.value = response
                }

                override fun onFailure(call: Call<ArrayList<PostDocumentsResponse>>, t: Throwable) {
                    throwError(t)
                }
            })
        return baseResponse
    }

    fun getVendorsForDropDown(token: String): MutableLiveData<Response<ArrayList<VendorDropDownItem>>> {
        val baseResponse = MutableLiveData<Response<ArrayList<VendorDropDownItem>>>()
        RetrofitClient.instance.apiService.getVendorsForDropDown(authToken = token).enqueue(
            object : Callback<ArrayList<VendorDropDownItem>> {
                override fun onResponse(
                    call: Call<ArrayList<VendorDropDownItem>>,
                    response: Response<ArrayList<VendorDropDownItem>>
                ) {
                    baseResponse.value = response
                }

                override fun onFailure(call: Call<ArrayList<VendorDropDownItem>>, t: Throwable) {
                    throwError(t)
                }
            }
        )
        return baseResponse
    }
}