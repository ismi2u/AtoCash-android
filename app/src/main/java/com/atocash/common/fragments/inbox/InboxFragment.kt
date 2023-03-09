package com.atocash.common.fragments.inbox

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.base.view.SuperFragment
import com.atocash.common.activity.cashAdvanceStatusDetails.CashAdvanceStatusDetailActivity
import com.atocash.common.activity.expenseReimburseListing.ExpenseReimburseListingActivity
import com.atocash.common.activity.travelRequestDetails.TravelRequestDetailActivity
import com.atocash.common.fragments.inbox.adapter.InboxAdapter
import com.atocash.databinding.FragmentInboxBinding
import com.atocash.dialog.RejectCommentDialog
import com.atocash.network.response.*
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showFailureToast
import com.atocash.utils.extensions.showShortToast
import com.atocash.utils.recycler.EvenMarginDecorator
import com.google.gson.Gson
import com.google.gson.GsonBuilder


class InboxFragment :
    SuperFragment<FragmentInboxBinding, InboxViewModel>(),
    InboxNavigator {

    private lateinit var viewModel: InboxViewModel
    private lateinit var binding: FragmentInboxBinding

    private var currentType: InboxType = InboxType.ADVANCE

    enum class InboxType {
        EXPENSE, ADVANCE, TRAVEL
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_inbox
    }

    override fun getViewModel(): InboxViewModel {
        viewModel = ViewModelProvider(this).get(InboxViewModel::class.java)
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    private fun initViewsAndClicks() {
        binding.inboxRv.layoutAnimation = getBottomScaleAnim()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.inboxRv.layoutManager = layoutManager
        binding.inboxRv.addItemDecoration(
            EvenMarginDecorator(
                16,
                EvenMarginDecorator.VERTICAL
            )
        )
    }

    override fun showDropDown() {
        val popup = PopupMenu(requireContext(), binding.inboxListTypeTv)
        popup.inflate(R.menu.inbox_pop_up_menu)
        popup.setOnMenuItemClickListener { item ->
            if (item != null) {
                when (item.itemId) {
                    R.id.menu_cash_advance -> loadCashAdvances()
                    R.id.menu_travel_request -> loadTravelRequests()
                    R.id.menu_expense_reimbursement -> loadExpenses()
                }
            }
            false
        }
        popup.show()
    }

    private fun isCurrentType(type: InboxType): Boolean {
        currentType.let {
            return it == type
        }
    }

    override fun onResume() {
        super.onResume()

        currentType.let {
            loadApprovalStatusTypes()
            when (it) {
                InboxType.ADVANCE -> loadCashAdvances()
                InboxType.EXPENSE -> loadExpenses()
                InboxType.TRAVEL -> loadTravelRequests()
                else -> loadCashAdvances()
            }
        }
    }

    data class ApprovalStatusTypes(
        var id: Int? = 0,
        var status: String? = null,
        var statusDesc: String? = null
    )

    private var approvalStatusTypes = ArrayList<ApprovalStatusTypes>()
    private fun loadApprovalStatusTypes() {
        viewModel.loadApprovalTypes(getToken()).observe(viewLifecycleOwner, Observer {
            when (it.code()) {
                200 -> {
                    val items = it.body()
                    items?.let { items_ ->
                        for (item in items_) {
                            approvalStatusTypes.add(
                                ApprovalStatusTypes(item.id, item.status, item.statusDesc)
                            )
                        }
                    }
                }
                401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                else -> requireContext().showErrorResponse(it.errorBody()?.string())
            }
        })
    }

    private fun loadExpenses() {
        viewModel.isLoading.set(true)
        currentType = InboxType.EXPENSE
        binding.inboxListTypeTv.text = getString(R.string.title_expense_reimnbursement)

        viewModel.getExpenseReimburse(getToken()).observe(viewLifecycleOwner, Observer {
            viewModel.isLoading.set(false)
            when (it.code()) {
                200 -> {
                    val items = it.body()
                    items?.let { items_ ->
                        setExpenseAdapter(items_)
                    }
                }
                401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                else -> requireContext().showErrorResponse(it.errorBody()?.string())
            }
        })
    }

    var chosenExpenseList = ArrayList<InboxExpenseReimburseResponse>()
    private fun setExpenseAdapter(items_: ArrayList<InboxExpenseReimburseResponse>) {
        chosenExpenseList = arrayListOf()
        val adapter =
            InboxAdapter(InboxType.EXPENSE, ArrayList(), ArrayList(), items_)
        adapter.setExpenseCallback(object :
            InboxAdapter.InboxExpenseReimburseCallback {
            override fun onItemClick(item: InboxExpenseReimburseResponse) {
                showExpensDetails(item)
            }

            override fun onChosen(item: InboxExpenseReimburseResponse, isChecked: Boolean) {
                if (isChecked) {
                    chosenExpenseList.add(item)
                } else {
                    if (chosenExpenseList.contains(item)) {
                        chosenExpenseList.remove(item)
                    }
                }
                setVisibilityForButtons()
            }
        })
        binding.inboxRv.adapter = adapter
        setVisibilityForButtons()
    }

    private fun showExpensDetails(item: InboxExpenseReimburseResponse) {
        if (AppHelper.isNetworkConnected(requireContext())) {
            showLoading()
            item.expenseReimburseRequestId?.let {
                viewModel.getExpenseDetails(getToken(), it)
                    .observe(viewLifecycleOwner, Observer {
                        cancelLoading()
                        if (it.code() == 200) {
                            val response = it.body()
                            response?.let { response_ ->
                                openExpenseDetails(response_)
                            }
                        }
                    })
            }
        } else {
            showSnack(getString(R.string.check_internet))
        }
    }

    private fun openExpenseDetails(response_: InboxExpenseReimburseResponse) {
        val bundle = Bundle()
        bundle.putBoolean(Keys.IntentData.IS_VIEW, true)
        bundle.putString("InboxExpenseReimburseResponse", Gson().toJson(response_))
        intentHelper.goTo(
            requireActivity(),
            ExpenseReimburseListingActivity::class.java,
            bundle,
            false
        )
    }

    private fun loadTravelRequests() {
        viewModel.isLoading.set(true)
        currentType = InboxType.TRAVEL
        binding.inboxListTypeTv.text = getString(R.string.title_travel_request)

        viewModel.getTravelExpenses(getToken()).observe(viewLifecycleOwner, Observer {
            viewModel.isLoading.set(false)
            when (it.code()) {
                200 -> {
                    val items = it.body()
                    items?.let { items_ ->
                        setAdapterForTravel(items_)
                    }
                }
                401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                else -> requireContext().showErrorResponse(it.errorBody()?.string())
            }
        })
    }

    var chosenTravelItems = ArrayList<InboxTravelRequestResponse>()
    private fun setAdapterForTravel(items_: ArrayList<InboxTravelRequestResponse>) {
        chosenExpenseList = arrayListOf()
        val adapter =
            InboxAdapter(InboxType.TRAVEL, ArrayList(), items_, ArrayList())
        adapter.setTravelExpenseCallback(object :
            InboxAdapter.InboxTravelExpenseCallback {
            override fun onItemClick(item: InboxTravelRequestResponse) {
                showTravelDetails(item)
            }

            override fun onChosen(item: InboxTravelRequestResponse, isChecked: Boolean) {
                if (isChecked) {
                    chosenTravelItems.add(item)
                } else {
                    if (chosenTravelItems.contains(item)) {
                        chosenTravelItems.remove(item)
                    }
                }
                setVisibilityForButtons()
            }
        })
        binding.inboxRv.adapter = adapter
        setVisibilityForButtons()
    }

    private fun showTravelDetails(item: InboxTravelRequestResponse) {
        if (AppHelper.isNetworkConnected(requireContext())) {
            showLoading()
            viewModel.getTravelRequestDetails(getToken(), item.travelApprovalRequestId)
                .observe(viewLifecycleOwner, Observer {
                    cancelLoading()
                    if (it.code() == 200) {
                        val response = it.body()
                        response?.let { response_ ->
                            openTravelDetails(response_)
                        }
                    }
                })
        } else {
            showSnack(getString(R.string.check_internet))
        }
    }

    private fun openTravelDetails(response_: TravelResponse) {
        val bundle = Bundle()
        bundle.putString("TravelResponse", Gson().toJson(response_))
        intentHelper.goTo(
            requireActivity(),
            TravelRequestDetailActivity::class.java,
            bundle,
            false
        )
    }

    private fun loadCashAdvances() {
        viewModel.isLoading.set(true)
        currentType = InboxType.ADVANCE
        binding.inboxListTypeTv.text = getString(R.string.title_cash_advance)

        viewModel.getCashAdvances(getToken()).observe(viewLifecycleOwner, Observer {
            viewModel.isLoading.set(false)
            when (it.code()) {
                200 -> {
                    val items = it.body()
                    items?.let { items_ ->
                        setAdapterForCash(items_)
                    }
                }
                401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                else -> requireContext().showErrorResponse(it.errorBody()?.string())
            }
        })
    }

    var chosenCashAdvanceItems = ArrayList<InboxCashAdvanceResponse>()
    private fun setAdapterForCash(items_: ArrayList<InboxCashAdvanceResponse>) {
        chosenCashAdvanceItems = ArrayList()
        val adapter =
            InboxAdapter(InboxType.ADVANCE, items_, ArrayList(), ArrayList())
        adapter.setCashAdvanceCallback(object :
            InboxAdapter.InboxCashAdvanceCallback {
            override fun onItemClick(item: InboxCashAdvanceResponse) {
                showCashDetails(item)
            }

            override fun onChosen(item: InboxCashAdvanceResponse, isChecked: Boolean) {
                if (isChecked) {
                    chosenCashAdvanceItems.add(item)
                } else {
                    if (chosenCashAdvanceItems.contains(item)) {
                        chosenCashAdvanceItems.remove(item)
                    }
                }
                setVisibilityForButtons()
            }
        })
        binding.inboxRv.adapter = adapter
        setVisibilityForButtons()
    }

    private fun setVisibilityForButtons() {
        TransitionManager.beginDelayedTransition(binding.inboxParent)

        printLog("current type ${getCurrentType()}")
        printLog("chosenCashAdvanceItems ${chosenCashAdvanceItems.size}")
        printLog("chosenExpenseList ${chosenExpenseList.size}")
        printLog("chosenTravelItems ${chosenTravelItems.size}")

        when (getCurrentType()) {
            InboxType.ADVANCE -> binding.inboxFooter.visibility =
                if (chosenCashAdvanceItems.isEmpty()) View.GONE else View.VISIBLE
            InboxType.EXPENSE -> binding.inboxFooter.visibility =
                if (chosenExpenseList.isEmpty()) View.GONE else View.VISIBLE
            else -> binding.inboxFooter.visibility =
                if (chosenTravelItems.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun getCurrentType(): InboxType {
        return currentType
    }

    private fun showCashDetails(item: InboxCashAdvanceResponse) {
        if (AppHelper.isNetworkConnected(requireContext())) {
            showLoading()
            item.cashAdvanceRequestId?.let {
                viewModel.getCashDetails(getToken(), it)
                    .observe(viewLifecycleOwner, Observer {
                        cancelLoading()
                        if (it.code() == 200) {
                            val response = it.body()
                            response?.let { response_ ->
                                openDetails(response_)
                            }
                        }
                    })
            }
        } else {
            showSnack(getString(R.string.check_internet))
        }
    }

    private fun openDetails(response_: PettyCashResponse) {
        val bundle = Bundle()
        bundle.putString("PettyCashResponse", Gson().toJson(response_))
        intentHelper.goTo(
            requireActivity(),
            CashAdvanceStatusDetailActivity::class.java,
            bundle,
            false
        )
    }

    override fun onReject() {
        when (getCurrentType()) {
            InboxType.ADVANCE -> {
                if (chosenCashAdvanceItems.isEmpty()) {
                    requireContext().showFailureToast(getString(R.string.no_req_selected))
                } else {
                    RejectCommentDialog(
                        requireContext(),
                        object : RejectCommentDialog.RejectCommentDialogCallback {
                            override fun onNext(rejectComment: String) {
                                rejectCashAdvance(rejectComment)
                            }
                        }).show()
                }
            }
            InboxType.EXPENSE -> {
                if (chosenExpenseList.isEmpty()) {
                    requireContext().showFailureToast(getString(R.string.no_req_selected))
                } else {
                    RejectCommentDialog(
                        requireContext(),
                        object : RejectCommentDialog.RejectCommentDialogCallback {
                            override fun onNext(rejectComment: String) {
                                rejectExpense(rejectComment)
                            }
                        }).show()
                }
            }
            InboxType.TRAVEL -> {
                if (chosenTravelItems.isEmpty()) {
                    requireContext().showFailureToast(getString(R.string.no_req_selected))
                } else {
                    RejectCommentDialog(
                        requireContext(),
                        object : RejectCommentDialog.RejectCommentDialogCallback {
                            override fun onNext(rejectComment: String) {
                                rejectTravelRequest(rejectComment)
                            }
                        }).show()
                }
            }
        }
    }

    private fun rejectTravelRequest(rejectComment: String) {
        for (items in chosenTravelItems) {
            items.approvalStatusTypeId = 5
            items.comments = rejectComment
        }
        val jsonArray = GsonBuilder().serializeNulls().setPrettyPrinting().create()
            .toJsonTree(chosenTravelItems).asJsonArray
        for (i in 0 until jsonArray.size()) {
            val jsonItem = jsonArray[i]
            jsonItem.asJsonObject.remove("isChecked")
        }
        for (i in 0 until jsonArray.size()) {
            val jsonItem = jsonArray[i]
            jsonItem.asJsonObject.remove("isChecked")
        }
        viewModel.approveRejectTravelRequest(getToken(), jsonArray)
            .observe(viewLifecycleOwner, Observer {
                when (it.code()) {
                    200 -> {
                        requireContext().showShortToast(getString(R.string.travel_request_rejected_successfully))
                        chosenTravelItems = arrayListOf()
                        loadTravelRequests()
                    }
                    401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                    else -> requireContext().showErrorResponse(it.errorBody()?.string())
                }
            })
    }

    private fun rejectExpense(rejectComment: String) {
        for (items in chosenExpenseList) {
            items.approvalStatusTypeId = 5
            items.comments = rejectComment
        }

        val jsonArray = GsonBuilder().create().toJsonTree(chosenExpenseList).asJsonArray
        for (i in 0 until jsonArray.size()) {
            val jsonItem = jsonArray[i]
            jsonItem.asJsonObject.remove("isChecked")
        }

        viewModel.approveRejectExpense(getToken(), jsonArray)
            .observe(viewLifecycleOwner, Observer {
                when (it.code()) {
                    200 -> {
                        requireContext().showShortToast(getString(R.string.expense_reimburse_rejected_successfully))
                        chosenExpenseList = arrayListOf()
                        loadExpenses()
                    }
                    401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                    else -> requireContext().showErrorResponse(it.errorBody()?.string())
                }
            })
    }

    private fun rejectCashAdvance(rejectComment: String) {
        for (items in chosenCashAdvanceItems) {
            items.approvalStatusTypeId = 5
            items.comments = rejectComment
        }

        val jsonArray = GsonBuilder().serializeNulls().setPrettyPrinting().create()
            .toJsonTree(chosenCashAdvanceItems).asJsonArray
        for (i in 0 until jsonArray.size()) {
            val jsonItem = jsonArray[i]
            jsonItem.asJsonObject.remove("isChecked")
        }

        viewModel.approveRejectCashAdvances(getToken(), jsonArray)
            .observe(viewLifecycleOwner, Observer {
                when (it.code()) {
                    200 -> {
                        requireContext().showShortToast(getString(R.string.cash_advance_rejected_successfully))
                        chosenCashAdvanceItems = arrayListOf()
                        loadCashAdvances()
                    }
                    401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                    else -> requireContext().showErrorResponse(it.errorBody()?.string())
                }
            })
    }

    override fun onApprove() {
        when (getCurrentType()) {
            InboxType.ADVANCE -> {
                if (chosenCashAdvanceItems.isEmpty()) {
                    requireContext().showFailureToast(getString(R.string.no_req_selected))
                } else {
                    approveCashRequest()
                }
            }
            InboxType.EXPENSE -> {
                if (chosenExpenseList.isEmpty()) {
                    requireContext().showFailureToast(getString(R.string.no_req_selected))
                } else {
                    approveExpenseList()
                }
            }
            InboxType.TRAVEL -> {
                if (chosenTravelItems.isEmpty()) {
                    requireContext().showFailureToast(getString(R.string.no_req_selected))
                } else {
                    approveTravelRequests()
                }
            }
        }
    }

    private fun approveTravelRequests() {
        Log.e("Thulasi", "Chosen travel request ${Gson().toJson(chosenTravelItems)}")

        for (items in chosenTravelItems) {
            items.approvalStatusType = Keys.StatusType.APPROVED
            items.approvalStatusTypeId = getStatusIdForType(Keys.StatusType.APPROVED)!!
            items.comments = ""
        }

        val jsonArray = GsonBuilder().serializeNulls().setPrettyPrinting().create()
            .toJsonTree(chosenTravelItems).asJsonArray
        for (i in 0 until jsonArray.size()) {
            val jsonItem = jsonArray[i]
            jsonItem.asJsonObject.remove("isChecked")
        }

        viewModel.approveRejectTravelRequest(getToken(), jsonArray)
            .observe(viewLifecycleOwner, Observer {
                when (it.code()) {
                    200 -> {
                        requireContext().showShortToast(getString(R.string.travel_request_approved_successfully))
                        chosenTravelItems = arrayListOf()
                        loadTravelRequests()
                    }
                    401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                    else -> requireContext().showErrorResponse(it.errorBody()?.string())
                }
            })
    }

    private fun approveExpenseList() {
        for (items in chosenExpenseList) {
            items.approvalStatusType = Keys.StatusType.APPROVED
            items.approvalStatusTypeId = getStatusIdForType(Keys.StatusType.APPROVED)!!
            items.comments = ""
        }

        val jsonArray = GsonBuilder().serializeNulls().setPrettyPrinting().create()
            .toJsonTree(chosenExpenseList).asJsonArray
        for (i in 0 until jsonArray.size()) {
            val jsonItem = jsonArray[i]
            jsonItem.asJsonObject.remove("isChecked")
        }

        printLog(jsonArray.toString())

        showLoading("Approving expense requests...")
        viewModel.approveRejectExpense(getToken(), jsonArray)
            .observe(viewLifecycleOwner, Observer {
                cancelLoading()
                when (it.code()) {
                    200 -> {
                        requireContext().showShortToast(getString(R.string.exp_reimburse_approved_successfully))
                        chosenExpenseList = arrayListOf()
                        loadExpenses()
                    }
                    401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                    else -> requireContext().showErrorResponse(it.errorBody()?.string())
                }
            })
    }

    private fun approveCashRequest() {
        for (items in chosenCashAdvanceItems) {
            items.approvalStatusType = Keys.StatusType.APPROVED
            items.approvalStatusTypeId = getStatusIdForType(Keys.StatusType.APPROVED)!!
            items.comments = ""
        }

        val jsonArray = GsonBuilder().serializeNulls().setPrettyPrinting().create()
            .toJsonTree(chosenCashAdvanceItems).asJsonArray
        for (i in 0 until jsonArray.size()) {
            val jsonItem = jsonArray[i]
            jsonItem.asJsonObject.remove("isChecked")
        }

        printLog(jsonArray.toString())

        showLoading("Approving cash requests...")
        viewModel.approveRejectCashAdvances(getToken(), jsonArray)
            .observe(viewLifecycleOwner, Observer {
                cancelLoading()
                when (it.code()) {
                    200 -> {
                        requireContext().showShortToast(getString(R.string.cash_advance_approved_successfully))
                        chosenCashAdvanceItems = arrayListOf()
                        loadCashAdvances()
                    }
                    401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                    else -> requireContext().showErrorResponse(it.errorBody()?.string())
                }
            })
    }

    private fun getStatusIdForType(statusString: String): Int? {
        if (approvalStatusTypes.isNullOrEmpty().not()) {
            for (item in approvalStatusTypes) {
                if (item.status == statusString) {
                    return item.id
                }
            }
        }
        return -1
    }
}