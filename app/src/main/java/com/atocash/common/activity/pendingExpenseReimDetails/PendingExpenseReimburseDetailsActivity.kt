package com.atocash.common.activity.pendingExpenseReimDetails

import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.R
import com.atocash.BR
import com.atocash.base.view.SuperCompatActivity
import com.atocash.common.activity.expenseReimburseListing.adapter.SubClaimsViewListingAdapter
import com.atocash.databinding.ActivityPendingExpenseReimburseDetailsBinding
import com.atocash.dialog.ExpenseSubClaimDetailsDialog
import com.atocash.network.response.ExpenseRaisedForEmployeeResponse
import com.atocash.network.response.ExpenseReimburseListingResponse
import com.atocash.utils.DateUtils
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showFailureToast
import com.google.gson.Gson

class PendingExpenseReimburseDetailsActivity :
    SuperCompatActivity<ActivityPendingExpenseReimburseDetailsBinding, PendingExpenseReimburseDetailsViewModel>(),
    PendingExpenseReimburseDetailsNavigator {

    private lateinit var binding: ActivityPendingExpenseReimburseDetailsBinding
    private lateinit var viewModel: PendingExpenseReimburseDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)
        initViewAndClicks()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_pending_expense_reimburse_details
    }

    override fun getViewModel(): PendingExpenseReimburseDetailsViewModel {
        viewModel = ViewModelProvider(this).get(PendingExpenseReimburseDetailsViewModel::class.java)
        return viewModel
    }

    private fun initViewAndClicks() {
        binding.detailsToolBar.toolTv.text = getString(R.string.pending_expense_reimbure_details)
        binding.detailsToolBar.backIv.setOnClickListener {
            onBackPressed()
        }

        val bundle = intent.extras
        bundle?.let {
            val itemStr = it.getString("ExpenseRaisedForEmployeeResponse")
            val item = Gson().fromJson(itemStr, ExpenseRaisedForEmployeeResponse::class.java)
            updateUi(item)
        }
    }

    private fun updateUi(item: ExpenseRaisedForEmployeeResponse) {
        binding.idTv.text = item.id.toString()

        if (TextUtils.isEmpty(item.departmentName)) {
            binding.departmentTv.text = item.projectName
            binding.departmentTitleTv.text = "Project: "
        } else {
            binding.departmentTv.text = item.departmentName
        }

        binding.currentStatusTv.text = item.approvalStatusType
        binding.advanceAmountTv.text = item.totalClaimAmount?.let { withCurrency(it) }
        if (item.expReimReqDate.isNotEmpty()) {
            binding.reqDateTv.text = DateUtils.getDate(item.expReimReqDate)
        }
        binding.empTv.text = item.employeeName

        val addedClaims = item.expensesSubClaims
        val adapter = SubClaimsViewListingAdapter(
            dataStorage,
            addedClaims as ArrayList<ExpenseReimburseListingResponse>,
            true,
            object : SubClaimsViewListingAdapter.EmployeeExpenseCallback {
                override fun onClick(
                    item: ExpenseReimburseListingResponse,
                    action: SubClaimsViewListingAdapter.ExpenseReimburseListAction
                ) {
                    if (action == SubClaimsViewListingAdapter.ExpenseReimburseListAction.VIEW) {
                        getAndShowDetailsForView(item)
                    }
                }
            })
        binding.claimsRv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.claimsRv.adapter = adapter
    }

    private fun getAndShowDetailsForView(item: ExpenseReimburseListingResponse) {
        if (isNetworkConnected) {
            viewModel.getExpenseSubClaimDetails(getToken(), item).observe(this, Observer {
                cancelLoading()
                when (it.code()) {
                    200, 201 -> {
                        it.body()?.let { items ->
                            showExpenseSubClaimDetails(item, items)
                        }
                    }
                    401 -> showUnAuthDialog()
                    409 -> showErrorResponse(it.errorBody()?.string())
                    else -> {
                        showErrorResponse(it.errorBody()?.string())
                        finish()
                    }
                }
            })
        } else {
            showFailureToast(getString(R.string.check_internet))
        }
    }

    private fun showExpenseSubClaimDetails(
        item: ExpenseReimburseListingResponse,
        items: ArrayList<String>
    ) {
        ExpenseSubClaimDetailsDialog(item, items).show(supportFragmentManager, "Details")
    }
}