package com.atocash.activities.admin.projects

import android.os.Bundle
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.adapter.CostCenterDropDownAdapter
import com.atocash.adapter.DropDownAdapter
import com.atocash.adapter.ProjectManagerDropDownAdapter
import com.atocash.adapter.ProjectsDropDownAdapter
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityManageProjectsBinding
import com.atocash.network.response.*
import com.atocash.utils.AppHelper
import com.atocash.utils.CustomTextWatcher
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.util.*

class ManageProjectsActivity
    : SuperCompatActivity<ActivityManageProjectsBinding, ManageProjectsViewModel>(),
    ManageProjectsNavigator {

    private lateinit var binding: ActivityManageProjectsBinding
    private lateinit var viewModel: ManageProjectsViewModel

    private var isEditMode = false
    private var projectId = 0
    private var subProjectId = 0

    private var isProject = false
    private var purpose = ""
    private var subProjectsResponse: SubProjectsResponse? = null
    private var mainProjectResponse: ProjectsResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_manage_projects
    }

    override fun getViewModel(): ManageProjectsViewModel {
        viewModel = ViewModelProvider(this).get(ManageProjectsViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        val bundle = intent.extras
        bundle?.let {
            isEditMode = it.getBoolean(Keys.IntentData.IS_EDIT)
            purpose = it.getString(Keys.IntentData.FROM).toString()
            if (purpose.isEmpty().not()) {
                if (purpose.toLowerCase(Locale.getDefault()) == "project") {
                    isProject = true
                    initBackWithTitle(binding.toolbar.toolParent, getString(R.string.manage_projects))
                    if (isEditMode) {
                        val dataStr = it.getString("ProjectData")
                        dataStr?.let { dataStr_ ->
                            mainProjectResponse =
                                Gson().fromJson(dataStr_, ProjectsResponse::class.java)
                            updateUiForProject(mainProjectResponse)
                        }
                    }
                } else {
                    isProject = false
                    initBackWithTitle(binding.toolbar.toolParent, getString(R.string.manage_sub_projects))
                    if (isEditMode) {
                        val dataStr = it.getString("SubProjectData")
                        dataStr?.let { dataStr_ ->
                            subProjectsResponse =
                                Gson().fromJson(dataStr_, SubProjectsResponse::class.java)
                            updateUiForSubProject(subProjectsResponse)
                        }
                    }
                }
            }
        }

        viewModel.isProject.set(isProject)

        binding.createBtn.text =
            if (isEditMode) getString(R.string.btn_update) else getString(R.string.btn_create)

        initUiListeners()

        if (!isEditMode) {
            if (isProject) {
                loadCostCentersForDropDown()
            } else {
                loadProjects()
            }
        }
    }

    private fun updateUiForSubProject(subProjectsResponse: SubProjectsResponse?){
        subProjectsResponse?.let {
            subProjectId = it.id
            binding.taskNameEd.setText(it.subProjectName)
            binding.taskDescEd.setText(it.subProjectDesc)
            binding.projectEd.setText(it.projectName)

            projectId = it.projectId

            loadProjects()
        }
    }

    private fun updateUiForProject(mainProjectResponse: ProjectsResponse?) {
        mainProjectResponse?.let {
            projectId = it.id

            binding.taskNameEd.setText(it.projectName)
            binding.taskDescEd.setText(it.projectDesc)

            statusItem = StatusDropDownResponse()
            statusItem.id = it.statusTypeId
            statusItem.status = if (it.statusTypeId == 1) "Active" else "Inactive"
            binding.statusEd.setText(statusItem.status, false)

            costCenterItem = CostCenterDropDownResponse()
            costCenterItem.id = it.costCenterId
//            binding.projectCostCenterEd.setText(costCenterItem.costCenterCode, false)

            projectManagerResponse = ProjectManagerResponse()
            projectManagerResponse.id = it.projectManagerId
//            projectManagerResponse.fullName = it.projectManager
//            binding.projectManagerEd.setText(it.projectManager, false)

            loadCostCentersForDropDown()
        }
    }

    private fun initUiListeners() {
        binding.createBtn.setOnClickListener {
            onCreateProject()
        }

        binding.taskNameEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) binding.taskNameLayout.error = ""
            }
        })

        binding.taskDescEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) binding.taskDescLayout.error = ""
            }
        })

        binding.projectCostCenterEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) binding.projectCostCenterLayout.error = ""
            }
        })

        binding.projectManagerEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) binding.projectManagerLayout.error = ""
            }
        })

        binding.statusEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) binding.statusLayout.error = ""
            }
        })

        if (!isProject) {
            binding.projectEd.addTextChangedListener(object : CustomTextWatcher() {
                override fun onTextChanged(text: String) {
                    if (text.isNotEmpty()) binding.projectLayout.error = ""
                }
            })
        }
    }

    override fun onBack() {
        AppHelper.hideKeyboard(this)
        onBackPressed()
    }

    override fun updateUi(msg: String, isDone: Boolean) {
        cancelLoading()
        showShortToast(msg)
        if (isDone) {
            finish()
        }
    }

    private fun loadCostCentersForDropDown() {
        showLoading()
        viewModel.getCostCenterForDropDown(getToken()).observe(this, androidx.lifecycle.Observer {
            cancelLoading()

            if (it.isNullOrEmpty().not()) {
                setCostCenterAdapter(it)
            }

            loadStatus()
        })
    }

    var costCenterItem = CostCenterDropDownResponse()
    private fun setCostCenterAdapter(it: ArrayList<CostCenterDropDownResponse>) {
        val adapter = CostCenterDropDownAdapter(this, R.layout.item_status_spinner_view, it)
        binding.projectCostCenterEd.setAdapter(adapter)
        binding.projectCostCenterEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                costCenterItem = it[position]
                binding.projectCostCenterEd.setText(it[position].costCenterCode, false)
                adapter.filter.filter(null)
            }

        if (isEditMode) {
            for (items in it) {
                if (items.id == costCenterItem.id) {
                    costCenterItem.costCenterCode = items.costCenterCode
                    binding.projectCostCenterEd.setText(costCenterItem.costCenterCode)
                }
            }
        }
    }

    private fun loadStatus() {
        showLoading()
        viewModel.getStatusForDropDown(getToken()).observe(this, androidx.lifecycle.Observer {
            cancelLoading()
            if (it.isNullOrEmpty().not()) {
                setDropDownAdapter(it)
            }

            if (isProject) {
                loadProjectManagers()
            }
        })
    }

    private fun loadProjects() {
        showLoading()
        viewModel.getProjectsForDropDown(getToken())
            .observe(this, androidx.lifecycle.Observer {
                cancelLoading()
                it.let { res ->
                    if (res.isNullOrEmpty().not()) {
                        setProjectsAdapter(res)
                    }
                }
            })
    }

    var projectsResponse = ProjectsResponse()
    private fun setProjectsAdapter(res: ArrayList<ProjectsResponse>) {
        val adapter = ProjectsDropDownAdapter(this, R.layout.item_status_spinner_view, res)
        binding.projectEd.setAdapter(adapter)
        binding.projectEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                projectsResponse = res[position]
                binding.projectEd.setText(res[position].projectName, false)
                adapter.filter.filter(null)
            }
    }

    var statusItem = StatusDropDownResponse()
    private fun setDropDownAdapter(it: ArrayList<StatusDropDownResponse>) {
        val adapter = DropDownAdapter(this, R.layout.item_status_spinner_view, it)
        binding.statusEd.setAdapter(adapter)
        binding.statusEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                statusItem = it[position]
                binding.statusEd.setText(it[position].status, false)
                adapter.filter.filter(null)
            }
    }

    private fun loadProjectManagers() {
        showLoading()
        viewModel.getProjectsManagersForDropDown(getToken())
            .observe(this, androidx.lifecycle.Observer {
                cancelLoading()
                it.let { res ->
                    if (res.isNullOrEmpty().not()) {
                        setProjectManagerAdapter(res)
                    }
                }
            })
    }

    var projectManagerResponse = ProjectManagerResponse()
    private fun setProjectManagerAdapter(it: ArrayList<ProjectManagerResponse>) {
        val adapter = ProjectManagerDropDownAdapter(this, R.layout.item_status_spinner_view, it)
        binding.projectManagerEd.setAdapter(adapter)
        binding.projectManagerEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                projectManagerResponse = it[position]
                binding.projectManagerEd.setText(it[position].fullName, false)
                adapter.filter.filter(null)
            }

        if (isEditMode) {
            for (items in it) {
                if (items.id == projectManagerResponse.id) {
                    projectManagerResponse.fullName = items.fullName
                    binding.projectManagerEd.setText(projectManagerResponse.fullName)
                }
            }
        }
    }

    override fun onCreateProject() {
        if (isAllDataAvailable()) {
            printLog("All data avilable")
            if (isNetworkConnected) {
                if (isProject) {
                    printLog("Proceed to project")
                    proceedForProject()
                } else {
                    printLog("Proceed to project")
                    proceedForSubProject()
                }
            } else {
                showSnack(getString(R.string.check_internet))
            }
        }
    }

    private fun proceedForProject() {
        if (isEditMode) {
            showLoading()
            viewModel.updateProject(getToken(), projectId, getProjectJson())
                .observe(this, androidx.lifecycle.Observer {
                    cancelLoading()
                    when {
                        it.code() == 200 -> {
                            showShortToast(getString(R.string.project_updated_success))
                            finish()
                        }
                        it.code() == 401 -> {
                            showUnAuthDialog()
                        }
                        else -> {
                            showErrorResponse(it.errorBody()?.string())
                        }
                    }
                })
        } else {
            showLoading()
            viewModel.createProject(getToken(), getProjectJson())
                .observe(this, androidx.lifecycle.Observer {
                    cancelLoading()
                    when {
                        it.code() == 201 -> {
                            showShortToast(getString(R.string.project_create_success))
                            finish()
                        }
                        it.code() == 401 -> {
                            showUnAuthDialog()
                        }
                        else -> {
                            showErrorResponse(it.errorBody()?.string())
                        }
                    }
                })
        }
    }

    private fun proceedForSubProject() {
        if (isEditMode) {
            showLoading()
            viewModel.updateSubProject(getToken(), subProjectId, getSubProjectJson())
                .observe(this, androidx.lifecycle.Observer {
                    cancelLoading()
                    when {
                        it.code() == 200 -> {
                            showShortToast(getString(R.string.sub_project_update_success))
                            finish()
                        }
                        it.code() == 401 -> {
                            showUnAuthDialog()
                        }
                        else -> {
                            showErrorResponse(it.errorBody()?.string())
                        }
                    }
                })
        } else {
            showLoading()
            viewModel.createSubProject(getToken(), getSubProjectJson())
                .observe(this, androidx.lifecycle.Observer {
                    cancelLoading()
                    when {
                        it.code() == 201 -> {
                            showShortToast(getString(R.string.sub_project_delete_success))
                            finish()
                        }
                        it.code() == 401 -> {
                            showUnAuthDialog()
                        }
                        else -> {
                            showErrorResponse(it.errorBody()?.string())
                        }
                    }
                })
        }
    }

    private fun getSubProjectJson(): JsonObject {
        val jsonObject = JsonObject()

        jsonObject.addProperty("subProjectName", binding.taskNameEd.text.toString().trim())
        jsonObject.addProperty("subProjectDesc", binding.taskDescEd.text.toString().trim())
        jsonObject.addProperty("projectId", projectsResponse.id)

        if (isEditMode) {
            jsonObject.addProperty("id", subProjectId)
        }

        return jsonObject
    }

    private fun getProjectJson(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("costCenterId", costCenterItem.id)
        jsonObject.addProperty("projectManagerId", projectManagerResponse.id)

        jsonObject.addProperty("projectName", binding.taskNameEd.text.toString().trim())
        jsonObject.addProperty("projectDesc", binding.taskDescEd.text.toString().trim())

        jsonObject.addProperty("statusTypeId", statusItem.id)

        if (isEditMode) {
            jsonObject.addProperty("id", projectId)
        }

        return jsonObject
    }

    private fun isAllDataAvailable(): Boolean {
        AppHelper.hideKeyboard(this)

        if (binding.taskNameEd.text.toString().trim().isEmpty()) {
            binding.taskNameLayout.error = getString(R.string.err_enter_name)
            return false
        }

        if (binding.taskDescEd.text.toString().trim().isEmpty()) {
            binding.taskDescLayout.error = getString(R.string.err_enter_name)
            return false
        }

        if (isProject) {
            if (binding.projectCostCenterEd.text.toString().trim().isEmpty() || costCenterItem.costCenterCode.isEmpty()
            ) {
                binding.projectCostCenterLayout.error = getString(R.string.err_choose_cost_center)
                return false
            }

            if (binding.projectManagerEd.text.toString().trim().isEmpty()) {
                binding.projectManagerLayout.error = getString(R.string.err_choose_project_manager)
                return false
            }

            if (binding.statusEd.text.toString().trim().isEmpty() || statusItem.status.isEmpty()) {
                binding.statusLayout.error = getString(R.string.err_choose_status)
                return false
            }
        } else {
            if (binding.projectEd.text.toString().trim().isEmpty()) {
                binding.projectLayout.error = getString(R.string.err_choose_project)
                return false
            }
        }

        return true
    }
}