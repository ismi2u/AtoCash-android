package com.atocash.activities.admin.common

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.adapter.ProjectManagerDropDownAdapter
import com.atocash.adapter.SubProjectsDropDownAdapter
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityManageAdminBinding
import com.atocash.network.response.ProjectManagerResponse
import com.atocash.network.response.SubProjectsResponse
import com.atocash.network.response.TasksResponse
import com.atocash.network.response.UsersResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.CustomTextWatcher
import com.atocash.utils.Keys
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.util.*

class ManageAdminActivity :
    SuperCompatActivity<ActivityManageAdminBinding, ManageAdminViewModel>(),
    ManageAdminNavigator {

    private lateinit var binding: ActivityManageAdminBinding
    private lateinit var viewModel: ManageAdminViewModel

    private var isEditMode = false

    private var purpose = ""

    private var taskItemId = 0
    private var subProjectId = 0
    private var subProjectsResponse: SubProjectsResponse? = null
    private var tasksResponse: TasksResponse? = null

    private var userItemId = 0
    private var employeeItemId = 0
    private var employeeDropDownResponse: ProjectManagerResponse? = null
    private var userResponse: UsersResponse? = null

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
        return R.layout.activity_manage_admin
    }

    override fun getViewModel(): ManageAdminViewModel {
        viewModel = ViewModelProvider(this).get(ManageAdminViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        var title = ""
        val bundle = intent.extras
        bundle?.let {
            isEditMode = it.getBoolean(Keys.IntentData.IS_EDIT)
            purpose = it.getString(Keys.IntentData.FROM).toString()
            when (purpose) {
                "Tasks" -> {
                    title = getString(R.string.manage_tasks)
                    binding.taskContainer.visibility = View.VISIBLE
                    if (isEditMode) {
                        val dataStr = it.getString("TaskData")
                        dataStr?.let { dataStr_ ->
                            tasksResponse = Gson().fromJson(dataStr_, TasksResponse::class.java)
                            updateUiForTasks(tasksResponse)
                        }
                    } else {
                        loadSubProjectsForDropDown()
                    }
                }
                "UserRoles" -> {
                    title = getString(R.string.manage_user_roles)
                    binding.userRolesContainer.visibility = View.VISIBLE
                }
                "Users" -> {
                    title = getString(R.string.manage_users)
                    binding.createUserContainer.visibility = View.VISIBLE
                    if (isEditMode) {
                        val dataStr = it.getString("UserData")
                        dataStr?.let { dataStr_ ->
                            userResponse = Gson().fromJson(dataStr_, UsersResponse::class.java)
                            updateUiForUsers(userResponse)
                        }
                    } else {
                        loadEmployeesForDropDown()
                    }
                }
                else -> {
                }
            }
        }

        initBackWithTitle(binding.toolbar.toolParent, title)

        binding.createBtn.text =
            if (isEditMode) getString(R.string.btn_update) else getString(R.string.btn_create)

        initUiListeners()
    }

    private fun updateUiForUsers(userResponse: UsersResponse?) {

    }

    private fun updateUiForTasks(tasksResponse: TasksResponse?) {
        tasksResponse?.let {
            taskItemId = it.id

            binding.taskNameEd.setText(it.taskName)
            binding.taskDescEd.setText(it.taskDesc)

            subProjectId = it.subProjectId

            loadSubProjectsForDropDown()
        }
    }

    private fun initUiListeners() {
        when (purpose) {
            "Tasks" -> {
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

                binding.taskSubProjectEd.addTextChangedListener(object : CustomTextWatcher() {
                    override fun onTextChanged(text: String) {
                        if (text.isNotEmpty()) binding.taskSubProjectLayout.error = ""
                    }
                })
            }
            "UserRoles" -> {
                binding.roleNameEd.addTextChangedListener(object : CustomTextWatcher() {
                    override fun onTextChanged(text: String) {
                        if (text.isNotEmpty()) binding.roleNameLayout.error = ""
                    }
                })
            }
            "Users" -> {
                binding.projectEmpEd.addTextChangedListener(object : CustomTextWatcher() {
                    override fun onTextChanged(text: String) {
                        if (text.isNotEmpty()) binding.projectEmpLayout.error = ""
                    }
                })

                binding.projectEd.addTextChangedListener(object : CustomTextWatcher() {
                    override fun onTextChanged(text: String) {
                        if (text.isNotEmpty()) binding.projectLayout.error = ""
                    }
                })
            }
        }
    }

    private fun loadSubProjectsForDropDown() {
        showLoading()
        viewModel.getSubProjectsForDropDown(getToken())
            .observe(this, androidx.lifecycle.Observer {
                cancelLoading()
                it.let { res ->
                    if (res.isNullOrEmpty().not()) {
                        setSubProjectsAdapter(res)
                    }
                }
            })
    }

    private fun loadEmployeesForDropDown() {
        showLoading()
        viewModel.getEmployeesForDropDown(getToken())
            .observe(this, androidx.lifecycle.Observer {
                cancelLoading()
                it.let { res ->
                    if (res.isNullOrEmpty().not()) {
                        setEmployeesAdapter(res)
                    }
                }
            })
    }

    private fun setEmployeesAdapter(res: ArrayList<ProjectManagerResponse>) {
        val adapter = ProjectManagerDropDownAdapter(this, R.layout.item_status_spinner_view, res)
        binding.empNameEd.setAdapter(adapter)
        binding.empNameEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                employeeItemId = res[position].id
                employeeDropDownResponse = res[position]
                binding.empNameEd.setText(res[position].fullName, false)
                adapter.filter.filter(null)
            }

        if (isEditMode) {
            for (item in res) {
                if (item.id == subProjectId) {
                    employeeDropDownResponse = item
                    binding.taskSubProjectEd.setText(item.fullName)
                }
            }
        }
    }

    private fun setSubProjectsAdapter(res: ArrayList<SubProjectsResponse>) {
        val adapter = SubProjectsDropDownAdapter(this, R.layout.item_status_spinner_view, res)
        binding.taskSubProjectEd.setAdapter(adapter)
        binding.taskSubProjectEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                subProjectId = res[position].id
                subProjectsResponse = res[position]
                binding.taskSubProjectEd.setText(res[position].subProjectName, false)
                adapter.filter.filter(null)
            }

        if (isEditMode) {
            for (item in res) {
                if (item.id == subProjectId) {
                    subProjectsResponse = item
                    binding.taskSubProjectEd.setText(item.subProjectName)
                }
            }
        }
    }

    override fun onCreateItem() {
        if (isNetworkConnected) {
            when (purpose) {
                "Tasks" -> {
                    if (isTaskDataAvailable()) {
                        if (!isEditMode) createTask() else updateTask()
                    }
                }
                "UserRoles" -> {
                    if (isUserRolesDataAvailable()) {
                        if (!isEditMode) createUserRole() else updateUserRole()
                    }
                }
                "Users" -> {
                    if (isUsersDataAvailable()) {
                        if (!isEditMode) createUser() else updateUser()
                    }
                }
            }
        } else showSnack(getString(R.string.check_internet))
    }

    private fun isTaskDataAvailable(): Boolean {
        AppHelper.hideKeyboard(this)

        if (binding.taskNameEd.text.toString().trim().isEmpty()) {
            binding.taskNameLayout.error = getString(R.string.err_enter_name)
            return false
        }

        if (binding.taskDescEd.text.toString().trim().isEmpty()) {
            binding.taskDescLayout.error = getString(R.string.err_enter_desc)
            return false
        }

        if (binding.taskSubProjectEd.text.toString().trim().isEmpty()) {
            binding.taskSubProjectLayout.error = getString(R.string.err_choose_sub_project)
            return false
        }

        return true
    }

    private fun isUserRolesDataAvailable(): Boolean {
        AppHelper.hideKeyboard(this)

        if (binding.roleNameEd.text.toString().trim().isEmpty()) {
            binding.roleNameLayout.error = getString(R.string.err_enter_role_name)
            return false
        }

        return true
    }

    private fun isAssignProjectDataAvailable(): Boolean {
        AppHelper.hideKeyboard(this)

        if (binding.projectEmpEd.text.toString().trim().isEmpty()) {
            binding.projectEmpLayout.error = getString(R.string.err_choose_employee)
            return false
        }

        if (binding.projectEd.text.toString().trim().isEmpty()) {
            binding.projectEmpLayout.error = getString(R.string.err_choose_project)
            return false
        }

        return true
    }

    private fun isUsersDataAvailable(): Boolean {
        AppHelper.hideKeyboard(this)

        if (binding.empNameEd.text.toString().trim().isEmpty()) {
            binding.empNameEd.error = getString(R.string.err_choose_employee)
            return false
        }

        if (binding.empEmailEd.text.toString().trim().isEmpty()) {
            binding.empEmailEd.error = getString(R.string.err_enter_emp_email)
            return false
        }

        if (binding.empUserNameEd.text.toString().trim().isEmpty()) {
            binding.empUserNameEd.error = getString(R.string.err_enter_user_name)
            return false
        }

        if (binding.empPasswordEd.text.toString().trim().isEmpty()) {
            binding.empPasswordEd.error = getString(R.string.err_enter_password)
            return false
        }

        return true
    }

    private fun createTask() {
        showLoading()
        viewModel.createTask(getToken(), getTaskJson())
            .observe(this, androidx.lifecycle.Observer {
                cancelLoading()
                when {
                    it.code() == 201 -> {
                        showShortToast(getString(R.string.task_created_success))
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

    private fun updateTask() {
        showLoading()
        viewModel.updateTask(getToken(), taskItemId, getTaskJson())
            .observe(this, androidx.lifecycle.Observer {
                cancelLoading()
                when {
                    it.code() == 204 -> {
                        showShortToast(getString(R.string.task_updated_success))
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

    private fun getTaskJson(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("subProjectId", subProjectsResponse?.id)
        jsonObject.addProperty("taskName", binding.taskNameEd.text.toString().trim())
        jsonObject.addProperty("taskDesc", binding.taskDescEd.text.toString().trim())

        if (isEditMode) {
            jsonObject.addProperty("id", taskItemId)
        }

        return jsonObject
    }


    private fun createUser() {
        showLoading()
        viewModel.createUser(getToken(), getTaskJson())
            .observe(this, androidx.lifecycle.Observer {
                cancelLoading()
                when {
                    it.code() == 201 -> {
                        showShortToast(getString(R.string.user_created_success))
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

    private fun updateUser() {
        showLoading()
        viewModel.updateUser(getToken(), taskItemId, getTaskJson())
            .observe(this, androidx.lifecycle.Observer {
                cancelLoading()
                when {
                    it.code() == 204 -> {
                        showShortToast(getString(R.string.user_updated_success))
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

    //todo update API
    private fun createUserRole() {
        showLoading()
        viewModel.createUserRole(getToken(), getTaskJson())
            .observe(this, androidx.lifecycle.Observer {
                cancelLoading()
                when {
                    it.code() == 201 -> {
                        showShortToast(getString(R.string.user_role_created_success))
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

    //todo update API
    private fun updateUserRole() {
        showLoading()
        viewModel.updateUserRole(getToken(), taskItemId, getTaskJson())
            .observe(this, androidx.lifecycle.Observer {
                cancelLoading()
                when {
                    it.code() == 204 -> {
                        showShortToast(getString(R.string.user_role_updates_success))
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

    override fun onBack() {
        AppHelper.hideKeyboard(this)
        onBackPressed()
    }
}