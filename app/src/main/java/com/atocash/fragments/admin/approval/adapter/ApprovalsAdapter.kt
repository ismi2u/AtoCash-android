package com.atocash.fragments.admin.approval.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atocash.R
import com.atocash.base.recycler.BaseVH
import com.atocash.base.recycler.EmptyVH
import com.atocash.base.recycler.LoadingVH
import com.atocash.databinding.*
import com.atocash.network.response.ApprovalBaseResponse
import com.atocash.utils.Keys

class ApprovalsAdapter(
    private val approvalType: ApprovalType,
    private val items: ArrayList<ApprovalBaseResponse>,
    private val callback: ApprovalAdapterCallback
) :
    RecyclerView.Adapter<BaseVH>() {

    private var isLoadingAdded = false

    interface ApprovalAdapterCallback {
        fun onEdit(item: ApprovalBaseResponse)
        fun onDelete(item: ApprovalBaseResponse)
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
                when (approvalType) {
                    ApprovalType.GROUP -> {
                        baseVH = GroupItemVh(
                            ItemApprovalGroupBinding.inflate(
                                LayoutInflater.from(
                                    parent.context
                                ), parent, false
                            )
                        )
                    }
                    ApprovalType.LEVEL -> {
                        baseVH = LevelItemVh(
                            ItemApprovalLevelBinding.inflate(
                                LayoutInflater.from(
                                    parent.context
                                ), parent, false
                            )
                        )
                    }
                    ApprovalType.ROLE_MAP -> {
                        baseVH = RoleMapItemVh(
                            ItemApprovalRoleMapBinding.inflate(
                                LayoutInflater.from(
                                    parent.context
                                ), parent, false
                            )
                        )
                    }
                    else -> {
                        baseVH = StatusItemVh(
                            ItemApprovalStatusBinding.inflate(
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
                    ), parent.context.getString(R.string.no_approval_data_available)
                )
            }
        }
        return baseVH
    }

    inner class GroupItemVh(private val binding: ItemApprovalGroupBinding) : BaseVH(binding.root) {
        override fun onBind(position: Int) {
            val itemVm = ApprovalGroupVhVm(items[position])
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

    inner class StatusItemVh(private val binding: ItemApprovalStatusBinding) :
        BaseVH(binding.root) {
        override fun onBind(position: Int) {
            val itemVm = ApprovalStatusVhVm(items[position])
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

    inner class LevelItemVh(private val binding: ItemApprovalLevelBinding) : BaseVH(binding.root) {
        override fun onBind(position: Int) {
            val itemVm = ApprovalLevelVhVm(items[position])
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

    inner class RoleMapItemVh(private val binding: ItemApprovalRoleMapBinding) :
        BaseVH(binding.root) {
        override fun onBind(position: Int) {
            val itemVm = ApprovalRoleMapVhVm(items[position])
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

    fun add(item: ApprovalBaseResponse) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun addAll(newItems: ArrayList<ApprovalBaseResponse>) {
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

    fun remove(item: ApprovalBaseResponse) {
        val position: Int = items.indexOf(item)
        if (position > -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): ApprovalBaseResponse {
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
        add(ApprovalBaseResponse())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position: Int = items.size - 1
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeItem(item: ApprovalBaseResponse) {

    }
}