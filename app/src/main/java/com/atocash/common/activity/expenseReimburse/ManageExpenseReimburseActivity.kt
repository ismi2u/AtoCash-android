package com.atocash.common.activity.expenseReimburse

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.adapter.ExpenseTypesDropDownAdapter
import com.atocash.adapter.NonProjectExpenseTypesDropDownAdapter
import com.atocash.adapter.PickedDocsAdapter
import com.atocash.adapter.VendorDropDownAdapter
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityManageExpenseReimburseBinding
import com.atocash.network.response.*
import com.atocash.utils.*
import com.atocash.utils.DateUtils.EXPENSE_REIMBURSE_FORMAT
import com.atocash.utils.DateUtils.EXPENSE_REIMBURSE_FORMAT_SS
import com.atocash.utils.DateUtils.HIPHEN_SLASHED_DATE_FORMAT
import com.atocash.utils.Keys.IntentHelper.OPEN_CAMERA
import com.atocash.utils.extensions.markRequired
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.atocash.utils.fileUtils.ImageHelpers
import com.atocash.utils.permissions.Permissions
import com.atocash.utils.permissions.PermissionsHelper
import com.google.gson.Gson
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.activity.FilePickerActivity.MEDIA_FILES
import com.jaiselrahman.filepicker.config.Configurations
import com.jaiselrahman.filepicker.model.MediaFile
import kotlinx.android.synthetic.main.activity_manage_expense_reimburse.*
import kotlinx.android.synthetic.main.dialog_expense_reimburse_init.*
import kotlinx.android.synthetic.main.fragment_emp_advance.*
import kotlinx.android.synthetic.main.item_expense_reimburse.*
import ru.slybeaver.slycalendarview.SlyCalendarDialog
import java.io.File
import java.text.DecimalFormat
import java.util.*


