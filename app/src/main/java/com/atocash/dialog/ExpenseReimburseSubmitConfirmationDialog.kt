package com.atocash.dialog

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.R
import com.atocash.common.activity.expenseReimburseListing.adapter.ExpenseReimburseSubmitAdapter
import com.atocash.utils.AppHelper
import com.atocash.utils.extensions.getDecimalFormatValue

class ExpenseReimburseSubmitConfirmationDialog(
    context: Context,
    private val choiceDialogCallback: ExpReimSubmitCallback
) :
    Dialog(context, R.style.CustomDialogAnim) {

    private val submitBtn: AppCompatButton
    private val expenseOrDeptTv: TextView
    private val totalClaimAmountTv: TextView
    private val subClaimsRv: RecyclerView

    private var items = ArrayList<ExpenseReimburseSubmitAdapter.ExpenseReimburseSubmitListItems>()
    private lateinit var adapter: ExpenseReimburseSubmitAdapter

    interface ExpReimSubmitCallback {
        fun onNext()
    }

    init {
        val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_exp_reim_submit_confirmation, null)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (window != null) window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        submitBtn = view.findViewById(R.id.next_btn)
        totalClaimAmountTv = view.findViewById(R.id.totalAmtTv)
        expenseOrDeptTv = view.findViewById(R.id.expenseOrDeptTv)
        subClaimsRv = view.findViewById(R.id.claims_rv)

        setUiListeners()

        setContentView(view)
        setCancelable(true)
    }

    private fun setUiListeners() {
        submitBtn.setOnClickListener {
            choiceDialogCallback.onNext()
        }
    }

    private fun updateUi() {
        if(totalClaimAmount.isNotEmpty()) {
            totalClaimAmountTv.text = getDecimalFormatValue(totalClaimAmount.toFloat())
        }
        expenseOrDeptTv.text = expenseOrDept
        adapter = ExpenseReimburseSubmitAdapter(items = items)
        subClaimsRv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        subClaimsRv.adapter = adapter
        show()
    }

    private var totalClaimAmount = ""
    private var expenseOrDept = ""
    fun setItemsAndShow(
        totalClaimAmount: String, expenseOrDept: String,
        items: ArrayList<ExpenseReimburseSubmitAdapter.ExpenseReimburseSubmitListItems>
    ) {
        this.totalClaimAmount = totalClaimAmount
        this.expenseOrDept = expenseOrDept
        this.items = items
        updateUi()
    }

    private var mProgressDialog: ProgressDialog? = null

    fun showLoading(msg: String) {
        cancelLoading()
        mProgressDialog = AppHelper.showLoadingDialog(context, msg)
    }

    fun showLoading() {
        cancelLoading()
        mProgressDialog = AppHelper.showLoadingDialog(context, context.getString(R.string.loading))
    }

    fun cancelLoading() {
        mProgressDialog?.let {
            if (it.isShowing) it.cancel()
        }
    }
}
