package com.atocash.fragments.admin.roles

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.costCenter.ManageCostCenterActivity
import com.atocash.activities.admin.jobRoles.ManageJobRolesActivity
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.base.view.SuperFragment
import com.atocash.databinding.FragmentRolesBinding
import com.atocash.fragments.admin.roles.adapter.JobRolesAdapter
import com.atocash.network.response.CostCenterResponse
import com.atocash.network.response.JobRolesResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.showShortToast
import com.atocash.utils.recycler.EvenMarginDecorator
import com.google.gson.Gson

class JobRolesFragment :
    SuperFragment<FragmentRolesBinding, JobRolesViewModel>(),
    JobRolesNavigator, JobRolesAdapter.CostCenterCallback {

    private lateinit var viewModelJob: JobRolesViewModel
    private lateinit var binding: FragmentRolesBinding

    private lateinit var adapater: JobRolesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModelJob.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_roles
    }

    override fun getViewModel(): JobRolesViewModel {
        viewModelJob =
            ViewModelProvider(this).get(JobRolesViewModel::class.java)
        return viewModelJob
    }

    private fun initViewsAndClicks() {
        binding.addJobRole.setOnClickListener {
            intentHelper.goTo(requireActivity(), ManageJobRolesActivity::class.java, null, false)
        }

        adapater = JobRolesAdapter(ArrayList(), this)
        binding.jobRoleRv.layoutAnimation = getBottomScaleAnim()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.jobRoleRv.layoutManager = layoutManager
        binding.jobRoleRv.addItemDecoration(
            EvenMarginDecorator(
                16,
                EvenMarginDecorator.VERTICAL
            )
        )
        binding.jobRoleRv.adapter = adapater
    }

    override fun onResume() {
        super.onResume()

        loadJobRoles()
    }

    private fun loadJobRoles() {
        viewModelJob.isLoading.set(true)

        viewModelJob.getRoles(getToken()).observe(viewLifecycleOwner, Observer {
            viewModelJob.isLoading.set(false)
            if (it.code() == 200) {
                val listResponse = it.body()
                if (listResponse.isNullOrEmpty().not()) {
                    listResponse?.let { it1 -> adapater.addAll(it1) }
                }
            } else if (it.code() == 401) {
                (requireActivity() as AdminMainActivity).showUnAuthDialog()
            } else {
                requireContext().showShortToast(it.message())
            }
        })
    }

    override fun onEdit(item: JobRolesResponse) {
        val bundleArgs = Bundle()
        bundleArgs.putString("JobRolesResponse", Gson().toJson(item))
        bundleArgs.putBoolean(Keys.IntentData.IS_EDIT, true)
        intentHelper.goTo(
            requireActivity(),
            ManageJobRolesActivity::class.java,
            bundle = bundleArgs
        )
    }

    override fun onDelete(item: JobRolesResponse) {
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

    private fun proceedToDelete(item: JobRolesResponse) {
        if (AppHelper.isNetworkConnected(requireContext())) {
            showLoading(getString(R.string.deleting))
            viewModelJob.deleteJobRoles(getToken(), item).observe(viewLifecycleOwner, Observer {
                cancelLoading()
                when {
                    it.code() == 200 -> {
                        requireContext().showShortToast(getString(R.string.info_cost_center_deleted_success))
                        adapater.remove(item)
                    }
                    it.code() == 401 -> {
                        requireContext().showShortToast(it.message())
                    }
                    it.code() == 409 -> {
                        requireContext().showShortToast(getString(R.string.err_cannot_delete_cost_center))
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