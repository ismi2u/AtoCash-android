package com.atocash.common.fragments.report

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.base.view.SuperFragment
import com.atocash.common.fragments.inbox.InboxFragment
import com.atocash.databinding.FragmentReportBinding
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast

class ReportFragment :
    SuperFragment<FragmentReportBinding, ReportViewModel>(),
    ReportNavigator {

    private lateinit var binding: FragmentReportBinding
    private lateinit var viewModel: ReportViewModel

    private var currentType: InboxFragment.InboxType = InboxFragment.InboxType.ADVANCE

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_report
    }

    override fun getViewModel(): ReportViewModel {
        viewModel = ViewModelProvider(this).get(ReportViewModel::class.java)
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    private fun initViewsAndClicks() {
        binding.inboxListTypeTv.text = getString(R.string.cash_advance)

    }

    override fun showDropDown() {
        val popup = PopupMenu(requireContext(), binding.inboxListTypeTv)
        popup.inflate(R.menu.inbox_pop_up_menu)
        popup.setOnMenuItemClickListener { item ->
            if (item != null) {
                when (item.itemId) {
                    R.id.menu_cash_advance -> loadCashAdvances()
                    R.id.menu_travel_request -> loadTravelRequests()
                    R.id.menu_expense_reimbursement -> loadExpenses()
                }
            }
            false
        }
        popup.show()
    }

    private fun loadExpenses() {
        requireContext().showShortToast("Clicked expenses")
    }

    private fun loadTravelRequests() {
        requireContext().showShortToast("Clicked travel request")
    }

    private fun loadCashAdvances() {
//        viewModel.isLoading.set(true)
        currentType = InboxFragment.InboxType.ADVANCE
        binding.inboxListTypeTv.text = getString(R.string.title_cash_advance)

//        viewModel.getCashAdvances(getToken()).observe(viewLifecycleOwner, Observer {
//            viewModel.isLoading.set(false)
//            when (it.code()) {
//                200 -> {
//                    val items = it.body()
//                    items?.let { items_ ->
//                        setAdapterForCash(items_)
//                    }
//                }
//                401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
//                else -> requireContext().showErrorResponse(it.errorBody()?.string())
//            }
//        })
//    }
    }
}