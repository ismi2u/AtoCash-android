package com.atocash.common.fragments.expenseReimburse

import androidx.fragment.app.FragmentStatePagerAdapter

interface ExpenseReimbursementNavigator {
    fun updateTabView(fragmentPagerAdapter: FragmentStatePagerAdapter)
    fun onAddExpense()
}