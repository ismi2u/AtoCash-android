package com.atocash.fragments.admin.costCenter

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.costCenter.ManageCostCenterActivity
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.base.view.SuperFragment
import com.atocash.databinding.FragmentCostCenterBinding
import com.atocash.fragments.admin.costCenter.adapter.CostCenterAdapter
import com.atocash.network.response.CostCenterResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.atocash.utils.recycler.EvenMarginDecorator
import com.google.gson.Gson

class CostCenterFragment : SuperFragment<FragmentCostCenterBinding, CostCenterViewModel>(),
    CostCenterNavigator {

    private lateinit var viewModel: CostCenterViewModel
    private lateinit var binding: FragmentCostCenterBinding

    private lateinit var adapter: CostCenterAdapter

    private var isItemsLoading = false
    private var isFinalPage = false
    private var currentPage: Int = Keys.RecyclerItem.PAGE_START

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_cost_center
    }

    override fun getViewModel(): CostCenterViewModel {
        viewModel = ViewModelProvider(this).get(CostCenterViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        binding.addCostCenter.setOnClickListener {
            intentHelper.goTo(requireActivity(), ManageCostCenterActivity::class.java, null, false)
        }

        adapter = CostCenterAdapter(ArrayList(), object : CostCenterAdapter.CostCenterCallback {
            override fun onEdit(item: CostCenterResponse) {
                val bundleArgs = Bundle()
                bundleArgs.putString("CostCenterItem", Gson().toJson(item))
                bundleArgs.putBoolean(Keys.IntentData.IS_EDIT, true)
                intentHelper.goTo(
                    requireActivity(),
                    ManageCostCenterActivity::class.java,
                    bundle = bundleArgs
                )
            }

            override fun onDelete(item: CostCenterResponse) {
                showDeleteDialog(item)
            }
        })
        binding.costCenterRv.layoutAnimation = getBottomScaleAnim()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.costCenterRv.layoutManager = layoutManager
        binding.costCenterRv.addItemDecoration(
            EvenMarginDecorator(
                16,
                EvenMarginDecorator.VERTICAL
            )
        )
        binding.costCenterRv.adapter = adapter

        currentPage = 0
    }

    private fun showDeleteDialog(item: CostCenterResponse) {
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setMessage(getString(R.string.ask_to_delete))
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            getString(R.string.yes)
        ) { dialog_, which ->
            dialog_?.dismiss()
            proceedToDelete(item)
        }
        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            getString(R.string.no)
        ) { dialog_, which ->
            dialog_?.dismiss()
        }
        dialog.show()
    }

    private fun proceedToDelete(item: CostCenterResponse) {
        if (AppHelper.isNetworkConnected(requireContext())) {
            showLoading(getString(R.string.deleting))
            viewModel.deleteCostCenter(getToken(), item).observe(viewLifecycleOwner, Observer {
                cancelLoading()
                when {
                    it.code() == 204 || it.code() == 200 -> {
                        requireContext().showShortToast(getString(R.string.info_cost_center_deleted_success))
                        adapter.remove(item)
                    }
                    it.code() == 401 -> {
                        (requireActivity() as AdminMainActivity).showUnAuthDialog()
                    }
                    it.code() == 409 -> {
                        requireContext().showShortToast(getString(R.string.err_cannot_delete_cost_center))
                    }
                    else -> {
                        requireContext().showShortToast(it.message())
                    }
                }
            })
        } else {
            showSnack(getString(R.string.check_internet))
        }
    }

    private fun loadItems(pageNo: String) {
        if (pageNo == "0") viewModel.isLoading.set(true)

        viewModel.getCostCenterList(getToken()).observe(viewLifecycleOwner, Observer {
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

    override fun onResume() {
        super.onResume()
        loadItems(currentPage.toString())
    }
}