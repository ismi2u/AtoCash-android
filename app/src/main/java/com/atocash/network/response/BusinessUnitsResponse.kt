package com.atocash.network.response
import com.google.gson.annotations.SerializedName


/**
 * Created by Thulasi Rajan P on 26/01/23.
 */
data class BusinessUnitDetails(
    @SerializedName("businessDesc")
    val businessDesc: String? = null,
    @SerializedName("businessType")
    val businessType: String? = null,
    @SerializedName("businessTypeId")
    val businessTypeId: Int? = null,
    @SerializedName("businessUnitCode")
    val businessUnitCode: String? = null,
    @SerializedName("businessUnitName")
    val businessUnitName: String? = null,
    @SerializedName("costCenter")
    val costCenter: String? = null,
    @SerializedName("costCenterId")
    val costCenterId: Int? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("locationId")
    val locationId: Int? = null,
    @SerializedName("statusType")
    val statusType: String? = null,
    @SerializedName("statusTypeId")
    val statusTypeId: Int? = null
)