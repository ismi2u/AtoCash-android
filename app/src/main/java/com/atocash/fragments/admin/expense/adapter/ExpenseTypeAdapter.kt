package com.atocash.fragments.admin.expense.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atocash.R
import com.atocash.base.recycler.BaseVH
import com.atocash.base.recycler.EmptyVH
import com.atocash.base.recycler.LoadingVH
import com.atocash.databinding.*
import com.atocash.network.response.ExpenseTypeResponse
import com.atocash.utils.Keys

class ExpenseTypeAdapter(
    private val items: ArrayList<ExpenseTypeResponse>,
    private val callback: CostCenterCallback
) :
    RecyclerView.Adapter<BaseVH>() {

    private var isLoadingAdded = false

    interface CostCenterCallback {
        fun onEdit(item: ExpenseTypeResponse)
        fun onDelete(item: ExpenseTypeResponse)
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
                ItemExpenseTypesBinding.inflate(
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
                    ), parent.context.getString(R.string.no_expense_data_available)
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

    inner class ItemVh(private val binding: ItemExpenseTypesBinding) : BaseVH(binding.root) {
        override fun onBind(position: Int) {
            val itemVm = ExpenseTypeVhVm(items[position])
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

    fun add(item: ExpenseTypeResponse) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun addAll(newItems: ArrayList<ExpenseTypeResponse>) {
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

    fun remove(item: ExpenseTypeResponse) {
        val position: Int = items.indexOf(item)
        if (position > -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): ExpenseTypeResponse {
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
        add(ExpenseTypeResponse())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position: Int = items.size - 1
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}