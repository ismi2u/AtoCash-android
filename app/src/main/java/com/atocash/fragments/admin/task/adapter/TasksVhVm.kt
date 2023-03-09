package com.atocash.fragments.admin.task.adapter

import androidx.databinding.ObservableField
import com.atocash.network.response.TasksResponse

class TasksVhVm(
    response: TasksResponse
) {
    val name = ObservableField<String>()
    val desc = ObservableField<String>()
    val projectName = ObservableField<String>()

    init {
        name.set(response.taskName)
        desc.set(response.taskDesc)
        projectName.set(response.subProject)
    }

}