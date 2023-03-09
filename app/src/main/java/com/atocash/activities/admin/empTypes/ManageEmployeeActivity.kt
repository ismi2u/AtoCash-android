package com.atocash.activities.admin.empTypes

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.adapter.*
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityManageEmploymeeBinding
import com.atocash.network.response.*
import com.atocash.utils.*
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.util.*

class ManageEmployeeActivity :
    SuperCompatActivity<ActivityManageEmploymeeBinding, ManageEmployeeViewModel>(),
    ManageEmployeeNavigator {

    private lateinit var binding: ActivityManageEmploymeeBinding
    private lateinit var viewModel: ManageEmployeeViewModel

    private var isEditMode = false
    private var employeeId = 0
    private var employmentId = 0
    private var from = ""

    private var employmentResponse: EmpTypeModel? = null

    private var statusTypeId = 0
    private var currencyTypeId = 0
    private var approvalGroupId = 0
    private var employmentTypeId = 0
    private var departmentId = 0
    private var roleId = 0
    private var employeeResponse: EmployeesResponse? = null

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
        return R.layout.activity_manage_employmee
    }

    override fun getViewModel(): ManageEmployeeViewModel {
        viewModel = ViewModelProvider(this).get(ManageEmployeeViewModel::class.java)
        return viewModel
    }

    /*
    * NationalId, passportNo, TaxNo, Email, Mobile, Nationality, DateJoined, DOB, Gender,
    * ApprovalGroup, Role, EmploymentType, Department, Status
    * */
    private fun initViewsAndClicks() {
        val bundle = intent.extras
        bundle?.let {
            isEditMode = it.getBoolean(Keys.IntentData.IS_EDIT)
            from = it.getString(Keys.IntentData.FROM).toString()
            if (from == "Employee") {
                initBackWithTitle(binding.toolbar.toolParent, getString(R.string.manage_employee))
                binding.employeeContainer.visibility = View.VISIBLE
                if (isEditMode) {
                    val dataStr = it.getString("employeeItem")
                    dataStr?.let { dataStr_ ->
                        employeeResponse =
                            Gson().fromJson(dataStr_, EmployeesResponse::class.java)
                        updateUiForEmployee(employeeResponse)
                    }
                }
            } else {
                initBackWithTitle(
                    binding.toolbar.toolParent,
                    getString(R.string.manage_employement_type)
                )
                binding.employementContainer.visibility = View.VISIBLE
                if (isEditMode) {
                    val dataStr = it.getString("EmploymentData")
                    dataStr?.let { dataStr_ ->
                        employmentResponse =
                            Gson().fromJson(dataStr_, EmpTypeModel::class.java)
                        updateUiForEmployement(employmentResponse)
                    }
                }
            }
        }

        if (from == "Employee") {
            initUiListenersForEmployee()
            loadDropDowns()
        } else {
            initUiListenersForEmployment()
        }

        binding.createBtn.text =
            if (isEditMode) getString(R.string.btn_update) else getString(R.string.btn_create)
    }

    private fun updateUiForEmployee(employeeResponse: EmployeesResponse?) {
        employeeResponse?.let {
            binding.firstNameEd.setText(it.firstName)
            binding.lastNameEd.setText(it.lastName)
            binding.middleNameEd.setText(it.middleName)
            binding.empCodeEd.setText(it.empCode)
            binding.bankAccountEd.setText(it.bankAccount)
            binding.bankCardEd.setText(it.bankCardNo)
            binding.nationalIdEd.setText(it.nationalID)
            binding.emailEd.setText(it.email)
            binding.mobileEd.setText(it.mobileNumber)

//            DateUtils.getISODate(it.dob)?.let { dob ->
//                binding.dateBirthEd.setText(dob)
//            }
//
//            DateUtils.getISODate(it.dob)?.let { doj ->
//                binding.dateJoinedEd.setText(doj)
//            }

            binding.genderEd.setText(it.gender)

            binding.taxNoEd.setText(it.taxNumber)
            binding.passportNoEd.setText(it.passportNo)

            statusTypeId = it.statusTypeId
            currencyTypeId = it.currencyTypeId
            approvalGroupId = it.approvalGroupId
            employmentTypeId = it.employmentTypeId
            departmentId = it.departmentId
            roleId = it.roleId
        }
    }

    private fun updateUiForEmployement(employmentResponse: EmpTypeModel?) {
        employmentResponse?.let {
            employmentId = it.id

            binding.empNameEd.setText(it.empJobTypeCode)
            binding.empDescEd.setText(it.empJobTypeDesc)
        }
    }

    private fun initUiListenersForEmployment() {
        binding.empNameEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.empNameLayout.error = ""
                }
            }
        })

        binding.empDescEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.empDescLayout.error = ""
                }
            }
        })
    }

    private fun initUiListenersForEmployee() {
        binding.dateJoinedEd.setOnClickListener {
            AppHelper.hideKeyboard(this@ManageEmployeeActivity)
            showDateJoinedDatePicker()
        }

        binding.dateBirthEd.setOnClickListener {
            AppHelper.hideKeyboard(this@ManageEmployeeActivity)
            showDateOfBirthDatePicker()
        }

        binding.firstNameEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.firstNameLayout.error = ""
                }
            }
        })

        binding.lastNameEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.lastNameLayout.error = ""
                }
            }
        })

        binding.empCodeEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.empCodeLayout.error = ""
                }
            }
        })

        binding.bankAccountEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.bankAccountLayout.error = ""
                }
            }
        })

        binding.bankCardEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.bankCardLayout.error = ""
                }
            }
        })

        binding.nationalIdEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.nationalIdLayout.error = ""
                }
            }
        })

        binding.emailEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.emailLayout.error = ""
                }
            }
        })

        binding.mobileEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.mobileLayout.error = ""
                }
            }
        })

        binding.nationalityEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.nationalityLayout.error = ""
                }
            }
        })

        binding.dateBirthEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.dateBirthLayout.error = ""
                }
            }
        })

        binding.dateJoinedEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.dateJoinedLayout.error = ""
                }
            }
        })

        binding.genderEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.genderLayout.error = ""
                }
            }
        })

        binding.approvalEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.approvalLayout.error = ""
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

        binding.employmentTypeEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.employmentTypeLayout.error = ""
                }
            }
        })

        binding.departmentEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.departmentLayout.error = ""
                }
            }
        })

        binding.currencyEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.currencyLayout.error = ""
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
    }

    var dobMillis = ""
    var dojMillis = ""
    private fun showDateOfBirthDatePicker() {
        val dateListener: DatePickerDialog.OnDateSetListener

        val myCalendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        dateListener = DatePickerDialog.OnDateSetListener { _view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            dobMillis = DateUtils.fromCalendar(myCalendar)
            binding.dateBirthEd.setText(AppHelper.getDate(myCalendar))
        }

        val dialog = DatePickerDialog(
            this, dateListener, myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.maxDate = System.currentTimeMillis() - 1000
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    private fun showDateJoinedDatePicker() {
        val dateListener: DatePickerDialog.OnDateSetListener

        val myCalendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        dateListener = DatePickerDialog.OnDateSetListener { _view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            dojMillis = DateUtils.fromCalendar(myCalendar)
            binding.dateJoinedEd.setText(AppHelper.getDate(myCalendar))
        }

        val dialog = DatePickerDialog(
            this, dateListener, myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.maxDate = System.currentTimeMillis() - 1000
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    override fun onCreateCostCenter() {
        AppHelper.hideKeyboard(this)
        if (isDataAvailable()) {
            if (from == "Employee") {
                proceedToManageEmployee()
            } else {
                proceedToManageEmployment()
            }
        }
    }

    private fun proceedToManageEmployment() {
        if (isEditMode) {
            showLoading(getString(R.string.updating_employee_type))
            viewModel.updateEmployementType(getToken(), employmentId, getEmploymentJson())
                .observe(this, Observer {
                    cancelLoading()
                    when {
                        it.code() == 201 -> {
                            showShortToast(getString(R.string.em_type_updated))
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
            showLoading(getString(R.string.creating_employee_type))
            viewModel.createEmployementType(getToken(), getEmploymentJson())
                .observe(this, Observer {
                    cancelLoading()
                    when {
                        it.code() == 201 -> {
                            showShortToast(getString(R.string.em_type_created))
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

    private fun proceedToManageEmployee() {
        if (isEditMode) {
            showLoading(getString(R.string.updating_employee_type))
            viewModel.updateEmployeeType(getToken(), employmentId, getEmployeeJson())
                .observe(this, Observer {
                    cancelLoading()
                    when {
                        it.code() == 201 -> {
                            showShortToast(getString(R.string.emp_updated_success))
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
            showLoading(getString(R.string.creating_employee_type))
            viewModel.createEmployeeType(getToken(), getEmployeeJson())
                .observe(this, Observer {
                    cancelLoading()
                    when {
                        it.code() == 201 -> {
                            showShortToast(getString(R.string.emp_created_success))
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

    private fun isEmployeeDataAvail(): Boolean {
        //middle name, passport, tax - no need for validation

        if (binding.firstNameEd.text.toString().trim().isEmpty()) {
            binding.firstNameLayout.error = getString(R.string.err_enter_first_name)
            return false
        }

        if (binding.lastNameEd.text.toString().trim().isEmpty()) {
            binding.lastNameLayout.error = getString(R.string.err_enter_last_name)
            return false
        }

        if (binding.empCodeEd.text.toString().trim().isEmpty()) {
            binding.empCodeLayout.error = getString(R.string.err_enter_emp_code)
            return false
        }

        if (binding.bankAccountEd.text.toString().trim().isEmpty()) {
            binding.bankAccountLayout.error = getString(R.string.err_enter_bank_account)
            return false
        }

        //account no 10 digits validation
        if (binding.bankAccountEd.text.toString().trim().length != 10) {
            binding.bankAccountLayout.error = getString(R.string.err_enter_valid_bank_account)
            return false
        }

        if (binding.bankCardEd.text.toString().trim().isEmpty()) {
            binding.bankCardLayout.error = getString(R.string.err_enter_bank_card_no)
            return false
        }

        //card no 16 digits validation
        if (binding.bankCardEd.text.toString().trim().length != 16) {
            binding.bankCardLayout.error = getString(R.string.err_enter_bank_card_no_valid)
            return false
        }

        if (binding.nationalIdEd.text.toString().trim().isEmpty()) {
            binding.nationalIdLayout.error = getString(R.string.err_enter_national_id)
            return false
        }

        if (binding.emailEd.text.toString().trim().isEmpty()) {
            binding.emailLayout.error = getString(R.string.err_enter_email)
            return false
        }

        if (binding.mobileEd.text.toString().trim().isEmpty()) {
            binding.mobileLayout.error = getString(R.string.err_enter_mobile)
            return false
        }

        if (binding.nationalityEd.text.toString().trim().isEmpty()) {
            binding.nationalityLayout.error = getString(R.string.err_choose_nationality)
            return false
        }

        if (binding.dateBirthEd.text.toString().trim().isEmpty()) {
            binding.dateBirthLayout.error = getString(R.string.err_choose_date_of_birth)
            return false
        }

        if (binding.dateJoinedEd.text.toString().trim().isEmpty()) {
            binding.dateJoinedLayout.error = getString(R.string.err_choose_date_joined)
            return false
        }

        if (binding.genderEd.text.toString().trim().isEmpty()) {
            binding.genderLayout.error = getString(R.string.err_choose_gender)
            return false
        }

        if (binding.approvalEd.text.toString().trim().isEmpty()) {
            binding.approvalLayout.error = getString(R.string.err_choose_approval_group)
            return false
        }

        if (binding.roleEd.text.toString().trim().isEmpty()) {
            binding.roleLayout.error = getString(R.string.err_choose_role)
            return false
        }

        if (binding.employmentTypeEd.text.toString().trim().isEmpty()) {
            binding.employmentTypeLayout.error = getString(R.string.err_choose_employment_type)
            return false
        }

        if (binding.departmentEd.text.toString().trim().isEmpty()) {
            binding.departmentLayout.error = getString(R.string.err_choose_department)
            return false
        }

        if (binding.currencyEd.text.toString().trim().isEmpty()) {
            binding.currencyLayout.error = getString(R.string.err_choose_currency)
            return false
        }

        if (binding.statusEd.text.toString().trim().isEmpty()) {
            binding.statusLayout.error = getString(R.string.err_choose_status)
            return false
        }

        return true
    }

    private fun isDataAvailable(): Boolean {
        AppHelper.hideKeyboard(this)

        if (from == "Employee") {
            return isEmployeeDataAvail()
        } else {
            val empName = binding.empNameEd.text.toString().trim()
            val empDesc = binding.empDescEd.text.toString().trim()

            if (empName.isEmpty()) {
                binding.empNameLayout.error = getString(R.string.err_enter_emp_name)
                return false
            }

            if (empDesc.isEmpty()) {
                binding.empDescLayout.error = getString(R.string.err_enter_emp_desc)
                return false
            }
        }
        return true
    }

    private fun getEmploymentJson(): JsonObject {
        val json = JsonObject()
        json.addProperty("empJobTypeCode", binding.empNameEd.text.toString().trim())
        json.addProperty("empJobTypeDesc", binding.empDescEd.text.toString().trim())
        return json
    }

    private fun getEmployeeJson(): JsonObject {
        val json = JsonObject()
        json.addProperty("firstName", binding.firstNameEd.text.toString().trim())
        json.addProperty("middleName", binding.middleNameEd.text.toString().trim())
        json.addProperty("lastName", binding.lastNameEd.text.toString().trim())
        json.addProperty("empCode", binding.empCodeEd.text.toString().trim())
        json.addProperty("bankAccount", binding.bankAccountEd.text.toString().trim())
        json.addProperty("bankCardNo", binding.bankCardEd.text.toString().trim())
        json.addProperty("nationalID", binding.nationalIdEd.text.toString().trim())
        json.addProperty("passportNo", binding.passportNoEd.text.toString().trim())
        json.addProperty("taxNumber", binding.taxNoEd.text.toString().trim())
        json.addProperty("nationality", binding.nationalityEd.text.toString().trim())
        json.addProperty("dob", dobMillis)
        json.addProperty("doj", dojMillis)
        json.addProperty("gender", binding.genderEd.text.toString().trim())
        json.addProperty("email", binding.emailEd.text.toString().trim())
        json.addProperty("mobileNumber", binding.mobileEd.text.toString().trim())
        json.addProperty("employmentTypeId", employmentTypeId)
        json.addProperty("departmentId", departmentId)
        json.addProperty("roleId", roleId)
        json.addProperty("approvalGroupId", approvalGroupId)
        json.addProperty("currencyTypeId", currencyTypeId)
        json.addProperty("statusTypeId", statusTypeId)
        if (isEditMode) {
            json.addProperty("id", employeeId)
        }
        return json
    }

    private fun loadDropDowns() {
        viewModel.getStatusForDropDowN(getToken()).observe(this, Observer {
            when {
                it.isSuccessful -> {
                    val list = it.body()
                    list?.let { statusItems ->
                        if (statusItems.isNullOrEmpty().not()) {
                            setDropDownAdapter(statusItems)
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

        viewModel.getCurrencyTypesForDropDown(getToken()).observe(this, Observer {
            when {
                it.isSuccessful -> {
                    val list = it.body()
                    list?.let { statusItems ->
                        if (statusItems.isNullOrEmpty().not()) {
                            setCurrencyDropDownAdapter(statusItems)
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

        viewModel.getDepartmentsForDropDown(getToken()).observe(this, Observer {
            when {
                it.isSuccessful -> {
                    val list = it.body()
                    list?.let { statusItems ->
                        if (statusItems.isNullOrEmpty().not()) {
                            setDepartmentDropDownAdapter(statusItems)
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

        viewModel.getEmploymentTypesForDropDown(getToken()).observe(this, Observer {
            when {
                it.isSuccessful -> {
                    val list = it.body()
                    list?.let { statusItems ->
                        if (statusItems.isNullOrEmpty().not()) {
                            setEmploymentDropDownAdapter(statusItems)
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

        setGenderAdapter()
        setNationalityAdapter()
    }

    private fun setGenderAdapter() {
        val genderList = arrayListOf("Male", "Female", "Others")
        val adapter =
            CommonStringDropDownAdapter(this, R.layout.item_status_spinner_view, genderList)
        binding.genderEd.setAdapter(adapter)
        binding.genderEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                binding.genderEd.setText(genderList[position], false)
                adapter.filter.filter(null)
            }
    }

    private fun setNationalityAdapter() {
        val list = CurrencyReader.readCurrencyJson(resources.openRawResource(R.raw.countries))
        if (list.isNullOrEmpty()) {
            printLog("Countries read failed")
        } else {
            printLog("Countries read success")
            val adapter = CommonStringDropDownAdapter(this, R.layout.item_status_spinner_view, list)
            binding.nationalityEd.setAdapter(adapter)
            binding.nationalityEd.onItemClickListener =
                AdapterView.OnItemClickListener { parent, arg1, position, id ->
                    binding.nationalityEd.setText(list[position], false)
                    adapter.filter.filter(null)
                }
        }

        if (isEditMode) {
//            binding.nationalityEd.setText()
        }
    }

    var empTypeItem = EmpTypesDropDownResponse()
    private fun setEmploymentDropDownAdapter(statusItems: ArrayList<EmpTypesDropDownResponse>) {
        val adapter = EmpTypesDropDownAdapter(this, R.layout.item_status_spinner_view, statusItems)
        binding.employmentTypeEd.setAdapter(adapter)
        binding.employmentTypeEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                AppHelper.hideKeyboard(this@ManageEmployeeActivity)
                empTypeItem = statusItems[position]
                employmentId = empTypeItem.id
                binding.employmentTypeEd.setText(statusItems[position].empJobTypeCode, false)
                adapter.filter.filter(null)
            }

        if (isEditMode) {
            for (item in statusItems) {
                if (item.id == employmentId) {
                    empTypeItem = item
                    binding.employmentTypeEd.setText(item.empJobTypeCode)
                }
            }
        }
    }

    var departmentItem = DepartmentDropDownResponse()
    private fun setDepartmentDropDownAdapter(statusItems: ArrayList<DepartmentDropDownResponse>) {
        val adapter =
            DepartmentDropDownAdapter(this, R.layout.item_status_spinner_view, statusItems)
        binding.departmentEd.setAdapter(adapter)
        binding.departmentEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                AppHelper.hideKeyboard(this@ManageEmployeeActivity)
                departmentItem = statusItems[position]
                departmentId = departmentItem.id
                binding.departmentEd.setText(statusItems[position].deptDesc, false)
                adapter.filter.filter(null)
            }

        if (isEditMode) {
            for (item in statusItems) {
                if (item.id == departmentId) {
                    departmentItem = item
                    binding.departmentEd.setText(departmentItem.deptDesc)
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
                AppHelper.hideKeyboard(this@ManageEmployeeActivity)
                jobRoleItem = statusItems[position]
                roleId = jobRoleItem.id
                binding.roleEd.setText(statusItems[position].roleCode, false)
                adapter.filter.filter(null)
            }

        if (isEditMode) {
            for (item in statusItems) {
                if (item.id == roleId) {
                    jobRoleItem = item
                    binding.roleEd.setText(jobRoleItem.roleCode)
                }
            }
        }
    }

    var approvalItem = ApprovalGroupDropDownResponse()
    private fun setApprovalDropDownAdapter(statusItems: ArrayList<ApprovalGroupDropDownResponse>) {
        val adapter = ApprovalDropDownAdapter(this, R.layout.item_status_spinner_view, statusItems)
        binding.approvalEd.setAdapter(adapter)
        binding.approvalEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                AppHelper.hideKeyboard(this@ManageEmployeeActivity)
                approvalItem = statusItems[position]
                approvalGroupId = approvalItem.id
                binding.approvalEd.setText(statusItems[position].approvalGroupCode, false)
                adapter.filter.filter(null)
            }

        if (isEditMode) {
            for (item in statusItems) {
                if (item.id == approvalGroupId) {
                    approvalItem = item
                    binding.approvalEd.setText(approvalItem.approvalGroupCode)
                }
            }
        }
    }

    var currencyItem = CurrencyDropDownResponse()
    private fun setCurrencyDropDownAdapter(it: ArrayList<CurrencyDropDownResponse>) {
        val adapter = CurrencyDownAdapter(this, R.layout.item_status_spinner_view, it)
        binding.currencyEd.setAdapter(adapter)
        binding.currencyEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                AppHelper.hideKeyboard(this@ManageEmployeeActivity)
                currencyItem = it[position]
                currencyTypeId = currencyItem.id
                binding.currencyEd.setText(it[position].currencyCode, false)
                adapter.filter.filter(null)
            }

        if (isEditMode) {
            for (item in it) {
                if (item.id == currencyTypeId) {
                    currencyItem = item
                    binding.currencyEd.setText(currencyItem.currencyCode)
                }
            }
        }
    }

    var statusItem = StatusDropDownResponse()
    private fun setDropDownAdapter(it: ArrayList<StatusDropDownResponse>) {
        val adapter = DropDownAdapter(this, R.layout.item_status_spinner_view, it)
        binding.statusEd.setAdapter(adapter)
        binding.statusEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                AppHelper.hideKeyboard(this@ManageEmployeeActivity)
                statusItem = it[position]
                statusTypeId = statusItem.id
                binding.statusEd.setText(it[position].status, false)
                adapter.filter.filter(null)
            }

        if (isEditMode) {
            for (item in it) {
                if (item.id == statusTypeId) {
                    statusItem = item
                    binding.statusEd.setText(item.status)
                }
            }
        }
    }

    override fun onBack() {
        AppHelper.hideKeyboard(this)
        onBackPressed()
    }
}