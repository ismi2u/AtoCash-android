package com.atocash.fragments.admin.department

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.department.AdminDepartmentActivity
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.base.view.SuperFragment
import com.atocash.databinding.FragmentDepartmentBinding
import com.atocash.fragments.admin.department.adapter.DepartmentAdapter
import com.atocash.network.response.DepartmentResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.atocash.utils.recycler.EvenMarginDecorator
import com.google.gson.Gson

class DepartmentFragment :
    SuperFragment<FragmentDepartmentBinding, DepartmentViewModel>(),
    DepartmentNavigator, DepartmentAdapter.CostCenterCallback {

    private lateinit var viewModel: DepartmentViewModel
    private lateinit var binding: FragmentDepartmentBinding

    private lateinit var adapter: DepartmentAdapter

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
        return R.layout.fragment_department
    }

    override fun getViewModel(): DepartmentViewModel {
        viewModel = ViewModelProvider(this).get(DepartmentViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        binding.addDepartment.setOnClickListener {
            intentHelper.goTo(requireActivity(), AdminDepartmentActivity::class.java, null, false)
        }

        adapter = DepartmentAdapter(ArrayList(), this)
        binding.departmentRv.layoutAnimation = getBottomScaleAnim()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.departmentRv.layoutManager = layoutManager
        binding.departmentRv.addItemDecoration(
            EvenMarginDecorator(
                16,
                EvenMarginDecorator.VERTICAL
            )
        )
        binding.departmentRv.adapter = adapter
    }

    private fun loadDepartment() {
        viewModel.isLoading.set(true)
//        viewModel.getAllDepartments(getToken()).observe(viewLifecycleOwner, Observer {
//            printLog("observed")
//            viewModel.isLoading.set(false)
//            if(!it.data.isNullOrEmpty()){
//                printLog("departments added to adapter")
//                adapter.addAll(it.data)
//            } else {
//                printLog("departments not added to adapter")
//            }
//        })

        viewModel.getDepartmentsList(getToken()).observe(viewLifecycleOwner, Observer {
            viewModel.isLoading.set(false)
            if (it.isNullOrEmpty().not()) {
                adapter.addAll(it)
            }
        })
    }

    override fun onEdit(item: DepartmentResponse) {
        val bundleArgs = Bundle()
        bundleArgs.putString("DepartmentItem", Gson().toJson(item))
        bundleArgs.putBoolean(Keys.IntentData.IS_EDIT, true)
        intentHelper.goTo(
            requireActivity(),
            AdminDepartmentActivity::class.java,
            bundle = bundleArgs
        )
    }

    override fun onDelete(item: DepartmentResponse) {
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

    private fun proceedToDelete(item: DepartmentResponse) {
        if (AppHelper.isNetworkConnected(requireContext())) {
            showLoading(getString(R.string.deleting))
            viewModel.deleteDepartment(getToken(), item).observe(viewLifecycleOwner, Observer {
                cancelLoading()
                when {
                    it.code() == 204 || it.code() == 200 -> {
                        requireContext().showShortToast(getString(R.string.info_department_deleted_success))
                        adapter.remove(item)
                    }
                    it.code() == 401 -> {
                        showUnAuthDialog()
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

    override fun showUnAuthDialog() {
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setMessage(getString(R.string.session_expired))
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE, getString(R.string.ok)
        ) { dialog_, which ->
            (requireActivity() as AdminMainActivity).proceedToLogout()
            dialog_.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    override fun onResume() {
        super.onResume()

        loadDepartment()
    }
}