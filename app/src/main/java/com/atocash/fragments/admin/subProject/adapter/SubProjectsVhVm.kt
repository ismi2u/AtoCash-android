package com.atocash.fragments.admin.subProject.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.SubProjectsResponse

class SubProjectsVhVm(
    response: SubProjectsResponse
) {
    val name = ObservableField<String>()
    val desc = ObservableField<String>()
    val projectName = ObservableField<String>()

    init {
        name.set(response.subProjectName)
        desc.set(response.subProjectDesc)
        projectName.set(response.projectName)
    }

}