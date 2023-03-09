package com.atocash.common.fragments.expenseReimburse.pending

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.base.view.SuperFragment
import com.atocash.common.activity.expenseReimburse.ManageExpenseReimburseActivity
import com.atocash.common.activity.expenseReimburseDetails.ExpenseReimburseDetailsActivity
import com.atocash.common.activity.expenseReimburseListing.ExpenseReimburseListingActivity
import com.atocash.common.activity.pendingExpenseReimDetails.PendingExpenseReimburseDetailsActivity
import com.atocash.common.fragments.expenseReimburse.adapter.ExpenseReimburseAdapter
import com.atocash.database.AtoCashDB
import com.atocash.database.DatabaseHelperImpl
import com.atocash.databinding.FragmentPendingExpenseReimburseBinding
import com.atocash.network.response.ExpenseRaisedForEmployeeResponse
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.showDebugToast
import com.atocash.utils.recycler.EvenMarginDecorator
import com.google.gson.Gson

class PendingExpenseReimburseFragment :
    SuperFragment<FragmentPendingExpenseReimburseBinding, PendingExpenseReimburseViewModel>(),
    PendingExpenseReimburseNavigator, ExpenseReimburseAdapter.EmployeeExpenseCallback {

    private lateinit var binding: FragmentPendingExpenseReimburseBinding
    private lateinit var viewModel: PendingExpenseReimburseViewModel

    private lateinit var adapter: ExpenseReimburseAdapter
    private var dbHelper: DatabaseHelperImpl? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_pending_expense_reimburse
    }

    override fun getViewModel(): PendingExpenseReimburseViewModel {
        viewModel = ViewModelProvider(this).get(PendingExpenseReimburseViewModel::class.java)
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    private fun initViewsAndClicks() {
        dbHelper = DatabaseHelperImpl(AtoCashDB.getDatabaseClient(requireContext()).appDao())
        adapter = ExpenseReimburseAdapter(
            ArrayList(),
            isSubmitted = false,
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

        viewModel.isLoading.set(false)
        viewModel.items.observe(viewLifecycleOwner, Observer {
            if (it.isNullOrEmpty()) {
                printLog("No pending found!")
            } else {
                printLog("pending found!")
                adapter.addAll(it as ArrayList<ExpenseRaisedForEmployeeResponse>)
            }
        })
    }

    fun onEdit(item: ExpenseRaisedForEmployeeResponse) {
        val bundle = Bundle()
        bundle.putBoolean(Keys.IntentData.IS_EDIT, true)
        bundle.putString("ExpenseReimburseInitData", Gson().toJson(item))
        intentHelper.goTo(
            requireActivity(),
//            ManageExpenseReimburseActivity::class.java,
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
        dbHelper?.let {
            viewModel.deleteItem(it, item.id)
            loadItems()
        }
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
            PendingExpenseReimburseDetailsActivity::class.java,
            bundle,
            false
        )
    }

    private fun onCopy(item: ExpenseRaisedForEmployeeResponse) {
        requireActivity().showDebugToast("Clicked copy")
    }

    override fun onResume() {
        super.onResume()

        loadItems()
    }

    private fun loadItems() {
        dbHelper?.let {
            val userId = dataStorage.getString(Keys.UserData.ID).toString().toInt()
            viewModel.getPendingExpenses(it, userId)
        }
    }
}