class ManageExpenseReimburseActivity :
    SuperCompatActivity<ActivityManageExpenseReimburseBinding, ManageExpenseReimbursementViewModel>(),
    ManageExpenseReimbursementNavigator {

    private lateinit var binding: ActivityManageExpenseReimburseBinding
    private lateinit var viewModel: ManageExpenseReimbursementViewModel

    private var expenseTypeItem = ExpenseTypeResponse()
    private var expenseSubCategory = ExpenseTypeNonProjectResponse()
    private var isEditMode = false
    private var expenseTypeId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewDataBinding()
        binding.view = this@ManageExpenseReimburseActivity

        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
        setRequiredFields()
        setUiListeners()
    }

    private fun setRequiredFields() {
        binding.apply {
            expenseLayout.markRequired()
            nonProjectExpenseLayout.markRequired()
            invoiceDateLayout.markRequired()
            invoiceNoLayout.markRequired()
            expenseAmtLayout.markRequired()
            taxNoLayout.markRequired()
            taxLayout.markRequired()
            taxAmountLayout.markRequired()
            descLayout.markRequired()
            documentLayout.markRequired()
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_manage_expense_reimburse
    }

    override fun getViewModel(): ManageExpenseReimbursementViewModel {
        viewModel = ViewModelProvider(this)[ManageExpenseReimbursementViewModel::class.java]
        return viewModel
    }

    private var isCopy = false
    private var isEdit = false
    private var tempItem: ExpenseReimburseListingResponse? = null
    private var expenseFor: String = ""

    private fun initViewsAndClicks() {
        binding.expReimburseToolBar.toolTv.text = getString(R.string.manage_expense_reimburse)
        binding.expReimburseToolBar.backIv.setOnClickListener {
            onBackPressed()
        }

        val bundle = intent.extras
        bundle?.let {
            isCopy = it.getBoolean("isCopy")
            isEdit = it.getBoolean("isEdit")
            expenseFor = it.getString("expenseFor").toString()

            if (isEdit || isCopy) {
                val tempItemStr = it.getString("CopyItem")
                tempItem = Gson().fromJson(tempItemStr, ExpenseReimburseListingResponse::class.java)
                updateUiForTemp()
                Log.e("Thulasi", "temp item ${Gson().toJson(tempItem)}")
            }

            binding.apply {
                if (expenseFor.lowercase() != "project") {
                    expenseLayout.hint = getString(R.string.hint_expense_category)
                    nonProjectExpenseLayout.visibility = View.VISIBLE
                } else {
                    expenseLayout.hint = getString(R.string.hint_expense)
                    nonProjectExpenseLayout.visibility = View.GONE
                }
            }
        }

        if (!isCopy && !isEdit) loadExpenseDetails()

        binding.createBtn.text =
            if (isEdit) getString(R.string.btn_update) else getString(R.string.btn_create)

        loadVendors(null)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUiForTemp() {
        tempItem?.let {
            binding.invoiceNoEd.setText(it.invoiceNo)

            invoiceDate = it.invoiceDate.toString()
            binding.invoiceDateEd.setText(DateUtils.getDate(invoiceDate))

            binding.expenseAmtEd.setText(it.expenseReimbClaimAmount.toString())
            it.vendorId?.let { vendorId_ ->
                vendorId = vendorId_.toString()
            }

            binding.taxAmountEd.setText(it.taxAmount.toString())
            binding.descEd.setText(it.description.toString())

            binding.apply {
                it.location?.let { location -> expenseLocationEd.setText(location) }
                it.expNoOfDays?.let { expNoDays -> noOfDaysEd.setText(expNoDays.toString()) }
                it.additionalVendor?.let { additionalVendor ->
                    otherVendorEd.setText(
                        additionalVendor
                    )
                }
                it.taxNo?.let { taxNo -> taxNoEd.setText(taxNo) }
                it.taxAmount?.let { taxAmount -> taxAmountEd.setText(taxAmount.toString()) }
                it.tax?.let { tax -> taxEd.setText(tax.toString()) }
                it.expenseReimbClaimAmount?.let { expenseReimbClaimAmount ->
                    claimAmountEd.setText(
                        expenseReimbClaimAmount.toString()
                    )
                }

                if (it.isVAT == true) {
                    isVatSwitch.isChecked = true
                    getVatData()
                }

                val sDateParsed = it.expStrtDate?.let { date ->
                    DateUtils.parseIsoDate(date, format = EXPENSE_REIMBURSE_FORMAT_SS)
                } ?: ""
                val eDateParsed = it.expEndDate?.let { date ->
                    DateUtils.parseIsoDate(date, format = EXPENSE_REIMBURSE_FORMAT_SS)
                } ?: ""
                if (sDateParsed.isNotEmpty() && eDateParsed.isNotEmpty()) {
                    startDate = it.expStrtDate.toString()
                    endDate = it.expEndDate.toString()

                    startDateEndDateEd.setText("$sDateParsed -> $eDateParsed")
                } else {
                    startDate = null
                    endDate = null
                }

                Log.e("Thulasi", "Start date $startDate")
                Log.e("Thulasi", "End date $endDate")
            }

            loadVendors(VendorDropDownItem(id = it.vendorId, vendorName = it.vendor.toString()))
            loadExpenseDetails()
        }
    }

    private fun setUiListeners() {
        binding.taxEd.setText("0")
        binding.taxAmountEd.background = ContextCompat.getDrawable(this, R.drawable.disabled_ed)

        binding.invoiceDateEd.setOnClickListener {
            AppHelper.hideKeyboard(this@ManageExpenseReimburseActivity)
            showInvoiceDatePicker()
        }

        binding.documentEd.setOnClickListener {
            binding.documentLayout.error = ""
            checkPermissionAndOpenPickerDialog()
        }

        docsAdapter = PickedDocsAdapter(false, object : PickedDocsAdapter.PickedDocsCallback {
            override fun onDelete(item: PostDocumentsResponse) {
                removeDocument(item)
            }

            override fun onView(item: PostDocumentsResponse) {

            }
        })
        binding.docsRv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.docsRv.adapter = docsAdapter

        binding.nonProjectExpenseEd.addTextChangedListener {
            if (!it.isNullOrEmpty()) binding.nonProjectExpenseLayout.error = ""
        }

        binding.expenseEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.expenseLayout.error = ""
                }
            }
        })

        binding.invoiceNoEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.invoiceNoLayout.error = ""
                }
            }
        })

        binding.invoiceDateEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.invoiceDateLayout.error = ""
                }
            }
        })

        binding.expenseAmtEd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.isEmpty().not()) {
                        calculateTaxAmt()
                    }
                }
            }
        })

        binding.expenseAmtEd.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                binding.expenseAmtLayout.error = ""
            }
        }

        binding.taxNoEd.addTextChangedListener {
            if (it.toString().isNotEmpty()) binding.taxNoLayout.error = ""
        }

        binding.taxEd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    if (s.toString() == "0" || s.toString() == "0.0") return

                    if (s.toString().toFloat() < 100) {
                        binding.taxLayout.error = ""
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.isEmpty().not()) {
                        when {
                            it.toString() == "0" || it.toString() == "0.0" -> return
                            s.toString().toFloat() > 100 -> binding.taxLayout.error =
                                getString(R.string.tax_must_be_under_100)
                            else -> calculateTaxAmt()
                        }
                    }
                }
            }
        })

        binding.expenseLocationEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.expenseLocationLayout.error = ""
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

        binding.isVatSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getVatData()
            } else {
                disableTaxFields(0)
            }
        }
    }

    private fun getVatData() {
        if (AppHelper.isNetworkConnected(this)) {
            viewModel.getVatPercentage(getToken()).observe(this) {
                if (it.isSuccessful) {
                    it.body()?.vatPercentage?.let { it1 -> disableTaxFields(it1) }
                } else {
                    enableTaxFields()
                }
            }
        } else {
            showShortToast(getString(R.string.check_internet))
        }
    }

    private fun disableTaxFields(vatPercentage: Int) {
        binding.apply {
            taxEd.isEnabled = false
            taxEd.setText(vatPercentage.toString())
            taxAmountEd.setText("0")
        }
        if (isEdit.not() || isCopy.not()) {
            calculateTaxAmt()
        }
    }

    private fun enableTaxFields() {
        binding.apply {
            taxEd.isEnabled = true
        }
        calculateTaxAmt()
    }


    private fun checkPermissionAndOpenPickerDialog() {
        permissionsHelper.initCameraAndStoragePermissions()
        permissionsHelper.requestPermission(object : PermissionsHelper.PermissionCallback {
            override fun onGranted() {
                showFilePickerDialog()
            }

            override fun onSinglePermissionGranted(grantedPermission: Array<String>?) {

            }

            override fun onDenied() {
                showShortToast(Permissions.CANNOT_OPEN_CAMERA_GALLERY)
            }

            override fun onDeniedCompletely() {
                permissionsHelper.askAndOpenSettings(
                    Permissions.READ_GALLERY_CAMERA_STRING,
                    Permissions.CANNOT_OPEN_CAMERA_GALLERY
                )
            }
        })
    }

    private fun showFilePickerDialog() {
        AppHelper.showImageChooseDialog(
            this,
            object : AppHelper.ImageChooserCallback {
                override fun onChosen(chosenItem: Int) {
                    when (chosenItem) {
                        0 -> startActivityForResult(
                            IntentHelper.getCameraIntent(this@ManageExpenseReimburseActivity),
                            Keys.IntentHelper.OPEN_CAMERA
                        )
                        1 -> showDocumentPicker()
                    }
                }
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissionsHelper.onRequestPermissionsResult(
            requestCode,
            Permissions.camAndStoragePermission,
            grantResults
        )
    }

    private fun calculateTaxAmt() {
        if (binding.taxEd.text.toString().isEmpty() ||
            binding.expenseAmtEd.text.toString().isEmpty()
        ) {
            binding.taxAmountEd.setText("0.00")
            binding.claimAmountEd.setText("0.00")
            return
        }

        val tax = binding.taxEd.text.toString().toFloat()
        val expenseAmtStr = binding.expenseAmtEd.text.toString()
        if (expenseAmtStr != "null") {
            val expenseAmt = expenseAmtStr.toFloat()
            val calculatedAmt: Float = tax * (expenseAmt / 100)

            val decimalFormat = DecimalFormat("0.00")
            val taxableAmt = decimalFormat.format(calculatedAmt)

            val claimAmount = taxableAmt.toFloat() + expenseAmt

            binding.claimAmountEd.setText(claimAmount.toString())
            binding.taxAmountEd.setText(taxableAmt.toString())
        }
    }

    private fun isAllDataAvailable(): Boolean {
        if (binding.expenseEd.text.toString().trim().isEmpty()) {
            binding.expenseLayout.error = getString(R.string.choose_expense_category)
            return false
        }
        if (binding.nonProjectExpenseEd.text.toString().trim().isEmpty()) {
            binding.nonProjectExpenseLayout.error = getString(R.string.choose_expense)
            return false
        }
        if (binding.invoiceNoEd.text.toString().trim().isEmpty()) {
            binding.invoiceNoLayout.error = getString(R.string.enter_invoice_no)
            return false
        }
        if (binding.invoiceDateEd.text.toString().trim().isEmpty()) {
            binding.invoiceDateLayout.error = getString(R.string.pick_invoice_date)
            return false
        }
        if (binding.expenseAmtEd.text.toString().trim().isEmpty()) {
            binding.expenseAmtLayout.error = getString(R.string.enter_expense_amount)
            return false
        }
        if (binding.taxNoEd.text.toString().trim().isEmpty()) {
            binding.taxNoLayout.error = getString(R.string.err_enter_tax_no)
            return false
        }
        if (binding.taxEd.text.toString().trim().isEmpty()) {
            binding.taxLayout.error = getString(R.string.enter_tax)
            return false
        }
        if (binding.descEd.text.toString().trim().isEmpty()) {
            binding.descLayout.error = getString(R.string.enter_desc)
            return false
        }
        if (uploadedDocs.isEmpty()) {
            binding.documentLayout.error = getString(R.string.choose_documents)
            return false
        }
        return true
    }

    private lateinit var docsAdapter: PickedDocsAdapter
    private fun removeDocument(item: PostDocumentsResponse) {
        uploadedDocs.remove(item)
        docsAdapter.removeItem(item)
    }

    private fun showDocumentPicker() {
        val intent = Intent(this, FilePickerActivity::class.java)
        intent.putExtra(
            FilePickerActivity.CONFIGS, Configurations.Builder()
                .setCheckPermission(false)
                .setShowImages(true)
                .enableImageCapture(false)
                .setMaxSelection(10)
                .setSkipZeroSizeFiles(true)
                .build()
        )
        startActivityForResult(intent, FILE_REQUEST_CODE)
    }

    var files: ArrayList<MediaFile> = ArrayList()
    private var imageUri: Uri? = null
    private var imagePath = ""

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                OPEN_CAMERA -> {
                    imageUri = IntentHelper.imageUri
                    if (imageUri != null) onImageReceived(imageUri!!)
                    else showShortToast("Taken image invalid, take another image!")
                }
                FILE_REQUEST_CODE -> {
                    files = data?.getParcelableArrayListExtra(MEDIA_FILES)!!
                    if (files.size > 0) {
                        uploadAndAddFiles(files)
                    }
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun onImageReceived(imageUri: Uri) {
        val invoiceNo = binding.invoiceNoEd.text.toString().trim()
        val thumbnail: Bitmap = ImageHelpers.getBitmapFromUri(this, imageUri)
        ImageHelpers.insertImage(
            null,
            thumbnail,
            invoiceNo,
            "profile image description"
        )
        imagePath = ImageHelpers.getRealPathFromURI(this, imageUri)
        val mediaFile = MediaFile()
        mediaFile.name = File(imagePath).name
        mediaFile.path = imagePath
        val mediaFileList = ArrayList<MediaFile>()
        mediaFileList.add(mediaFile)
        val fileSize = ImageHelpers.convertStorageSize(File(imagePath).length())
        printLog(
            "File size ${
                java.lang.String.format(
                    "%.2f %S",
                    fileSize.value,
                    fileSize.suffix
                )
            }"
        )
        uploadAndAddFiles(mediaFileList)
    }

    private val chosenFiles = ArrayList<Documents>()
    private var uploadedDocs = ArrayList<PostDocumentsResponse>()

    private fun uploadAndAddFiles(files: ArrayList<MediaFile>) {
        if (files.isEmpty()) return
        chosenFiles.clear()
        files.forEachIndexed { index, mediaFile ->
            val uploadedDocument = Documents()
            uploadedDocument.actualFileName = mediaFile.name.toString()
            uploadedDocument.filePath = mediaFile.path.toString()
            chosenFiles.add(uploadedDocument)
        }
        showLoading("Uploading documents...")
        viewModel.postDocuments(getToken(), chosenFiles).observe(this, Observer {
            cancelLoading()
            when (it.code()) {
                200 -> {
                    val response = it.body()
                    response?.let { docRes ->
                        if (docRes.isNotEmpty()) {
                            uploadedDocs.addAll(docRes)
                            docsAdapter.addAll(docRes)
                        }
                    }
                }
                else -> {
                    showErrorResponse(it.errorBody()?.string())
                }
            }
        })
    }

    var invoiceDate = ""
    private fun showInvoiceDatePicker() {
        val dateListener: DatePickerDialog.OnDateSetListener

        val myCalendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        dateListener = DatePickerDialog.OnDateSetListener { _view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            invoiceDate = DateUtils.fromCalendar(myCalendar, EXPENSE_REIMBURSE_FORMAT)
            binding.invoiceDateEd.setText(AppHelper.getDate(myCalendar))
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

    override fun onBack() {
        onBackPressed()
    }

    override fun onAdd() {
        AppHelper.hideKeyboard(this)

        if (isAllDataAvailable()) {
            val claimItem = if (isEdit || isCopy) tempItem else ExpenseReimburseListingResponse()

            if (isEdit || isCopy) {
                claimItem?.businessTypeId = tempItem?.businessTypeId
                claimItem?.businessUnitId = tempItem?.businessUnitId
                claimItem?.currencyTypeId = tempItem?.currencyTypeId
                claimItem?.projectId = tempItem?.projectId
                claimItem?.subProjectId = tempItem?.subProjectId
                claimItem?.workTaskId = tempItem?.projectId
            }

            claimItem?.invoiceDate = invoiceDate
            claimItem?.expenseType = binding.expenseEd.text.toString().trim()
            claimItem?.invoiceNo = binding.invoiceNoEd.text.toString().trim()
            claimItem?.expenseReimbClaimAmount = binding.expenseAmtEd.text.toString().toFloat()
            claimItem?.vendorId = vendorId?.toInt()
            claimItem?.vendor = vendorType.vendorName.toString()
            claimItem?.additionalVendor = binding.otherVendorEd.text.toString().trim()
            claimItem?.tax = binding.taxEd.text.toString().toFloat()
            claimItem?.taxAmount = binding.taxAmountEd.text.toString().toFloat()
            if (binding.expenseLocationEd.text.isNullOrEmpty().not()) {
                claimItem?.location = binding.expenseLocationEd.text.toString().trim()
            }
            claimItem?.description = binding.descEd.text.toString().trim()
            claimItem?.expStrtDate = startDate
            claimItem?.expEndDate = endDate
            if (noOfDays != 0L) {
                claimItem?.expNoOfDays = noOfDays
            }
            claimItem?.isVAT = binding.isVatSwitch.isChecked
            claimItem?.taxNo = binding.taxNoEd.text.toString()

            if (isEdit || isCopy) {
                claimItem?.expenseCategoryId = expenseSubCategory.id
                claimItem?.expenseTypeId = expenseTypeId
            } else {
                claimItem?.expenseCategoryId = expenseTypeId
                claimItem?.expenseTypeId = expenseSubCategory.id
            }

            val fileIds = ArrayList<Int>()
            if (uploadedDocs.isEmpty().not()) {
                for (item in uploadedDocs) {
                    val id = item.id
                    val fileName = item.actualFileName
                    printLog("Id $id FileName $fileName")
                    fileIds.add(item.id.toString().toInt())
                }
            }
            claimItem?.documentIDs = fileIds.joinToString(",")

            val claimItemData = Gson().toJson(claimItem)
            Log.e("Thulasi", "Claim item $claimItemData")

            val intent = Intent()
            intent.putExtra("ExpenseReimburseListingResponse", claimItemData)
            intent.putExtra("isCopy", isCopy)
            intent.putExtra("isEdit", isEdit)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun loadVendors(selectedItem: VendorDropDownItem?) {
        if (AppHelper.isNetworkConnected(this)) {
            viewModel.getVendorsForDropDown(getToken()).observe(this) {
                when (it.code()) {
                    200 -> {
                        it.body()?.let { data ->
                            setVendorAdapter(data, selectedItem)
                        }
                    }
                    401 -> showUnAuthDialog()
                    else -> showErrorResponse(it.errorBody()?.toString())
                }
            }
        }
    }

    var vendorType = VendorDropDownItem()
    var vendorId: String? = null
    private fun setVendorAdapter(
        data: ArrayList<VendorDropDownItem>,
        selectedItem: VendorDropDownItem?
    ) {
        Log.e("Thulasi", "selected vendor ${selectedItem?.vendorName}")
        Log.e("Thulasi", "selected id ${selectedItem?.id}")
        if (data.isNotEmpty()) {
            val adapter = VendorDropDownAdapter(
                this,
                R.layout.item_status_spinner_view,
                data
            )
            edVendor.setAdapter(adapter)
            edVendor.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    vendorType = data[position]
                    vendorType.id?.let { vendorId = it.toString() }
                    edVendor.setText(data[position].vendorName, false)
                    adapter.filter.filter(null)
                    if (vendorId != null && vendorId == "0") {
                        other_vendor_layout.visibility = View.VISIBLE
                    } else {
                        other_vendor_layout.visibility = View.GONE
                    }
                }

            if (isEdit) {
                for (vendor in data) {
                    Log.e("Thulasi", "vendor data ${vendor.vendorName}")
                    Log.e("Thulasi", "vendor id ${vendor.id}")
                    if (vendor.id == selectedItem?.id) {
                        binding.edVendor.setText(selectedItem?.vendorName)
                        vendorId = selectedItem?.id.toString()
                    }
                }
                if (vendorId != null && vendorId == "0") {
                    other_vendor_layout.visibility = View.VISIBLE
                } else {
                    other_vendor_layout.visibility = View.GONE
                }
            }

        } else {
            showShortToast(getString(R.string.no_business_types_avaialble))
        }
    }

    private fun loadExpenseDetails() {
        if (AppHelper.isNetworkConnected(this)) {
            showLoading()
            if (expenseFor.lowercase().equals("project", ignoreCase = false)) {
                viewModel.getExpenseTypes(getToken()).observe(this) {
                    cancelLoading()
                    when (it.code()) {
                        200 -> {
                            val items = it.body()
                            items?.let { items_ ->
                                setExpenseAdapter(items_)
                            }
                        }
                        401 -> showUnAuthDialog()
                        else -> showErrorResponse(it.errorBody()?.string())
                    }
                }
            } else {
                viewModel.getExpenseTypesNonProject(getToken(), expenseFor).observe(this) {
                    cancelLoading()
                    when (it.code()) {
                        200 -> {
                            val items = it.body()
                            items?.let { items_ ->
                                setExpenseAdapterForNonProject(items_)
                            }
                        }
                        401 -> showUnAuthDialog()
                        else -> showErrorResponse(it.errorBody()?.string())
                    }
                }
            }
        } else {
            showShortToast(getString(R.string.check_internet))
        }
    }

    private fun setExpenseAdapterForNonProject(items_: ArrayList<ExpenseTypeNonProjectResponse>) {
        val adapter = NonProjectExpenseTypesDropDownAdapter(
            this, R.layout.item_status_spinner_view,
            items_
        )
        binding.expenseEd.setAdapter(adapter)
        binding.expenseEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                AppHelper.hideKeyboard(this@ManageExpenseReimburseActivity)
                val data = items_[position]
                expenseTypeItem = ExpenseTypeResponse(
                    id = data.id, expenseTypeName = data.expenseCategoryName
                )
                expenseTypeId = expenseTypeItem.id
                binding.expenseEd.setText(items_[position].expenseCategoryName, false)
                adapter.filter.filter(null)
                loadExpenseSubItemForNonProject(expenseTypeItem)
            }

        if (isCopy || isEdit) {
            expenseTypeId = tempItem?.expenseCategoryId.toString().toInt()
            for (item in items_) {
                if (item.id == expenseTypeId) {
                    expenseTypeItem = ExpenseTypeResponse(
                        id = item.id, expenseTypeName = item.expenseCategoryName
                    )
                    binding.expenseEd.setText(expenseTypeItem.expenseTypeName)
                    adapter.filter.filter(null)
                    loadExpenseSubItemForNonProject(expenseTypeItem)
                }
            }
        }
    }

    private fun loadExpenseSubItemForNonProject(expenseTypeItem: ExpenseTypeResponse) {
        binding.nonProjectExpenseEd.setText("")
        if (AppHelper.isNetworkConnected(this)) {
            viewModel.getExpenseTypesNonProjectCategoryId(
                getToken(),
                expenseTypeItem.id
            ).observe(this) {
                cancelLoading()
                when (it.code()) {
                    200 -> {
                        val items = it.body()
                        items?.let { items_ ->
                            setExpenseAdapterForNonProjectCatergoryId(items_)
                        }
                    }
                    401 -> showUnAuthDialog()
                    else -> showErrorResponse(it.errorBody()?.string())
                }
            }
        } else {
            showShortToast(getString(R.string.check_internet))
        }
    }

    private fun setExpenseAdapterForNonProjectCatergoryId(items_: ArrayList<ExpenseTypeResponse>) {
        val adapter = ExpenseTypesDropDownAdapter(this, R.layout.item_status_spinner_view, items_)
        binding.nonProjectExpenseEd.setAdapter(adapter)
        binding.nonProjectExpenseEd.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                AppHelper.hideKeyboard(this@ManageExpenseReimburseActivity)
                expenseSubCategory = ExpenseTypeNonProjectResponse(
                    id = items_[position].id
                )
                binding.nonProjectExpenseEd.setText(items_[position].expenseTypeName, false)
                adapter.filter.filter(null)
            }

        if (isCopy || isEdit) {
            expenseTypeId = tempItem?.expenseTypeId.toString().toInt()
            binding.nonProjectExpenseEd.setText(tempItem?.expenseType)
            adapter.filter.filter(null)

            for (item in items_) {
                if (item.id == expenseTypeId) {
                    expenseSubCategory = ExpenseTypeNonProjectResponse(
                        id = expenseTypeItem.id
                    )
                }
            }
        }
    }

    private fun setExpenseAdapter(items_: ArrayList<ExpenseTypeResponse>) {
        val adapter = ExpenseTypesDropDownAdapter(
            this, R.layout.item_status_spinner_view,
            items_
        )
        binding.expenseEd.setAdapter(adapter)
        binding.expenseEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                AppHelper.hideKeyboard(this@ManageExpenseReimburseActivity)
                expenseTypeItem = items_[position]
                expenseTypeId = expenseTypeItem.id
                binding.expenseEd.setText(items_[position].expenseTypeName, false)
                adapter.filter.filter(null)
            }

        if (isCopy || isEdit) {
            expenseTypeId = tempItem?.expenseTypeId.toString().toInt()
            for (item in items_) {
                if (item.id == expenseTypeId) {
                    expenseTypeItem = item
                    binding.expenseEd.setText(expenseTypeItem.expenseTypeName)
                    adapter.filter.filter(null)
                }
            }
        }
    }

    var startDate: String? = null
    var endDate: String? = null
    var noOfDays = 0L

    fun onSelectStartEndDate() {
        SlyCalendarDialog()
            .setSingle(false)
            .setHeaderColor(R.color.appPrimary)
            .setSelectedColor(R.color.appPrimary)
            .setCallback(startDateEndDateCallback)
            .show(supportFragmentManager, "TAG_SLY_CALENDAR")
    }

    private val startDateEndDateCallback = object : SlyCalendarDialog.Callback {
        override fun onCancelled() {

        }

        override fun onDataSelected(
            firstDate: Calendar?,
            secondDate: Calendar?,
            hours: Int,
            minutes: Int
        ) {
            startDate =
                firstDate?.let { DateUtils.fromCalendar(it, EXPENSE_REIMBURSE_FORMAT) } ?: ""
            endDate = secondDate?.let { DateUtils.fromCalendar(it, EXPENSE_REIMBURSE_FORMAT) } ?: ""

            val sDate =
                firstDate?.let { DateUtils.fromCalendar(it, HIPHEN_SLASHED_DATE_FORMAT) } ?: ""
            val eDate =
                secondDate?.let { DateUtils.fromCalendar(it, HIPHEN_SLASHED_DATE_FORMAT) } ?: ""

            if (sDate.isNotEmpty() && eDate.isNotEmpty()) {
                binding.startDateEndDateEd.setText("$sDate -> $eDate")
            }

            if (startDate.isNullOrEmpty().not() && endDate.isNullOrEmpty().not()) {
                noOfDays = DateUtils.getDaysBetween(
                    firstDate,
                    secondDate
                )
                binding.noOfDaysEd.setText(noOfDays.toString())
            }
        }

    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("isCopy", isCopy)
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    companion object {
        const val FILE_REQUEST_CODE = 101
    }
}