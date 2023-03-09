package com.atocash.fragments.admin.assignProject

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.adapter.ProjectManagerDropDownAdapter
import com.atocash.adapter.ProjectsDropDownAdapter
import com.atocash.base.view.SuperFragment
import com.atocash.databinding.FragmentAssignProjectBinding
import com.atocash.network.response.ProjectManagerResponse
import com.atocash.network.response.ProjectsResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.CustomTextWatcher
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.google.gson.JsonObject
import java.util.*

class AssignProjectFragment :
    SuperFragment<FragmentAssignProjectBinding, AssignProjectViewModel>(),
    AssignProjectNavigator {

    private lateinit var viewModel: AssignProjectViewModel
    private lateinit var binding: FragmentAssignProjectBinding

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
        return R.layout.fragment_assign_project
    }

    override fun getViewModel(): AssignProjectViewModel {
        viewModel =
            ViewModelProvider(this).get(AssignProjectViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        binding.employeeEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.employeeLayout.error = ""
                }
            }
        })
        binding.projectEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.projectLayout.error = ""
                }
            }
        })

        loadEmployees()
    }

    private fun loadEmployees() {
        viewModel.getEmployeesForDropDown(getToken())
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                it.let { res ->
                    if (res.isNullOrEmpty().not()) {
                        setEmployeesAdapter(res)
                    }
                }
            })
    }

    private var employeeItemId = 0
    private var projectId = 0
    private var employeeDropDownResponse: ProjectManagerResponse? = null

    private var empAdapter: ProjectManagerDropDownAdapter? = null
    private fun setEmployeesAdapter(res: ArrayList<ProjectManagerResponse>) {
        empAdapter =
            ProjectManagerDropDownAdapter(requireContext(), R.layout.item_status_spinner_view, res)
        binding.employeeEd.setAdapter(empAdapter)
        binding.employeeEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                employeeItemId = res[position].id
                employeeDropDownResponse = res[position]
                binding.employeeEd.setText(res[position].fullName, false)
                setEmployeeFilter(empAdapter!!)
            }
        loadProjects()
    }

    private fun setEmployeeFilter(adapter: ProjectManagerDropDownAdapter) {
        adapter.filter.filter(null)
    }

    private fun loadProjects() {
        showLoading()
        viewModel.getProjectsForDropDown(getToken())
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                cancelLoading()
                it.let { res ->
                    if (res.isNullOrEmpty().not()) {
                        setProjectsAdapter(res)
                    }
                }
            })
    }

    var projectsResponse = ProjectsResponse()
    private var projAdapter: ProjectsDropDownAdapter? = null

    private fun setProjectsAdapter(res: ArrayList<ProjectsResponse>) {
        projAdapter =
            ProjectsDropDownAdapter(requireContext(), R.layout.item_status_spinner_view, res)
        binding.projectEd.setAdapter(projAdapter)
        binding.projectEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                projectId = res[position].id
                projectsResponse = res[position]
                binding.projectEd.setText(res[position].projectName, false)
                setProjectFilter(projAdapter!!)
            }
    }

    private fun setProjectFilter(adapter: ProjectsDropDownAdapter) {
        adapter.filter.filter(null)
    }

    override fun onAssignProject() {
        if (AppHelper.isNetworkConnected(requireContext())) {
            if (isAllDataAvailable()) {
                showLoading()
                viewModel.assignProject(getToken(), getProjectJson())
                    .observe(this, androidx.lifecycle.Observer {
                        cancelLoading()
                        when {
                            it.code() == 200 -> {
                                requireContext().showShortToast(getString(R.string.project_assigned_success))
                                clearUi()
                            }
                            it.code() == 401 -> {
                                (requireActivity() as AdminMainActivity).showUnAuthDialog()
                            }
                            else -> {
                                requireContext().showErrorResponse(it.errorBody()?.string())
                            }
                        }
                    })
            }
        } else {
            showSnack(getString(R.string.check_internet))
        }
    }

    private fun getProjectJson(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("projectId", projectId)
        jsonObject.addProperty("employeeId", employeeItemId)
        return jsonObject
    }

    private fun clearUi() {
        binding.employeeEd.setText("")
        binding.projectEd.setText("")

        employeeItemId = 0
        employeeDropDownResponse = ProjectManagerResponse()

        projectId = 0
        projectsResponse = ProjectsResponse()

        empAdapter?.let { setEmployeeFilter(it) }
        projAdapter?.let { setProjectFilter(it) }
    }

    private fun isAllDataAvailable(): Boolean {
        AppHelper.hideKeyboard(requireContext())

        if (binding.employeeEd.text.toString().trim().isEmpty()) {
            binding.employeeLayout.error = getString(R.string.err_choose_employee)
            return false
        }

        if (binding.projectEd.text.toString().trim().isEmpty()) {
            binding.projectLayout.error = getString(R.string.err_choose_project)
            return false
        }

        return true
    }
}