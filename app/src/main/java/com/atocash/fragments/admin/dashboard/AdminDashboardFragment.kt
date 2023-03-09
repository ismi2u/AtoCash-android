package com.atocash.fragments.admin.dashboard

import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.cashAdvance.AdminCashAdvanceActivity
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.activities.admin.travelRequest.ManageTravelRequestActivity
import com.atocash.adapter.MyPagerAdapter
import com.atocash.base.view.SuperFragment
import com.atocash.common.activity.expenseReimburse.ManageExpenseReimburseActivity
import com.atocash.databinding.FragmentAdminDashboardBinding
import com.atocash.dialog.ExpenseReimburseInitDialog
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.showSnack

class AdminDashboardFragment :
    SuperFragment<FragmentAdminDashboardBinding, AdminDashboardViewModel>(),
    AdminDashboardNavigator {

    private lateinit var binding: FragmentAdminDashboardBinding
    private lateinit var viewModel: AdminDashboardViewModel

    private lateinit var pagerAdapter: MyPagerAdapter

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_admin_dashboard
    }

    override fun getViewModel(): AdminDashboardViewModel {
        viewModel = ViewModelProvider(this)[AdminDashboardViewModel::class.java]
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    private fun initViewsAndClicks() {
        viewModel.loadFragments(requireContext(), childFragmentManager)

        val adminMenu = binding.adminDashboardBottomNav.menu
        val inboxMenu: MenuItem = adminMenu.findItem(R.id.navigation_admin_inbox)

//        show inbox when the role is multiple and also has user type
        if (hasMultipleRoles()) {
            val roles = getRole().split(",")
            if(roles.contains(Keys.LoginType.EMPLOYEE)) {
                inboxMenu.isVisible = roles.contains(Keys.LoginType.MANAGER)
            } else {
                inboxMenu.isVisible = true
            }
        } else {
            when (getRole()) {
                Keys.LoginType.EMPLOYEE -> inboxMenu.isVisible = false
                else -> inboxMenu.isVisible = true
            }
        }

        (requireActivity() as AdminMainActivity).setPageTitle(getString(R.string.title_dashboard))

        binding.adminDashboardBottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_admin_expense ->
                    setAndShowFragment(getString(R.string.title_expense))
                R.id.navigation_admin_cash_advance ->
                    setAndShowFragment(getString(R.string.title_cash_advance))
                R.id.navigation_admin_inbox ->
                    setAndShowFragment(getString(R.string.title_inbox))
                R.id.navigation_admin_travel_request ->
                    setAndShowFragment(getString(R.string.title_travel_request))
                R.id.navigation_admin_dashboard ->
                    setAndShowFragment(getString(R.string.title_dashboard))
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun setAndShowFragment(fragmentName: String) {
        binding.expandableFab.isExpanded = false
        if (!TextUtils.isEmpty(fragmentName)) {
            printLog("FragmentName $fragmentName")
            if (fragmentName == getString(R.string.title_inbox)) {
                binding.expandableFab.hide()
            } else {
                binding.expandableFab.show()
            }
            val fragmentId: Int = pagerAdapter.getFragment(fragmentName)
            printLog("fragmentId $fragmentId")
            binding.adminDashboardPager.setCurrentItem(fragmentId, false)
            (requireActivity() as AdminMainActivity).setPageTitle(fragmentName)
        }
    }

    override fun setAdapter(pagerAdapter: MyPagerAdapter) {
        this.pagerAdapter = pagerAdapter
        binding.adminDashboardPager.adapter = pagerAdapter
        binding.adminDashboardPager.offscreenPageLimit = 5
    }

    override fun onCreateExpense() {
        showExpenseCreationDialog()
    }

    private fun showExpenseCreationDialog() {
        val dialog = ExpenseReimburseInitDialog(
            requireContext(),
            object : ExpenseReimburseInitDialog.ExpenseReimburseInitCallback {
                override fun onNext(expenseReimburseInitData: ExpenseReimburseInitDialog.ExpenseReimburseInitData) {
                    openExpenseReimburse()
                }
            })
        dialog.show()
    }

    private fun openExpenseReimburse() {
        //todo send data of the before dialog via intent
        val bundle = Bundle()
        bundle.putBoolean(Keys.IntentData.IS_EDIT, false)
        intentHelper.goTo(
            requireActivity(),
            ManageExpenseReimburseActivity::class.java,
            bundle,
            false
        )
    }

    override fun onCashAdvance() {
        val bundle = Bundle()
        bundle.putBoolean(Keys.IntentData.IS_EDIT, false)
        intentHelper.goTo(requireActivity(), AdminCashAdvanceActivity::class.java, bundle, false)
    }

    override fun onTravelRequest() {
        val bundle = Bundle()
        bundle.putBoolean(Keys.IntentData.IS_EDIT, false)
        intentHelper.goTo(requireActivity(), ManageTravelRequestActivity::class.java, bundle, false)
    }

    override fun onReport() {
        requireContext().showSnack("Clicked report")
    }
}