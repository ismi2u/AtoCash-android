package com.atocash.fragments.admin.costCenter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atocash.R
import com.atocash.base.recycler.BaseVH
import com.atocash.base.recycler.EmptyVH
import com.atocash.base.recycler.LoadingVH
import com.atocash.databinding.ItemCostCenterBinding
import com.atocash.databinding.ItemEmptyRecyclerViewBinding
import com.atocash.databinding.ItemLoadingRecyclerViewBinding
import com.atocash.network.response.CostCenterResponse
import com.atocash.utils.Keys

class CostCenterAdapter(
    private val items: ArrayList<CostCenterResponse>,
    private val callback: CostCenterCallback
) :
    RecyclerView.Adapter<BaseVH>() {

    private var isLoadingAdded = false

    interface CostCenterCallback {
        fun onEdit(item: CostCenterResponse)
        fun onDelete(item: CostCenterResponse)
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
            Keys.RecyclerItem.ITEM -> baseVH = ItemVh(
                ItemCostCenterBinding.inflate(
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
                    ), parent.context.getString(R.string.no_cost_centers)
                )
            }
        }
        return baseVH
    }

    override fun getItemCount(): Int {
        return if (items.isNullOrEmpty()) 1 else items.size
    }

    override fun onBindViewHolder(holder: BaseVH, position: Int) {
        holder.onBind(position)
    }

    inner class ItemVh(private val binding: ItemCostCenterBinding) : BaseVH(binding.root) {
        override fun onBind(position: Int) {
            val itemVm = CostCenterVhVm(items[position])
            binding.viewModel = itemVm
            binding.executePendingBindings()

            binding.delete.setOnClickListener {
                callback.onDelete(items[position])
            }

            binding.edit.setOnClickListener {
                callback.onEdit(items[position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items.isNotEmpty()) {
            Keys.RecyclerItem.ITEM
        } else {
            if (position == items.size - 1 && isLoadingAdded) Keys.RecyclerItem.LOADING
            else Keys.RecyclerItem.EMPTY
        }
    }

    fun add(item: CostCenterResponse) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun addAll(newItems: ArrayList<CostCenterResponse>) {
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

    fun remove(item: CostCenterResponse) {
        val position: Int = items.indexOf(item)
        if (position > -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): CostCenterResponse {
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
        add(CostCenterResponse())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position: Int = items.size - 1
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeItem(item: CostCenterResponse) {

    }
}