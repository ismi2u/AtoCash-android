package com.atocash.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.R
import com.atocash.adapter.PickedDocsAdapter
import com.atocash.common.activity.viewDoc.WebViewActivity
import com.atocash.network.response.ExpenseReimburseListingResponse
import com.atocash.network.response.PostDocumentsResponse
import com.atocash.utils.DateUtils
import com.atocash.utils.IntentHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.getDecimalFormatValue
import com.atocash.utils.extensions.printLog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ExpenseSubClaimDetailsDialog(
    private val expenseItem: ExpenseReimburseListingResponse,
    private val items: ArrayList<String>
) :
    BottomSheetDialogFragment() {

    private lateinit var invoiceNoTv: TextView
    private lateinit var claimAmtTv: TextView
    private lateinit var invoiceDateTv: TextView
    private lateinit var locationTv: TextView
    private lateinit var taxAmtTv: TextView
    private lateinit var vendorTv: TextView
    private lateinit var expenseTv: TextView
    private lateinit var docsRv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_expense_sub_claim_details, null)

        invoiceNoTv = view.findViewById(R.id.invoice_no_tv)
        claimAmtTv = view.findViewById(R.id.claim_amt_tv)
        invoiceDateTv = view.findViewById(R.id.invoice_date_tv)
        locationTv = view.findViewById(R.id.location_tv)
        taxAmtTv = view.findViewById(R.id.tax_amt_tv)
        vendorTv = view.findViewById(R.id.vendor_tv)
        expenseTv = view.findViewById(R.id.expense_tv)
        docsRv = view.findViewById(R.id.docs_rv)

        invoiceNoTv.text = expenseItem.invoiceNo
        claimAmtTv.text = expenseItem.expenseReimbClaimAmount?.let { getDecimalFormatValue(it) }
        invoiceDateTv.text = DateUtils.getDate(expenseItem.invoiceDate.toString())
        locationTv.text = expenseItem.location
        taxAmtTv.text = expenseItem.taxAmount?.let { getDecimalFormatValue(it) }
        expenseTv.text = expenseItem.expenseType

        if (expenseItem.additionalVendor.isNullOrEmpty()) {
            vendorTv.text = "-"
        } else {
            vendorTv.text = expenseItem.additionalVendor
        }

        val docsAdapter = PickedDocsAdapter(
            hideDelete = true,
            callback = object : PickedDocsAdapter.PickedDocsCallback {
                override fun onDelete(item: PostDocumentsResponse) {

                }

                override fun onView(item: PostDocumentsResponse) {
                    val bundle = Bundle()
                    printLog("image path from document response ${item.actualFileName}")
                    bundle.putString(Keys.IntentData.URL, item.actualFileName.toString())
                    val intentHelper = IntentHelper(requireActivity())
                    intentHelper.goTo(requireActivity(), WebViewActivity::class.java, bundle, false)
                }
            })
        docsRv.layoutManager = LinearLayoutManager(docsRv.context, RecyclerView.VERTICAL, false)
        docsRv.adapter = docsAdapter

        val fileItems = ArrayList<PostDocumentsResponse>()
        var imagePath = Keys.Api.IMAGE_PREFIX
        imagePath = imagePath.substring(0, imagePath.length - 1)
        for (item in items) {
            fileItems.add(
                PostDocumentsResponse(
                    id = null,
                    actualFileName = imagePath + item
                )
            )
        }
        docsAdapter.addAll(fileItems)

        dialog.setContentView(view)
        dialog.setCancelable(true)

        return dialog
    }
}