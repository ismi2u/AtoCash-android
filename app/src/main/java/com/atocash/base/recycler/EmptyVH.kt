package com.atocash.base.recycler

import com.atocash.databinding.ItemEmptyRecyclerViewBinding

class EmptyVH internal constructor(
    private val itemEmptyBinding: ItemEmptyRecyclerViewBinding,
    private val emptyMsg: String
) :
    BaseVH(itemEmptyBinding.root) {

    override fun onBind(position: Int) {
        val emptyVhViewModel = EmptyVhVm()
        emptyVhViewModel.updateEmptyMessage(emptyMsg)
        itemEmptyBinding.viewModel = emptyVhViewModel
        itemEmptyBinding.executePendingBindings()
    }
}