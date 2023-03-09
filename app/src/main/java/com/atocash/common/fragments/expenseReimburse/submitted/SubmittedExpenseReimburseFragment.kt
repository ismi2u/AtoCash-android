package com.atocash.common.fragments.expenseReimburse.submitted

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.base.view.SuperFragment
import com.atocash.common.activity.expenseReimburseDetails.ExpenseReimburseDetailsActivity
import com.atocash.common.activity.expenseReimburseListing.ExpenseReimburseListingActivity
import com.atocash.common.fragments.expenseReimburse.adapter.ExpenseReimburseAdapter
import com.atocash.databinding.FragmentSubmittedExpenseReimburseBinding
import com.atocash.network.response.ExpenseRaisedForEmployeeResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.*
import com.atocash.utils.recycler.EvenMarginDecorator
import com.google.gson.Gson

class SubmittedExpenseReimburseFragment :
    SuperFragment<FragmentSubmittedExpenseReimburseBinding, SubmittedExpenseReimburseViewModel>(),
    SubmittedExpenseReimburseNavigator, ExpenseReimburseAdapter.EmployeeExpenseCallback {

    private lateinit var binding: FragmentSubmittedExpenseReimburseBinding
    private lateinit var viewModel: SubmittedExpenseReimburseViewModel

    private lateinit var adapter: ExpenseReimburseAdapter

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_submitted_expense_reimburse
    }

    override fun getViewModel(): SubmittedExpenseReimburseViewModel {
        viewModel = ViewModelProvider(this).get(SubmittedExpenseReimburseViewModel::class.java)
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    private fun initViewsAndClicks() {
        adapter = ExpenseReimburseAdapter(
            ArrayList(),
            isSubmitted = true,
            callback = this
        )
        binding.expenseRv.layoutAnimation = getBottomScaleAnim()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.expenseRv.layoutManager = layoutManager
        binding.expenseRv.addItemDecoration(
            EvenMarginDecorator(
                16,
                EvenMarginDecorator.VERTICAL
            )
        )
        binding.expenseRv.adapter = adapter
    }

    fun onEdit(item: ExpenseRaisedForEmployeeResponse) {
        val bundle = Bundle()
        bundle.putBoolean(Keys.IntentData.IS_EDIT, true)
        bundle.putString("ExpenseReimburseInitData", Gson().toJson(item))
        intentHelper.goTo(
            requireActivity(),
            ExpenseReimburseListingActivity::class.java,
            bundle,
            false
        )
    }

    fun onDelete(item: ExpenseRaisedForEmployeeResponse) {
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setMessage("Are you sure you want to delete?")
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "Yes"
        ) { dialog_, which ->
            dialog_?.dismiss()
            proceedToDelete(item)
        }
        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            "No"
        ) { dialog_, which ->
            dialog_?.dismiss()
        }
        dialog.show()
    }

    private fun proceedToDelete(item: ExpenseRaisedForEmployeeResponse) {
        if (AppHelper.isNetworkConnected(requireContext())) {
            showLoading(getString(R.string.deleting))
            viewModel.deleteRequest(getToken(), item).observe(viewLifecycleOwner, Observer {
                cancelLoading()
                when {
                    it.code() == 204 || it.code() == 200 -> {
                        requireContext().showShortToast(getString(R.string.info_cash_request_deleted_success))
                        adapter.remove(item)
                    }
                    it.code() == 401 -> {
                        (requireActivity() as AdminMainActivity).showUnAuthDialog()
                    }
                    it.code() == 409 -> {
                        requireContext().showShortToast(getString(R.string.err_cannot_delete_cash_request))
                    }
                    else -> {
                        requireContext().showErrorResponse(it.errorBody()?.string())
                    }
                }
            })
        } else {
            requireContext().showFailureToast(getString(R.string.check_internet))
        }
    }

    override fun onResume() {
        super.onResume()

        loadItems()
    }

    private fun loadItems() {
        if (AppHelper.isNetworkConnected(requireContext())) {
            viewModel.isLoading.set(true)
            viewModel.getExpenseReimburseList(getToken()).observe(viewLifecycleOwner, Observer {
                viewModel.isLoading.set(false)
                when (it.code()) {
                    200 -> {
                        val list = it.body()
                        if (list.isNullOrEmpty().not()) {
                            setAdapterForList(list)
                        }
                    }
                    401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                    else -> requireActivity().showErrorResponse(it.errorBody()?.string())
                }
            })
        } else {
            requireContext().showFailureToast(getString(R.string.check_internet))
        }
    }

    private fun setAdapterForList(list: ArrayList<ExpenseRaisedForEmployeeResponse>?) {
        list?.let { adapter.addAll(it) }
    }

    override fun onClick(
        item: ExpenseRaisedForEmployeeResponse,
        action: ExpenseReimburseAdapter.ExpenseReimburseListAction
    ) {
        when (action) {
            ExpenseReimburseAdapter.ExpenseReimburseListAction.EDIT -> onEdit(item)
            ExpenseReimburseAdapter.ExpenseReimburseListAction.DELETE -> onDelete(item)
            ExpenseReimburseAdapter.ExpenseReimburseListAction.COPY -> onCopy(item)
            ExpenseReimburseAdapter.ExpenseReimburseListAction.VIEW -> onDetails(item)
        }
    }

    private fun onDetails(item: ExpenseRaisedForEmployeeResponse) {
        val bundle = Bundle()
        bundle.putString("ExpenseRaisedForEmployeeResponse", Gson().toJson(item))
        intentHelper.goTo(
            requireActivity(),
            ExpenseReimburseDetailsActivity::class.java,
            bundle,
            false
        )
    }

    private fun onCopy(item: ExpenseRaisedForEmployeeResponse) {
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setMessage("You have to add documents for the copied claim manually. Do you want to proceed?")
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "Yes"
        ) { dialog_, _ ->
            dialog_?.dismiss()
            proceedToCopyItem(item)
        }
        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            "No"
        ) { dialog_, _ ->
            dialog_?.dismiss()
        }
        dialog.show()
    }

    private fun proceedToCopyItem(item: ExpenseRaisedForEmployeeResponse) {
        printLog("copy item ${Gson().toJson(item)}")
        val clonedItem = item.clone()

        clonedItem.id = null
        clonedItem.approvalStatusTypeId = null

        val bundle = Bundle()
        bundle.putBoolean(Keys.IntentData.IS_COPY, true)
        bundle.putString("ExpenseReimburseData", Gson().toJson(item))
        intentHelper.goTo(
            requireActivity(),
            ExpenseReimburseListingActivity::class.java,
            bundle,
            false
        )
    }
}