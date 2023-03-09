package com.atocash.activities.admin.costCenter

import android.os.Bundle
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.adapter.DropDownAdapter
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityManageCostCenterBinding
import com.atocash.network.response.CostCenterResponse
import com.atocash.network.response.StatusDropDownResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.CustomTextWatcher
import com.atocash.utils.Keys
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.google.gson.Gson
import com.google.gson.JsonObject

class ManageCostCenterActivity
    : SuperCompatActivity<ActivityManageCostCenterBinding, ManageCostCenterViewModel>(),
    ManageCostCenterNavigator {

    private lateinit var binding: ActivityManageCostCenterBinding
    private lateinit var viewModel: ManageCostCenterViewModel

    private var isEditMode = false
    private var costCenterId = 0
    private var costCenterResponse: CostCenterResponse? = null

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
        return R.layout.activity_manage_cost_center
    }

    override fun getViewModel(): ManageCostCenterViewModel {
        viewModel = ViewModelProvider(this).get(ManageCostCenterViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        initBackWithTitle(binding.toolbar.toolParent, getString(R.string.manage_cost_center))

        val bundle = intent.extras
        bundle?.let {
            isEditMode = it.getBoolean(Keys.IntentData.IS_EDIT)
            if (isEditMode) {
                val expenseTypeStr = it.getString("CostCenterItem")
                expenseTypeStr?.let { expenseTypeStr_ ->
                    costCenterResponse =
                        Gson().fromJson(expenseTypeStr_, CostCenterResponse::class.java)
                    updateUiForEditMode(costCenterResponse)
                }
            }
        }

        binding.createBtn.text =
            if (isEditMode) getString(R.string.btn_update) else getString(R.string.btn_create)

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

        if (!isEditMode) loadStatus()
    }

    private fun updateUiForEditMode(costCenterResponse: CostCenterResponse?) {
        costCenterResponse?.let {
            costCenterId = it.id

            binding.codeEd.setText(it.costCenterCode)
            binding.descEd.setText(it.costCenterDesc)

            statusItem = StatusDropDownResponse()
            statusItem.id = it.statusTypeId
            statusItem.status = if (it.statusTypeId == 1) "Active" else "Inactive"

            binding.statusEd.setText(statusItem.status, false)

            loadStatus()
        }
    }

    private fun loadStatus() {
        showLoading()
        viewModel.getStatusForDropDown(getToken()).observe(this, Observer {
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

    override fun onCreateCostCenter() {
        AppHelper.hideKeyboard(this)
        if (isDataAvailable()) {
            if (isEditMode) {
                showLoading(getString(R.string.updating_cost_center))
                viewModel.updateCostCenter(getToken(), costCenterId, getCostCenterRequest())
                    .observe(this, Observer {
                        cancelLoading()
                        when {
                            it.code() == 201 -> {
                                showShortToast("Cost Center Updated successfully!")
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
                showLoading(getString(R.string.creating_cost_center))
                viewModel.createCostCenter(getToken(), getCostCenterRequest())
                    .observe(this, Observer {
                        cancelLoading()
                        when {
                            it.code() == 200 -> {
                                showShortToast("Cost center Updated successfully!")
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
        }
    }

    private fun getCostCenterRequest(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("costCenterCode", binding.codeEd.text.toString().trim())
        jsonObject.addProperty("costCenterDesc", binding.descEd.text.toString().trim())
        jsonObject.addProperty("statusTypeId", statusItem.id)
        if (isEditMode) {
            jsonObject.addProperty("id", costCenterId)
        }
        return jsonObject
    }

    private fun isDataAvailable(): Boolean {
        AppHelper.hideKeyboard(this)

        val code = binding.codeEd.text.toString().trim()
        val desc = binding.descEd.text.toString().trim()

        if (code.isEmpty()) {
            binding.codeLayout.error = getString(R.string.err_enter_cost_center_code)
            return false
        }

        if (desc.isEmpty()) {
            binding.descLayout.error = getString(R.string.err_enter_cost_center_desc)
            return false
        }

        return true
    }

    override fun onBack() {
        AppHelper.hideKeyboard(this)
        onBackPressed()
    }

    override fun showMsg(msg: String) {
        cancelLoading()
        showShortToast(msg)
        finish()
    }

    override fun updateUi(msg: String, isDone: Boolean) {
        cancelLoading()
        showShortToast(msg)
        finish()
    }
}