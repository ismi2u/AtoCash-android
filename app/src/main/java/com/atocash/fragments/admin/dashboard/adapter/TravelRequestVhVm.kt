package com.atocash.fragments.admin.dashboard.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.TravelResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.DateUtils

class TravelRequestVhVm(
    response: TravelResponse
) {
    val description = ObservableField<String>()
    val requestDate = ObservableField<String>()
    val canShowOptions = ObservableField<Boolean>()
    val projectOrDeptName = ObservableField<String>()
    val status = ObservableField<String>()
    val id = ObservableField<String>()

    init {
        description.set(response.travelPurpose)
        id.set("ID: ${response.id}")

        status.set(response.approvalStatusType)

        val startDate = response.travelStartDate?.let { DateUtils.getDateNew(it) }
        val endDate = response.travelEndDate?.let { DateUtils.getDateNew(it) }

        AppHelper.printLog("startDate ${response.travelStartDate} $startDate")
        AppHelper.printLog("endDate ${response.travelEndDate} $endDate")

        if (startDate.isNullOrEmpty().not() && endDate.isNullOrEmpty().not()) {
            requestDate.set("$startDate - $endDate")
        }

        if(response.businessType.isNullOrEmpty().not()) {
            projectOrDeptName.set(response.businessType)
        } else {
            projectOrDeptName.set(response.projectName)
        }

        canShowOptions.set(response.showEditDelete)
    }

}