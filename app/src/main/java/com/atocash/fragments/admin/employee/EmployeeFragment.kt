package com.atocash.fragments.admin.employee

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.empTypes.ManageEmployeeActivity
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.base.view.SuperFragment
import com.atocash.databinding.FragmentEmployeeBinding
import com.atocash.fragments.admin.employee.adapter.EmployeeAdapter
import com.atocash.network.MockData
import com.atocash.network.response.CostCenterResponse
import com.atocash.network.response.EmployeesResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.showShortToast
import com.atocash.utils.recycler.EvenMarginDecorator
import com.google.gson.Gson

class EmployeeFragment :
    SuperFragment<FragmentEmployeeBinding, EmployeeViewModel>(),
    EmployeeNavigator, EmployeeAdapter.CostCenterCallback {

    private lateinit var viewModel: EmployeeViewModel
    private lateinit var binding: FragmentEmployeeBinding

    private lateinit var adapter: EmployeeAdapter

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
        return R.layout.fragment_employee
    }

    override fun getViewModel(): EmployeeViewModel {
        viewModel =
            ViewModelProvider(this).get(EmployeeViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        binding.addEmployees.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(Keys.IntentData.IS_EDIT, false)
            bundle.putString(Keys.IntentData.FROM, "Employee")
            intentHelper.goTo(requireActivity(), ManageEmployeeActivity::class.java, bundle, false)
        }

        adapter = EmployeeAdapter(ArrayList(), this)
        binding.employeesRv.layoutAnimation = getBottomScaleAnim()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.employeesRv.layoutManager = layoutManager
        binding.employeesRv.addItemDecoration(
            EvenMarginDecorator(
                16,
                EvenMarginDecorator.VERTICAL
            )
        )
        binding.employeesRv.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        loadEmployees()
    }

    private fun loadEmployees() {
        viewModel.isLoading.set(true)

        viewModel.getEmployees(getToken()).observe(viewLifecycleOwner, Observer {
            viewModel.isLoading.set(false)
            if (it.code() == 200) {
                val listResponse = it.body()
                if (listResponse.isNullOrEmpty().not()) {
                    listResponse?.let { it1 -> adapter.addAll(it1) }
                }
            } else if (it.code() == 401) {
                (requireActivity() as AdminMainActivity).showUnAuthDialog()
            } else {
                requireContext().showShortToast(it.message())
            }
        })
    }

    override fun onEdit(item: EmployeesResponse) {
        val bundle = Bundle()
        bundle.putString("employeeItem", Gson().toJson(item))
        bundle.putBoolean(Keys.IntentData.IS_EDIT, true)
        bundle.putString(Keys.IntentData.FROM, "Employee")
        intentHelper.goTo(requireActivity(), ManageEmployeeActivity::class.java, bundle, false)
    }

    override fun onDelete(item: EmployeesResponse) {
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

    private fun proceedToDelete(item: EmployeesResponse) {
        if (AppHelper.isNetworkConnected(requireContext())) {
            showLoading(getString(R.string.deleting))
            viewModel.deleteEmployee(getToken(), item).observe(viewLifecycleOwner, Observer {
                cancelLoading()
                when {
                    it.code() == 204 || it.code() == 200 -> {
                        requireContext().showShortToast(getString(R.string.emp_deleted_success))
                        adapter.remove(item)
                    }
                    it.code() == 401 -> {
                        (requireActivity() as AdminMainActivity).showUnAuthDialog()
                    }
                    it.code() == 409 -> {
                        requireContext().showShortToast(getString(R.string.emp_deleted_failed))
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

}