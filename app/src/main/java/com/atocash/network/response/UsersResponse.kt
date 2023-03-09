package com.atocash.network.response

data class UsersResponse(
    var employeeId: Int = 0,
    var accessFailedCount: Int = 0,
    var id: String = "",
    var userName: String = "",
    var normalizedUserName: String = "",
    var email: String = "",
    var normalizedEmail: String = "",
    var concurrencyStamp: String = "",
    var emailConfirmed: Boolean = false,
    var phoneNumberConfirmed: Boolean = false,
    var twoFactorEnabled: Boolean = false,
    var lockoutEnabled: Boolean = false
)