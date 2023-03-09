package com.atocash.network.response

data class LoginResponse(
    val firstName: String? = null,
    val lastName: String? = null,
    val empId: String? = null,
    val email: String? = null,
    val currencyCode: String? = null,
    val token: String? = null,
    val currencyId: Int = -1,
    val role: ArrayList<String> = ArrayList()
)