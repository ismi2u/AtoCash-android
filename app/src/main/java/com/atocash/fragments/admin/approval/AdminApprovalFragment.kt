package com.atocash.fragments.admin.approval

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.approval.ManageApprovalActivity
import com.atocash.adapter.MyPagerAdapter
import com.atocash.base.view.SuperFragment
import com.atocash.databinding.FragmentApprovalBinding
import com.atocash.utils.Keys

class AdminApprovalFragment :
    SuperFragment<FragmentApprovalBinding, AdminApprovalViewModel>(),
    AdminApprovalNavigator {

    private lateinit var viewModelAdmin: AdminApprovalViewModel
    private lateinit var binding: FragmentApprovalBinding

    private lateinit var pagerAdapter: MyPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModelAdmin.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_approval
    }

    override fun getViewModel(): AdminApprovalViewModel {
        viewModelAdmin =
            ViewModelProvider(this).get(AdminApprovalViewModel::class.java)
        return viewModelAdmin
    }

    private fun initViewsAndClicks() {
        viewModelAdmin.loadFragments(requireActivity(), childFragmentManager)

        binding.approvalBottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_level ->
                    setAndShowFragment(getString(R.string.title_level))
                R.id.navigation_groups ->
                    setAndShowFragment(getString(R.string.title_groups))
                R.id.navigation_role_map ->
                    setAndShowFragment(getString(R.string.title_role_map))
                R.id.navigation_status ->
                    setAndShowFragment(getString(R.string.title_status))
            }
            return@setOnNavigationItemSelectedListener true
        }

        binding.approvalPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (binding.expandableFab.isExpanded) {
                    binding.expandableFab.isExpanded = false
                }
            }
        })
    }

    private fun setAndShowFragment(fragmentName: String) {
        if (!TextUtils.isEmpty(fragmentName)) {
            val fragmentId: Int = pagerAdapter.getFragment(fragmentName)
            binding.approvalPager.setCurrentItem(fragmentId, false)
        }
    }

    override fun setAdapter(pagerAdapter: MyPagerAdapter) {
        this.pagerAdapter = pagerAdapter
        binding.approvalPager.adapter = pagerAdapter
        binding.approvalPager.offscreenPageLimit = 4
    }

    override fun onFabClick() {
        //binding.expandableFab.isExpanded = !binding.expandableFab.isExpanded
    }

    override fun onCreateLevel() {
        val bundle = Bundle()
        bundle.putString(Keys.IntentData.FROM, Keys.Approval.LEVEL)
        intentHelper.goTo(requireActivity(), ManageApprovalActivity::class.java, bundle, false)
    }

    override fun onCreateGroup() {
        val bundle = Bundle()
        bundle.putString(Keys.IntentData.FROM, Keys.Approval.GROUP)
        intentHelper.goTo(requireActivity(), ManageApprovalActivity::class.java, bundle, false)
    }

    override fun onCreateRoleMap() {
        val bundle = Bundle()
        bundle.putString(Keys.IntentData.FROM, Keys.Approval.ROLE_MAP)
        intentHelper.goTo(requireActivity(), ManageApprovalActivity::class.java, bundle, false)
    }

    override fun onCreateStatus() {
        val bundle = Bundle()
        bundle.putString(Keys.IntentData.FROM, Keys.Approval.STATUS)
        intentHelper.goTo(requireActivity(), ManageApprovalActivity::class.java, bundle, false)
    }

}