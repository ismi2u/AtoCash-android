package com.atocash.fragments.admin.approval

import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.atocash.R
import com.atocash.adapter.MyPagerAdapter
import com.atocash.base.common.BaseViewModel
import com.atocash.fragments.admin.approval.group.ApprovalGroupFragment
import com.atocash.fragments.admin.approval.level.ApprovalLevelFragment
import com.atocash.fragments.admin.approval.roleMap.ApprovalRoleMapFragment
import com.atocash.fragments.admin.approval.status.ApprovalStatusFragment

class AdminApprovalViewModel : BaseViewModel<AdminApprovalNavigator>() {

    var isLoading = ObservableField<Boolean>()

    private lateinit var levelFragment: ApprovalLevelFragment
    private lateinit var groupFragment: ApprovalGroupFragment
    private lateinit var roleMapFragment: ApprovalRoleMapFragment
    private lateinit var statusFragment: ApprovalStatusFragment

    private lateinit var pagerAdapter: MyPagerAdapter

    fun loadFragments(
        activity: FragmentActivity,
        childFragmentManager: FragmentManager
    ) {
        levelFragment = ApprovalLevelFragment()
        groupFragment = ApprovalGroupFragment()
        roleMapFragment = ApprovalRoleMapFragment()
        statusFragment = ApprovalStatusFragment()

        pagerAdapter = MyPagerAdapter(childFragmentManager)

        pagerAdapter.addFragment(activity.getString(R.string.title_level), levelFragment)
        pagerAdapter.addFragment(activity.getString(R.string.title_groups), groupFragment)
        pagerAdapter.addFragment(activity.getString(R.string.title_role_map), roleMapFragment)
        pagerAdapter.addFragment(activity.getString(R.string.title_status), statusFragment)

        getNavigator().setAdapter(pagerAdapter)
    }

    fun onFabClick() {
        getNavigator().onFabClick()
    }

    fun onCreateLevel() {
        getNavigator().onCreateLevel()
    }

    fun onCreateGroup() {
        getNavigator().onCreateGroup()
    }

    fun onCreateRoleMap() {
        getNavigator().onCreateRoleMap()
    }

    fun onCreateStatus() {
        getNavigator().onCreateStatus()
    }
}