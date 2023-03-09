package com.atocash.network.response

data class UserRolesResponse(
    var id: String = "",
    var normalizedName: String = "",
    var name: String = "",
    var concurrencyStamp: String = ""
)