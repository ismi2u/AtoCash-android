package com.atocash.activities.admin.currency

import android.os.Bundle
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.adapter.CommonStringDropDownAdapter
import com.atocash.adapter.DropDownAdapter
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityManageCurrencyBinding
import com.atocash.network.response.CurrencyResponse
import com.atocash.network.response.StatusDropDownResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.CurrencyReader
import com.atocash.utils.CustomTextWatcher
import com.atocash.utils.Keys
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.google.gson.Gson
import com.google.gson.JsonObject

class ManageCurrencyActivity
    : SuperCompatActivity<ActivityManageCurrencyBinding, ManageCurrencyViewModel>(),
    ManageCurrencyNavigator {

    private lateinit var binding: ActivityManageCurrencyBinding
    private lateinit var viewModel: ManageCurrencyViewModel

    private var isEditMode = false
    private var currencyId = 0
    private var response: CurrencyResponse? = null

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
        return R.layout.activity_manage_currency
    }

    override fun getViewModel(): ManageCurrencyViewModel {
        viewModel = ViewModelProvider(this).get(ManageCurrencyViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        initBackWithTitle(binding.toolbar.toolParent, getString(R.string.manage_currency))

        val bundle = intent.extras
        bundle?.let {
            isEditMode = it.getBoolean(Keys.IntentData.IS_EDIT)
            if (isEditMode) {
                val expenseTypeStr = it.getString("CurrencyItem")
                expenseTypeStr?.let { expenseTypeStr_ ->
                    response =
                        Gson().fromJson(expenseTypeStr_, CurrencyResponse::class.java)
                    updateUiForEditMode(response)
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

        binding.countryEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.countryLayout.error = ""
                }
            }
        })

        binding.statusEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.statusLayout.error = ""
                }
            }
        })

        binding.nameEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.nameLayout.error = ""
                }
            }
        })

        binding.countryEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.countryLayout.error = ""
                }
            }
        })

        binding.statusEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.statusLayout.error = ""
                }
            }
        })

        if (!isEditMode) loadStatus()
    }

    private fun updateUiForEditMode(response: CurrencyResponse?) {
        response?.let {
            currencyId = it.id

            binding.codeEd.setText(it.currencyCode)
            binding.nameEd.setText(it.currencyName)
            binding.countryEd.setText(it.country)

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

        loadCountryList()
    }

    private fun loadCountryList() {
        val list = CurrencyReader.readCurrencyJson(resources.openRawResource(R.raw.countries))
        val adapter = CommonStringDropDownAdapter(this, R.layout.item_status_spinner_view, list)
        binding.countryEd.setAdapter(adapter)
        binding.countryEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                binding.countryEd.setText(list[position], false)
                adapter.filter.filter(null)
            }
    }

    override fun onCreateCostCenter() {
        AppHelper.hideKeyboard(this)
        if (isDataAvailable()) {
            if (isEditMode) {
                showLoading(getString(R.string.updating_currency))
                viewModel.updateCurrency(getToken(), currencyId, getRequest())
                    .observe(this, Observer {
                        cancelLoading()
                        when {
                            it.code() == 201 -> {
                                showShortToast("Currency Updated successfully!")
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
                showLoading(getString(R.string.creating_currency))
                viewModel.createCurrency(getToken(), getRequest())
                    .observe(this, Observer {
                        cancelLoading()
                        when {
                            it.code() == 201 -> {
                                showShortToast("Currency created successfully!")
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

    private fun getRequest(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("currencyCode", binding.codeEd.text.toString().trim())
        jsonObject.addProperty("currencyName", binding.nameEd.text.toString().trim())
        jsonObject.addProperty("country", binding.countryEd.text.toString().trim())
        jsonObject.addProperty("statusTypeId", statusItem.id)
        if (isEditMode) {
            jsonObject.addProperty("id", currencyId)
        }
        return jsonObject
    }

    private fun isDataAvailable(): Boolean {
        AppHelper.hideKeyboard(this)

        if (binding.codeEd.text.toString().trim().isEmpty()) {
            binding.codeLayout.error = getString(R.string.err_enter_code)
            return false
        }

        if (binding.nameEd.text.toString().trim().isEmpty()) {
            binding.nameLayout.error = getString(R.string.err_enter_name)
            return false
        }

        if (binding.countryEd.text.toString().trim().isEmpty()) {
            binding.countryLayout.error = getString(R.string.err_choose_country)
            return false
        }

        if (binding.statusEd.text.toString().trim().isEmpty()) {
            binding.statusLayout.error = getString(R.string.err_choose_status)
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