package com.atocash.fragments.admin.project.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.ProjectsResponse

class ProjectsVhVm(
    response: ProjectsResponse
) {
    val name = ObservableField<String>()
    val desc = ObservableField<String>()

    init {
        name.set(response.projectName)
        desc.set(response.projectDesc)
    }

}