package com.atocash.activities.admin.expenseTypes

import android.os.Bundle
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.adapter.DropDownAdapter
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityManageExpenseBinding
import com.atocash.network.response.ExpenseTypeResponse
import com.atocash.network.response.StatusDropDownResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.CustomTextWatcher
import com.atocash.utils.Keys
import com.atocash.utils.extensions.showShortToast
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.util.*

class ManageExpenseTypeActivity :
    SuperCompatActivity<ActivityManageExpenseBinding, ManageExpenseTypeViewModel>(),
    ManageExpenseTypeNavigator {

    private lateinit var binding: ActivityManageExpenseBinding
    private lateinit var typeViewModel: ManageExpenseTypeViewModel

    private var isEditMode = false
    private var expenseId = 0
    private var expenseTypeResponse: ExpenseTypeResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewDataBinding()
        typeViewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndCLicks()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_manage_expense
    }

    override fun getViewModel(): ManageExpenseTypeViewModel {
        typeViewModel = ViewModelProvider(this).get(ManageExpenseTypeViewModel::class.java)
        return typeViewModel
    }

    override fun onBack() {
        AppHelper.hideKeyboard(this)
        onBackPressed()
    }

    override fun updateUi(msg: String, isDone: Boolean) {
        cancelLoading()
        showShortToast(msg)
        if (isDone) onBackPressed()
    }

    private fun initViewsAndCLicks() {
        initBackWithTitle(binding.toolbar.toolParent, getString(R.string.title_expense))

        val bundle = intent.extras
        bundle?.let {
            isEditMode = it.getBoolean(Keys.IntentData.IS_EDIT)
            if (isEditMode) {
                val expenseTypeStr = it.getString("ExpenseItem")
                expenseTypeStr?.let { expenseTypeStr_ ->
                    expenseTypeResponse =
                        Gson().fromJson(expenseTypeStr_, ExpenseTypeResponse::class.java)
                    updateUiForEditMode(expenseTypeResponse)
                }
            }
        }

        binding.createBtn.text = if(isEditMode) getString(R.string.btn_create) else getString(R.string.btn_update)

        binding.nameEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) binding.nameLayout.error = ""
            }
        })

        binding.descEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) binding.descLayout.error = ""
            }
        })

        binding.statusEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) binding.statusLayout.error = ""
            }
        })

        loadStatus()
    }

    private fun updateUiForEditMode(expenseTypeResponse: ExpenseTypeResponse?) {
        expenseTypeResponse?.let {
            expenseId = it.id

            binding.nameEd.setText(it.expenseTypeName)
            binding.descEd.setText(it.expenseTypeDesc)

            statusItem = StatusDropDownResponse()
            statusItem.id = it.statusTypeId
            statusItem.status = if (it.statusTypeId == 1) "Active" else "Inactive"

            binding.statusEd.setText(statusItem.status, false)
        }
    }

    private fun loadStatus() {
        showLoading()
        typeViewModel.getStatusForDropDown(getToken()).observe(this, Observer {
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

    private fun isAllDataAvailable(): Boolean {
        AppHelper.hideKeyboard(this)

        if (binding.nameEd.text.toString().trim().isEmpty()) {
            binding.nameLayout.error = getString(R.string.err_enter_name)
            return false
        }
        if (binding.descEd.text.toString().trim().isEmpty()) {
            binding.descLayout.error = getString(R.string.err_enter_name)
            return false
        }
        if (binding.statusEd.text.toString().trim().isEmpty() || statusItem.status.isEmpty()) {
            binding.statusLayout.error = getString(R.string.err_choose_status)
            return false
        }
        return true
    }

    override fun onCreateExpense() {
        if (isAllDataAvailable()) {
            if (isNetworkConnected) {
                showLoading()
                if (isEditMode) {
                    typeViewModel.updateExpenseType(getToken(), expenseId, getProjectJson())
                        .observe(this, androidx.lifecycle.Observer {
                            cancelLoading()
                        })
                } else {
                    typeViewModel.createExpenseType(getToken(), getProjectJson())
                        .observe(this, androidx.lifecycle.Observer {
                            cancelLoading()
                        })
                }
            } else {
                showSnack(getString(R.string.check_internet))
            }
        }
    }

    private fun getProjectJson(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("expenseTypeName", binding.nameEd.text.toString().trim())
        jsonObject.addProperty("expenseTypeDesc", binding.descEd.text.toString().trim())

        jsonObject.addProperty("statusTypeId", statusItem.id)

        if (isEditMode) {
            jsonObject.addProperty("id", expenseId)
        }

        return jsonObject
    }

}