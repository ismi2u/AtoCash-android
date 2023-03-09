package com.atocash.network.response

data class SubProjectsResponse(
    var id: Int = 0,
    var projectId: Int = 0,
    var code: String = "",
    var name: String = "",
    var projectName: String = "",
    var subProjectDesc: String = "",
    var subProjectName: String = "",
    var description: String = ""
)