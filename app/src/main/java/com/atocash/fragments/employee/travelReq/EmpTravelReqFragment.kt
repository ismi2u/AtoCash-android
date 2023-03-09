package com.atocash.fragments.employee.travelReq

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.employee.advance.ManageEmpAdvanceActivity
import com.atocash.base.view.SuperFragment
import com.atocash.databinding.FragmentEmployeeTravelRequestBinding
import com.atocash.fragments.employee.travelReq.adapter.EmployeeTravelReqAdapter
import com.atocash.network.MockData
import com.atocash.network.response.EmployeeTravelRequestModel
import com.atocash.utils.recycler.EvenMarginDecorator

class EmpTravelReqFragment :
    SuperFragment<FragmentEmployeeTravelRequestBinding, EmpTravelReqViewModel>(),
    EmpTravelReqNavigator, EmployeeTravelReqAdapter.EmployeeExpenseCallback {

    private lateinit var viewModelEmployee: EmpTravelReqViewModel
    private lateinit var binding: FragmentEmployeeTravelRequestBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModelEmployee.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_employee_travel_request
    }

    override fun getViewModel(): EmpTravelReqViewModel {
        viewModelEmployee =
            ViewModelProvider(this).get(EmpTravelReqViewModel::class.java)
        return viewModelEmployee
    }

    private lateinit var adapter: EmployeeTravelReqAdapter
    private fun initViewsAndClicks() {
        binding.addTravelReq.setOnClickListener {
            intentHelper.goTo(requireActivity(), ManageEmpAdvanceActivity::class.java, bundle = null, needToFinish = false)
        }

        adapter = EmployeeTravelReqAdapter(ArrayList(), this)
        binding.travelReqRv.layoutAnimation = getBottomScaleAnim()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.travelReqRv.layoutManager = layoutManager
        binding.travelReqRv.addItemDecoration(
            EvenMarginDecorator(
                16,
                EvenMarginDecorator.VERTICAL
            )
        )
        binding.travelReqRv.adapter = adapter

        viewModelEmployee.isLoading.set(false)
    }

    override fun onClick(item: EmployeeTravelRequestModel) {
        showSnack("Clicked item")
    }

    override fun onEdit(item: EmployeeTravelRequestModel) {
        showSnack("Clicked edit")
    }

    override fun onDelete(item: EmployeeTravelRequestModel) {
        showSnack("Clicked delete")
    }
}