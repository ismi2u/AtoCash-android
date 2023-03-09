package com.atocash.fragments.admin.userRoles.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.UserRolesResponse

class UserRolesVhVm(
    response: UserRolesResponse
) {
    val name = ObservableField<String>()
    val desc = ObservableField<String>()

    init {
        name.set(response.name)
    }

}