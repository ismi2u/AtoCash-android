package com.atocash.activities.admin.department

import android.os.Bundle
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.adapter.CostCenterDropDownAdapter
import com.atocash.adapter.DropDownAdapter
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityDepartmentBinding
import com.atocash.network.response.CostCenterDropDownResponse
import com.atocash.network.response.DepartmentResponse
import com.atocash.network.response.StatusDropDownResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.CustomTextWatcher
import com.atocash.utils.Keys
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.google.gson.Gson
import com.google.gson.JsonObject

class AdminDepartmentActivity
    : SuperCompatActivity<ActivityDepartmentBinding, AdminDepartmentViewModel>(),
    AdminDepartmentNavigator {

    private lateinit var binding: ActivityDepartmentBinding
    private lateinit var viewModelAdmin: AdminDepartmentViewModel

    private var isEditMode = false
    private var departmentId = 0
    private var departmentResponse: DepartmentResponse? = null
    var costCenterItem = CostCenterDropDownResponse()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewDataBinding()
        viewModelAdmin.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_department
    }

    override fun getViewModel(): AdminDepartmentViewModel {
        viewModelAdmin = ViewModelProvider(this).get(AdminDepartmentViewModel::class.java)
        return viewModelAdmin
    }

    private fun initViewsAndClicks() {
        initBackWithTitle(binding.toolbar.toolParent, getString(R.string.manage_department))

        val bundle = intent.extras
        bundle?.let {
            isEditMode = it.getBoolean(Keys.IntentData.IS_EDIT)
            if (isEditMode) {
                val expenseTypeStr = it.getString("DepartmentItem")
                expenseTypeStr?.let { expenseTypeStr_ ->
                    departmentResponse =
                        Gson().fromJson(expenseTypeStr_, DepartmentResponse::class.java)
                    updateUiForEditMode(departmentResponse)
                }
            }
        }

        binding.createBtn.text =
            if (isEditMode) getString(R.string.btn_update) else getString(R.string.btn_create)

        binding.codeEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) binding.codeLayout.error = ""
            }
        })

        binding.descEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) binding.descLayout.error = ""
            }
        })

        binding.costCenterEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) binding.costCenterLayout.error = ""
            }
        })

        if (!isEditMode) loadCostCentersForDropDown()
    }

    private fun updateUiForEditMode(costCenterResponse: DepartmentResponse?) {
        costCenterResponse?.let {
            departmentId = it.id

            binding.codeEd.setText(it.deptCode)
            binding.descEd.setText(it.deptName)

            statusItem = StatusDropDownResponse()
            statusItem.id = it.statusTypeId
            statusItem.status = if (it.statusTypeId == 1) "Active" else "Inactive"

            costCenterItem = CostCenterDropDownResponse(
                id = it.costCenterId,
                costCenterCode = it.costCenter.toString()
            )

            binding.statusEd.setText(statusItem.status, false)
            binding.costCenterEd.setText(costCenterItem.costCenterCode, false)

            loadCostCentersForDropDown()
        }
    }

    override fun onCreate() {
        AppHelper.hideKeyboard(this, binding.statusEd)
        if (isAllDataAvailable()) {
            if (isNetworkConnected) {
                showLoading()
                if (!isEditMode) {
                    viewModelAdmin.createDepartment(getToken(), getDepartmentReq())
                        .observe(this, Observer {
                            cancelLoading()
                            when {
                                it.code() == 200 -> {
                                    showShortToast(getString(R.string.department_created_success))
                                    finish()
                                }
                                it.code() == 401 -> {
                                    finish()
                                    showUnAuthDialog()
                                }
                                else -> {
                                    finish()
                                    showErrorResponse(it.errorBody()?.string())
                                }
                            }
                        })
                } else {
                    viewModelAdmin.updateDepartment(getToken(), departmentId, getDepartmentReq())
                        .observe(this, Observer {
                            cancelLoading()
                            when {
                                it.code() == 201 -> {
                                    showShortToast(getString(R.string.department_updated_success))
                                    finish()
                                }
                                it.code() == 401 -> {
                                    finish()
                                    showUnAuthDialog()
                                }
                                else -> {
                                    finish()
                                    showErrorResponse(it.errorBody()?.string())
                                }
                            }
                        })
                }
            } else {
                showSnack(getString(R.string.check_internet))
            }
        }
    }

    private fun getDepartmentReq(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("deptCode", binding.codeEd.text.toString().trim())
        jsonObject.addProperty("deptName", binding.descEd.text.toString().trim())
        jsonObject.addProperty("costCenterId", costCenterItem.id)
        jsonObject.addProperty("statusTypeId", statusItem.id)
        if (isEditMode) {
            jsonObject.addProperty("id", departmentId)
        }
        return jsonObject
    }


    private fun isAllDataAvailable(): Boolean {
        if (binding.codeEd.text.toString().trim().isEmpty()) {
            binding.codeLayout.error = getString(R.string.err_department_code)
            return false
        }

        if (binding.descEd.text.toString().trim().isEmpty()) {
            binding.descLayout.error = getString(R.string.err_enter_department_name)
            return false
        }

        if (costCenterItem.costCenterCode.isEmpty()) {
            binding.costCenterLayout.error = getString(R.string.err_choose_cost_center)
            return false
        }
        return true
    }

    override fun onBack() {
        AppHelper.hideKeyboard(this, binding.statusEd)
        onBackPressed()
    }

    override fun showMsg(msg: String, isDone: Boolean) {
        cancelLoading()
        showShortToast(msg)
        if (isDone) finish()
    }

    private fun loadCostCentersForDropDown() {
        showLoading()
        viewModelAdmin.getCostCenterForDropDown(getToken()).observe(this, Observer {
            cancelLoading()
            if (it.isNullOrEmpty().not()) {
                setCostCenterAdapter(it)
            }
        })
        loadStatus()
    }

    private fun loadStatus() {
        showLoading()
        viewModelAdmin.getStatusForDropDown(getToken()).observe(this, Observer {
            cancelLoading()
            if (it.isNullOrEmpty().not()) {
                setDropDownAdapter(it)
            }
        })
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

    private fun setCostCenterAdapter(it: ArrayList<CostCenterDropDownResponse>) {
        val adapter = CostCenterDropDownAdapter(this, R.layout.item_status_spinner_view, it)
        binding.costCenterEd.setAdapter(adapter)
        binding.costCenterEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                costCenterItem = it[position]
                binding.costCenterEd.setText(it[position].costCenterCode, false)
                adapter.filter.filter(null)
            }
    }
}