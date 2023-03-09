package com.atocash.network.response
import com.google.gson.annotations.SerializedName


data class PettyCashResponse(
    @SerializedName("approvalStatusType")
    val approvalStatusType: String? = null,
    @SerializedName("approvalStatusTypeId")
    val approvalStatusTypeId: Int? = null,
    @SerializedName("approverActionDate")
    val approverActionDate: Any? = null,
    @SerializedName("businessType")
    val businessType: String? = null,
    @SerializedName("businessTypeId")
    val businessTypeId: Int? = null,
    @SerializedName("businessUnit")
    val businessUnit: String? = null,
    @SerializedName("businessUnitId")
    val businessUnitId: Int? = null,
    @SerializedName("cashAdvanceAmount")
    val cashAdvanceAmount: Int? = null,
    @SerializedName("cashAdvanceRequestDesc")
    val cashAdvanceRequestDesc: String? = null,
    @SerializedName("comments")
    val comments: String? = null,
    @SerializedName("costCenterId")
    val costCenterId: Int? = null,
    @SerializedName("costCentre")
    val costCentre: String? = null,
    @SerializedName("creditToBank")
    val creditToBank: String? = null,
    @SerializedName("currencyType")
    val currencyType: Any? = null,
    @SerializedName("currencyTypeId")
    val currencyTypeId: Int? = null,
    @SerializedName("employeeId")
    val employeeId: Int? = null,
    @SerializedName("employeeName")
    val employeeName: String? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("isSettled")
    val isSettled: Boolean = false,
    @SerializedName("jobRole")
    val jobRole: String? = null,
    @SerializedName("jobRoleId")
    val jobRoleId: Int? = null,
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("project")
    val project: String? = null,
    @SerializedName("projectId")
    val projectId: Int? = null,
    @SerializedName("projectRole")
    val projectRole: String? = null,
    @SerializedName("requestDate")
    val requestDate: String? = null,
    @SerializedName("showEditDelete")
    val showEditDelete: Boolean? = null,
    @SerializedName("subProject")
    val subProject: String? = null,
    @SerializedName("subProjectId")
    val subProjectId: Int? = null,
    @SerializedName("workTask")
    val workTask: String? = null,
    @SerializedName("workTaskId")
    val workTaskId: Int? = null
): BaseResponse()