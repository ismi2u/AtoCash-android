package com.atocash.base.recycler

import androidx.databinding.ObservableField

/**
 * Created by geniuS on 1/8/2020.
 */
class LoadingVhVm {

    private val isLoading = ObservableField<Boolean>()

    fun getIsLoading(): ObservableField<Boolean>? {
        return isLoading
    }

    fun setIsLoading(loadingStatus: Boolean) {
        isLoading.set(loadingStatus)
    }
}