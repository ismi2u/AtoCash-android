package com.atocash.network.response

data class TasksResponse(
    var id: Int = 0,
    var subProjectId: Int = 0,
    var taskName: String = "",
    var taskDesc: String = "",
    var subProject: String = ""
)