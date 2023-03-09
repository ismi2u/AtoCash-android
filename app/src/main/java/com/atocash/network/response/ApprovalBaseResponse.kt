package com.atocash.network.response

class ApprovalBaseResponse  (
    var id: Int = -1,

    var approvalGroupCode: String = "",
    var approvalGroupDesc: String = "",

    var levelDesc: String = "",
    var level: Int = 0,

    var approvalGroupId: Int = 0,
    var roleId: Int = 0,
    var approvalLevel: Int = 0,
    var approvalLevelId: Int = 0,
    var approvalGroup: String = "",
    var role: String = "",

    var status: String = "",
    var statusDesc: String = ""
)