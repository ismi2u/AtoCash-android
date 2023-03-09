package com.atocash.fragments.admin.employmentType

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
import com.atocash.databinding.FragmentEmployeeTypeBinding
import com.atocash.fragments.admin.employmentType.adapter.EmpTypeAdapter
import com.atocash.network.response.EmpTypeModel
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.atocash.utils.recycler.EvenMarginDecorator
import com.google.gson.Gson

class EmployeeTypeFragment :
    SuperFragment<FragmentEmployeeTypeBinding, EmployeeTypeViewModel>(),
    EmployeeTypeNavigator, EmpTypeAdapter.CostCenterCallback {

    private lateinit var viewModel: EmployeeTypeViewModel
    private lateinit var binding: FragmentEmployeeTypeBinding

    private lateinit var adapter: EmpTypeAdapter

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
        return R.layout.fragment_employee_type
    }

    override fun getViewModel(): EmployeeTypeViewModel {
        viewModel =
            ViewModelProvider(this).get(EmployeeTypeViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        binding.addEmploymentType.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Keys.IntentData.FROM, "Employment")
            bundle.putBoolean(Keys.IntentData.IS_EDIT, false)
            intentHelper.goTo(requireActivity(), ManageEmployeeActivity::class.java, bundle, false)
        }

        adapter = EmpTypeAdapter(ArrayList(), this)
        binding.employementTypeRv.layoutAnimation = getBottomScaleAnim()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.employementTypeRv.layoutManager = layoutManager
        binding.employementTypeRv.addItemDecoration(
            EvenMarginDecorator(
                16,
                EvenMarginDecorator.VERTICAL
            )
        )
        binding.employementTypeRv.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        loadEmploymentTypes()
    }

    private fun loadEmploymentTypes() {
        viewModel.isLoading.set(true)

        viewModel.getEmployeeTypes(getToken()).observe(viewLifecycleOwner, Observer {
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

    override fun onEdit(item: EmpTypeModel) {
        val bundle = Bundle()
        bundle.putBoolean(Keys.IntentData.IS_EDIT, true)
        bundle.putString(Keys.IntentData.FROM, "Employment")
        bundle.putString("EmploymentData", Gson().toJson(item))
        intentHelper.goTo(requireActivity(), ManageEmployeeActivity::class.java, bundle, false)
    }

    override fun onDelete(item: EmpTypeModel) {
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

    private fun proceedToDelete(item: EmpTypeModel) {
        if (AppHelper.isNetworkConnected(requireContext())) {
            showLoading(getString(R.string.deleting))
            viewModel.deleteEmploymentType(getToken(), item).observe(viewLifecycleOwner, Observer {
                cancelLoading()
                when {
                    it.code() == 204 || it.code() == 200 -> {
                        requireContext().showShortToast(getString(R.string.info_emp_type_deleted_success))
                        adapter.remove(item)
                    }
                    it.code() == 401 -> {
                        (requireActivity() as AdminMainActivity).showUnAuthDialog()
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
}