package com.atocash.common.activity.expenseReimburseDetails

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.adapter.TimelineAdapter
import com.atocash.base.view.SuperCompatActivity
import com.atocash.common.activity.expenseReimburseListing.adapter.ExpenseReimburseListingAdapter
import com.atocash.common.activity.expenseReimburseListing.adapter.SubClaimsViewListingAdapter
import com.atocash.databinding.ActivityExpenseReimburseDetailsBinding
import com.atocash.dialog.ExpenseSubClaimDetailsDialog
import com.atocash.network.response.ExpenseRaisedForEmployeeResponse
import com.atocash.network.response.ExpenseReimburseListingResponse
import com.atocash.network.response.TimeLineItem
import com.atocash.utils.DateUtils
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showFailureToast
import com.google.gson.Gson

class ExpenseReimburseDetailsActivity :
    SuperCompatActivity<ActivityExpenseReimburseDetailsBinding, ExpenseReimburseDetailsViewModel>(),
    ExpenseReimburseDetailsNavigator {

    private lateinit var binding: ActivityExpenseReimburseDetailsBinding
    private lateinit var viewModel: ExpenseReimburseDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_expense_reimburse_details
    }

    override fun getViewModel(): ExpenseReimburseDetailsViewModel {
        viewModel = ViewModelProvider(this).get(ExpenseReimburseDetailsViewModel::class.java)
        return viewModel
    }

    var id = 0
    private fun initViewsAndClicks() {
        binding.detailsToolBar.toolTv.text = getString(R.string.expense_reimburse_details)
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

    @SuppressLint("SetTextI18n")
    private fun updateUi(item: ExpenseRaisedForEmployeeResponse) {
        binding.idTv.text = item.id.toString()

        if (item.projectName.isNullOrEmpty().not()) {
            binding.departmentTv.text = item.projectName
            binding.departmentTitleTv.text = "${getString(R.string.hint_project)} :"
        } else {
            binding.departmentTitleTv.text = "${getString(R.string.business_type)} :"
            binding.departmentTv.text = item.businessType
        }

        binding.currentStatusTv.text = item.approvalStatusType
        binding.advanceAmountTv.text = item.totalClaimAmount.toString()
        binding.reqDateTv.text = DateUtils.getDate(item.expReimReqDate)
        binding.empTv.text = item.employeeName

        id = item.id!!
        loadApprovalFlow()
    }

    private fun loadApprovalFlow() {
        if (isNetworkConnected) {
            showLoading()
            viewModel.getApprovalFlowForExpenseReimburseRequest(getToken(), id)
                .observe(this, Observer {
                    cancelLoading()
                    if (it.code() == 200) {
                        val approvalFlow = it.body()
                        approvalFlow?.let { approvalFlow_ ->
                            setFlowAdapter(approvalFlow_)
                        }
                    } else {
                        showErrorResponse(it.errorBody()?.string())
                    }
                })
        } else {
            showSnack(getString(R.string.check_internet))
        }
    }

    private fun setFlowAdapter(approvalFlow_: ArrayList<TimeLineItem>) {
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val adapter = TimelineAdapter(approvalFlow_)
        binding.rolesRv.layoutManager = layoutManager
        binding.rolesRv.adapter = adapter
        loadSubClaims()
    }

    private fun loadSubClaims() {
        if (isNetworkConnected) {
            showLoading()
            viewModel.getExpenseSubClaims(getToken(), id).observe(this, Observer {
                cancelLoading()
                when (it.code()) {
                    200, 201 -> {
                        it.body()?.let { items ->
                            setSubclaimsList(items)
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

    private fun setSubclaimsList(items: ArrayList<ExpenseReimburseListingResponse>) {
        val adapter = SubClaimsViewListingAdapter(
            dataStorage,
            items,
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

    private fun onListingAction(
        item: ExpenseReimburseListingResponse,
        action: ExpenseReimburseListingAdapter.ExpenseReimburseListAction
    ) {
        if (action == ExpenseReimburseListingAdapter.ExpenseReimburseListAction.VIEW) {
            getAndShowDetailsForView(item)
        }
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