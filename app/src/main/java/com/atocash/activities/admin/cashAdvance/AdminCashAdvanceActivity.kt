package com.atocash.activities.admin.cashAdvance

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.adapter.*
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityAdminCashAdvanceBinding
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.*
import com.atocash.utils.Keys
import com.atocash.utils.extensions.markRequired
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_admin_cash_advance.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminCashAdvanceActivity :
    SuperCompatActivity<ActivityAdminCashAdvanceBinding, AdminCashAdvanceViewModel>(),
    AdminCashAdvanceNavigator {

    private lateinit var binding: ActivityAdminCashAdvanceBinding
    private lateinit var viewModel: AdminCashAdvanceViewModel

    private var currencyItem = CurrencyDropDownResponse()
    private var currencyTypeId = 0
    private var cashAdvanceId = 0
    private var projectId: String? = null
    private var subProjectId: String? = null
    private var workTaskId: String? = null
    private var isEditMode = false
    private var pettyCashResponse: PettyCashResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        currencyItem = CurrencyDropDownResponse(
            dataStorage.getInt(Keys.UserData.CURRENCY_ID),
            dataStorage.getString(Keys.UserData.CURRENCY_CODE).toString()
        )
        currencyTypeId = dataStorage.getInt(Keys.UserData.CURRENCY_ID)

        initViewsAndClicks()
        setRequiredFields()
    }

    private fun setRequiredFields() {
        binding.apply {
            advanceAmtLayout.markRequired()
            descLayout.markRequired()
            currencyLayout.markRequired()
            lytBusinessType.markRequired()
            lytBusinessUnit.markRequired()
            lytLocation.markRequired()
            projectLayout.markRequired()
            subProjectLayout.markRequired()
            taskLayout.markRequired()

            projectEd.addTextChangedListener {
                if(it.toString().isNotEmpty()) projectLayout.error = ""
            }
            subProjectEd.addTextChangedListener {
                if(it.toString().isNotEmpty()) subProjectLayout.error = ""
            }
            taskEd.addTextChangedListener {
                if(it.toString().isNotEmpty()) taskLayout.error = ""
            }
            edBusinessType.addTextChangedListener {
                if(it.toString().isNotEmpty()) lytBusinessType.error = ""
            }
            edBusinessUnit.addTextChangedListener {
                if(it.toString().isNotEmpty()) lytBusinessUnit.error = ""
            }
            edLocation.addTextChangedListener {
                if(it.toString().isNotEmpty()) lytLocation.error = ""
            }
            advanceAmtEd.addTextChangedListener {
                if(it.toString().isNotEmpty()) advanceAmtLayout.error = ""
            }
            descEd.addTextChangedListener {
                if(it.toString().isNotEmpty()) descLayout.error = ""
            }
            currencyEd.addTextChangedListener {
                if(it.toString().isNotEmpty()) currencyLayout.error = ""
            }
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_admin_cash_advance
    }

    override fun getViewModel(): AdminCashAdvanceViewModel {
        viewModel = ViewModelProvider(this).get(AdminCashAdvanceViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        initBackWithTitle(binding.toolbar.toolParent, getString(R.string.manage_cash_request))
        viewModel.isProjectEnabled.set(false)
        viewModel.isSubProjectEnabled.set(false)
        viewModel.isTaskEnabled.set(false)

        binding.currencyEd.background = ContextCompat.getDrawable(this, R.drawable.disabled_ed)
        binding.currencyEd.setText(currencyItem.currencyCode)

        val bundle = intent.extras
        bundle?.let {
            isEditMode = it.getBoolean(Keys.IntentData.IS_EDIT)
            if (isEditMode) {
                val itemStr = it.getString("PettyCashItem")
                itemStr?.let { itemStr_ ->
                    pettyCashResponse =
                        Gson().fromJson(itemStr_, PettyCashResponse::class.java)
                    updateUiForEditMode(pettyCashResponse)
                }
            }
        }

        binding.projectSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isProjectEnabled.set(isChecked)
            if (isChecked) {
                loadProjects()
                lytBusinessType.visibility = View.GONE
                lytBusinessUnit.visibility = View.GONE
                lytLocation.visibility = View.GONE

                businessTypeId = -1
                businessUnitId = -1
                binding.edLocation.setText("")
                binding.edBusinessType.setText("")
                binding.edBusinessUnit.setText("")
            } else {
                lytBusinessType.visibility = View.VISIBLE
                lytBusinessUnit.visibility = View.VISIBLE
                lytLocation.visibility = View.VISIBLE
            }
        }

        binding.createBtn.text =
            if (isEditMode) getString(R.string.btn_update) else getString(R.string.btn_create)

        if (isEditMode && viewModel.isProjectEnabled.get()!!) {
            printLog("isEditMode and project is also chosen")
            loadProjects()
        } else {
            loadBusinessTypes()
        }
    }

    private fun loadBusinessTypes() {
        showLoading()
        RetrofitClient.instance.apiService.getBusinessTypesForDropDown(getToken()).enqueue(
            object : Callback<ArrayList<BusinessTypesForDropDownResponseItem>> {
                override fun onResponse(
                    call: Call<ArrayList<BusinessTypesForDropDownResponseItem>>,
                    response: Response<ArrayList<BusinessTypesForDropDownResponseItem>>
                ) {
                    cancelLoading()
                    response.body()?.let { setBusinessTypesAdapter(it) }
                }

                override fun onFailure(
                    call: Call<ArrayList<BusinessTypesForDropDownResponseItem>>,
                    t: Throwable
                ) {
                    cancelLoading()
                }
            }
        )
    }

    var businessType = BusinessTypesForDropDownResponseItem()
    var businessTypeId: Int? = -1
    private fun setBusinessTypesAdapter(it: ArrayList<BusinessTypesForDropDownResponseItem>) {
        if (it.isNotEmpty()) {
            val adapter = BusinessTypesDropDownAdapter(
                this,
                R.layout.item_status_spinner_view,
                it
            )
            edBusinessType.setAdapter(adapter)
            edBusinessType.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    businessType = it[position]
                    businessTypeId = businessType.id
                    edBusinessType.setText(it[position].businessTypeName, false)
                    adapter.filter.filter(null)
                    loadBusinessUnits()
                }
        } else {
            showShortToast(getString(R.string.no_business_types_avaialble))
        }
    }

    private fun loadBusinessUnits() {
        showLoading()

        val jsonObject = JsonObject()
        jsonObject.addProperty("empId", dataStorage?.getString(Keys.UserData.ID).toString())
        jsonObject.addProperty("businessTypeId", businessTypeId)

        RetrofitClient.instance.apiService.getBusinessUnitsForDropDown(
            getToken(), jsonObject
        ).enqueue(
            object : Callback<ArrayList<BusinessUnitsListResponseItem>> {
                override fun onResponse(
                    call: Call<ArrayList<BusinessUnitsListResponseItem>>,
                    response: Response<ArrayList<BusinessUnitsListResponseItem>>
                ) {
                    cancelLoading()
                    response.body()?.let { setBusinessUnitsAdapter(it) }
                }

                override fun onFailure(
                    call: Call<ArrayList<BusinessUnitsListResponseItem>>,
                    t: Throwable
                ) {
                    cancelLoading()
                }
            }
        )
    }

    var businessUnit = BusinessUnitsListResponseItem()
    var businessUnitId: Int? = -1
    private fun setBusinessUnitsAdapter(it: ArrayList<BusinessUnitsListResponseItem>) {
        if (it.isNotEmpty()) {
            val adapter = BusinessUnitsDropDownAdapter(
                this,
                R.layout.item_status_spinner_view,
                it
            )
            lytBusinessUnit.visibility = View.VISIBLE
            binding.edBusinessUnit.setAdapter(adapter)
            binding.edBusinessUnit.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    businessUnit = it[position]
                    businessUnitId = businessUnit.id
                    binding.edBusinessUnit.setText(it[position].businessUnitName, false)
                    adapter.filter.filter(null)
                    loadBusinessDetails(businessUnitId)
                }
        } else {
            showShortToast(getString(R.string.no_business_units_avaialble))
        }
    }

    private fun loadBusinessDetails(businessUnitId: Int?) {
        businessUnitId?.let {
            showLoading()
            RetrofitClient.instance.apiService.getBusinessUnitsDetails(getToken(), it).enqueue(
                object : Callback<BusinessUnitDetails> {
                    override fun onResponse(
                        call: Call<BusinessUnitDetails>,
                        response: Response<BusinessUnitDetails>
                    ) {
                        cancelLoading()
                        response.body()?.let { details ->
                            setBusinessDetails(details)
                        }
                    }

                    override fun onFailure(call: Call<BusinessUnitDetails>, t: Throwable) {
                        cancelLoading()
                    }
                }
            )
        }
    }

    var businessUnitDetails: BusinessUnitDetails? = null
    private fun setBusinessDetails(details: BusinessUnitDetails) {
        businessUnitDetails = details
        binding.edLocation.setText(details.location.toString())
        lytLocation.visibility = View.VISIBLE
    }

    private fun updateUiForEditMode(pettyCashResponse: PettyCashResponse?) {
        pettyCashResponse?.let {
            binding.projectSwitch.visibility = View.GONE

            binding.advanceAmtEd.setText(pettyCashResponse.cashAdvanceAmount.toString())
            binding.descEd.setText(pettyCashResponse.cashAdvanceRequestDesc)

            currencyItem = CurrencyDropDownResponse(
                dataStorage.getInt(Keys.UserData.CURRENCY_ID),
                dataStorage.getString(Keys.UserData.CURRENCY_CODE).toString()
            )
            currencyTypeId = dataStorage.getInt(Keys.UserData.CURRENCY_ID)

            binding.currencyEd.setText(dataStorage.getString(Keys.UserData.CURRENCY_CODE))
            cashAdvanceId = pettyCashResponse.id.toString().toInt()

            if (TextUtils.isEmpty(pettyCashResponse.project).not()) {

                printLog("project enabled")

                viewModel.isProjectEnabled.set(true)
                binding.projectSwitch.isChecked = true
                projectsResponse = AssignedProjectsResponse()
                pettyCashResponse.projectId?.let {
                    projectsResponse.id = it
                    projectId = it.toString()
                }
                pettyCashResponse.project?.let {
                    projectsResponse.projectName = it

                }

                binding.projectEd.setText(pettyCashResponse.project)

                viewModel.isSubProjectEnabled.set(true)

                subProjectId = pettyCashResponse.subProjectId.toString()
                subProjectsResponse = SubProjectsResponse()
                pettyCashResponse.subProject?.let {
                    subProjectsResponse.subProjectName = it
                }
                pettyCashResponse.subProjectId?.let {
                    subProjectsResponse.id = it
                }
                subProjectId = pettyCashResponse.subProjectId.toString()

                binding.subProjectEd.setText(pettyCashResponse.subProject)

                viewModel.isTaskEnabled.set(true)
                workTaskId = pettyCashResponse.workTaskId.toString()
                taskResponse = TasksResponse()

                pettyCashResponse.workTask?.let {
                    taskResponse.taskName = it
                }
                pettyCashResponse.id?.let {
                    taskResponse.id = it
                }
                workTaskId = pettyCashResponse.workTaskId.toString()

                binding.taskEd.setText(pettyCashResponse.workTask)
//                loadCurrency()
            } else {
//                loadCurrency()

                binding.lytBusinessUnit.visibility = View.VISIBLE
                binding.lytBusinessType.visibility = View.VISIBLE
                binding.lytLocation.visibility = View.VISIBLE

                binding.edLocation.setText(pettyCashResponse.location)
                binding.edBusinessUnit.setText(pettyCashResponse.businessUnit)
                binding.edBusinessType.setText(pettyCashResponse.businessType)

                businessTypeId = pettyCashResponse.businessTypeId
                businessUnitId = pettyCashResponse.businessUnitId

                currencyItem = CurrencyDropDownResponse(
                    dataStorage.getInt(Keys.UserData.CURRENCY_ID),
                    dataStorage.getString(Keys.UserData.CURRENCY_CODE).toString()
                )
                currencyTypeId = dataStorage.getInt(Keys.UserData.CURRENCY_ID)

                printLog("project not enabled")
            }
        }
    }

