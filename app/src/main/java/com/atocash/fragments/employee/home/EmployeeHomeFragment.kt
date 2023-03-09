package com.atocash.fragments.employee.home

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.base.view.SuperFragment
import com.atocash.databinding.FragmentEmployeeHomeBinding
import com.atocash.dialog.WalletDetailsDialog
import com.atocash.network.response.EmployeesResponse
import com.atocash.network.response.ExpenseReimburseCountResponse
import com.atocash.network.response.MaxLimitBalanceAndCashInHand
import com.atocash.network.response.dashboard.CashAdvanceCountResponseData
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import kotlinx.android.synthetic.main.fragment_employee_home.*
import org.eazegraph.lib.models.PieModel


class EmployeeHomeFragment :
    SuperFragment<FragmentEmployeeHomeBinding, EmployeeHomeViewModel>(),
    EmployeeHomeNavigator {

    private lateinit var viewModel: EmployeeHomeViewModel
    private lateinit var binding: FragmentEmployeeHomeBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_employee_home
    }

    override fun getViewModel(): EmployeeHomeViewModel {
        viewModel = ViewModelProvider(this)[EmployeeHomeViewModel::class.java]
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    private fun initViewsAndClicks() {
        firstNameTv.text = dataStorage.getString(Keys.UserData.FIRST_NAME)
        lastNameTv.text = dataStorage.getString(Keys.UserData.LAST_NAME)

        loadEmployeeInfo()
        wallet_cv.setOnClickListener {
            getWalletDetails()
        }
        updateUiForUserRole()
    }

    private var employeeInfo: EmployeesResponse? = null
    private fun loadEmployeeInfo() {
        viewModel.getEmployeeInfo(getToken()).observe(viewLifecycleOwner, Observer {
            cancelLoading()
            when (it.code()) {
                200 -> {
                    employeeInfo = it.body()
                }
                401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                else -> requireContext().showShortToast(it.message())
            }
        })
    }

    private fun getWalletDetails() {
        if (AppHelper.isNetworkConnected(requireContext())) {
            showLoading()
            viewModel.getWalletDetails(getToken()).observe(viewLifecycleOwner, Observer {
                cancelLoading()
                when (it.code()) {
                    200 -> {
                        showExpenseDetails(it.body())
                    }
                    401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                    else -> requireContext().showShortToast(it.message())
                }
            })
        } else {
            requireActivity().showShortToast(getString(R.string.check_internet))
        }
    }

    private fun showExpenseDetails(body: MaxLimitBalanceAndCashInHand?) {
        body?.let {
            val currencyType = dataStorage.getString(Keys.UserData.CURRENCY_CODE)
            WalletDetailsDialog(
                currencyType,
                it,
                employeeInfo
            ).show(childFragmentManager, "Wallet Details")
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun loadExpenseCount() {
        viewModel.loadExpenseCount(getToken()).observe(viewLifecycleOwner, Observer {
            when (it.code()) {
                200 -> {
                    val pettyCashData = it.body()
                    pettyCashData?.let { count ->
                        updateUiForExpenseCount(count)
                    }
                }
                401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                else -> requireContext().showShortToast(it.message())
            }
        })
    }

    private fun updateUiForExpenseCount(count: ExpenseReimburseCountResponse) {
        binding.expenseRequestTv.text = count.totalCount.toString()

        binding.expenseRequestChart.clearChart()
        binding.expenseRequestChart.addPieSlice(
            PieModel(
                getString(R.string.approved), count.approvedCount.toString().toFloat(),
                ContextCompat.getColor(requireContext(), R.color.status_approved)
            )
        )
        binding.expenseRequestChart.addPieSlice(
            PieModel(
                getString(R.string.pending), count.pendingCount.toString().toFloat(),
                ContextCompat.getColor(requireContext(), R.color.status_pending)
            )
        )
        binding.expenseRequestChart.addPieSlice(
            PieModel(
                getString(R.string.rejected), count.rejectedCount.toString().toFloat(),
                ContextCompat.getColor(requireContext(), R.color.status_rejected)
            )
        )
    }

    private fun laodTravelRequestCount() {
        showLoading()
        viewModel.loadTravelRequest(getToken()).observe(viewLifecycleOwner, Observer {
            cancelLoading()
            when (it.code()) {
                200 -> {
                    val pettyCashData = it.body()
                    pettyCashData?.let { count ->
                        updateUiForTravelRequest(count)
                    }
                }
                401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                else -> requireContext().showShortToast(it.message())
            }
        })
    }

    private fun updateUiForTravelRequest(count: ExpenseReimburseCountResponse) {
        binding.travelRequestTv.text = count.totalCount.toString()
        binding.travelRequestChart.clearAnimation()
        binding.travelRequestChart.addPieSlice(
            PieModel(
                getString(R.string.approved), count.approvedCount.toString().toFloat(),
                ContextCompat.getColor(requireContext(), R.color.status_approved)
            )
        )
        binding.travelRequestChart.addPieSlice(
            PieModel(
                getString(R.string.pending), count.pendingCount.toString().toFloat(),
                ContextCompat.getColor(requireContext(), R.color.status_pending)
            )
        )
        binding.travelRequestChart.addPieSlice(
            PieModel(
                getString(R.string.rejected), count.rejectedCount.toString().toFloat(),
                ContextCompat.getColor(requireContext(), R.color.status_rejected)
            )
        )
    }

    private fun loadCurrenBalance() {
        val currencyType = dataStorage.getString(Keys.UserData.CURRENCY_CODE)
        viewModel.loadPettyCashBalance(getToken()).observe(viewLifecycleOwner, Observer {
            when (it.code()) {
                200 -> {
                    val balance = it.body()
                    balance?.let { balance_ ->
                        printLog("balance_.curBalance ${balance_.curCashBal}")
                        binding.walletBalanceTv.text = "${balance_.curCashBal} $currencyType"
                    }
                }
                401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                else -> requireContext().showShortToast(it.message())
            }
        })
    }

    private fun loadBalanceVsAdvanced() {
        viewModel.loadPettyCashBalanceVsAdvanced(getToken()).observe(viewLifecycleOwner, Observer {
            when (it.code()) {
                200 -> {

                }
                401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                else -> requireContext().showShortToast(it.message())
            }
        })
    }

    private fun updateUiForUserRole() {
        var isEmployee = false
        if (!hasMultipleRoles()) {
            when (getRole()) {
                Keys.LoginType.ATOMINOS_ADMIN -> {
                }
                Keys.LoginType.ADMIN -> {
                }
                Keys.LoginType.MANAGER -> {
                }
                Keys.LoginType.FINANCE_MANAGER -> {
                }
                Keys.LoginType.EMPLOYEE -> {
                    isEmployee = true
                    binding.inboxRequestCv.visibility = View.GONE
                }
            }
        }

        loadCurrenBalance()
        loadExpenseCount()

        laodTravelRequestCount()
        loadCashAdvances()

        if (!isEmployee) {
//            getInboxCount()
        }
    }

    private var inboxCount = 0
    private fun loadTravelRequests() {
        viewModel.getTravelExpenses(getToken()).observe(viewLifecycleOwner, Observer {
            when (it.code()) {
                200 -> {
                    val items = it.body()
                    items?.let { items_ ->
                        if (items_.isNotEmpty()) {
                            inboxCount += items_.size
                        }
                    }
                }
                401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                else -> requireContext().showErrorResponse(it.errorBody()?.string())
            }
            updateInboxCount()
        })
    }

    private fun updateInboxCount() {
        binding.inboxTv.text = inboxCount.toString()
        binding.inboxRequestChart.clearAnimation()
        binding.inboxRequestChart.addPieSlice(
            PieModel(
                getString(R.string.approved), 0f,
                ContextCompat.getColor(requireContext(), R.color.card_pink)
            )
        )
    }

    private fun loadExpenses() {
        viewModel.getExpenseReimburse(getToken()).observe(viewLifecycleOwner, Observer {
            when (it.code()) {
                200 -> {
                    val items = it.body()
                    items?.let { items_ ->
                        if (items_.isNotEmpty()) {
                            inboxCount += items_.size
                        }
                    }
                    loadTravelRequests()
                }
                401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                else -> requireContext().showErrorResponse(it.errorBody()?.string())
            }
        })
    }

    private fun loadCashAdvances() {
        viewModel.getCashAdvances(getToken()).observe(viewLifecycleOwner, Observer {
            when (it.code()) {
                200 -> {
                    val items = it.body()
                    updateUiForCashAdvance(items)
                    loadExpenses()
                }
                401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                else -> requireContext().showErrorResponse(it.errorBody()?.string())
            }
        })
    }

    private fun updateUiForCashAdvance(items: CashAdvanceCountResponseData?) {
        binding.cashAdvanceTv.text = items?.totalCount.toString()

        binding.cashAdvanceChart.clearChart()
        binding.cashAdvanceChart.addPieSlice(
            PieModel(
                getString(R.string.approved), items?.approvedCount.toString().toFloat(),
                ContextCompat.getColor(requireContext(), R.color.status_approved)
            )
        )
        binding.cashAdvanceChart.addPieSlice(
            PieModel(
                getString(R.string.pending), items?.pendingCount.toString().toFloat(),
                ContextCompat.getColor(requireContext(), R.color.status_pending)
            )
        )
        binding.cashAdvanceChart.addPieSlice(
            PieModel(
                getString(R.string.rejected), items?.rejectedCount.toString().toFloat(),
                ContextCompat.getColor(requireContext(), R.color.status_rejected)
            )
        )
    }
}