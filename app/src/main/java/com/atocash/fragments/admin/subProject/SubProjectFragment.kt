package com.atocash.fragments.admin.subProject

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.activities.admin.projects.ManageProjectsActivity
import com.atocash.base.view.SuperFragment
import com.atocash.databinding.FragmentSubProjectBinding
import com.atocash.fragments.admin.subProject.adapter.SubProjectsAdapter
import com.atocash.network.MockData
import com.atocash.network.response.ProjectsResponse
import com.atocash.network.response.SubProjectsResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.showShortToast
import com.atocash.utils.recycler.EvenMarginDecorator
import com.google.gson.Gson

class SubProjectFragment :
    SuperFragment<FragmentSubProjectBinding, SubProjectViewModel>(),
    SubProjectNavigator, SubProjectsAdapter.CostCenterCallback {

    private lateinit var viewModel: SubProjectViewModel
    private lateinit var binding: FragmentSubProjectBinding

    private lateinit var adapter: SubProjectsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_sub_project
    }

    override fun getViewModel(): SubProjectViewModel {
        viewModel =
            ViewModelProvider(this).get(SubProjectViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        binding.addProject.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(Keys.IntentData.IS_EDIT, false)
            bundle.putString(Keys.IntentData.FROM, "SubProject")
            intentHelper.goTo(requireActivity(), ManageProjectsActivity::class.java, bundle, false)
        }

        adapter = SubProjectsAdapter(ArrayList(), this)
        binding.projectRv.layoutAnimation = getBottomScaleAnim()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.projectRv.layoutManager = layoutManager
        binding.projectRv.addItemDecoration(
            EvenMarginDecorator(
                16,
                EvenMarginDecorator.VERTICAL
            )
        )
        binding.projectRv.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        loadSubProjects()
    }

    private fun loadSubProjects() {
        viewModel.isLoading.set(true)

        viewModel.getProjectList(getToken()).observe(viewLifecycleOwner, Observer {
            viewModel.isLoading.set(false)
            if (it.code() == 200) {
                val listResponse = it.body()
                if (listResponse.isNullOrEmpty().not()) {
                    listResponse?.let { it1 -> adapter.addAll(it1) }
                }
            } else if (it.code() == 401) {
                (requireActivity() as AdminMainActivity).showUnAuthDialog()
            } else {
                requireContext().showShortToast(it.message())
            }
        })
    }

    override fun onEdit(item: SubProjectsResponse) {
        val bundle = Bundle()
        bundle.putString("SubProjectData", Gson().toJson(item))
        bundle.putBoolean(Keys.IntentData.IS_EDIT, true)
        bundle.putString(Keys.IntentData.FROM, "SubProject")
        intentHelper.goTo(requireActivity(), ManageProjectsActivity::class.java, bundle, false)
    }

    override fun onDelete(item: SubProjectsResponse) {
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

    private fun proceedToDelete(item: SubProjectsResponse) {
        if (AppHelper.isNetworkConnected(requireContext())) {
            showLoading(getString(R.string.deleting))
            viewModel.deleteSubProject(getToken(), item).observe(viewLifecycleOwner, Observer {
                cancelLoading()
                when {
                    it.code() == 204 || it.code() == 200 -> {
                        requireContext().showShortToast(getString(R.string.info_sub_project_deleted_success))
                        adapter.remove(item)
                    }
                    it.code() == 401 -> {
                        requireContext().showShortToast(it.message())
                    }
                    it.code() == 409 -> {
                        requireContext().showShortToast(getString(R.string.err_cannot_delete_sub_project))
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