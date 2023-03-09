package com.atocash.fragments.admin.expense

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.expenseTypes.ManageExpenseTypeActivity
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.base.view.SuperFragment
import com.atocash.databinding.FragmentExpenseTypesBinding
import com.atocash.fragments.admin.expense.adapter.ExpenseTypeAdapter
import com.atocash.network.response.ExpenseTypeResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.atocash.utils.recycler.EvenMarginDecorator
import com.google.gson.Gson

class ExpenseTypesFragment :
    SuperFragment<FragmentExpenseTypesBinding, ExpenseTypesViewModel>(), ExpenseTypesNavigator,
    ExpenseTypeAdapter.CostCenterCallback {

    private lateinit var viewModel: ExpenseTypesViewModel
    private lateinit var binding: FragmentExpenseTypesBinding

    private lateinit var adapter: ExpenseTypeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_expense_types
    }

    override fun getViewModel(): ExpenseTypesViewModel {
        viewModel =
            ViewModelProvider(this).get(ExpenseTypesViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        binding.addExpenseType.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Keys.IntentData.FROM, "")
            intentHelper.goTo(requireActivity(), ManageExpenseTypeActivity::class.java, bundle, false)
        }

        adapter = ExpenseTypeAdapter(ArrayList(), this)
        binding.expenseTypesRv.layoutAnimation = getBottomScaleAnim()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.expenseTypesRv.layoutManager = layoutManager
        binding.expenseTypesRv.addItemDecoration(
            EvenMarginDecorator(
                16,
                EvenMarginDecorator.VERTICAL
            )
        )
        binding.expenseTypesRv.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        loadExpenses()
    }

    private fun loadExpenses() {
        viewModel.isLoading.set(true)
        viewModel.getExpenseTypesList(getToken()).observe(viewLifecycleOwner, Observer {
            viewModel.isLoading.set(false)
            if (it.isNullOrEmpty().not()) {
                adapter.addAll(it)
            }
        })
    }

    override fun onEdit(item: ExpenseTypeResponse) {
        val bundle = Bundle()
        bundle.putString("ExpenseItem", Gson().toJson(item))
        bundle.putBoolean(Keys.IntentData.IS_EDIT, true)
        intentHelper.goTo(requireActivity(), ManageExpenseTypeActivity::class.java, bundle, false)
    }

    override fun onDelete(item: ExpenseTypeResponse) {
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setMessage(getString(R.string.ask_to_delete))
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            getString(R.string.yes)
        ) { dialog_, which ->
            dialog_?.dismiss()
            proceedToDelete(item)
        }
        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            getString(R.string.no)
        ) { dialog_, which ->
            dialog_?.dismiss()
        }
        dialog.show()
    }

    private fun proceedToDelete(item: ExpenseTypeResponse) {
        if (AppHelper.isNetworkConnected(requireContext())) {
            showLoading(getString(R.string.deleting))
            viewModel.deleteExpenseTypes(getToken(), item).observe(viewLifecycleOwner, Observer {
                cancelLoading()
                when {
                    it.code() == 204 || it.code() == 200 -> {
                        requireContext().showShortToast(getString(R.string.info_expense_type_deleted_success))
                        adapter.remove(item)
                    }
                    it.code() == 401 -> {
                        (requireActivity() as AdminMainActivity).showUnAuthDialog()
                    }
                    else -> {
                        requireContext().showErrorResponse(it.errorBody()?.string())
                    }
                }
            })
        } else {
            showSnack(getString(R.string.check_internet))
        }
    }
}