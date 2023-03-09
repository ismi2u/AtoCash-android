package com.atocash.common.activity.expenseReimburseListing.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.atocash.R
import com.atocash.utils.extensions.getDecimalFormatValue

class ExpenseReimburseSubmitAdapter(private val items: ArrayList<ExpenseReimburseSubmitListItems>) :
    RecyclerView.Adapter<ExpenseReimburseSubmitAdapter.ExpenseReimburseSubmitVH>() {

    data class ExpenseReimburseSubmitListItems(
        var type: String,
        var amount: String
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseReimburseSubmitVH {
        return ExpenseReimburseSubmitVH(
            LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.expense_submit_item, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ExpenseReimburseSubmitVH, position: Int) {
        holder.onBindView(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ExpenseReimburseSubmitVH(view: View) : RecyclerView.ViewHolder(view.rootView) {

        private val titleTv = view.findViewById<TextView>(R.id.title_tv)
        private val amountTv = view.findViewById<TextView>(R.id.totalAmtTv)

        fun onBindView(position: Int) {
            titleTv.text = items[position].type
            amountTv.text = getDecimalFormatValue(items[position].amount.toFloat())
        }
    }
}