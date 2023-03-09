package com.atocash.fragments.admin.employmentType.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.EmpTypeModel

class EmpTypeVhVm(
    model: EmpTypeModel
) {
    val name = ObservableField<String>()
    val desc = ObservableField<String>()

    init {
        name.set(model.empJobTypeCode)
        desc.set(model.empJobTypeDesc)
    }

}