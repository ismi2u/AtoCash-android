package com.atocash.fragments.admin.approval

import com.atocash.adapter.MyPagerAdapter

interface AdminApprovalNavigator {
    abstract fun setAdapter(pagerAdapter: MyPagerAdapter)
    fun onFabClick()
    fun onCreateLevel()
    fun onCreateGroup()
    fun onCreateRoleMap()
    fun onCreateStatus()
}