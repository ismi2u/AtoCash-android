package com.atocash.fragments.employee.advance

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.cashAdvance.AdminCashAdvanceActivity
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.base.recycler.RecyclerAction
import com.atocash.base.view.SuperFragment
import com.atocash.databinding.FragmentEmpAdvanceBinding
import com.atocash.fragments.admin.dashboard.adapter.AdminCashAdvanceAdapter
import com.atocash.network.response.PettyCashResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.atocash.utils.recycler.EvenMarginDecorator
import com.google.gson.Gson

class EmpAdvanceFragment :
    SuperFragment<FragmentEmpAdvanceBinding, EmpAdvanceViewModel>(),
    EmpAdvanceNavigator {

    private lateinit var viewModelEmp: EmpAdvanceViewModel
    private lateinit var binding: FragmentEmpAdvanceBinding

    private lateinit var adapter: AdminCashAdvanceAdapter

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_emp_advance
    }

    override fun getViewModel(): EmpAdvanceViewModel {
        viewModelEmp =
            ViewModelProvider(this).get(EmpAdvanceViewModel::class.java)
        return viewModelEmp
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModelEmp.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    private fun initViewsAndClicks() {
        adapter = AdminCashAdvanceAdapter(
            ArrayList(),
            object : AdminCashAdvanceAdapter.AdminCashAdvanceAdapterCallback {
                override fun onItemClick(recyclerAction: RecyclerAction, item: PettyCashResponse) {
                    when (recyclerAction) {
                        RecyclerAction.EDIT -> {
                            openForEdit(item)
                        }
                        RecyclerAction.DELETE -> {
                            onDelete(item)
                        }
                        RecyclerAction.DETAILS -> {

                        }
                    }
                }
            })
        binding.advanceRv.layoutAnimation = getBottomScaleAnim()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.advanceRv.layoutManager = layoutManager
        binding.advanceRv.addItemDecoration(
            EvenMarginDecorator(
                16,
                EvenMarginDecorator.VERTICAL
            )
        )
        binding.advanceRv.adapter = adapter

        binding.addAdvance.setOnClickListener {
            onCreateRequest()
        }
    }

    private fun onCreateRequest() {
        val bundle = Bundle()
        bundle.putBoolean(Keys.IntentData.IS_EDIT, false)
        intentHelper.goTo(requireActivity(), AdminCashAdvanceActivity::class.java, bundle, false)
    }

    private fun openForEdit(item: PettyCashResponse) {
        val bundle = Bundle()
        bundle.putBoolean(Keys.IntentData.IS_EDIT, true)
        bundle.putString("PettyCashItem", Gson().toJson(item))
        intentHelper.goTo(requireActivity(), AdminCashAdvanceActivity::class.java, bundle, false)
    }

    private fun onDelete(item: PettyCashResponse) {
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setMessage("Are you sure you want to delete?")
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "Yes"
        ) { dialog_, which ->
            dialog_?.dismiss()
            proceedToDelete(item)
        }
        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            "No"
        ) { dialog_, which ->
            dialog_?.dismiss()
        }
        dialog.show()
    }

    private fun proceedToDelete(item: PettyCashResponse) {
        if (AppHelper.isNetworkConnected(requireContext())) {
            showLoading(getString(R.string.deleting))
            viewModelEmp.deleteCashAdvance(getToken(), item).observe(viewLifecycleOwner, Observer {
                cancelLoading()
                when {
                    it.code() == 204 || it.code() == 200 -> {
                        requireContext().showShortToast(getString(R.string.info_cash_request_deleted_success))
                        adapter.remove(item)
                    }
                    it.code() == 401 -> {
                        (requireActivity() as AdminMainActivity).showUnAuthDialog()
                    }
                    it.code() == 409 -> {
                        requireContext().showShortToast(getString(R.string.err_cannot_delete_cash_request))
                    }
                    else -> {
                        requireContext().showErrorResponse(it.errorBody()?.string())
                    }
                }
            })
        } else {
            showSnack(getString(R.string.check_internet))
        }
    }

    override fun onResume() {
        super.onResume()

        loadItems()
    }

    private fun loadItems() {
        viewModelEmp.isLoading.set(true)
        viewModelEmp.getCashAdvanceRequest(getToken()).observe(viewLifecycleOwner, Observer {
            viewModelEmp.isLoading.set(false)
            when (it.code()) {
                200 -> {
                    val items = it.body()
                    items?.let { items_ ->
                        adapter.addAll(items_)
                    }
                }
                401 -> (requireActivity() as AdminMainActivity).showUnAuthDialog()
                else -> requireContext().showErrorResponse(it.errorBody()?.string())
            }
        })
    }
}