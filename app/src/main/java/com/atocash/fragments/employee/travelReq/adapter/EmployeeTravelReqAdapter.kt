package com.atocash.fragments.employee.travelReq.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atocash.R
import com.atocash.base.recycler.BaseVH
import com.atocash.base.recycler.EmptyVH
import com.atocash.base.recycler.LoadingVH
import com.atocash.databinding.ItemEmployeeTravelReqBinding
import com.atocash.databinding.ItemEmptyRecyclerViewBinding
import com.atocash.databinding.ItemLoadingRecyclerViewBinding
import com.atocash.network.response.EmployeeTravelRequestModel
import com.atocash.utils.Keys

class EmployeeTravelReqAdapter(
    private val items: ArrayList<EmployeeTravelRequestModel>,
    private val callback: EmployeeExpenseCallback
) :
    RecyclerView.Adapter<BaseVH>() {

    private var isLoadingAdded = false

    interface EmployeeExpenseCallback {
        fun onClick(item: EmployeeTravelRequestModel)
        fun onEdit(item: EmployeeTravelRequestModel)
        fun onDelete(item: EmployeeTravelRequestModel)
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
                ItemEmployeeTravelReqBinding.inflate(
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

    inner class ItemVh(private val binding: ItemEmployeeTravelReqBinding) : BaseVH(binding.root) {

        override fun onBind(position: Int) {
            val itemVm = EmployeeTravelReqVhVm(items[position])
            binding.viewModel = itemVm
            binding.executePendingBindings()

            binding.expenseParent.setOnClickListener {
                callback.onClick(items[position])
            }

            binding.edit.setOnClickListener {
                callback.onEdit(items[position])
            }

            binding.delete.setOnClickListener {
                callback.onDelete(items[position])
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

    fun add(item: EmployeeTravelRequestModel) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun addAll(newItems: ArrayList<EmployeeTravelRequestModel>) {
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

    private fun remove(item: EmployeeTravelRequestModel) {
        val position: Int = items.indexOf(item)
        if (position > -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): EmployeeTravelRequestModel {
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
        add(EmployeeTravelRequestModel())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position: Int = items.size - 1
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}