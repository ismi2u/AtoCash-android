package com.atocash.common.activity.cashAdvanceStatusDetails

import android.os.Bundle
import android.text.TextUtils
import android.transition.TransitionManager
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.adapter.TimelineAdapter
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityCashAdvanceStatusDetailBinding
import com.atocash.network.response.PettyCashResponse
import com.atocash.network.response.TimeLineItem
import com.atocash.utils.DateUtils
import com.atocash.utils.extensions.showErrorResponse
import com.google.gson.Gson
import java.util.*

class CashAdvanceStatusDetailActivity :
    SuperCompatActivity<ActivityCashAdvanceStatusDetailBinding, CashAdvanceStatusDetailViewModel>(),
    CashAdvanceStatusDetailNavigator {

    private lateinit var binding: ActivityCashAdvanceStatusDetailBinding
    private lateinit var viewModel: CashAdvanceStatusDetailViewModel

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
        return R.layout.activity_cash_advance_status_detail
    }

    override fun getViewModel(): CashAdvanceStatusDetailViewModel {
        viewModel = ViewModelProvider(this).get(CashAdvanceStatusDetailViewModel::class.java)
        return viewModel
    }

    var id = 0
    private fun initViewsAndClicks() {
        binding.cashAdvToolBar.toolTv.text = getString(R.string.cash_advance_details)
        binding.cashAdvToolBar.backIv.setOnClickListener {
            onBackPressed()
        }

        val bundle = intent.extras
        bundle?.let {
            val itemStr = it.getString("PettyCashResponse")
            val item = Gson().fromJson(itemStr, PettyCashResponse::class.java)
            updateUi(item)
        }
    }

    private fun updateUi(item: PettyCashResponse) {
        binding.idTv.text = item.id.toString()

        TransitionManager.beginDelayedTransition(binding.parentLinear)

        if (item.project.isNullOrEmpty().not()) {
            binding.departmentTv.text = item.project
            binding.departmentTitleTv.text = "Project: "

            if (item.subProject.toString().isEmpty().not()) {
                binding.subProjectContainer.visibility = View.VISIBLE
                binding.subProjectTv.text = item.subProject
            }

            if (item.workTask.toString().isEmpty().not()) {
                binding.workTaskContainer.visibility = View.VISIBLE
                binding.workTaskTv.text = item.workTask
            }

        } else {
            binding.departmentTitleTv.text = "${getString(R.string.business_type)}:"
            binding.departmentTv.text = item.costCentre
        }

        binding.currentStatusTv.text = item.approvalStatusType
        binding.advanceAmountTv.text = item.cashAdvanceAmount.toString()
        binding.reqDateTv.text = item.requestDate?.let { DateUtils.parseIsoDate(it) }
        binding.empTv.text = item.employeeName
        binding.descTv.text = item.cashAdvanceRequestDesc

        if (item.approvalStatusType.toString().toLowerCase(Locale.getDefault()) == "rejected") {
            binding.commentsContainer.visibility = View.VISIBLE
            binding.commentsTv.text = item.comments
        }

        item.isSettled.let {
            binding.settlementStatusContainer.visibility = View.VISIBLE
            binding.settlementStatusTv.text =
                if (it) getString(R.string.action_settled) else getString(R.string.action_un_settled)
        }

        item.creditToBank?.let {
            binding.bankCreditContainer.visibility = View.VISIBLE
            binding.bankCreditTv.text = it
        } ?: run {
            binding.bankCreditContainer.visibility = View.GONE
        }

        id = item.id.toString().toInt()
        loadApprovalFlow()
    }

    private fun loadApprovalFlow() {
        if (isNetworkConnected) {
//            showLoading()
            viewModel.getApprovalFlowForRequest(getToken(), id).observe(this, Observer {
//                cancelLoading()
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
    }
}