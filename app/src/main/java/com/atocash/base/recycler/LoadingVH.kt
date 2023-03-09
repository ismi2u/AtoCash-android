package com.atocash.base.recycler

import com.atocash.databinding.ItemLoadingRecyclerViewBinding

/**
 * Created by geniuS on 1/9/2020.
 */
class LoadingVH internal constructor(private val itemLoadingBinding: ItemLoadingRecyclerViewBinding) :
    BaseVH(itemLoadingBinding.root) {

    override fun onBind(position: Int) {
        val loadingVhViewModel = LoadingVhVm()
        itemLoadingBinding.viewModel = loadingVhViewModel
        itemLoadingBinding.executePendingBindings()
    }
}