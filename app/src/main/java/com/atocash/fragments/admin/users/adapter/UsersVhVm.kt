package com.atocash.fragments.admin.users.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.UsersResponse

class UsersVhVm(
    response: UsersResponse
) {
    val name = ObservableField<String>()
    val email = ObservableField<String>()

    init {
        name.set(response.normalizedUserName)
        email.set(response.normalizedEmail)
    }

}