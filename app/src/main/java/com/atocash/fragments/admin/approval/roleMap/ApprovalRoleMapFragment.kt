package com.atocash.fragments.admin.approval.roleMap

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.approval.ManageApprovalActivity
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.base.view.SuperFragment
import com.atocash.databinding.FragmentApprovalRoleMapBinding
import com.atocash.fragments.admin.approval.adapter.ApprovalType
import com.atocash.fragments.admin.approval.adapter.ApprovalsAdapter
import com.atocash.fragments.admin.department.adapter.DepartmentAdapter
import com.atocash.network.response.ApprovalBaseResponse
import com.atocash.network.response.DepartmentResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.showShortToast
import com.atocash.utils.recycler.EvenMarginDecorator
import com.google.gson.Gson

class ApprovalRoleMapFragment :
    SuperFragment<FragmentApprovalRoleMapBinding, ApprovalRoleMapViewModel>(),
    ApprovalRoleMapNavigator, ApprovalsAdapter.ApprovalAdapterCallback {

    private lateinit var viewModel: ApprovalRoleMapViewModel
    private lateinit var binding: FragmentApprovalRoleMapBinding

    private lateinit var adapter: ApprovalsAdapter


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_approval_role_map
    }

    override fun getViewModel(): ApprovalRoleMapViewModel {
        viewModel =
            ViewModelProvider(this).get(ApprovalRoleMapViewModel::class.java)
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    private fun initViewsAndClicks() {
        binding.addApprovalRoleMap.setOnClickListener {

        }

        adapter = ApprovalsAdapter(ApprovalType.ROLE_MAP, ArrayList(), this)
        binding.approvalRoleMapRv.layoutAnimation = getBottomScaleAnim()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.approvalRoleMapRv.layoutManager = layoutManager
        binding.approvalRoleMapRv.addItemDecoration(
            EvenMarginDecorator(
                16,
                EvenMarginDecorator.VERTICAL
            )
        )
        binding.approvalRoleMapRv.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        loadApprovalRoles()
    }

    private fun loadApprovalRoles() {
        viewModel.isLoading.set(true)
        viewModel.getApprovalRoles(getToken()).observe(viewLifecycleOwner, Observer {
            if (it.isNullOrEmpty().not()) {
                viewModel.isLoading.set(false)
                adapter.addAll(it)
            } else {
                viewModel.isLoading.set(false)
            }
        })
    }

    override fun onEdit(item: ApprovalBaseResponse) {
        val bundle = Bundle()
        bundle.putString("ApprovalBaseResponse", Gson().toJson(item))
        bundle.putBoolean(Keys.IntentData.IS_EDIT, true)
        bundle.putString(Keys.IntentData.FROM, Keys.Approval.ROLE_MAP)
        intentHelper.goTo(requireActivity(), ManageApprovalActivity::class.java, bundle, false)
    }

    override fun onDelete(item: ApprovalBaseResponse) {
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

    private fun proceedToDelete(item: ApprovalBaseResponse) {
        if (AppHelper.isNetworkConnected(requireContext())) {
            showLoading(getString(R.string.deleting))
            viewModel.deleteApprovalRoleMap(getToken(), item).observe(viewLifecycleOwner, Observer {
                cancelLoading()
                when {
                    it.code() == 204 || it.code() == 200 -> {
                        requireContext().showShortToast(getString(R.string.role_map_deleted_success))
                        adapter.remove(item)
                    }
                    it.code() == 401 -> {
                        (requireActivity() as AdminMainActivity).showUnAuthDialog()
                    }
                    it.code() == 409 -> {
                        requireContext().showShortToast(getString(R.string.role_map_cannot_delete))
                    }
                    else -> {
                        requireContext().showShortToast(it.message())
                    }
                }
            })
        } else {
            showSnack(getString(R.string.check_internet))
        }
    }
}