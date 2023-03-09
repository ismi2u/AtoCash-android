package com.atocash.fragments.admin.dashboard

import com.atocash.adapter.MyPagerAdapter

interface AdminDashboardNavigator {
    fun onCreateExpense()
    fun onCashAdvance()
    fun onTravelRequest()
    fun onReport()
    abstract fun setAdapter(pagerAdapter: MyPagerAdapter)
}