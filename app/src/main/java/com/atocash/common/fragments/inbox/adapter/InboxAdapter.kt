package com.atocash.common.fragments.inbox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atocash.R
import com.atocash.base.recycler.BaseVH
import com.atocash.base.recycler.EmptyVH
import com.atocash.base.recycler.LoadingVH
import com.atocash.common.fragments.inbox.InboxFragment
import com.atocash.databinding.*
import com.atocash.network.response.ApprovalBaseResponse
import com.atocash.network.response.InboxCashAdvanceResponse
import com.atocash.network.response.InboxExpenseReimburseResponse
import com.atocash.network.response.InboxTravelRequestResponse
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog

class InboxAdapter(
    private val type: InboxFragment.InboxType,
    private var cashAdvanceItems: ArrayList<InboxCashAdvanceResponse>,
    var travelRequestItems: ArrayList<InboxTravelRequestResponse>,
    var expenseReimburseItems: ArrayList<InboxExpenseReimburseResponse>
) :
    RecyclerView.Adapter<BaseVH>() {
    private var isLoadingAdded = false

    private var inboxCashAdvanceCallback: InboxCashAdvanceCallback? = null
    private var inboxExpenseReimburseCallback: InboxExpenseReimburseCallback? = null
    private var inboxTravelExpenseCallback: InboxTravelExpenseCallback? = null

    fun setCashAdvanceCallback(inboxCashAdvanceCallback: InboxCashAdvanceCallback) {
        this.inboxCashAdvanceCallback = inboxCashAdvanceCallback
    }

    fun setTravelExpenseCallback(inboxTravelExpenseCallback: InboxTravelExpenseCallback) {
        this.inboxTravelExpenseCallback = inboxTravelExpenseCallback
    }

    fun setExpenseCallback(inboxExpenseReimburseCallback: InboxExpenseReimburseCallback) {
        this.inboxExpenseReimburseCallback = inboxExpenseReimburseCallback
    }

    interface InboxCashAdvanceCallback {
        fun onItemClick(item: InboxCashAdvanceResponse)
        fun onChosen(item: InboxCashAdvanceResponse, isChecked: Boolean)
    }

    interface InboxExpenseReimburseCallback {
        fun onItemClick(item: InboxExpenseReimburseResponse)
        fun onChosen(item: InboxExpenseReimburseResponse, isChecked: Boolean)
    }

    interface InboxTravelExpenseCallback {
        fun onChosen(item: InboxTravelRequestResponse, isChecked: Boolean)
        fun onItemClick(item: InboxTravelRequestResponse)
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
                when (type) {
                    InboxFragment.InboxType.ADVANCE -> {
                        baseVH = CashAdvanceItemVh(
                            ItemInboxCashAdvanceStatusBinding.inflate(
                                LayoutInflater.from(
                                    parent.context
                                ), parent, false
                            )
                        )
                    }
                    InboxFragment.InboxType.TRAVEL -> {
                        baseVH = TravelVh(
                            ItemInboxTravelRequestBinding.inflate(
                                LayoutInflater.from(
                                    parent.context
                                ), parent, false
                            )
                        )
                    }
                    InboxFragment.InboxType.EXPENSE -> {
                        baseVH = ExpenseItemVh(
                            ItemInboxExpenseReimburseBinding.inflate(
                                LayoutInflater.from(
                                    parent.context
                                ), parent, false
                            )
                        )
                    }
                }
            else -> {
                baseVH = EmptyVH(
                    ItemEmptyRecyclerViewBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    ), parent.context.getString(R.string.no_data_avail)
                )
            }
        }
        return baseVH
    }

    inner class CashAdvanceItemVh(private val binding: ItemInboxCashAdvanceStatusBinding) :
        BaseVH(binding.root) {
        override fun onBind(position: Int) {
            val itemVm = InboxCashAdvanceVhVm(cashAdvanceItems[position])
            binding.viewModel = itemVm
            binding.executePendingBindings()

            binding.cashChkBox.isClickable = false

            binding.itemInboxCashAdvanceParent.setOnClickListener {
                val isChecked = cashAdvanceItems[position].isChecked
                inboxCashAdvanceCallback?.onChosen(cashAdvanceItems[position], !isChecked)
                cashAdvanceItems[position].isChecked = !isChecked
                notifyItemChanged(position)
                printLog("isChecked ${!isChecked}")
            }

            binding.view.setOnClickListener {
                inboxCashAdvanceCallback?.onItemClick(cashAdvanceItems[position])
            }
        }
    }

    inner class TravelVh(private val binding: ItemInboxTravelRequestBinding) :
        BaseVH(binding.root) {
        override fun onBind(position: Int) {
            val itemVm = InboxTravelRequestVhVm(travelRequestItems[position])
            binding.viewModel = itemVm
            binding.executePendingBindings()

            binding.view.setOnClickListener {
                inboxTravelExpenseCallback?.onItemClick(travelRequestItems[position])
            }

            binding.itemInboxExpenseParent.setOnClickListener {
                val isChecked = travelRequestItems[position].isChecked
                inboxTravelExpenseCallback?.onChosen(travelRequestItems[position], !isChecked)
                travelRequestItems[position].isChecked = !isChecked
                notifyItemChanged(position)
                printLog("isChecked ${!isChecked}")
            }
        }
    }

    inner class ExpenseItemVh(private val binding: ItemInboxExpenseReimburseBinding) :
        BaseVH(binding.root) {
        override fun onBind(position: Int) {
            val itemVm = InboxExpenseVhVm(expenseReimburseItems[position])
            binding.viewModel = itemVm
            binding.executePendingBindings()

            binding.itemInboxExpenseParent.setOnClickListener {
                val isChecked = expenseReimburseItems[position].isChecked
                inboxExpenseReimburseCallback?.onChosen(expenseReimburseItems[position], !isChecked)
                expenseReimburseItems[position].isChecked = !isChecked
                notifyItemChanged(position)
                printLog("isChecked ${!isChecked}")
            }

            binding.view.setOnClickListener {
                inboxExpenseReimburseCallback?.onItemClick(expenseReimburseItems[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return when (type) {
            InboxFragment.InboxType.EXPENSE -> if (expenseReimburseItems.isNullOrEmpty()) 1 else expenseReimburseItems.size
            InboxFragment.InboxType.TRAVEL -> if (travelRequestItems.isNullOrEmpty()) 1 else travelRequestItems.size
            else -> if (cashAdvanceItems.isNullOrEmpty()) 1 else cashAdvanceItems.size
        }
    }

    override fun onBindViewHolder(holder: BaseVH, position: Int) {
        holder.onBind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return when (type) {
            InboxFragment.InboxType.EXPENSE -> {
                if (expenseReimburseItems.isNotEmpty()) {
                    Keys.RecyclerItem.ITEM
                } else {
                    if (position == expenseReimburseItems.size - 1 && isLoadingAdded) Keys.RecyclerItem.LOADING
                    else Keys.RecyclerItem.EMPTY
                }
            }
            InboxFragment.InboxType.TRAVEL -> {
                if (travelRequestItems.isNotEmpty()) {
                    Keys.RecyclerItem.ITEM
                } else {
                    if (position == travelRequestItems.size - 1 && isLoadingAdded) Keys.RecyclerItem.LOADING
                    else Keys.RecyclerItem.EMPTY
                }
            }
            else -> {
                if (cashAdvanceItems.isNotEmpty()) {
                    Keys.RecyclerItem.ITEM
                } else {
                    if (position == cashAdvanceItems.size - 1 && isLoadingAdded) Keys.RecyclerItem.LOADING
                    else Keys.RecyclerItem.EMPTY
                }
            }
        }
    }

    fun add(item: InboxTravelRequestResponse) {
        travelRequestItems.add(item)
        notifyItemInserted(travelRequestItems.size - 1)
    }

    fun add(item: InboxCashAdvanceResponse) {
        cashAdvanceItems.add(item)
        notifyItemInserted(cashAdvanceItems.size - 1)
    }

    fun add(item: InboxExpenseReimburseResponse) {
        expenseReimburseItems.add(item)
        notifyItemInserted(expenseReimburseItems.size - 1)
    }

    fun addAllExpenses(newItems: ArrayList<InboxExpenseReimburseResponse>) {
        expenseReimburseItems.clear()
        expenseReimburseItems.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addAllCashAdvance(newItems: ArrayList<InboxCashAdvanceResponse>) {
        cashAdvanceItems.clear()
        cashAdvanceItems.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addAllTravelReq(newItems: ArrayList<InboxTravelRequestResponse>) {
        travelRequestItems.clear()
        travelRequestItems.addAll(newItems)
        notifyDataSetChanged()
    }

    fun clear() {
        isLoadingAdded = false
        while (itemCount > 0) {
            when (type) {
                InboxFragment.InboxType.EXPENSE -> {
                    remove(getExpenseItem(0))
                }
                InboxFragment.InboxType.TRAVEL -> {
                    remove(getTravelRequest(0))
                }
                else -> {
                    remove(getCashAdvanceItem(0))
                }
            }
        }
    }

    fun remove(item: InboxExpenseReimburseResponse) {
        val position: Int = expenseReimburseItems.indexOf(item)
        if (position > -1) {
            expenseReimburseItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun remove(item: InboxTravelRequestResponse) {
        val position: Int = travelRequestItems.indexOf(item)
        if (position > -1) {
            travelRequestItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun remove(item: InboxCashAdvanceResponse) {
        val position: Int = cashAdvanceItems.indexOf(item)
        if (position > -1) {
            cashAdvanceItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getExpenseItem(position: Int): InboxExpenseReimburseResponse {
        return expenseReimburseItems[position]
    }

    private fun getCashAdvanceItem(position: Int): InboxCashAdvanceResponse {
        return cashAdvanceItems[position]
    }

    private fun getTravelRequest(position: Int): InboxTravelRequestResponse {
        return travelRequestItems[position]
    }

    fun isEmpty(): Boolean {
        return itemCount == 0
    }

    fun isPositionFooter(position: Int): Boolean {
        return position == itemCount - 1 && isLoadingAdded
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        when (type) {
            InboxFragment.InboxType.EXPENSE -> {
                add(InboxExpenseReimburseResponse())
            }
            InboxFragment.InboxType.TRAVEL -> {
                add(InboxTravelRequestResponse())
            }
            else -> {
                add(InboxCashAdvanceResponse())
            }
        }
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        when (type) {
            InboxFragment.InboxType.EXPENSE -> {
                val position: Int = expenseReimburseItems.size - 1
                expenseReimburseItems.removeAt(position)
                notifyItemRemoved(position)
            }
            InboxFragment.InboxType.TRAVEL -> {
                val position: Int = travelRequestItems.size - 1
                travelRequestItems.removeAt(position)
                notifyItemRemoved(position)
            }
            else -> {
                val position: Int = cashAdvanceItems.size - 1
                cashAdvanceItems.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    fun removeItem(item: ApprovalBaseResponse) {

    }
}