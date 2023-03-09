package com.atocash.common.activity.expenseReimburseListing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atocash.R
import com.atocash.base.recycler.BaseVH
import com.atocash.base.recycler.EmptyVH
import com.atocash.base.recycler.LoadingVH
import com.atocash.databinding.ItemEmptyRecyclerViewBinding
import com.atocash.databinding.ItemLoadingRecyclerViewBinding
import com.atocash.databinding.ItemSubClaimViewBinding
import com.atocash.network.response.ExpenseReimburseListingResponse
import com.atocash.utils.DataStorage
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.roundOffToTwoDecimal
import java.text.DecimalFormat

class SubClaimsViewListingAdapter(
    private val dataStorage: DataStorage,
    private val items: ArrayList<ExpenseReimburseListingResponse>,
    private val isSubmitted: Boolean,
    private val callback: EmployeeExpenseCallback
) :
    RecyclerView.Adapter<BaseVH>() {

    private var isLoadingAdded = false

    enum class ExpenseReimburseListAction {
        EDIT, DELETE, COPY, VIEW
    }

    interface EmployeeExpenseCallback {
        fun onClick(item: ExpenseReimburseListingResponse, action: ExpenseReimburseListAction)
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
                ItemSubClaimViewBinding.inflate(
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

    inner class ItemVh(private val binding: ItemSubClaimViewBinding) :
        BaseVH(binding.root) {

        override fun onBind(position: Int) {
            val item = items[position]
            val itemVm = SubClaimsListingVhVm(items[position], isSubmitted)
            binding.viewModel = itemVm
            binding.executePendingBindings()

            if (item.expenseReimbClaimAmount != null) {
                item.expenseReimbClaimAmount?.let { expenseReimbClaimAmount ->
                    item.taxAmount?.let { taxAmt ->
                        val amountWithTax: Float = expenseReimbClaimAmount.plus(taxAmt)
                        val taxableAmt = amountWithTax.roundOffToTwoDecimal()
                        binding.amountTv.text = withCurrency(taxableAmt)
                    }
                }
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
        }
    }

    fun withCurrency(amount: Float): String {
        val roundedValue = amount.roundOffToTwoDecimal().toString()
        return "Claim Amount: $roundedValue " + dataStorage.getString(Keys.UserData.CURRENCY_CODE)
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
        if (items.isEmpty()) {
            notifyDataSetChanged()
        } else {
            notifyItemInserted(items.size - 1)
        }
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
        resultItem: ArrayList<ExpenseReimburseListingResponse>,
        editItem: ExpenseReimburseListingResponse?
    ) {
        editItem?.let {
            items.remove(it)
            items.addAll(resultItem)
            notifyDataSetChanged()
        }
    }
}