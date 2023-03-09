package com.atocash.network.response.dashboard
import com.atocash.network.response.BaseResponse
import com.google.gson.annotations.SerializedName


/**
 * Created by Thulasi Rajan P on 27/01/23.
 */
data class CashAdvanceCountResponseData(
    @SerializedName("approvedCount")
    val approvedCount: Int = 0,
    @SerializedName("pendingCount")
    val pendingCount: Int = 0,
    @SerializedName("rejectedCount")
    val rejectedCount: Int = 0,
    @SerializedName("totalCount")
    val totalCount: Int = 0
): BaseResponse()