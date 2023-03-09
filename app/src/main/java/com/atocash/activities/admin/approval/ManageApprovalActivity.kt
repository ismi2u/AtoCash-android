package com.atocash.activities.admin.approval

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.adapter.ApprovalDropDownAdapter
import com.atocash.adapter.ApprovalLevelDropDownAdapter
import com.atocash.adapter.JobRolesDropDownAdapter
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityManageApprovalBinding
import com.atocash.network.response.ApprovalBaseResponse
import com.atocash.network.response.ApprovalGroupDropDownResponse
import com.atocash.network.response.JobRolesDropDownResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.CustomTextWatcher
import com.atocash.utils.Keys
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_manage_approval.*
import java.util.ArrayList

class ManageApprovalActivity :
    SuperCompatActivity<ActivityManageApprovalBinding, ManageApprovalViewModel>(),
    ManageApprovalNavigator {

    private lateinit var binding: ActivityManageApprovalBinding
    private lateinit var viewModel: ManageApprovalViewModel

    private var isEditMode = false
    private var purpose = ""

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
        return R.layout.activity_manage_approval
    }

    override fun getViewModel(): ManageApprovalViewModel {
        viewModel = ViewModelProvider(this).get(ManageApprovalViewModel::class.java)
        return viewModel
    }

    private var approvalBaseResponse: ApprovalBaseResponse? = null

    private fun initViewsAndClicks() {
        var title = ""
        val bundle = intent.extras
        bundle?.let {
            isEditMode = it.getBoolean(Keys.IntentData.IS_EDIT)
            purpose = it.getString(Keys.IntentData.FROM).toString()
            when (purpose) {
                Keys.Approval.GROUP -> {
                    title = getString(R.string.manage_approval_group)
                    binding.groupContainer.visibility = View.VISIBLE
                    if (isEditMode) {
                        val dataStr = it.getString("ApprovalBaseResponse")
                        dataStr?.let {
                            approvalBaseResponse =
                                Gson().fromJson(dataStr, ApprovalBaseResponse::class.java)
                            updateUiForGroup(approvalBaseResponse)
                        }
                    } else {
                    }
                }
                Keys.Approval.LEVEL -> {
                    title = getString(R.string.manage_approval_level)
                    binding.levelContainer.visibility = View.VISIBLE
                    if (isEditMode) {
                        val dataStr = it.getString("ApprovalBaseResponse")
                        dataStr?.let {
                            approvalBaseResponse =
                                Gson().fromJson(dataStr, ApprovalBaseResponse::class.java)
                            updateUiForLevel(approvalBaseResponse)
                        }
                    } else {
                    }
                }
                Keys.Approval.ROLE_MAP -> {
                    title = getString(R.string.manage_approval_role_map)
                    binding.roleMapContainer.visibility = View.VISIBLE
                    if (isEditMode) {
                        val dataStr = it.getString("ApprovalBaseResponse")
                        dataStr?.let {
                            approvalBaseResponse =
                                Gson().fromJson(dataStr, ApprovalBaseResponse::class.java)
                            updateUiForRoleMap(approvalBaseResponse)
                        }
                    } else {
                        loadDropDowns()
                    }
                }
                Keys.Approval.STATUS -> {
                    title = getString(R.string.manage_approval_status)
                    binding.statusContainer.visibility = View.VISIBLE
                    if (isEditMode) {
                        val dataStr = it.getString("ApprovalBaseResponse")
                        dataStr?.let {
                            approvalBaseResponse =
                                Gson().fromJson(dataStr, ApprovalBaseResponse::class.java)
                            updateUiForStatus(approvalBaseResponse)
                        }
                    } else {
                    }
                }
                else -> {
                    finish()
                }
            }
        }

        initBackWithTitle(binding.toolbar.toolParent, title)

        initListenersForUi()

        binding.createBtn.text =
            if (isEditMode) getString(R.string.btn_update) else getString(R.string.btn_create)
    }

    private fun updateUiForLevel(approvalBaseResponse: ApprovalBaseResponse?) {
        approvalBaseResponse?.let {
            levelId = it.id
            binding.levelEd.setText(it.level.toString())
            binding.levelDescEd.setText(it.levelDesc)
        }
    }

    private fun updateUiForStatus(approvalBaseResponse: ApprovalBaseResponse?) {
        approvalBaseResponse?.let {
            statusId = it.id
            binding.statusEd.setText(it.status)
            binding.statusDescEd.setText(it.statusDesc)
        }
    }

    private var roleGroupId = 0
    private var roleApprovalId = 0
    private var roleId = 0

    private fun updateUiForRoleMap(approvalBaseResponse: ApprovalBaseResponse?) {
        approvalBaseResponse?.let {
            roleMapId = it.id

            binding.roleGroupEd.setText(it.approvalGroup)
            roleGroupId = it.approvalGroupId

            binding.roleEd.setText(it.role)
            roleId = it.roleId

            binding.roleLevelEd.setText(it.approvalLevel.toString())
            roleMapApprovalLevelId = it.approvalLevelId

            loadDropDowns()
        }
    }

    private fun loadDropDowns() {
        viewModel.getApprovalGroupsForDropDown(getToken()).observe(this, Observer {
            when {
                it.isSuccessful -> {
                    val list = it.body()
                    list?.let { statusItems ->
                        if (statusItems.isNullOrEmpty().not()) {
                            setApprovalDropDownAdapter(statusItems)
                        }
                    }
                }
                it.code() == 401 -> {
                    showUnAuthDialog()
                }
                else -> {
                    showErrorResponse(it.errorBody()?.string())
                }
            }
        })

        viewModel.getJobRolesForDropDown(getToken()).observe(this, Observer {
            when {
                it.isSuccessful -> {
                    val list = it.body()
                    list?.let { statusItems ->
                        if (statusItems.isNullOrEmpty().not()) {
                            setJobRolesDropDownAdapter(statusItems)
                        }
                    }
                }
                it.code() == 401 -> {
                    showUnAuthDialog()
                }
                else -> {
                    showErrorResponse(it.errorBody()?.string())
                }
            }
        })

        viewModel.getApprovalLevels(getToken()).observe(this, Observer {
            when {
                it.isSuccessful -> {
                    val list = it.body()
                    list?.let { statusItems ->
                        if (statusItems.isNullOrEmpty().not()) {
                            setApprovalLevelAdapter(statusItems)
                        }
                    }
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

    var approvalLevelItem = ApprovalBaseResponse()
    private fun setApprovalLevelAdapter(statusItems: ArrayList<ApprovalBaseResponse>) {
        val adapter = ApprovalLevelDropDownAdapter(this, R.layout.item_status_spinner_view, statusItems)
        binding.roleLevelEd.setAdapter(adapter)
        binding.roleLevelEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                AppHelper.hideKeyboard(this@ManageApprovalActivity)
                approvalLevelItem = statusItems[position]
                roleMapApprovalLevelId = approvalItem.id
                binding.roleLevelEd.setText(statusItems[position].level.toString(), false)
                adapter.filter.filter(null)
            }

        if(isEditMode) {
            for(item in statusItems) {
                if(item.id == roleMapApprovalLevelId) {
                    approvalLevelItem = item
                    binding.roleLevelEd.setText(approvalLevelItem.level.toString())
                }
            }
        }
    }

    var approvalItem = ApprovalGroupDropDownResponse()
    private fun setApprovalDropDownAdapter(statusItems: ArrayList<ApprovalGroupDropDownResponse>) {
        val adapter = ApprovalDropDownAdapter(this, R.layout.item_status_spinner_view, statusItems)
        binding.roleLevelEd.setAdapter(adapter)
        binding.roleLevelEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                AppHelper.hideKeyboard(this@ManageApprovalActivity)
                approvalItem = statusItems[position]
                roleApprovalId = approvalItem.id
                binding.roleLevelEd.setText(statusItems[position].approvalGroupCode, false)
                adapter.filter.filter(null)
            }

        if(isEditMode) {
            for(item in statusItems) {
                if(item.id == roleApprovalId) {
                    approvalItem = item
                    binding.roleLevelEd.setText(approvalItem.approvalGroupCode)
                }
            }
        }
    }

    var jobRoleItem = JobRolesDropDownResponse()
    private fun setJobRolesDropDownAdapter(statusItems: ArrayList<JobRolesDropDownResponse>) {
        val adapter = JobRolesDropDownAdapter(this, R.layout.item_status_spinner_view, statusItems)
        binding.roleEd.setAdapter(adapter)
        binding.roleEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                AppHelper.hideKeyboard(this@ManageApprovalActivity)
                jobRoleItem = statusItems[position]
                roleId = jobRoleItem.id
                binding.roleEd.setText(statusItems[position].roleCode, false)
                adapter.filter.filter(null)
            }

        if(isEditMode) {
            for(item in statusItems) {
                if(item.id == roleId) {
                    jobRoleItem = item
                    binding.roleEd.setText(jobRoleItem.roleCode)
                }
            }
        }
    }

    private fun updateUiForGroup(approvalBaseResponse: ApprovalBaseResponse?) {
        approvalBaseResponse?.let {
            groupId = it.id
            binding.groupEd.setText(it.approvalGroup)
            binding.groupDescEd.setText(it.approvalGroupDesc)
        }
    }

    private fun initListenersForUi() {
        binding.createBtn.setOnClickListener {
            onCreateApproval()
        }

        when (purpose) {
            Keys.Approval.GROUP -> {
                binding.groupEd.addTextChangedListener(object : CustomTextWatcher() {
                    override fun onTextChanged(text: String) {
                        if (text.isNotEmpty()) {
                            binding.groupLayout.error = ""
                        }
                    }
                })
                binding.groupDescEd.addTextChangedListener(object : CustomTextWatcher() {
                    override fun onTextChanged(text: String) {
                        if (text.isNotEmpty()) {
                            binding.groupDescLayout.error = ""
                        }
                    }
                })
            }
            Keys.Approval.LEVEL -> {
                binding.levelEd.addTextChangedListener(object : CustomTextWatcher() {
                    override fun onTextChanged(text: String) {
                        if (text.isNotEmpty()) {
                            binding.levelLayout.error = ""
                        }
                    }
                })
                binding.levelDescEd.addTextChangedListener(object : CustomTextWatcher() {
                    override fun onTextChanged(text: String) {
                        if (text.isNotEmpty()) {
                            binding.levelDescLayout.error = ""
                        }
                    }
                })
            }
            Keys.Approval.ROLE_MAP -> {
                binding.roleGroupEd.addTextChangedListener(object : CustomTextWatcher() {
                    override fun onTextChanged(text: String) {
                        if (text.isNotEmpty()) {
                            binding.roleGroupLayout.error = ""
                        }
                    }
                })
                binding.roleEd.addTextChangedListener(object : CustomTextWatcher() {
                    override fun onTextChanged(text: String) {
                        if (text.isNotEmpty()) {
                            binding.roleLayout.error = ""
                        }
                    }
                })
                binding.roleLevelEd.addTextChangedListener(object : CustomTextWatcher() {
                    override fun onTextChanged(text: String) {
                        if (text.isNotEmpty()) {
                            binding.roleLevelLayout.error = ""
                        }
                    }
                })
            }
            Keys.Approval.STATUS -> {
                binding.statusEd.addTextChangedListener(object : CustomTextWatcher() {
                    override fun onTextChanged(text: String) {
                        if (text.isNotEmpty()) {
                            binding.statusDescLayout.error = ""
                        }
                    }
                })
                binding.statusDescEd.addTextChangedListener(object : CustomTextWatcher() {
                    override fun onTextChanged(text: String) {
                        if (text.isNotEmpty()) {
                            binding.statusDescLayout.error = ""
                        }
                    }
                })
            }
        }
    }

    private fun isGroupDataAvail(): Boolean {
        if (binding.groupEd.text.toString().trim().isEmpty()) {
            binding.groupLayout.error = getString(R.string.err_enter_group)
            return false
        }
        if (binding.groupDescEd.text.toString().trim().isEmpty()) {
            binding.groupDescLayout.error = getString(R.string.err_enter_group_desc)
            return false
        }
        return true
    }

    private var groupId = 0
    private fun getGroupJson(): JsonObject {
        val json = JsonObject()
        json.addProperty("approvalGroupCode", binding.groupEd.text.toString().trim())
        json.addProperty("approvalGroupDesc", binding.groupDescEd.text.toString().trim())
        if (isEditMode) {
            json.addProperty("id", groupId)
        }
        return json
    }

    private fun isLevelDataAvail(): Boolean {
        if (binding.levelEd.text.toString().trim().isEmpty()) {
            binding.levelLayout.error = getString(R.string.err_enter_level)
            return false
        }
        if (binding.levelDescEd.text.toString().trim().isEmpty()) {
            binding.levelDescLayout.error = getString(R.string.err_enter_level_desc)
            return false
        }
        return true
    }

    private var levelId = 0
    private fun getLevelJson(): JsonObject {
        val json = JsonObject()
        json.addProperty("level", binding.levelEd.text.toString().trim().toInt())
        json.addProperty("levelDesc", binding.levelDescEd.text.toString().trim())
        if (isEditMode) {
            json.addProperty("id", levelId)
        }
        return json
    }

    private fun isRoleMapDataAvail(): Boolean {
        if (binding.roleGroupEd.text.toString().trim().isEmpty()) {
            binding.roleGroupLayout.error = getString(R.string.err_choose_group)
            return false
        }
        if (binding.roleEd.text.toString().trim().isEmpty()) {
            binding.roleLayout.error = getString(R.string.err_choose_role)
            return false
        }
        if (binding.roleLevelEd.text.toString().trim().isEmpty()) {
            binding.roleLevelLayout.error = getString(R.string.err_choose_level)
            return false
        }
        return true
    }

    private var roleMapId = 0
    private var roleMapApprovalLevelId = 0

    private fun getRoleMapJson(): JsonObject {
        val json = JsonObject()
        json.addProperty("approvalGroup", binding.roleGroupEd.text.toString().trim())
        json.addProperty("role", binding.roleEd.text.toString().trim())
        json.addProperty("approvalLevelId", roleMapApprovalLevelId)
        if (isEditMode) {
            json.addProperty("id", roleMapId)
        }
        return json
    }


    private fun isStatusDataAvail(): Boolean {
        if (binding.statusEd.text.toString().trim().isEmpty()) {
            binding.statusLayout.error = getString(R.string.err_enter_status)
            return false
        }
        if (binding.statusDescEd.text.toString().trim().isEmpty()) {
            binding.statusDescLayout.error = getString(R.string.err_enter_status_desc)
            return false
        }
        return true
    }

    private var statusId = 0
    private fun getStatusJson(): JsonObject {
        val json = JsonObject()
        json.addProperty("status", binding.statusEd.text.toString().trim().toInt())
        json.addProperty("statusDesc", binding.statusDescEd.text.toString().trim())
        if (isEditMode) {
            json.addProperty("id", statusId)
        }
        return json
    }

    private fun onCreateApproval() {
        if (isNetworkConnected) {
            when (purpose) {
                Keys.Approval.GROUP -> {
                    if (isGroupDataAvail()) {
                        if (isEditMode) {
                            updateGroup()
                        } else {
                            createGroup()
                        }
                    }
                }
                Keys.Approval.LEVEL -> {
                    if (isLevelDataAvail()) {
                        if (isEditMode) {
                            updateLevel()
                        } else {
                            createLevel()
                        }
                    }
                }
                Keys.Approval.ROLE_MAP -> {
                    if (isRoleMapDataAvail()) {
                        if (isEditMode) {
                            updateRoleMap()
                        } else {
                            createRoleMap()
                        }
                    }
                }
                Keys.Approval.STATUS -> {
                    if (isStatusDataAvail()) {
                        if (isEditMode) {
                            updateStatus()
                        } else {
                            createStatus()
                        }
                    }
                }
            }
        } else {
            showSnack(getString(R.string.check_internet))
        }
    }

    private fun updateGroup() {
        showLoading(getString(R.string.updating_group))
        viewModel.updateGroup(getToken(), groupId, getGroupJson())
            .observe(this, Observer {
                cancelLoading()
                when {
                    it.code() == 201 || it.code() == 200 -> {
                        showShortToast(getString(R.string.group_updated_success))
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

    private fun createGroup() {
        showLoading(getString(R.string.creating_group))
        viewModel.createGroup(getToken(), getGroupJson())
            .observe(this, Observer {
                cancelLoading()
                when {
                    it.code() == 201 -> {
                        showShortToast(getString(R.string.group_created_success))
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

    private fun updateLevel() {
        showLoading(getString(R.string.updating_level))
        viewModel.updateLevel(getToken(), levelId, getLevelJson())
            .observe(this, Observer {
                cancelLoading()
                when {
                    it.code() == 201 || it.code() == 200 -> {
                        showShortToast(getString(R.string.level_updated_success))
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

    private fun createLevel() {
        showLoading(getString(R.string.creating_level))
        viewModel.createLevel(getToken(), getLevelJson())
            .observe(this, Observer {
                cancelLoading()
                when {
                    it.code() == 201 -> {
                        showShortToast(getString(R.string.level_created_success))
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

    private fun updateRoleMap() {
        showLoading(getString(R.string.updating_roles))
        viewModel.updateRoleMap(getToken(), roleMapId, getRoleMapJson())
            .observe(this, Observer {
                cancelLoading()
                when {
                    it.code() == 201 || it.code() == 200 -> {
                        showShortToast(getString(R.string.role_map_updated_success))
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

    private fun createRoleMap() {
        showLoading(getString(R.string.creating_roles))
        viewModel.createRoleMap(getToken(), getRoleMapJson())
            .observe(this, Observer {
                cancelLoading()
                when {
                    it.code() == 201 -> {
                        showShortToast(getString(R.string.role_map_created_success))
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

    private fun updateStatus() {
        showLoading(getString(R.string.updating_status))
        viewModel.updateStatus(getToken(), statusId, getStatusJson())
            .observe(this, Observer {
                cancelLoading()
                when {
                    it.code() == 201 || it.code() == 200 -> {
                        showShortToast(getString(R.string.status_updated_success))
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

    private fun createStatus() {
        showLoading(getString(R.string.creating_status))
        viewModel.createStatus(getToken(), getStatusJson())
            .observe(this, Observer {
                cancelLoading()
                when {
                    it.code() == 201 || it.code() == 200 -> {
                        showShortToast(getString(R.string.status_created_success))
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