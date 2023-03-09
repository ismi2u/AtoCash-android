package com.atocash.activities.admin.jobRoles

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityManageJobRolesBinding
import com.atocash.network.response.JobRolesResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.CustomTextWatcher
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.google.gson.Gson
import com.google.gson.JsonObject

class ManageJobRolesActivity :
    SuperCompatActivity<ActivityManageJobRolesBinding, ManageJobRolesViewModel>(),
    ManageJobRolesNavigator {

    private lateinit var binding: ActivityManageJobRolesBinding
    private lateinit var viewModel: ManageJobRolesViewModel

    private var isEditMode = false
    private var jobRoleId = 0
    private var jobRolesResponse: JobRolesResponse? = null

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
        return R.layout.activity_manage_job_roles
    }

    override fun getViewModel(): ManageJobRolesViewModel {
        viewModel = ViewModelProvider(this).get(ManageJobRolesViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        initBackWithTitle(binding.toolbar.toolParent, getString(R.string.menu_job_roles))

        val bundle = intent.extras
        bundle?.let {
            isEditMode = it.getBoolean(Keys.IntentData.IS_EDIT)
            if (isEditMode) {
                val expenseTypeStr = it.getString("JobRolesResponse")
                expenseTypeStr?.let { expenseTypeStr_ ->
                    jobRolesResponse =
                        Gson().fromJson(expenseTypeStr_, JobRolesResponse::class.java)
                    updateUiForEditMode(jobRolesResponse)
                }
            }
        }

        binding.createBtn.text = if (isEditMode) getString(R.string.btn_update) else getString(R.string.btn_create)

        binding.codeEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.codeLayout.error = ""
                }
            }
        })

        binding.descEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.descLayout.error = ""
                }
            }
        })

        binding.pettyCashEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.pettyCashLayout.error = ""
                }
            }
        })
    }

    private fun updateUiForEditMode(jobRolesResponse: JobRolesResponse?) {
        jobRolesResponse?.let {
            jobRoleId = it.id

            binding.codeEd.setText(it.roleCode)
            binding.descEd.setText(it.roleName)
            binding.pettyCashEd.setText(it.maxPettyCashAllowed)
        }
    }

    override fun onCreate() {
        AppHelper.hideKeyboard(this)
        if (isDataAvailable()) {
            if (isEditMode) {
                showLoading(getString(R.string.updating_job_roles))
                viewModel.updateJobRoles(getToken(), jobRoleId, getJobRolesRequest())
                    .observe(this, Observer {
                        cancelLoading()
                        printLog(it.code().toString())
                        when {
                            it.code() == 200 -> {
                                showShortToast(getString(R.string.info_job_roles_updated_success))
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
                showLoading(getString(R.string.creating_job_roles))
                viewModel.createJobRoles(getToken(), getJobRolesRequest())
                    .observe(this, Observer {
                        cancelLoading()
                        printLog(it.code().toString())
                        when {
                            it.code() == 200 -> {
                                showShortToast(getString(R.string.info_job_roles_created_success))
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
    }

    private fun getJobRolesRequest(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("roleCode", binding.codeEd.text.toString().trim())
        jsonObject.addProperty("roleName", binding.descEd.text.toString().trim())
        jsonObject.addProperty("maxPettyCashAllowed", binding.pettyCashEd.text.toString().toInt())
        return jsonObject
    }

    private fun isDataAvailable(): Boolean {
        AppHelper.hideKeyboard(this)

        val code = binding.codeEd.text.toString().trim()
        val desc = binding.descEd.text.toString().trim()
        val maxAmout = binding.pettyCashEd.text.toString().trim()

        if (code.isEmpty()) {
            binding.codeLayout.error = getString(R.string.err_enter_code)
            return false
        }

        if (desc.isEmpty()) {
            binding.descLayout.error = getString(R.string.err_enter_name)
            return false
        }

        if (maxAmout.isEmpty()) {
            binding.pettyCashLayout.error = getString(R.string.err_enter_amount)
            return false
        }

        return true
    }

    override fun onBack() {
        AppHelper.hideKeyboard(this)
        onBackPressed()
    }
}