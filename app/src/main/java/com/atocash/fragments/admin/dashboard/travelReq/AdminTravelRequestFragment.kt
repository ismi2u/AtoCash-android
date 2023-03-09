package com.atocash.fragments.admin.dashboard.travelReq

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.activities.admin.travelRequest.ManageTravelRequestActivity
import com.atocash.base.recycler.RecyclerAction
import com.atocash.base.view.SuperFragment
import com.atocash.common.activity.travelRequestDetails.TravelRequestDetailActivity
import com.atocash.databinding.FragmentAdminTravelRequestBinding
import com.atocash.fragments.admin.dashboard.adapter.TravelRequestAdapter
import com.atocash.network.response.TravelResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.atocash.utils.recycler.EvenMarginDecorator
import com.google.gson.Gson

class   AdminTravelRequestFragment :
    SuperFragment<FragmentAdminTravelRequestBinding, AdminTravelRequestViewModel>(),
    AdminTravelRequestNavigator {

    private lateinit var binding: FragmentAdminTravelRequestBinding
    private lateinit var viewModel: AdminTravelRequestViewModel

    private lateinit var adapter: TravelRequestAdapter

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_admin_travel_request
    }

    override fun getViewModel(): AdminTravelRequestViewModel {
        viewModel = ViewModelProvider(this).get(AdminTravelRequestViewModel::class.java)
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    private fun initViewsAndClicks() {
        adapter = TravelRequestAdapter(
            ArrayList(),
            object : TravelRequestAdapter.TravelRequestAdapterCallback {
                override fun onItemClick(recyclerAction: RecyclerAction, item: TravelResponse) {
                    when (recyclerAction) {
                        RecyclerAction.EDIT -> {
                            openForEdit(item)
                        }
                        RecyclerAction.DELETE -> {
                            onDelete(item)
                        }
                        RecyclerAction.DETAILS -> {
                            onDetails(item)
                        }
                    }
                }
            })
        binding.cashAdvanceRv.layoutAnimation = getBottomScaleAnim()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.cashAdvanceRv.layoutManager = layoutManager
        binding.cashAdvanceRv.addItemDecoration(
            EvenMarginDecorator(
                16,
                EvenMarginDecorator.VERTICAL
            )
        )
        binding.cashAdvanceRv.adapter = adapter
    }

    private fun onDetails(item: TravelResponse) {
        val bundle = Bundle()
        bundle.putString("TravelResponse", Gson().toJson(item))
        intentHelper.goTo(
            requireActivity(),
            TravelRequestDetailActivity::class.java,
            bundle,
            false
        )
    }

    private fun openForEdit(item: TravelResponse) {
        val bundle = Bundle()
        bundle.putBoolean(Keys.IntentData.IS_EDIT, true)
        bundle.putString("TravelResponseItem", Gson().toJson(item))
        intentHelper.goTo(requireActivity(), ManageTravelRequestActivity::class.java, bundle, false)
    }

    private fun onDelete(item: TravelResponse) {
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

    private fun proceedToDelete(item: TravelResponse) {
        if (AppHelper.isNetworkConnected(requireContext())) {
            showLoading(getString(R.string.deleting))
            viewModel.deleteTravelRequest(getToken(), item).observe(viewLifecycleOwner, Observer {
                cancelLoading()
                when {
                    it.code() == 204 || it.code() == 200 -> {
                        requireContext().showShortToast(getString(R.string.info_travel_request_deleted_success))
                        adapter.remove(item)
                    }
                    it.code() == 401 -> {
                        (requireActivity() as AdminMainActivity).showUnAuthDialog()
                    }
                    it.code() == 409 -> {
                        requireContext().showShortToast(getString(R.string.info_travel_request_deleted_fail))
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
        viewModel.isLoading.set(false)
        viewModel.getTravelRequest(getToken()).observe(viewLifecycleOwner, Observer {
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

    override fun onTravelRequest() {
        val bundle = Bundle()
        bundle.putBoolean(Keys.IntentData.IS_EDIT, false)
        intentHelper.goTo(requireActivity(), ManageTravelRequestActivity::class.java, bundle, false)
    }
}