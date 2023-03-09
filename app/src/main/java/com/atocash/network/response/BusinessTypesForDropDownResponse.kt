package com.atocash.network.response
import com.google.gson.annotations.SerializedName


/**
 * Created by Thulasi Rajan P on 26/01/23.
 */
class BusinessTypesForDropDownResponse : ArrayList<BusinessTypesForDropDownResponseItem>()

data class BusinessTypesForDropDownResponseItem(
    @SerializedName("businessTypeName")
    val businessTypeName: String?= null,
    @SerializedName("id")
    val id: Int?= null
)