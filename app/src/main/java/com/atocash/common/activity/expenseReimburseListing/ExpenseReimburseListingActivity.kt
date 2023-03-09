package com.atocash.common.activity.expenseReimburseListing

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.base.view.SuperCompatActivity
import com.atocash.common.activity.expenseReimburse.ManageExpenseReimburseActivity
import com.atocash.common.activity.expenseReimburseListing.adapter.ExpenseReimburseListingAdapter
import com.atocash.common.activity.expenseReimburseListing.adapter.ExpenseReimburseSubmitAdapter
import com.atocash.database.AtoCashDB
import com.atocash.database.DatabaseHelperImpl
import com.atocash.databinding.ActivityExpenseReimburseListingBinding
import com.atocash.dialog.ExpenseReimburseInitDialog
import com.atocash.dialog.ExpenseReimburseSubmitConfirmationDialog
import com.atocash.dialog.ExpenseSubClaimDetailsDialog
import com.atocash.network.response.*
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.*
import com.google.gson.Gson
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class ExpenseReimburseListingActivity :
    SuperCompatActivity<ActivityExpenseReimburseListingBinding, ExpenseReimburseListingViewModel>(),
    ExpenseReimburseListingNavigator {

    private lateinit var binding: ActivityExpenseReimburseListingBinding
    private lateinit var viewModel: ExpenseReimburseListingViewModel

    private lateinit var adapter: ExpenseReimburseListingAdapter

    private var expenseInitData: ExpenseReimburseInitDialog.ExpenseReimburseInitData? = null
    private var expenseEditData: ExpenseRaisedForEmployeeResponse? = null

    private var isEdit = false
    private var isCopy = false
    private var isView = false

    private val dbHelper by lazy {
        DatabaseHelperImpl(AtoCashDB.getDatabaseClient(this).appDao())
    }

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
        return R.layout.activity_expense_reimburse_listing
    }

    override fun getViewModel(): ExpenseReimburseListingViewModel {
        viewModel = ViewModelProvider(this).get(ExpenseReimburseListingViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        binding.toolTv.text = getString(R.string.expense_reimburse_details)
        binding.backIv.setOnClickListener {
            onBackPressed()
        }

        val bundle = intent.extras
        bundle?.let {
            isEdit = it.getBoolean(Keys.IntentData.IS_EDIT, false)
            isCopy = it.getBoolean(Keys.IntentData.IS_COPY, false)
            isView = it.getBoolean(Keys.IntentData.IS_VIEW, false)

            Log.e("Thulasi", "isEdit $isEdit")
            Log.e("Thulasi", "isCopy $isCopy")
            Log.e("Thulasi", "isView $isView")

            val itemStr = it.getString("ExpenseReimburseInitData")
            expenseInitData = Gson().fromJson(
                itemStr,
                ExpenseReimburseInitDialog.ExpenseReimburseInitData::class.java
            )
            expenseInitData?.let {
                updateUi()
                binding.claimAmountTv.text = "0"
            }

            if (isView) {
                val viewDataStr = it.getString("InboxExpenseReimburseResponse")
                val viewData = Gson().fromJson(
                    viewDataStr,
                    InboxExpenseReimburseResponse::class.java
                )
                binding.updateBtn.visibility = View.VISIBLE
                binding.updateBtn.setOnClickListener { }
                updateUiForView(viewData)
            } else if (isEdit || isCopy) {
                binding.updateBtn.visibility = View.INVISIBLE
                binding.updateBtn.setOnClickListener { }
                Log.e("Thulasi", "isEdit or isCopy")
                val editItemStr = it.getString("ExpenseReimburseInitData").toString()
                if (editItemStr.isEmpty().not()) {
                    expenseEditData = Gson().fromJson(
                        editItemStr,
                        ExpenseRaisedForEmployeeResponse::class.java
                    )
                    if (expenseEditData != null) {
                        updatePendingEditItem(expenseEditData)
                    }
                }
            }
        }

        adapter = ExpenseReimburseListingAdapter(isView,
            ArrayList(),
            false,
            object : ExpenseReimburseListingAdapter.EmployeeExpenseCallback {
                override fun onClick(
                    item: ExpenseReimburseListingResponse,
                    action: ExpenseReimburseListingAdapter.ExpenseReimburseListAction
                ) {
                    onListingAction(item, action)
                }
            })
        binding.listingRv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.listingRv.adapter = adapter

        viewModel.savedId.observe(this) {
            onSaveCompleted()
        }

        viewModel.updatedId.observe(this) {
            onUpdateCompleted()
        }
    }

    private fun updatePendingEditItem(editItem: ExpenseRaisedForEmployeeResponse?) {
        editItem?.let {
            binding.claimAmountTv.text = it.totalClaimAmount.toString()
            binding.typeTv.text = getString(R.string.expense_reimburse)

            expenseInitData?.expenseTitle = editItem.expenseReportTitle

            getExpenseSubClaimForId(editItem.id)
        }
    }

    private fun getExpenseSubClaimForId(id: Int?) {
        if (isNetworkConnected) {
            showLoading()
            viewModel.getExpenseSubClaims(getToken(), id).observe(this, Observer {
                cancelLoading()
                when (it.code()) {
                    200, 201 -> {
                        it.body()?.let { items ->
//                            setSubclaimsList(items)
                            setClaimsAdapterForViewPurpose(items)
                        }
                    }
                    401 -> showUnAuthDialog()
                    409 -> showErrorResponse(it.errorBody()?.string())
                    else -> {
                        showErrorResponse(it.errorBody()?.string())
                        finish()
                    }
                }
            })
        } else {
            showFailureToast(getString(R.string.check_internet))
        }
    }

    private fun updateUiForView(viewData: InboxExpenseReimburseResponse?) {
        viewData?.let {
            binding.headerBtnContainer.visibility = View.GONE
            binding.btnGrp.visibility = View.GONE
            binding.claimAmountTv.text = it.totalClaimAmount.toString()
            binding.typeTv.text = getString(R.string.expense_reimburse)

            getExpenseSubClaim(viewData)
        }
    }

    private fun getExpenseSubClaim(viewData: InboxExpenseReimburseResponse) {
        if (isNetworkConnected) {
            showLoading()
            viewModel.getExpenseSubClaims(getToken(), viewData.id).observe(this, Observer {
                cancelLoading()
                when (it.code()) {
                    200, 201 -> {
                        it.body()?.let { items ->
                            setClaimsAdapterForViewPurpose(items)
                        }
                    }
                    401 -> showUnAuthDialog()
                    409 -> showErrorResponse(it.errorBody()?.string())
                    else -> {
                        showErrorResponse(it.errorBody()?.string())
                        finish()
                    }
                }
            })
        } else {
            showFailureToast(getString(R.string.check_internet))
        }
    }

    private fun setClaimsAdapterForViewPurpose(items: ArrayList<ExpenseReimburseListingResponse>) {
        for (item in items) {
            item.documentIDs = ""
        }
        adapter.clearItems()
        adapter.addAll(items)
    }

    private fun getAndShowDetailsForView(item: ExpenseReimburseListingResponse) {
        if (isNetworkConnected) {
            viewModel.getExpenseSubClaimDetails(getToken(), item).observe(this, Observer {
                cancelLoading()
                when (it.code()) {
                    200, 201 -> {
                        it.body()?.let { items ->
                            showExpenseSubClaimDetails(item, items)
                        }
                    }
                    401 -> showUnAuthDialog()
                    409 -> showErrorResponse(it.errorBody()?.string())
                    else -> {
                        showErrorResponse(it.errorBody()?.string())
                        finish()
                    }
                }
            })
        } else {
            showFailureToast(getString(R.string.check_internet))
        }
    }

    private fun showExpenseSubClaimDetails(
        item: ExpenseReimburseListingResponse,
        items: ArrayList<String>
    ) {
        ExpenseSubClaimDetailsDialog(item, items).show(supportFragmentManager, "Details")
    }

    private fun onListingAction(
        item: ExpenseReimburseListingResponse,
        action: ExpenseReimburseListingAdapter.ExpenseReimburseListAction
    ) {
        when (action) {
            ExpenseReimburseListingAdapter.ExpenseReimburseListAction.COPY -> {
                copyItem(item)
            }
            ExpenseReimburseListingAdapter.ExpenseReimburseListAction.DELETE -> {
                deleteItem(item)
            }
            ExpenseReimburseListingAdapter.ExpenseReimburseListAction.EDIT -> {
                editItem(item)
            }
            ExpenseReimburseListingAdapter.ExpenseReimburseListAction.VIEW -> {
                showExpenseDetailsDialog(item)
            }
        }
    }

    private var editItem: ExpenseReimburseListingResponse? = null
    private fun editItem(item: ExpenseReimburseListingResponse) {
        editItem = item
        Log.e("Thulasi", "edit item ${Gson().toJson(item)}")
        val bundle = Bundle()
        bundle.putString("CopyItem", Gson().toJson(item))
        bundle.putBoolean("isCopy", false)
        bundle.putBoolean("isEdit", true)
        intentHelper.startActivityForResult(
            this,
            ManageExpenseReimburseActivity::class.java,
            bundle,
            100
        )
    }

    private fun deleteItem(item: ExpenseReimburseListingResponse) {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setMessage("Are you sure you want to delete this claim?")
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "Yes"
        ) { dialog_, _ ->
            dialog_?.dismiss()

            adapter.remove(item)
            calculateTotalAmount()
        }
        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            "No"
        ) { dialog_, _ ->
            dialog_?.dismiss()
        }
        dialog.show()
    }

    private fun copyItem(item: ExpenseReimburseListingResponse) {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setMessage("You have to add documents for the copied claim manually. Do you want to proceed?")
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "Yes"
        ) { dialog_, _ ->
            dialog_?.dismiss()
            proceedToCopyItem(item)
        }
        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            "No"
        ) { dialog_, _ ->
            dialog_?.dismiss()
        }
        dialog.show()
    }

    private fun proceedToCopyItem(item: ExpenseReimburseListingResponse) {
        Log.e("Thulasi", "ExpenseReimburse ${Gson().toJson(item)}")
        val tempItem = item.clone()
        adapter.add(tempItem)

        val bundle = Bundle()
        bundle.putString("CopyItem", Gson().toJson(tempItem))
        bundle.putBoolean("isCopy", true)
        intentHelper.startActivityForResult(
            this,
            ManageExpenseReimburseActivity::class.java,
            bundle,
            100
        )
    }

    private fun showExpenseDetailsDialog(item: ExpenseReimburseListingResponse) {
        getAndShowDetailsForView(item)
    }

    override fun onSave() {
        if (isEdit) {
            updateExistingRecord()
        } else {
            createNewRecord()
        }
    }

    private fun updateExistingRecord() {
        val response = ExpenseRaisedForEmployeeResponse()
        expenseEditData?.let {
            response.expenseReportTitle = it.expenseReportTitle
            response.currencyTypeId = it.currencyTypeId
            response.currency = it.currency
            response.departmentName =
                if (it.projectName.isNullOrEmpty()) "Department" else "Project"

            it.projectId?.let { projectId_ -> response.projectId = projectId_ }
            it.subProjectId?.let { subProjectId_ -> response.subProjectId = subProjectId_ }
            it.workTaskId?.let { workTaskId_ -> response.workTaskId = workTaskId_ }

            it.projectName?.let { project_ -> response.projectName = project_ }
            it.subProjectName.let { subProject_ -> response.subProjectName = subProject_ }
            it.workTaskName.let { workTask_ -> response.workTaskName = workTask_ }
        }
        response.employeeId =
            dataStorage.getString(Keys.UserData.ID).toString().toInt()
        response.expensesSubClaims = adapter.getItems()

        response.approvalStatusType = "Pending"
        response.showEditDelete = true
        response.totalClaimAmount = binding.claimAmountTv.text.toString().trim().toFloat()

        viewModel.updateRecord(dbHelper, response)
    }

    private fun createNewRecord() {
        val response = ExpenseRaisedForEmployeeResponse()
        expenseInitData?.let {
            response.expenseReportTitle = it.expenseTitle.toString()
            response.currencyTypeId = it.currencyId
            response.currency = it.currency.toString()
            response.departmentName = if (it.isDepartment) "Department"
            else if (it.isProject) "Project"
            else "Business Area"

            it.projectId?.let { projectId_ -> response.projectId = projectId_ }
            it.subProjectId?.let { subProjectId_ -> response.subProjectId = subProjectId_ }
            it.workTaskId?.let { workTaskId_ -> response.workTaskId = workTaskId_ }

            it.projectName?.let { project_ -> response.projectName = project_ }
            it.subProjectName?.let { subProject_ -> response.subProjectName = subProject_ }
            it.workTaskName?.let { workTask_ -> response.workTaskName = workTask_ }
        }
        response.employeeId =
            dataStorage.getString(Keys.UserData.ID).toString().toInt()
        response.expensesSubClaims = adapter.getItems()

        response.approvalStatusType = "Pending"
        response.showEditDelete = true
        response.totalClaimAmount = binding.claimAmountTv.text.toString().trim().toFloat()

        viewModel.saveToLocalDb(dbHelper, response)
    }

    private var totalClaimAmount: String = ""
    private var expenseOrDept: String = ""
    private var itemsInSubmitDialog: ArrayList<ExpenseReimburseSubmitAdapter.ExpenseReimburseSubmitListItems> =
        ArrayList()

    override fun onSubmit() {
        val claimItems = adapter.getItems()
        if (claimItems.isEmpty()) {
            showShortToast("Add claims before submitting!")
            return
        }

        for (item in claimItems) {
            if (item.documentIDs.isNullOrEmpty()) {
                showShortToast("Add documents to all claims before submitting!")
                return
            }
        }

        itemsInSubmitDialog.clear()
        for (item in claimItems) {
            val amountWithTax = item.taxAmount?.let { item.expenseReimbClaimAmount?.plus(it) }
            this.itemsInSubmitDialog.add(
                ExpenseReimburseSubmitAdapter.ExpenseReimburseSubmitListItems(
                    item.expenseType.toString(), amountWithTax.toString()
                )
            )
        }

        ExpenseReimburseSubmitConfirmationDialog(
            this,
            object : ExpenseReimburseSubmitConfirmationDialog.ExpReimSubmitCallback {
                override fun onNext() {
                    callApiToSubmit()
                }
            }).setItemsAndShow(totalClaimAmount, expenseOrDept, itemsInSubmitDialog)
    }

    private fun callApiToSubmit() {
        if (AppHelper.isNetworkConnected(this)) {
            showLoading()
            viewModel.submitExpenseReimburse(getToken(), getJsonData()).observe(this, Observer {
                cancelLoading()
                when (it.code()) {
                    200, 201 -> {
                        showShortToast("Expense reimbursement added successfully!")
                        finish()
                    }
                    401 -> showUnAuthDialog()
                    409 -> showErrorResponse(it.errorBody()?.string())
                    else -> {
                        showErrorResponse(it.errorBody()?.string())
                        finish()
                    }
                }
            })
        } else {
            showSnack(getString(R.string.check_internet))
        }
    }

    private fun getJsonData(): ExpenseReimburseRequestDto {
        val expenseClaimDto = ExpenseReimburseRequestDto()
        expenseClaimDto.employeeId = getUserId()

        expenseClaimDto.businessTypeId = expenseInitData?.businessTypeId
        expenseClaimDto.businessUnitId = expenseInitData?.businessUnitId
        expenseClaimDto.currencyTypeId = expenseInitData?.currencyId

        expenseClaimDto.expenseReportTitle = expenseInitData?.expenseTitle
        expenseClaimDto.projectId =
            if (expenseInitData?.projectId != null) expenseInitData?.projectId.toString()
                .toInt() else null
        expenseClaimDto.subProjectId =
            if (expenseInitData?.subProjectId != null) expenseInitData?.subProjectId.toString()
                .toInt() else null
        expenseClaimDto.workTaskId =
            if (expenseInitData?.workTaskId != null) expenseInitData?.workTaskId.toString()
                .toInt() else null
        val items = adapter.getItems()
        val subClaimDtoList = arrayListOf<ExpenseSubClaimDto>()
        for (item in items) {
            val subClaimDto = ExpenseSubClaimDto()

            if(expenseClaimDto.businessTypeId == null) {
                expenseClaimDto.businessTypeId = item.businessTypeId
                expenseClaimDto.businessUnitId = item.businessUnitId
                expenseClaimDto.currencyTypeId = item.currencyTypeId
            }

            subClaimDto.invoiceNo = item.invoiceNo
            subClaimDto.invoiceDate = item.invoiceDate
            subClaimDto.location = item.location
            subClaimDto.vendorId = item.vendorId
            subClaimDto.description = item.description
            subClaimDto.tax = item.tax?.roundOffToTwoDecimal()
            subClaimDto.expenseReimbClaimAmount =
                item.expenseReimbClaimAmount?.roundOffToTwoDecimal()
            subClaimDto.taxAmount = item.taxAmount?.roundOffToTwoDecimal()
            subClaimDto.documentIds = item.documentIDs

            subClaimDto.expenseCategoryId = item.expenseCategoryId
            subClaimDto.expenseTypeId = item.expenseTypeId

            subClaimDto.isVAT = item.isVAT
            if (item.expStrtDate.isNullOrEmpty().not()) {
                subClaimDto.expStrtDate = item.expStrtDate
            }
            if (item.expEndDate.isNullOrEmpty().not()) {
                subClaimDto.expEndDate = item.expEndDate
            }
            if (item.taxNo.isNullOrEmpty().not()) {
                subClaimDto.taxNo = item.taxNo
            }
            if (item.expNoOfDays.toString().isEmpty().not()) {
                subClaimDto.expNoOfDays = item.expNoOfDays
            }

            var noOfDaysDate: ArrayList<String>?
            if (item.expStrtDate.isNullOrEmpty().not() &&
                item.expEndDate.isNullOrEmpty().not()
            ) {
                noOfDaysDate = arrayListOf()
                noOfDaysDate.add(item.expStrtDate.toString())
                noOfDaysDate.add(item.expEndDate.toString())
                subClaimDto.NoOfDaysDate = noOfDaysDate
            } else {
                subClaimDto.NoOfDaysDate = null
            }

            subClaimDtoList.add(subClaimDto)
        }
        expenseClaimDto.expenseSubClaims = subClaimDtoList

        return expenseClaimDto
    }

    override fun onUpdate() {
        val dialog = ExpenseReimburseInitDialog(
            this,
            object : ExpenseReimburseInitDialog.ExpenseReimburseInitCallback {
                override fun onNext(expenseReimburseInitData: ExpenseReimburseInitDialog.ExpenseReimburseInitData) {
                    expenseInitData = expenseReimburseInitData
                    updateUi()
                }
            })
        expenseInitData?.let { data ->
            dialog.setItemsAndShow(
                data.expenseTitle.toString(),
                isEdit,
                getToken(),
                dataStorage,
                CurrencyDropDownResponse(
                    id = dataStorage.getInt(Keys.UserData.CURRENCY_ID),
                    currencyCode = dataStorage.getString(Keys.UserData.CURRENCY_CODE).toString()
                ),
                data.projectId,
                data.projectName.toString(),
                data.subProjectId,
                data.subProjectName.toString(),
                data.workTaskId,
                data.workTaskName.toString(),
                data.businessTypeId,
                data.businessUnitId,
                location = data.location.toString(),
                data.businessType.toString()
            )
        }
    }

    private fun updateUi() {
        binding.typeTv.text = getString(R.string.expense_reimburse)
        expenseOrDept = binding.typeTv.text.toString().trim()
    }

    override fun onAddNew() {
        val bundle = Bundle()
        bundle.putBoolean("isCopy", false)
        bundle.putString("expenseFor", expenseOrDept)
        intentHelper.startActivityForResult(
            this,
            ManageExpenseReimburseActivity::class.java,
            bundle,
            100
        )
    }

    override fun onSaveCompleted() {
        showShortToast(getString(R.string.expense_saved_success))
        finish()
    }

    fun onUpdateCompleted() {
        showShortToast(getString(R.string.expense_update_success))
        finish()
    }

    //    private var expenseSubClaimsList = ArrayList<ExpenseReimburseListingResponse>()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                val isCopy = data?.getBooleanExtra("isCopy", false)
                val isEdit = data?.getBooleanExtra("isEdit", false)

