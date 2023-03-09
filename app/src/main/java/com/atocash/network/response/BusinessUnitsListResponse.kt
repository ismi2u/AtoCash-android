package com.atocash.network.response
import com.google.gson.annotations.SerializedName


/**
 * Created by Thulasi Rajan P on 26/01/23.
 */
class BusinessUnitsListResponse : ArrayList<BusinessUnitsListResponseItem>()

data class BusinessUnitsListResponseItem(
    @SerializedName("businessUnitName")
    val businessUnitName: String? = null,
    @SerializedName("id")
    val id: Int? = null
)