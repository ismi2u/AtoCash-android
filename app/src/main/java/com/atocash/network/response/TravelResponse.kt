package com.atocash.network.response

import com.google.gson.annotations.SerializedName

data class TravelResponse(
    val id: Int? = null,
    val departmentId: Int? = null,
    val projectId: Int? = null,
    val subProjectId: Int? = null,
    val employeeId: Int? = null,
    val workTaskId: Int? = null,
    val costCenterId: Int? = null,
    val approvalStatusTypeId: Int? = null,
    val approvalStatusType: String? = null,
    val approvedDate: String? = null,
    val employeeName: String? = null,
    val travelStartDate: String? = null,
    val travelEndDate: String? = null,
    val travelPurpose: String? = null,
    val reqRaisedDate: String? = null,
    val departmentName: String? = null,
    val projectName: String? = null,
    val subProjectName: String? = null,
    val workTaskName: String? = null,
    val costCenter: String? = null,
    val comments: String? = null,
    val showEditDelete: Boolean? = null,
    @SerializedName("businessType")
    val businessType: String? = null,
    @SerializedName("businessTypeId")
    val businessTypeId: Int? = null,
    @SerializedName("businessUnit")
    val businessUnit: String? = null,
    @SerializedName("businessUnitId")
    val businessUnitId: Int? = null,
    @SerializedName("location")
    val location: String? = null,
)