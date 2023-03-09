package com.atocash.common.fragments.expenseReimburse

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.base.view.SuperFragment
import com.atocash.common.activity.expenseReimburseListing.ExpenseReimburseListingActivity
import com.atocash.databinding.FragmentExpenseReimburseBinding
import com.atocash.dialog.ExpenseReimburseInitDialog
import com.atocash.network.response.CurrencyDropDownResponse
import com.atocash.utils.Keys
import com.atocash.utils.extensions.showErrorResponse
import com.google.gson.Gson

class ExpenseReimbursementFragment :
    SuperFragment<FragmentExpenseReimburseBinding, ExpenseReimbursementViewModel>(),
    ExpenseReimbursementNavigator {

    private lateinit var viewModel: ExpenseReimbursementViewModel
    private lateinit var binding: FragmentExpenseReimburseBinding

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
        return R.layout.fragment_expense_reimburse
    }

    override fun getViewModel(): ExpenseReimbursementViewModel {
        viewModel = ViewModelProvider(this).get(ExpenseReimbursementViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        viewModel.loadAllFragments(requireContext(), childFragmentManager)
    }

    override fun updateTabView(fragmentPagerAdapter: FragmentStatePagerAdapter) {
        binding.empExpensePager.adapter = fragmentPagerAdapter
        binding.empExpenseTab.setupWithViewPager(binding.empExpensePager)
        binding.empExpensePager.offscreenPageLimit = 2
    }

    override fun onAddExpense() {
        viewModel.getCurrencyTypesForDropDown(getToken()).observe(viewLifecycleOwner) {
            when {
                it.isSuccessful -> {
                    val list = it.body()
                    list?.let { statusItems ->
                        if (statusItems.isEmpty().not()) {
                            getCurrencyAndOpenDialog(statusItems)
                        }
                    }
                }
                it.code() == 401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                else -> requireContext().showErrorResponse(it.errorBody()?.string())
            }
        }
    }

    private fun getBusinessItems(statusItems: ArrayList<CurrencyDropDownResponse>) {
        viewModel.getBusinessTypesForDropDown(getToken()).observe(viewLifecycleOwner) {
            when {
                it.isSuccessful -> {
                    val businessItems = it.body()
                }
                it.code() == 401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                else -> requireContext().showErrorResponse(it.errorBody()?.string())
            }
        }
    }

    private fun getCurrencyAndOpenDialog(
        currencyItems: ArrayList<CurrencyDropDownResponse>
    ) {
        val userCurrencyCode = dataStorage.getString(Keys.UserData.CURRENCY_CODE).toString()
        var currencyItem: CurrencyDropDownResponse? = null
        for (item in currencyItems) {
            if (item.currencyCode == userCurrencyCode) {
                currencyItem = item
            }
        }

        val dialog = ExpenseReimburseInitDialog(
            requireContext(),
            object : ExpenseReimburseInitDialog.ExpenseReimburseInitCallback {
                override fun onNext(expenseReimburseInitData: ExpenseReimburseInitDialog.ExpenseReimburseInitData) {
                    openExpenseReimburse(expenseReimburseInitData)
                }
            })
        currencyItem?.let {
            dialog.setItemsAndShow(
                "",
                false,
                getToken(),
                dataStorage,
                CurrencyDropDownResponse(
                    id = dataStorage.getInt(Keys.UserData.CURRENCY_ID),
                    currencyCode = dataStorage.getString(Keys.UserData.CURRENCY_CODE).toString()
                ),
                null,
                "",
                null,
                "",
                null,
                "",
                null, null, "", ""
            )
        }
    }

    private fun openExpenseReimburse(expenseReimburseInitData: ExpenseReimburseInitDialog.ExpenseReimburseInitData) {
        val bundle = Bundle()
        bundle.putString("ExpenseReimburseInitData", Gson().toJson(expenseReimburseInitData))
        intentHelper.goTo(
            requireActivity(),
            ExpenseReimburseListingActivity::class.java,
            bundle,
            false
        )
    }
}