//    private fun loadCurrency() {
//        viewModel.getCurrencyTypesForDropDown(getToken()).observe(this, Observer {
//            when {
//                it.isSuccessful -> {
//                    val list = it.body()
//                    list?.let { statusItems ->
//                        if (statusItems.isNullOrEmpty().not()) {
//                            setCurrencyDropDownAdapter(statusItems)
//                        }
//                    }
//                }
//                it.code() == 401 -> {
//                    showUnAuthDialog()
//                }
//                else -> {
//                    showErrorResponse(it.errorBody()?.string())
//                }
//            }
//        })
//    }

//    private fun setCurrencyDropDownAdapter(it: ArrayList<CurrencyDropDownResponse>) {
//        if (isEditMode) {
//            for (item in it) {
//                if (item.id == currencyTypeId) {
////                    currencyTypeId = item.id
////                    currencyItem = item
//                    binding.currencyEd.setText(currencyItem.currencyCode)
//                }
//            }
//        } else {
//            val userCurrencyCode = dataStorage.getString(Keys.UserData.CURRENCY_CODE).toString()
//            for (item in it) {
//                if (item.currencyCode == userCurrencyCode) {
//                    currencyTypeId = item.id
//                    currencyItem = item
//                    binding.currencyEd.setText(currencyItem.currencyCode)
//                }
//            }
//        }
//
//        printLog("isProjectEnabled ${viewModel.isProjectEnabled.get()!!}")
//
//
//    }

    private fun loadProjects() {
        showLoading()
        viewModel.getAssignedProjectsForDropDown(getToken())
            .observe(this, androidx.lifecycle.Observer {
                cancelLoading()
                it.let { res ->
                    if (res.isNullOrEmpty().not()) {
                        setProjectsAdapter(res)
                    }
                }
            })
    }

    var projectsResponse = AssignedProjectsResponse()
    private fun setProjectsAdapter(res: ArrayList<AssignedProjectsResponse>) {
        if (!res.isNullOrEmpty()) {
            val adapter =
                AssignedProjectsDropDownAdapter(this, R.layout.item_status_spinner_view, res)
            binding.projectEd.setAdapter(adapter)
            binding.projectEd.onItemClickListener =
                AdapterView.OnItemClickListener { parent, arg1, position, id ->
                    projectsResponse = res[position]
                    projectId = projectsResponse.id.toString()
                    binding.projectEd.setText(res[position].projectName, false)
                    adapter.filter.filter(null)
                    loadSubProjectsForDropDown()
                }
        } else {
            showShortToast(getString(R.string.no_projects_avail))
        }

        if (isEditMode) {
            loadSubProjectsForDropDown()
        }
    }

    private fun loadSubProjectsForDropDown() {
        projectId?.let {
//            showLoading()
            viewModel.getSubProjectsForDropDown(getToken(), it.toInt())
                .observe(this, androidx.lifecycle.Observer { subProjectRes ->
//                    cancelLoading()
                    subProjectRes.let { res ->
                        if (res.isNullOrEmpty().not()) {
                            setSubProjectsAdapter(res)
                            viewModel.isSubProjectEnabled.set(true)
                        } else {
                            viewModel.isSubProjectEnabled.set(false)
                        }
                    }
                })
        }
    }

    private var subProjectsResponse = SubProjectsResponse()
    private fun setSubProjectsAdapter(res: ArrayList<SubProjectsResponse>) {
        val adapter = SubProjectsDropDownAdapter(this, R.layout.item_status_spinner_view, res)
        binding.subProjectEd.setAdapter(adapter)
        binding.subProjectEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                subProjectId = res[position].id.toString()
                subProjectsResponse = res[position]
                binding.subProjectEd.setText(res[position].subProjectName, false)
                adapter.filter.filter(null)
                loadTasks()
            }

        if (isEditMode) {
            loadTasks()
        }
    }

    private fun loadTasks() {
        subProjectId?.let {
//            showLoading()
            viewModel.getTasksForDropDown(getToken(), it.toInt())
                .observe(this, androidx.lifecycle.Observer { tasksRes ->
//                    cancelLoading()
                    tasksRes.let { res ->
                        if (res.isNullOrEmpty().not()) {
                            setTasksAdapter(res)
                            viewModel.isTaskEnabled.set(true)
                        } else {
                            viewModel.isTaskEnabled.set(false)
                        }
                    }
                })
        }
    }

    private var taskResponse = TasksResponse()
    private fun setTasksAdapter(res: ArrayList<TasksResponse>) {
        val adapter = TasksDropDownAdapter(this, R.layout.item_status_spinner_view, res)
        binding.taskEd.setAdapter(adapter)
        binding.taskEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                workTaskId = res[position].id.toString()
                taskResponse = res[position]
                binding.taskEd.setText(res[position].taskName, false)
                adapter.filter.filter(null)
            }
    }


    override fun onCreateAdvanceRequest() {
        if (isNetworkConnected) {
            if (isAllDataAvail()) {
                if (isEditMode) {
                    showLoading(getString(R.string.updating_cash_request))
                    viewModel.updateCashAdvance(getToken(), cashAdvanceId, getJsonData())
                        .observe(this, Observer {
                            cancelLoading()
                            when (it.code()) {
                                200, 201 -> {
                                    showShortToast(getString(R.string.success_cash_advance_udpate))
                                    finish()
                                }
                                401 -> showUnAuthDialog()
                                409 -> {
                                    showErrorResponse(it.errorBody()?.string())
                                }
                                else -> showErrorResponse(it.errorBody()?.string())
                            }
                        })
                } else {
                    showLoading(getString(R.string.creating_cash_request))
                    viewModel.createCashAdvance(getToken(), getJsonData())
                        .observe(this, Observer {
                            cancelLoading()
                            when (it.code()) {
                                200, 201 -> {
                                    showShortToast(getString(R.string.success_cash_advance_udpate))
                                    finish()
                                }
                                401 -> showUnAuthDialog()
                                409 -> {
                                    showErrorResponse(it.errorBody()?.string())
                                    finish()
                                }
                                else -> showErrorResponse(it.errorBody()?.string())
                            }
                        })
                }
            }
        } else {
            showSnack(getString(R.string.check_internet))
        }
    }

    private fun getJsonData(): JsonObject {
        val json = JsonObject()
        json.addProperty("employeeId", dataStorage.getString(Keys.UserData.ID).toString())
        json.addProperty("businessTypeId", businessTypeId)
        json.addProperty("businessUnitId", businessUnitId)
        json.addProperty(
            "cashAdvanceAmount",
            binding.advanceAmtEd.text.toString().trim().toInt()
        )
        json.addProperty("cashAdvanceRequestDesc", binding.descEd.text.toString().trim())
        json.addProperty("currencyTypeId", currencyTypeId)

        json.addProperty("projectId", projectId)
        json.addProperty("subProjectId", subProjectId)
        json.addProperty("workTaskId", workTaskId)

        if (isEditMode) {
            json.addProperty("id", cashAdvanceId)
        }

        return json
    }

    private fun isAllDataAvail(): Boolean {
        if(viewModel.isProjectEnabled.get() == true) {
            if (binding.advanceAmtEd.text.toString().trim().isEmpty()) {
                binding.advanceAmtLayout.error = getString(R.string.err_enter_amount)
                return false
            }
            if (binding.descEd.text.toString().trim().isEmpty()) {
                binding.descLayout.error = getString(R.string.err_enter_desc)
                return false
            }
            if (binding.currencyEd.text.toString().trim().isEmpty()) {
                binding.currencyLayout.error = getString(R.string.err_choose_currency)
                return false
            }
            if (binding.projectEd.text.toString().trim().isEmpty()) {
                binding.projectLayout.error = getString(R.string.err_choose_project)
                return false
            }
            if (binding.subProjectEd.text.toString().trim().isEmpty()) {
                binding.subProjectLayout.error = getString(R.string.err_choose_sub_project)
                return false
            }
            if (binding.taskEd.text.toString().trim().isEmpty()) {
                binding.taskLayout.error = getString(R.string.err_choose_task)
                return false
            }
        } else {
            if (binding.edBusinessType.text.toString().trim().isEmpty()) {
                binding.lytBusinessType.error = getString(R.string.choose_business_type)
                return false
            }
            if (binding.edBusinessUnit.text.toString().trim().isEmpty()) {
                binding.lytBusinessUnit.error = getString(R.string.choose_business_unit)
                return false
            }
            if (binding.edLocation.text.toString().trim().isEmpty()) {
                binding.lytLocation.error = getString(R.string.choose_location)
                return false
            }
            if (binding.advanceAmtEd.text.toString().trim().isEmpty()) {
                binding.advanceAmtLayout.error = getString(R.string.err_enter_amount)
                return false
            }
            if (binding.descEd.text.toString().trim().isEmpty()) {
                binding.descLayout.error = getString(R.string.err_enter_desc)
                return false
            }
            if (binding.currencyEd.text.toString().trim().isEmpty()) {
                binding.currencyLayout.error = getString(R.string.err_choose_currency)
                return false
            }
        }
        return true
    }

    override fun onBack() {
        onBackPressed()
    }
}