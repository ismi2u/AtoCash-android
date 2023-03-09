package com.atocash.base.recycler

import androidx.databinding.ObservableField

/**
 * Created by geniuS on 1/8/2020.
 */
class EmptyVhVm {

    val emptyMessage = ObservableField<String>()

    fun updateEmptyMessage(message: String) {
        if (message.isEmpty().not()) {
            emptyMessage.set(message)
        } else {
            emptyMessage.set("No data found!")
        }
    }
}