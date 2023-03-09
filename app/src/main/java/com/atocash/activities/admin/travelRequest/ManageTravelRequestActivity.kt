package com.atocash.activities.admin.travelRequest

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.adapter.*
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityManageTravelRequestBinding
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.*
import com.atocash.utils.AppHelper
import com.atocash.utils.CustomTextWatcher
import com.atocash.utils.DateUtils
import com.atocash.utils.DateUtils.EXPENSE_REIMBURSE_FORMAT
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
import java.util.*

class ManageTravelRequestActivity :
    SuperCompatActivity<ActivityManageTravelRequestBinding, ManageTravelRequestViewModel>(),
    ManageTravelRequestNavigator {

    private lateinit var binding: ActivityManageTravelRequestBinding
    private lateinit var viewModel: ManageTravelRequestViewModel

    private var endDateMillis = ""
    private var startDateMillis = ""

    private var startDateForApi = ""
    private var endDateForApi = ""

    private var endDateMinMillis: Long? = 0
    private var projectId: Int? = null
    private var subProjectId: Int? = null
    private var workTaskId: Int? = null
    private var itemId: Int? = null

    private var startDateDialog: DatePickerDialog? = null
    private var endDateCalendar: DatePickerDialog? = null

    private var isEdit = false
    private var travelRequestItem: TravelResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
        setRequiredFields()
    }

    private fun setProjectRequiredFields() {
        binding.apply {
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
        }
    }

    private fun setRequiredFields() {
        binding.apply {
            startDateLayout.markRequired()
            descLayout.markRequired()
            travelPurposeLayout.markRequired()

            lytBusinessType.markRequired()
            lytBusinessUnit.markRequired()
            lytLocation.markRequired()

            startDateEd.addTextChangedListener {
                if(it.toString().isNotEmpty()) startDateLayout.error = ""
            }
            endDateEd.addTextChangedListener {
                if(it.toString().isNotEmpty()) descLayout.error = ""
            }
            purposeEd.addTextChangedListener {
                if(it.toString().isNotEmpty()) travelPurposeLayout.error = ""
            }
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_manage_travel_request
    }

    override fun getViewModel(): ManageTravelRequestViewModel {
        viewModel = ViewModelProvider(this).get(ManageTravelRequestViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        initBackWithTitle(binding.toolbar.toolParent, getString(R.string.manage_travel_request))

        val bundle = intent.extras
        bundle?.let {
            isEdit = it.getBoolean(Keys.IntentData.IS_EDIT)
            if (isEdit) {
                val itemStr = it.getString("TravelResponseItem")
                itemStr?.let { itemStr_ ->
                    travelRequestItem =
                        Gson().fromJson(itemStr_, TravelResponse::class.java)
                    updateUiForEditMode(travelRequestItem)
                }
            }
        }

        initListeners()
        initStartDateCalendar()
        initEndDateCalendar()

        binding.createBtn.text =
            if (isEdit) getString(R.string.btn_update) else getString(R.string.btn_create)

        if (!isEdit) loadProjects()

        loadBusinessTypes()
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

    private fun updateUiForEditMode(travelRequestItem: TravelResponse?) {
        travelRequestItem?.let {

            it.travelStartDate?.let { it1 ->
                binding.startDateEd.setText(DateUtils.getDate(it1))
                printLog("travelStartDate $it")

                startDateForApi = DateUtils.getLongTimeWithIso(it1)
            }

            it.travelEndDate?.let { it1 ->
                printLog("travelEndDate $it")

                endDateMinMillis = DateUtils.getTimeInMillis(it1)
                binding.endDateEd.setText(DateUtils.getDate(it1))
                endDateMillis = DateUtils.getLongTime(it1)
                endDateForApi = DateUtils.getLongTimeWithIso(it1)
            }

            binding.purposeEd.setText(it.travelPurpose)

            it.workTaskId?.let { workTaskId_ -> workTaskId = workTaskId_ }
            it.subProjectId?.let { subProjectId_ -> subProjectId = subProjectId_ }
            it.projectId?.let { projectId_ ->
                projectId = projectId_
                loadProjects()
            } ?: run {
                binding.lytBusinessUnit.visibility = View.VISIBLE
                binding.lytBusinessType.visibility = View.VISIBLE
                binding.lytLocation.visibility = View.VISIBLE

                binding.edLocation.setText(travelRequestItem.location)
                binding.edBusinessUnit.setText(travelRequestItem.businessUnit)
                binding.edBusinessType.setText(travelRequestItem.businessType)

                businessTypeId = travelRequestItem.businessTypeId
                businessUnitId = travelRequestItem.businessUnitId
            }

            itemId = it.id
        }
    }

    private fun initListeners() {
        binding.projectSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isProjectEnabled.set(isChecked)
            if (isChecked) {
                resetBusinessFields(false)
            } else {
                resetBusinessFields(true)
            }
        }
        binding.edBusinessType.addTextChangedListener {
            if(it.toString().isNotEmpty()) binding.lytBusinessType.error = ""
        }
        binding.edBusinessUnit.addTextChangedListener {
            if(it.toString().isNotEmpty()) binding.lytBusinessUnit.error = ""
        }
        binding.edLocation.addTextChangedListener {
            if(it.toString().isNotEmpty()) binding.lytLocation.error = ""
        }
        binding.projectEd.addTextChangedListener {
            if(it.toString().isNotEmpty()) binding.projectLayout.error = ""
        }
        binding.subProjectEd.addTextChangedListener {
            if(it.toString().isNotEmpty()) binding.subProjectLayout.error = ""
        }
        binding.taskEd.addTextChangedListener {
            if(it.toString().isNotEmpty()) binding.taskLayout.error = ""
        }
        binding.projectEd.addTextChangedListener {
            if(it.toString().isNotEmpty()) binding.projectLayout.error = ""
        }
        binding.startDateEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.startDateLayout.error = ""
                }
            }
        })
        binding.endDateEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.descLayout.error = ""
                }
            }
        })
        binding.purposeEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.travelPurposeLayout.error = ""
                }
            }
        })
    }

    private fun resetBusinessFields(isVisible: Boolean) {
        lytBusinessType.visibility = if(isVisible) View.VISIBLE else View.GONE
        lytBusinessUnit.visibility = if(isVisible) View.VISIBLE else View.GONE
        lytLocation.visibility = if(isVisible) View.VISIBLE else View.GONE

        businessTypeId = -1
        businessUnitId = -1
        binding.edLocation.setText("")
        binding.edBusinessType.setText("")
        binding.edBusinessUnit.setText("")
    }

    private fun initStartDateCalendar() {
        val dateListener: DatePickerDialog.OnDateSetListener

        val myCalendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        dateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            endDateMinMillis = myCalendar.timeInMillis
            startDateMillis = DateUtils.fromCalendar(myCalendar)

            startDateForApi = DateUtils.fromCalendar(myCalendar, EXPENSE_REIMBURSE_FORMAT)

            binding.startDateEd.setText(AppHelper.getDate(myCalendar))

            initEndDateCalendar()
        }

        startDateDialog = DatePickerDialog(
            this, dateListener, myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        startDateDialog?.datePicker?.minDate = System.currentTimeMillis() - 1000
        startDateDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        startDateDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    private fun initEndDateCalendar() {
        val dateListener: DatePickerDialog.OnDateSetListener

        val myCalendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        dateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            endDateMillis = DateUtils.fromCalendar(myCalendar)

            endDateForApi = DateUtils.fromCalendar(myCalendar, EXPENSE_REIMBURSE_FORMAT)

            binding.endDateEd.setText(AppHelper.getDate(myCalendar))
        }

        endDateCalendar = DatePickerDialog(
            this, dateListener, myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        printLog("End date min millis $endDateMinMillis")
        if (endDateMinMillis == 0L) {
            endDateCalendar?.datePicker?.minDate = System.currentTimeMillis() - 1000
        } else {
            endDateMinMillis?.let {
                endDateCalendar?.datePicker?.minDate = it - 1000
            }
        }
        endDateCalendar?.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        endDateCalendar?.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onCreateTravelReq() {
        if (isAllDataAvail()) {
            if (isEdit) {
                editRequest()
            } else {
                createRequest()
            }
        }
    }

    private fun editRequest() {
        showLoading()
        viewModel.editTravelRequest(getToken(), itemId, getEditDto())
            .observe(this, androidx.lifecycle.Observer {
                cancelLoading()
                when (it.code()) {
                    200, 201 -> {
                        showShortToast(
                            if (isEdit) getString(R.string.travel_request_create_success) else getString(
                                R.string.travel_request_update_success
                            )
                        )
                        finish()
                    }
                    401 -> {
                        finish()
                        showUnAuthDialog()
                    }
                    else -> {
                        showErrorResponse(it.errorBody()?.string())
//                        finish()
                    }
                }
            })
    }

    private fun createRequest() {
        showLoading()
        viewModel.createTravelRequest(getToken(), getTravelRequestDto())
            .observe(this, androidx.lifecycle.Observer {
                cancelLoading()
                when (it.code()) {
                    200, 201 -> {
                        showShortToast(
                            if (isEdit) getString(R.string.travel_request_create_success) else getString(
                                R.string.travel_request_update_success
                            )
                        )
                        finish()
                    }
                    401 -> {
                        finish()
                        showUnAuthDialog()
                    }
                    else -> {
                        showErrorResponse(it.errorBody()?.string())
                        finish()
                    }
                }
            })
    }

    data class TravelRequestDto(
        var id: String? = null,
        var employeeId: String? = null,
        var travelStartDate: String? = null,
        var travelEndDate: String? = null,
        var travelPurpose: String? = null,
        var projectId: Int? = null,
        var subProjectId: Int? = null,
        var workTaskId: Int? = null,
        var businessTypeId: Int? = null,
        var businessUnitId: Int? = null,
    )

    private fun getEditDto(): TravelRequestDto {
        val dto = TravelRequestDto()
        dto.employeeId = getUserId()
        dto.id = itemId.toString()
        dto.travelStartDate = startDateForApi
        dto.travelPurpose = binding.purposeEd.text.toString().trim()
        dto.travelEndDate = endDateForApi
        dto.projectId = projectId
        dto.subProjectId = subProjectId
        dto.workTaskId = workTaskId
        dto.businessTypeId = businessTypeId
        dto.businessUnitId = businessUnitId
        return dto
    }

    private fun getTravelRequestDto(): com.atocash.network.request.TravelRequestDto {
        val dto = com.atocash.network.request.TravelRequestDto()
        dto.employeeId = getUserId()
        dto.travelStartDate = startDateForApi
        dto.travelEndDate = endDateForApi
        dto.travelPurpose = binding.purposeEd.text.toString().trim()
        dto.projectId = projectId
        dto.subProjectId = subProjectId
        dto.workTaskId = workTaskId
        dto.businessTypeId = businessTypeId
        dto.businessUnitId = businessUnitId
        return dto
    }

    private fun isAllDataAvail(): Boolean {
        if(binding.projectSwitch.isChecked) {
            if (binding.startDateEd.text.toString().trim().isEmpty()) {
                binding.startDateLayout.error = getString(R.string.choose_start_date)
                return false
            }
            if (binding.endDateEd.text.toString().trim().isEmpty()) {
                binding.descLayout.error = getString(R.string.choose_end_date)
                return false
            }
            if (binding.purposeEd.text.toString().trim().isEmpty()) {
                binding.travelPurposeLayout.error = getString(R.string.enter_travel_purpose)
                return false
            }
            if(binding.projectEd.text.toString().trim().isEmpty()) {
                binding.projectLayout.error = getString(R.string.err_choose_project)
                return false
            }
            if(binding.subProjectEd.text.toString().trim().isEmpty()) {
                binding.subProjectLayout.error = getString(R.string.err_choose_sub_project)
                return false
            }
            if(binding.taskEd.text.toString().trim().isEmpty()) {
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
            if (binding.startDateEd.text.toString().trim().isEmpty()) {
                binding.startDateLayout.error = getString(R.string.choose_start_date)
                return false
            }
            if (binding.endDateEd.text.toString().trim().isEmpty()) {
                binding.descLayout.error = getString(R.string.choose_end_date)
                return false
            }
            if (binding.purposeEd.text.toString().trim().isEmpty()) {
                binding.travelPurposeLayout.error = getString(R.string.enter_travel_purpose)
                return false
            }
        }
        return true
    }

    override fun onEndDateClick() {
        AppHelper.hideKeyboard(this@ManageTravelRequestActivity)
        endDateCalendar?.show()
    }

    override fun onStartDateClick() {
        AppHelper.hideKeyboard(this@ManageTravelRequestActivity)
        startDateDialog?.show()
    }

    private fun loadProjects() {
        setProjectRequiredFields()
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
                    projectId = projectsResponse.id
                    binding.projectEd.setText(res[position].projectName, false)
                    adapter.filter.filter(null)
                    loadSubProjectsForDropDown()
                }

            if (isEdit) {
                for (item in res) {
                    if (projectId == item.id) {
                        viewModel.isProjectEnabled.set(true)
                        binding.projectEd.setText(item.projectName)
                        binding.projectSwitch.isChecked = true
                        adapter.filter.filter(null)
                    }
                }
                loadSubProjectsForDropDown()
            }
        } else {
            showShortToast(getString(R.string.no_projects_avail))
        }
    }

    private fun loadSubProjectsForDropDown() {
        projectId?.let {
            showLoading()
            viewModel.getSubProjectsForDropDown(getToken(), it)
                .observe(this, androidx.lifecycle.Observer { subProjectRes ->
                    cancelLoading()
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
            AdapterView.OnItemClickListener { _, _, position, _ ->
                subProjectId = res[position].id
                subProjectsResponse = res[position]
                binding.subProjectEd.setText(res[position].subProjectName, false)
                adapter.filter.filter(null)
                loadTasks()
            }

        if (isEdit) {
            for (item in res) {
                if (subProjectId == item.id) {
                    binding.subProjectEd.setText(item.subProjectName)
                    adapter.filter.filter(null)
                }
            }
            loadTasks()
        }
    }

    private fun loadTasks() {
        subProjectId?.let {
            showLoading()
            viewModel.getTasksForDropDown(getToken(), it)
                .observe(this, androidx.lifecycle.Observer { tasksRes ->
                    cancelLoading()
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
                workTaskId = res[position].id
                taskResponse = res[position]
                binding.taskEd.setText(res[position].taskName, false)
                adapter.filter.filter(null)
            }

        if (isEdit) {
            for (item in res) {
                if (workTaskId == item.id) {
                    binding.taskEd.setText(item.taskName)
                    adapter.filter.filter(null)
                }
            }
        }
    }

}