package com.atocash.fragments.admin.roles.adapter

import android.content.Context
import androidx.databinding.ObservableField
import com.atocash.R
import com.atocash.network.response.JobRolesResponse

class JobRolesVhVm(
    context: Context,
    response: JobRolesResponse
) {
    val roleName = ObservableField<String>()
    val roleCode = ObservableField<String>()
    val amount = ObservableField<String>()

    init {
//        code.set("Code: " + response.code)
        roleName.set(response.roleName)
        roleCode.set(response.roleCode)
        amount.set(context.getString(R.string.cash_allowed) + response.maxPettyCashAllowed)
    }

}