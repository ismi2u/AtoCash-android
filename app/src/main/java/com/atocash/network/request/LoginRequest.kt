package com.atocash.network.request

data class LoginRequest(
    var email: String? = "",
    var password: String? = "",
    var remember: Boolean = false
)