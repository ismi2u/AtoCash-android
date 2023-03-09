package com.atocash.common.activity.expenseReimburseListing.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.atocash.R
import com.atocash.base.recycler.BaseVH
import com.atocash.base.recycler.EmptyVH
import com.atocash.base.recycler.LoadingVH
import com.atocash.databinding.ItemEmptyRecyclerViewBinding
import com.atocash.databinding.ItemExpenseReimburseListingBinding
import com.atocash.databinding.ItemLoadingRecyclerViewBinding
import com.atocash.network.response.ExpenseReimburseListingResponse
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog

class ExpenseReimburseListingAdapter(
    private val hideAllActions: Boolean,
    private val items: ArrayList<ExpenseReimburseListingResponse>,
    private val isSubmitted: Boolean,
    private val callback: EmployeeExpenseCallback
) : RecyclerView.Adapter<BaseVH>() {

    private var isLoadingAdded = false

    enum class ExpenseReimburseListAction {
        EDIT, DELETE, COPY, VIEW
    }

    interface EmployeeExpenseCallback {
        fun onClick(item: ExpenseReimburseListingResponse, action: ExpenseReimburseListAction)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
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
                ItemExpenseReimburseListingBinding.inflate(
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

    inner class ItemVh(private val binding: ItemExpenseReimburseListingBinding) :
        BaseVH(binding.root) {

        override fun onBind(position: Int) {
            val itemVm = ExpenseReimburseListingVhVm(items[position], isSubmitted)
            binding.viewModel = itemVm
            binding.executePendingBindings()

            if (hideAllActions) {
                binding.iconsContainer.visibility = View.GONE
            }

            binding.expenseParent.setOnClickListener {
                callback.onClick(items[position], ExpenseReimburseListAction.VIEW)
            }

            binding.edit.setOnClickListener {
                callback.onClick(items[position], ExpenseReimburseListAction.EDIT)
            }

            binding.copy.setOnClickListener {
                callback.onClick(items[position], ExpenseReimburseListAction.COPY)
            }

            binding.delete.setOnClickListener {
                callback.onClick(items[position], ExpenseReimburseListAction.DELETE)
            }

            val documentIds = items[position].documentIDs

            binding.apply {
                if (hideAllActions) {
                    val documentSize =
                        items[position].documentIDs.toString().split(",").size.toString()
                    noOfDocsAttached.apply {
                        text = binding.noOfDocsAttached.context.getString(
                            R.string.no_of_docs_attached, documentSize
                        )
                        setTextColor(ContextCompat.getColor(this.context, R.color.black))
                    }
                    itemInboxCashAdvanceParent.background = ContextCompat.getDrawable(
                        binding.itemInboxCashAdvanceParent.context,
                        R.drawable.ic_doc_attched
                    )
                    docAttachedIv.setImageDrawable(
                        ContextCompat.getDrawable(
                            binding.itemInboxCashAdvanceParent.context, R.drawable.tick_bg
                        )
                    )
                } else {
                    if (documentIds.isNullOrEmpty()) {
                        binding.apply {
                            itemInboxCashAdvanceParent.background = ContextCompat.getDrawable(
                                binding.itemInboxCashAdvanceParent.context,
                                R.drawable.ic_doc_not_attched
                            )
                            docAttachedIv.setImageDrawable(
                                ContextCompat.getDrawable(
                                    binding.itemInboxCashAdvanceParent.context, R.drawable.close_bg
                                )
                            )
                            noOfDocsAttached.apply {
                                text = binding.noOfDocsAttached.context.getString(
                                    R.string.please_check_whether_all_the_claims_are_attached_with_at_least_one_document
                                )
                                setTextColor(ContextCompat.getColor(this.context, R.color.rejected))
                            }
                        }
                    } else {
                        binding.apply {
                            itemInboxCashAdvanceParent.background = ContextCompat.getDrawable(
                                binding.itemInboxCashAdvanceParent.context,
                                R.drawable.ic_doc_attched
                            )
                            docAttachedIv.setImageDrawable(
                                ContextCompat.getDrawable(
                                    binding.itemInboxCashAdvanceParent.context, R.drawable.tick_bg
                                )
                            )
                            val documentSize =
                                items[position].documentIDs.toString().split(",").size.toString()
                            noOfDocsAttached.apply {
                                text = binding.noOfDocsAttached.context.getString(
                                    R.string.no_of_docs_attached, documentSize
                                )
                                setTextColor(ContextCompat.getColor(this.context, R.color.black))
                            }
                        }
                    }
                }
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

    fun add(item: ExpenseReimburseListingResponse) {
        items.add(item)
        notifyDataSetChanged()
    }

    fun addAll(newItems: ArrayList<ExpenseReimburseListingResponse>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun updateItems(item: ExpenseReimburseListingResponse) {

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

    fun remove(item: ExpenseReimburseListingResponse) {
        val position: Int = items.indexOf(item)
        if (position > -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): ExpenseReimburseListingResponse {
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
        add(ExpenseReimburseListingResponse())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position: Int = items.size - 1
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getItems(): ArrayList<ExpenseReimburseListingResponse> {
        return items
    }

    fun addEditedItem(
        resultItem: ExpenseReimburseListingResponse,
        editItem: ExpenseReimburseListingResponse?
    ) {
        editItem?.let {
            items.remove(it)
            items.add(resultItem)
            notifyDataSetChanged()
        }
    }
}