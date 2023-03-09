package com.atocash.base.recycler

interface BaseRecyclerCallback<T> {
    fun onItemClick(position: Int, data: T, action: RecyclerAction)
}

enum class RecyclerAction {
    EDIT,
    DETAILS,
    DELETE
}
