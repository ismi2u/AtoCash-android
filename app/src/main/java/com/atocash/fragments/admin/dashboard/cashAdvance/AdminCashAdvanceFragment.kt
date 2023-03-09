package com.atocash.fragments.admin.dashboard.cashAdvance

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
import com.atocash.common.activity.cashAdvanceStatusDetails.CashAdvanceStatusDetailActivity
import com.atocash.databinding.FragmentAdminCashAdvanceBinding
import com.atocash.fragments.admin.dashboard.adapter.AdminCashAdvanceAdapter
import com.atocash.network.response.PettyCashResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.atocash.utils.recycler.EvenMarginDecorator
import com.google.gson.Gson

class AdminCashAdvanceFragment :
    SuperFragment<FragmentAdminCashAdvanceBinding, AdminCashAdvanceViewModel>(),
    AdminCashAdvanceNavigator {

    private lateinit var binding: FragmentAdminCashAdvanceBinding
    private lateinit var viewModel: AdminCashAdvanceViewModel

    private lateinit var adapter: AdminCashAdvanceAdapter

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_admin_cash_advance
    }

    override fun getViewModel(): AdminCashAdvanceViewModel {
        viewModel = ViewModelProvider(this).get(AdminCashAdvanceViewModel::class.java)
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

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
                            getDetailsAndOpen(item)
                        }
                    }
                }
            })
        binding.cashAdvanceRv.layoutAnimation = getBottomScaleAnim()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.cashAdvanceRv.layoutManager = layoutManager
        binding.cashAdvanceRv.addItemDecoration(
            EvenMarginDecorator(
                20,
                EvenMarginDecorator.VERTICAL
            )
        )
        binding.cashAdvanceRv.adapter = adapter
    }

    private fun getDetailsAndOpen(response_: PettyCashResponse) {
        openDetails(response_)
//        if(AppHelper.isNetworkConnected(requireContext())) {
//            showLoading()
//            viewModel.getPettyCashDetails(getToken(), response_).observe(viewLifecycleOwner, Observer {
//                cancelLoading()
//                if (it.code() == 200) {
//                    val response = it.body()
//                    response?.let { response_ ->
//                    }
//                }
//            })
//        } else {
//            requireContext().showErrorResponse(getString(R.string.check_internet))
//        }
    }

    private fun openDetails(response_: PettyCashResponse) {
        val bundle = Bundle()
        bundle.putString("PettyCashResponse", Gson().toJson(response_))
        intentHelper.goTo(
            requireActivity(),
            CashAdvanceStatusDetailActivity::class.java,
            bundle,
            false
        )
    }

    private fun openForEdit(item: PettyCashResponse) {
        val bundle = Bundle()
        bundle.putBoolean(Keys.IntentData.IS_EDIT, true)
        bundle.putString("PettyCashItem", Gson().toJson(item))
        intentHelper.goTo(requireActivity(), AdminCashAdvanceActivity::class.java, bundle, false)
    }

    private fun onDelete(item: PettyCashResponse) {
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setMessage(getString(R.string.ask_to_delete))
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            getString(R.string.yes)
        ) { dialog_, _ ->
            dialog_?.dismiss()
            proceedToDelete(item)
        }
        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            getString(R.string.no)
        ) { dialog_, _ ->
            dialog_?.dismiss()
        }
        dialog.show()
    }

    private fun proceedToDelete(item: PettyCashResponse) {
        if (AppHelper.isNetworkConnected(requireContext())) {
            showLoading(getString(R.string.deleting))
            viewModel.deleteCashAdvance(getToken(), item).observe(viewLifecycleOwner, Observer {
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
        viewModel.isLoading.set(true)
        viewModel.getCashAdvanceRequest(getToken()).observe(this, Observer {
            viewModel.isLoading.set(false)
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

    override fun onAddCashAdvance() {
        val bundle = Bundle()
        bundle.putBoolean(Keys.IntentData.IS_EDIT, false)
        intentHelper.goTo(requireActivity(), AdminCashAdvanceActivity::class.java, bundle, false)
    }
}