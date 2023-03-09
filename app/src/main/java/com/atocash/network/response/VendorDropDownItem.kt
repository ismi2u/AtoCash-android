package com.atocash.network.response
import com.google.gson.annotations.SerializedName


/**
 * Created by Thulasi Rajan P on 26/01/23.
 */
data class VendorDropDownItem(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("vendorName")
    val vendorName: String? = null
)