package com.atocash.fragments.admin.dashboard

import android.content.Context
import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentManager
import com.atocash.R
import com.atocash.adapter.MyPagerAdapter
import com.atocash.base.common.BaseViewModel
import com.atocash.common.fragments.expenseReimburse.ExpenseReimbursementFragment
import com.atocash.common.fragments.inbox.InboxFragment
import com.atocash.fragments.admin.dashboard.cashAdvance.AdminCashAdvanceFragment
import com.atocash.fragments.admin.dashboard.travelReq.AdminTravelRequestFragment
import com.atocash.fragments.employee.home.EmployeeHomeFragment

class AdminDashboardViewModel : BaseViewModel<AdminDashboardNavigator>() {

    var isLoading = ObservableField<Boolean>(true)

    fun onCreateExpense() {
        getNavigator().onCreateExpense()
    }

    fun onCashAdvance() {
        getNavigator().onCashAdvance()
    }

    fun onTravelRequest() {
        getNavigator().onTravelRequest()
    }

    fun onReport() {
        getNavigator().onReport()
    }

    private lateinit var dashboardFragment: EmployeeHomeFragment
    private lateinit var expenseFragment: ExpenseReimbursementFragment
    private lateinit var cashAdvanceFragment: AdminCashAdvanceFragment
    private lateinit var travelRequestFragment: AdminTravelRequestFragment
//    private lateinit var reportFragment: AdminReportFragment

    private lateinit var inboxFragment: InboxFragment

    private lateinit var pagerAdapter: MyPagerAdapter

    fun loadFragments(context: Context, childFragmentManager: FragmentManager) {
        dashboardFragment = EmployeeHomeFragment()
        expenseFragment = ExpenseReimbursementFragment()
        cashAdvanceFragment = AdminCashAdvanceFragment()
        travelRequestFragment = AdminTravelRequestFragment()
        inboxFragment = InboxFragment()
//        reportFragment = AdminReportFragment()

        pagerAdapter = MyPagerAdapter(childFragmentManager)

        pagerAdapter.addFragment(context.getString(R.string.title_dashboard), dashboardFragment)
        pagerAdapter.addFragment(context.getString(R.string.title_expense), expenseFragment)
        pagerAdapter.addFragment(
            context.getString(R.string.title_cash_advance),
            cashAdvanceFragment
        )
        pagerAdapter.addFragment(
            context.getString(R.string.title_travel_request),
            travelRequestFragment
        )
        pagerAdapter.addFragment(context.getString(R.string.title_inbox), inboxFragment)
//        pagerAdapter.addFragment("Report", reportFragment)

        getNavigator().setAdapter(pagerAdapter)
    }
}