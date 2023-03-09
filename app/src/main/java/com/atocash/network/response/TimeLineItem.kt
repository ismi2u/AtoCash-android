package com.atocash.network.response

data class TimeLineItem (
    val approvalLevel: Int?= 0,
    val approverRole: String?= null,
    val approverName: String?= null,
    val approvalStatusType: String?= null,
    val approvedDate: String?= null
)