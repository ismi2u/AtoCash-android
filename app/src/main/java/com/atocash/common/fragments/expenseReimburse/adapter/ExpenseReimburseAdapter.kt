package com.atocash.common.fragments.expenseReimburse.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atocash.R
import com.atocash.base.recycler.BaseVH
import com.atocash.base.recycler.EmptyVH
import com.atocash.base.recycler.LoadingVH
import com.atocash.databinding.ItemEmptyRecyclerViewBinding
import com.atocash.databinding.ItemExpenseReimburseBinding
import com.atocash.databinding.ItemLoadingRecyclerViewBinding
import com.atocash.network.response.ExpenseRaisedForEmployeeResponse
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog
import java.util.*
import kotlin.collections.ArrayList

class ExpenseReimburseAdapter(
    private val items: ArrayList<ExpenseRaisedForEmployeeResponse>,
    private val isSubmitted: Boolean,
    private val callback: EmployeeExpenseCallback
) :
    RecyclerView.Adapter<BaseVH>() {

    private var isLoadingAdded = false

    enum class ExpenseReimburseListAction {
        EDIT, DELETE, COPY, VIEW
    }

    interface EmployeeExpenseCallback {
        fun onClick(item: ExpenseRaisedForEmployeeResponse, action: ExpenseReimburseListAction)
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
                ItemExpenseReimburseBinding.inflate(
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

    inner class ItemVh(private val binding: ItemExpenseReimburseBinding) : BaseVH(binding.root) {

        override fun onBind(position: Int) {
            val itemVm = ExpenseReimburseVhVm(items[position], isSubmitted)
            binding.viewModel = itemVm
            binding.executePendingBindings()

            binding.expenseParent.setOnClickListener {
                callback.onClick(items[position], ExpenseReimburseListAction.VIEW)
            }

            binding.edit.setOnClickListener {
                callback.onClick(items[position], ExpenseReimburseListAction.EDIT)
            }

            binding.copy.setOnClickListener {
                callback.onClick(items[position], ExpenseReimburseListAction.COPY)
            }

            if(items[position].approvalStatusType.toLowerCase(Locale.getDefault()) == "rejected") {
                binding.copy.visibility = View.VISIBLE
            } else {
                binding.copy.visibility = View.GONE
            }

            binding.delete.setOnClickListener {
                callback.onClick(items[position], ExpenseReimburseListAction.DELETE)
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

    fun add(item: ExpenseRaisedForEmployeeResponse) {
        items.add(item)
//        notifyItemInserted(items.size - 1)
        notifyDataSetChanged()
    }

    fun addAll(newItems: ArrayList<ExpenseRaisedForEmployeeResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
        printLog("Adapter item added and notified")
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

    fun remove(item: ExpenseRaisedForEmployeeResponse) {
        val position: Int = items.indexOf(item)
        if (position > -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): ExpenseRaisedForEmployeeResponse {
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
        add(ExpenseRaisedForEmployeeResponse())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position: Int = items.size - 1
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}