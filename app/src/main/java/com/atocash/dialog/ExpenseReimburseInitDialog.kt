package com.atocash.dialog

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.atocash.R
import com.atocash.adapter.*
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.response.*
import com.atocash.utils.AppHelper
import com.atocash.utils.CustomTextWatcher
import com.atocash.utils.DataStorage
import com.atocash.utils.Keys
import com.atocash.utils.extensions.markRequired
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.showShortToast
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExpenseReimburseInitDialog(
    private val _context: Context,
    private val choiceDialogCallback: ExpenseReimburseInitCallback
) :
    Dialog(_context, R.style.CustomDialogAnim), View.OnClickListener {

    private val nextBtn: AppCompatButton
    private val logoutContentTv: TextView

    private val projectSwitch: SwitchMaterial

    private val expenseTitleLayout: TextInputLayout
    private val expenseTitleEd: TextInputEditText

    private val edBusinessType: AutoCompleteTextView
    private val lytBusinessType: TextInputLayout
    private val edBusinessUnit: AutoCompleteTextView
    private val edLocation: AutoCompleteTextView
    private val lytBusinessUnit: TextInputLayout
    private val lytLocation: TextInputLayout

    private val currencyLayout: TextInputLayout
    private val currencyEd: AppCompatAutoCompleteTextView

    private val projectLayout: TextInputLayout
    private val projectEd: AppCompatAutoCompleteTextView

    private val subProjectLayout: TextInputLayout
    private val subProjectEd: AppCompatAutoCompleteTextView

    private val taskLayout: TextInputLayout
    private val taskEd: AppCompatAutoCompleteTextView

    private var currencyItem: CurrencyDropDownResponse? = null
    private var currencyTypeId = 0
    private var projectId: Int? = null
    private var subProjectId: Int? = null
    private var workTaskId: Int? = null

    data class ExpenseReimburseInitData(
        var expenseTitle: String? = null,
        var businessUnitId: Int? = null,
        var businessTypeId: Int? = null,
        var location: String? = null,
        var currencyId: Int? = 0,
        var currency: String? = null,
        var projectId: Int? = null,
        var projectName: String? = null,
        var subProjectId: Int? = null,
        var subProjectName: String? = null,
        var workTaskId: Int? = null,
        var workTaskName: String? = null,
        var isProject: Boolean = false,
        var isDepartment: Boolean = false,
        var isBusiness: Boolean = false,
        var businessType: String? = null,
    )

    private var isProjectAvail = false
    private var isSubProjectAvail = false
    private var isWorkTaskAvail = false

    private var forProject = false
    private var forDepartment = false
    private var forBusinessArea = false

    interface ExpenseReimburseInitCallback {
        fun onNext(expenseReimburseInitData: ExpenseReimburseInitData)
    }

    init {
        val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_expense_reimburse_init, null)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (window != null) window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        nextBtn = view.findViewById(R.id.next_btn)
        logoutContentTv = view.findViewById(R.id.logoutContent)

        projectSwitch = view.findViewById(R.id.project_switch)
        expenseTitleLayout = view.findViewById(R.id.expense_title_layout)
        expenseTitleEd = view.findViewById(R.id.expense_title_ed)
        currencyLayout = view.findViewById(R.id.currency_layout)
        currencyEd = view.findViewById(R.id.currency_ed)
        projectLayout = view.findViewById(R.id.project_layout)
        projectEd = view.findViewById(R.id.project_ed)
        subProjectLayout = view.findViewById(R.id.sub_project_layout)
        subProjectEd = view.findViewById(R.id.sub_project_ed)
        taskLayout = view.findViewById(R.id.task_layout)
        taskEd = view.findViewById(R.id.task_ed)
        edBusinessType = view.findViewById(R.id.edBusinessType)
        edBusinessUnit = view.findViewById(R.id.edBusinessUnit)
        lytBusinessType = view.findViewById(R.id.lytBusinessType)
        lytBusinessUnit = view.findViewById(R.id.lytBusinessUnit)
        lytLocation = view.findViewById(R.id.lytLocation)
        edLocation = view.findViewById(R.id.edLocation)

        setUiListeners()

        setContentView(view)
        setCancelable(true)

        setExpenseFor(isProject = false)
        setRequiredFields()
    }

    private fun setRequiredFields() {
        expenseTitleLayout.markRequired()
        currencyLayout.markRequired()
        lytBusinessType.markRequired()
        lytBusinessUnit.markRequired()
        lytLocation.markRequired()
        projectLayout.markRequired()
        subProjectLayout.markRequired()
        taskLayout.markRequired()
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (p0.id) {

            }
        }
    }

    private fun setExpenseFor(
        isProject: Boolean = false,
        isDepartment: Boolean = false,
        isBusiness: Boolean = false
    ) {
        when {
            isBusiness -> {
                forBusinessArea = true
                forProject = false
                forDepartment = false
            }
            isDepartment -> {
                forBusinessArea = false
                forProject = false
                forDepartment = true
            }
            isProject -> {
                forBusinessArea = false
                forProject = true
                forDepartment = false
            }
        }
    }

    private fun updateUi() {
        currencyEd.background = ContextCompat.getDrawable(context, R.drawable.disabled_ed)
        printLog("Currency ${currencyItem?.currencyCode}")
        currencyEd.setText(currencyItem?.currencyCode)

        expenseTitleEd.setText(title)

        projectId?.let {
            projectSwitch.isChecked = true
            loadProjects()
        } ?: run {
            loadBusinessTypes()

        }
    }

    private fun isDataAvailable(): Boolean {
        if (!forProject) {
            if (businessTypeId == null || businessTypeId == -1) {
                lytBusinessType.error = context.getString(R.string.choose_business_type)
                return false
            }

            if (businessUnitId == null || businessUnitId == -1) {
                lytBusinessUnit.error = context.getString(R.string.choose_business_unit)
                return false
            }

            if (edLocation.text.toString().trim().isEmpty()) {
                lytLocation.error = context.getString(R.string.choose_location)
                return false
            }

            if (expenseTitleEd.text.toString().trim().isEmpty()) {
                expenseTitleLayout.error = context.getString(R.string.err_enter_expense_title)
                return false
            }
        } else {
            if (expenseTitleEd.text.toString().trim().isEmpty()) {
                expenseTitleLayout.error = context.getString(R.string.err_enter_expense_title)
                return false
            }

            if (projectEd.text.toString().isEmpty()) {
                projectLayout.error = context.getString(R.string.err_choose_project)
                return false
            }

            if (subProjectEd.text.toString().isEmpty()) {
                subProjectLayout.error = context.getString(R.string.err_choose_sub_project)
                return false
            }

            if (taskEd.text.toString().isEmpty()) {
                taskLayout.error = context.getString(R.string.err_choose_task)
                return false
            }
        }
        return true
    }

    private fun setUiListeners() {
        projectSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                projectLayout.visibility = View.VISIBLE
                loadProjects()

                lytBusinessType.visibility = View.GONE
                lytBusinessUnit.visibility = View.GONE
                lytLocation.visibility = View.GONE

                forProject = true
            } else {
                forProject = false

                projectLayout.visibility = View.GONE
                subProjectLayout.visibility = View.GONE
                taskLayout.visibility = View.GONE

                lytBusinessType.visibility = View.VISIBLE
                loadBusinessTypes()
            }
        }

        nextBtn.setOnClickListener {
            if (isDataAvailable()) {

                val expenseReimburseInitData = if (forProject) {
                    ExpenseReimburseInitData(
                        expenseTitle = expenseTitleEd.text.toString().trim(),
                        currencyId = currencyItem?.id,
                        currency = currencyItem?.currencyCode,
                        projectId = projectId,
                        projectName = projectEd.text.toString().trim().ifEmpty { null },
                        subProjectId = subProjectId,
                        subProjectName = subProjectEd.text.toString().trim().ifEmpty { null },
                        workTaskId = workTaskId,
                        workTaskName = taskEd.text.toString().trim().ifEmpty { null },
                        isProject = forProject,
                        isBusiness = forBusinessArea,
                        isDepartment = forDepartment
                    )
                } else {
                    ExpenseReimburseInitData(
                        expenseTitle = expenseTitleEd.text.toString().trim(),
                        currencyId = currencyItem?.id,
                        currency = currencyItem?.currencyCode,
                        projectId = null,
                        projectName = null,
                        subProjectId = null,
                        subProjectName = null,
                        workTaskId = null,
                        workTaskName = null,
                        isProject = forProject,
                        isBusiness = forBusinessArea,
                        isDepartment = forDepartment,
                        businessTypeId = businessTypeId,
                        businessUnitId = businessUnitId,
                        location = location,
                        businessType = businessType.businessTypeName.toString()
                    )
                }

                dismiss()

                printLog("expense init data ${Gson().toJson(expenseReimburseInitData)}")

                choiceDialogCallback.onNext(expenseReimburseInitData)
            }
        }

        expenseTitleEd.addTextChangedListener(
            object : CustomTextWatcher() {
                override fun onTextChanged(text: String) {
                    if (text.isNotEmpty()) expenseTitleLayout.error = ""
                }
            })

        edBusinessType.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty()) lytBusinessType.error = ""
            }
        })

        edBusinessUnit.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) lytBusinessUnit.error = ""
            }
        })

        projectEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) projectLayout.error = ""
            }
        })

        subProjectEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) subProjectLayout.error = ""
            }
        })

        taskEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) taskLayout.error = ""
            }
        })
    }

    private fun loadBusinessTypes() {
        showLoading()
        RetrofitClient.instance.apiService.getBusinessTypesForDropDown(token).enqueue(
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

    private fun loadProjects() {
        showLoading()
        val empId = dataStorage?.getString(Keys.UserData.ID).toString().toInt()
        RetrofitClient.instance.apiService.getProjectsAssignedForEmployee(token, empId)
            .enqueue(object : Callback<ArrayList<AssignedProjectsResponse>> {
                override fun onResponse(
                    call: Call<ArrayList<AssignedProjectsResponse>>,
                    response: Response<ArrayList<AssignedProjectsResponse>>
                ) {
                    cancelLoading()
                    response.body()?.let { setProjectsAdapter(it) }
                }

                override fun onFailure(
                    call: Call<ArrayList<AssignedProjectsResponse>>,
                    t: Throwable
                ) {
                    cancelLoading()
                }

            })
    }

    var businessType = BusinessTypesForDropDownResponseItem()
    var businessTypeId: Int? = -1
    private fun setBusinessTypesAdapter(it: ArrayList<BusinessTypesForDropDownResponseItem>) {
        if (it.isNotEmpty()) {
            val adapter = BusinessTypesDropDownAdapter(
                context,
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
            businessTypeId = -1
            context.showShortToast(context.getString(R.string.no_business_types_avaialble))
        }
    }

    private fun loadBusinessUnits() {
        showLoading()

        val jsonObject = JsonObject()
        jsonObject.addProperty("empId", dataStorage?.getString(Keys.UserData.ID).toString())
        jsonObject.addProperty("businessTypeId", businessTypeId)

        RetrofitClient.instance.apiService.getBusinessUnitsForDropDown(
            token, jsonObject
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
                context,
                R.layout.item_status_spinner_view,
                it
            )
            lytBusinessUnit.visibility = View.VISIBLE
            edBusinessUnit.setAdapter(adapter)
            edBusinessUnit.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    businessUnit = it[position]
                    businessUnitId = businessUnit.id
                    edBusinessUnit.setText(it[position].businessUnitName, false)
                    adapter.filter.filter(null)
                    loadBusinessDetails(businessUnitId)
                }
        } else {
            edBusinessUnit.setText("")
            businessUnit = BusinessUnitsListResponseItem()
            businessUnitId = -1
            businessUnitDetails = null
            edLocation.setText("")
            lytLocation.visibility = View.VISIBLE
            val adapter = BusinessUnitsDropDownAdapter(
                context,
                R.layout.item_status_spinner_view,
                arrayListOf()
            )
            lytBusinessUnit.visibility = View.VISIBLE
            edBusinessUnit.setAdapter(adapter)
            context.showShortToast(context.getString(R.string.no_business_units_avaialble))
        }
    }

    private fun loadBusinessDetails(businessUnitId: Int?) {
        businessUnitId?.let {
            showLoading()
            RetrofitClient.instance.apiService.getBusinessUnitsDetails(token, it).enqueue(
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
    var location: String = ""
    private fun setBusinessDetails(details: BusinessUnitDetails) {
        businessUnitDetails = details
        location = details.location.toString()
        edLocation.setText(details.location.toString())
        lytLocation.visibility = View.VISIBLE
    }

    var projectsResponse = AssignedProjectsResponse()
    private fun setProjectsAdapter(body: ArrayList<AssignedProjectsResponse>) {
        if (!body.isNullOrEmpty()) {
            isProjectAvail = true
            val adapter =
                AssignedProjectsDropDownAdapter(context, R.layout.item_status_spinner_view, body)
            projectEd.setAdapter(adapter)
            projectEd.onItemClickListener =
                AdapterView.OnItemClickListener { parent, arg1, position, id ->
                    projectsResponse = body[position]
                    projectId = projectsResponse.id
                    projectEd.setText(body[position].projectName, false)
                    adapter.filter.filter(null)
                    loadSubProjectsForDropDown()
                }
        } else {
            context.showShortToast(context.getString(R.string.no_projects_avail))
        }
    }

    private fun loadSubProjectsForDropDown() {
        AppHelper.hideKeyboard(context, expenseTitleEd)
        showLoading()
        printLog("Project id : $projectId")
        RetrofitClient.instance.apiService.getSubProjectsForProjects(
            token,
            projectId.toString().toInt()
        )
            .enqueue(object : Callback<ArrayList<SubProjectsResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<SubProjectsResponse>>,
                    t: Throwable
                ) {
                    cancelLoading()
                }

                override fun onResponse(
                    call: Call<ArrayList<SubProjectsResponse>>,
                    response: Response<ArrayList<SubProjectsResponse>>
                ) {
                    cancelLoading()
                    response.body()?.let {
                        if (it.isNullOrEmpty().not()) {
                            isSubProjectAvail = true
                            subProjectLayout.visibility = View.VISIBLE
                            setSubProjectAdapter(it)
                        }
                    }
                }
            })
    }

    private var subProjectsResponse = SubProjectsResponse()
    private fun setSubProjectAdapter(it: ArrayList<SubProjectsResponse>) {
        val adapter = SubProjectsDropDownAdapter(context, R.layout.item_status_spinner_view, it)
        subProjectEd.setAdapter(adapter)
        subProjectEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                subProjectId = it[position].id
                subProjectsResponse = it[position]
                subProjectEd.setText(it[position].subProjectName, false)
                adapter.filter.filter(null)
                loadTasks()
            }
    }

    private fun loadTasks() {
        showLoading()
        RetrofitClient.instance.apiService.getWorkTasksForSubProject(token, subProjectsResponse.id)
            .enqueue(object : Callback<ArrayList<TasksResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<TasksResponse>>,
                    t: Throwable
                ) {
                    cancelLoading()
                }

                override fun onResponse(
                    call: Call<ArrayList<TasksResponse>>,
                    response: Response<ArrayList<TasksResponse>>
                ) {
                    cancelLoading()
                    if (response.isSuccessful) {
                        response.body()?.let {
                            isWorkTaskAvail = true
                            taskLayout.visibility = View.VISIBLE
                            setTaskAdapter(it)
                        }
                    }
                }
            })
    }

    private var taskResponse = TasksResponse()
    private fun setTaskAdapter(it: ArrayList<TasksResponse>) {
        val adapter = TasksDropDownAdapter(context, R.layout.item_status_spinner_view, it)
        taskEd.setAdapter(adapter)
        taskEd.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                workTaskId = it[position].id
                taskResponse = it[position]
                taskEd.setText(it[position].taskName, false)
                adapter.filter.filter(null)
            }
    }

    private var dataStorage: DataStorage? = null
    private var token = ""

    private var project: String = ""
    private var subProject: String = ""
    private var workTask: String = ""
    private var title: String = ""

    private var isEdit = false

    fun setItemsAndShow(
        title: String,
        isEdit: Boolean,
        token: String,
        dataStorage: DataStorage,
        currencyItem: CurrencyDropDownResponse,
        projectId: Int?,
        project: String,
        subProjectId: Int?,
        subProject: String,
        workTaskId: Int?,
        workTask: String,
        businessTypeId: Int?,
        businessUnitId: Int?,
        location: String,
        businessType: String?
    ) {
        printLog("Title $title")
        this.title = title
        this.isEdit = isEdit

        this.currencyItem = currencyItem
        this.token = token
        this.dataStorage = dataStorage

        this.projectId = projectId
        this.project = project
        this.subProjectId = subProjectId
        this.subProject = subProject
        this.workTaskId = workTaskId
        this.workTask = workTask

        projectId?.let {
            forProject = true
            isProjectAvail = true
            projectEd.setText(project)
            projectLayout.visibility = View.VISIBLE
        }

        subProjectId?.let {
            isSubProjectAvail = true
            subProjectEd.setText(subProject)
            subProjectLayout.visibility = View.VISIBLE
        }

        workTaskId?.let {
            isWorkTaskAvail = true
            taskEd.setText(workTask)
            taskLayout.visibility = View.VISIBLE
        }

        this.businessUnitId = businessUnitId
        this.businessTypeId = businessTypeId
        this.location = location

        businessUnitId?.let {
            forProject = false
            edBusinessType.setText(businessType.toString())
        }

        printLog("Currency ${currencyItem.currencyCode}")
        printLog("Currency ID ${currencyItem.id}")

        updateUi()

        currencyTypeId = currencyItem.id

        show()
    }

    private var mProgressDialog: ProgressDialog? = null

    fun showLoading(msg: String) {
        cancelLoading()
        mProgressDialog = AppHelper.showLoadingDialog(context, msg)
    }

    fun showLoading() {
        cancelLoading()
        mProgressDialog = AppHelper.showLoadingDialog(context, context.getString(R.string.loading))
    }

    fun cancelLoading() {
        mProgressDialog?.let {
            if (it.isShowing) it.cancel()
        }
    }
}
