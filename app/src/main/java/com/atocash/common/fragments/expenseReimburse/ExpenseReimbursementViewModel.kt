package com.atocash.common.fragments.expenseReimburse

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.atocash.R
import com.atocash.base.common.BaseViewModel
import com.atocash.common.fragments.expenseReimburse.pending.PendingExpenseReimburseFragment
import com.atocash.common.fragments.expenseReimburse.submitted.SubmittedExpenseReimburseFragment
import java.util.*

class ExpenseReimbursementViewModel : BaseViewModel<ExpenseReimbursementNavigator>() {

    private lateinit var fragmentNames: ArrayList<String>
    private lateinit var fragmentList: ArrayList<Fragment>

    private lateinit var fragmentManager: FragmentManager

    private lateinit var pendingEmpExpenseFragment: PendingExpenseReimburseFragment
    private lateinit var submittedEmpExpenseFragment: SubmittedExpenseReimburseFragment

    fun onAddExpense() {
        getNavigator().onAddExpense()
    }

    fun loadAllFragments(context: Context, childFragmentManager: FragmentManager) {
        fragmentNames = ArrayList()
        fragmentList = ArrayList()

        fragmentManager = childFragmentManager

        pendingEmpExpenseFragment = PendingExpenseReimburseFragment()
        submittedEmpExpenseFragment = SubmittedExpenseReimburseFragment()

        fragmentList.add(submittedEmpExpenseFragment)
        fragmentList.add(pendingEmpExpenseFragment)

        fragmentNames.add(context.getString(R.string.submitted))
        fragmentNames.add(context.getString(R.string.pending))

        val fragmentPagerAdapter: FragmentStatePagerAdapter =
            object : FragmentStatePagerAdapter(fragmentManager, 0) {
                override fun getItem(position: Int): Fragment {
                    return fragmentList[position]
                }

                override fun getCount(): Int {
                    return fragmentList.size
                }

                override fun getPageTitle(position: Int): CharSequence? {
                    return fragmentNames[position]
                }
            }
        getNavigator().updateTabView(fragmentPagerAdapter)
    }

}