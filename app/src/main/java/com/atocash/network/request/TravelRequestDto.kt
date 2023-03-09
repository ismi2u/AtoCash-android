package com.atocash.network.request

data class TravelRequestDto(
    var employeeId: String? = null,
    var travelStartDate: String? = null,
    var travelEndDate: String? = null,
    var travelPurpose: String? = null,
    var projectId: Int? = null,
    var subProjectId: Int? = null,
    var workTaskId: Int? = null,
    var businessTypeId: Int? = null,
    var businessUnitId: Int? = null,
)
