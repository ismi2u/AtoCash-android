package com.atocash.common.activity.travelRequestDetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.adapter.TimelineAdapter
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityTravelRequestDetailBinding
import com.atocash.network.response.TimeLineItem
import com.atocash.network.response.TravelResponse
import com.atocash.utils.DateUtils
import com.atocash.utils.extensions.showErrorResponse
import com.google.gson.Gson

class TravelRequestDetailActivity :
    SuperCompatActivity<ActivityTravelRequestDetailBinding, TravelRequestDetailsViewModel>(),
    TravelRequestDetailNavigator {

    private lateinit var binding: ActivityTravelRequestDetailBinding
    private lateinit var viewModel: TravelRequestDetailsViewModel

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
        return R.layout.activity_travel_request_detail
    }

    override fun getViewModel(): TravelRequestDetailsViewModel {
        viewModel = ViewModelProvider(this).get(TravelRequestDetailsViewModel::class.java)
        return viewModel
    }

    var id = 0
    private fun initViewsAndClicks() {
        binding.cashAdvToolBar.toolTv.text = getString(R.string.travel_request_details)
        binding.cashAdvToolBar.backIv.setOnClickListener {
            onBackPressed()
        }

        val bundle = intent.extras
        bundle?.let {
            val itemStr = it.getString("TravelResponse")
            val item = Gson().fromJson(itemStr, TravelResponse::class.java)
            updateUi(item)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi(item: TravelResponse) {
        binding.idTv.text = item.id.toString()

        if(item.businessType.isNullOrEmpty().not()) {
            binding.departmentTv.text = item.businessType
            binding.departmentTitleTv.text = "${getString(R.string.business_type)} :"
        } else {
            binding.departmentTitleTv.text = "${getString(R.string.hint_project)} :"
            binding.departmentTv.text = item.projectName
        }

        binding.currentStatusTv.text = item.approvalStatusType
        binding.reqDateTv.text = item.travelStartDate?.let { DateUtils.getDate(it) }
        binding.empTv.text = item.travelEndDate?.let { DateUtils.getDate(it) }
        binding.descTv.text = item.travelPurpose

        id = item.id!!
        loadApprovalFlow()
    }

    private fun loadApprovalFlow() {
        if (isNetworkConnected) {
            showLoading()
            viewModel.getApprovalFlowForRequest(getToken(), id).observe(this, Observer {
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
    }

}