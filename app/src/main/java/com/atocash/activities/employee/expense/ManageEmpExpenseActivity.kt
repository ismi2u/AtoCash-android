package com.atocash.activities.employee.expense

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.atocash.R
import com.atocash.BR
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityManageEmpExpenseBinding
import com.atocash.databinding.ActivityManageJobRolesBinding

class ManageEmpExpenseActivity :
    SuperCompatActivity<ActivityManageEmpExpenseBinding, ManageEmpExpenseViewModel>(),
    ManageEmpExpenseNavigator {

    private lateinit var binding: ActivityManageEmpExpenseBinding
    private lateinit var viewModel: ManageEmpExpenseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_manage_emp_expense
    }

    override fun getViewModel(): ManageEmpExpenseViewModel {
        viewModel = ViewModelProvider(this).get(ManageEmpExpenseViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        initBackWithTitle(binding.toolbar.toolParent, getString(R.string.manage_expense))
    }

    override fun onCreate() {
        showSnack("Clicked create")
    }

    override fun onBack() {
        showSnack("Clicked back")
    }
}