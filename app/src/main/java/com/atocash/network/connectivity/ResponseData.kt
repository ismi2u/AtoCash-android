package com.atocash.network.connectivity

data class ResponseData<T>(
    var message: String,
    var status: String,
    var data: T
)