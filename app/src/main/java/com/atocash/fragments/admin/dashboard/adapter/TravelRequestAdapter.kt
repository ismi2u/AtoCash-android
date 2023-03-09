package com.atocash.fragments.admin.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atocash.R
import com.atocash.base.recycler.BaseVH
import com.atocash.base.recycler.EmptyVH
import com.atocash.base.recycler.LoadingVH
import com.atocash.base.recycler.RecyclerAction
import com.atocash.databinding.ItemEmptyRecyclerViewBinding
import com.atocash.databinding.ItemLoadingRecyclerViewBinding
import com.atocash.databinding.ItemTravelRequestBinding
import com.atocash.network.response.TravelResponse
import com.atocash.utils.Keys

class TravelRequestAdapter(
    private val items: ArrayList<TravelResponse>,
    private val callback: TravelRequestAdapterCallback
) :
    RecyclerView.Adapter<BaseVH>() {

    private var isLoadingAdded = false

    interface TravelRequestAdapterCallback {
        fun onItemClick(recyclerAction: RecyclerAction, item: TravelResponse)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseVH {
        val baseVH: BaseVH
        when (viewType) {
            Keys.RecyclerItem.LOADING -> baseVH = LoadingVH(
                ItemLoadingRecyclerViewBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
            Keys.RecyclerItem.ITEM ->
                baseVH = ItemVh(
                    ItemTravelRequestBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            else -> {
                baseVH = EmptyVH(
                    ItemEmptyRecyclerViewBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    ), parent.context.getString(R.string.no_data_available)
                )
            }
        }
        return baseVH
    }

    inner class ItemVh(private val binding: ItemTravelRequestBinding) : BaseVH(binding.root) {
        override fun onBind(position: Int) {
            val itemVm = TravelRequestVhVm(items[position])
            binding.viewModel = itemVm
            binding.executePendingBindings()

            binding.cashAdvanceParent.setOnClickListener {
                callback.onItemClick(RecyclerAction.DETAILS, items[position])
            }

            binding.delete.setOnClickListener {
                callback.onItemClick(RecyclerAction.DELETE, items[position])
            }

            binding.edit.setOnClickListener {
                callback.onItemClick(RecyclerAction.EDIT, items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return if (items.isNullOrEmpty()) 1 else items.size
    }

    override fun onBindViewHolder(holder: BaseVH, position: Int) {
        holder.onBind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (items.isNotEmpty()) {
            Keys.RecyclerItem.ITEM
        } else {
            if (position == items.size - 1 && isLoadingAdded) Keys.RecyclerItem.LOADING
            else Keys.RecyclerItem.EMPTY
        }
    }

    fun add(item: TravelResponse) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun addAll(newItems: ArrayList<TravelResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }

    fun clear() {
        isLoadingAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    fun remove(item: TravelResponse) {
        val position: Int = items.indexOf(item)
        if (position > -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): TravelResponse {
        return items[position]
    }

    fun isEmpty(): Boolean {
        return itemCount == 0
    }

    fun isPositionFooter(position: Int): Boolean {
        return position == itemCount - 1 && isLoadingAdded
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(TravelResponse())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position: Int = items.size - 1
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeItem(item: TravelResponse) {

    }
}