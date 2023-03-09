package com.atocash.fragments.admin.department.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.DepartmentResponse

class DepartmentVhVm(
    model: DepartmentResponse
) {
    val code = ObservableField<String>()
    val name = ObservableField<String>()
    val costCenter = ObservableField<String>()

    init {
        code.set(model.deptCode)
        name.set(model.deptName)
        costCenter.set(model.costCenter)
    }

}