//                val fileIds = data?.getStringExtra("ExpenseReimburseListingChosenFiles")
//                val fileIdItem = Gson().fromJson(fileIds, Int::class.java)
                val expenseItemStr = data?.getStringExtra("ExpenseReimburseListingResponse")
                val expenseItem =
                    Gson().fromJson(expenseItemStr, ExpenseReimburseListingResponse::class.java)

                printLog("isEdit $isEdit")
                printLog("isEdit new item ${Gson().toJson(expenseItem)}")

                if (isEdit == true) {
                    printLog("addEditedItem")
                    adapter.addEditedItem(expenseItem, editItem)
                } else {
                    printLog("addAll")
                    adapter.add(expenseItem)
                }
                calculateTotalAmount()
            }
        }
    }

    private fun calculateTotalAmount() {
        var subClaimAmount = 0F
        val expenseSubClaimsList = adapter.getItems()
        if (expenseSubClaimsList.isEmpty().not()) {
            for (items in expenseSubClaimsList) {
                val amountWithTax = items.taxAmount?.let { items.expenseReimbClaimAmount?.plus(it) }
                printLog("SubClaim amount $amountWithTax")
                if (amountWithTax != null) {
                    subClaimAmount += amountWithTax
                }
            }
        }
        printLog("SubClaim amount $subClaimAmount")
        if (subClaimAmount.equals(0F)) {
            binding.claimAmountTv.text = "0.00"
        } else {
            val decimalFormat = DecimalFormat("##.00")
            totalClaimAmount = decimalFormat.format(subClaimAmount)
            binding.claimAmountTv.text = totalClaimAmount
        }
    }
}