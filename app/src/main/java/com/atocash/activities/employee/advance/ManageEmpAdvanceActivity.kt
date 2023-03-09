package com.atocash.activities.employee.advance

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityManageEmpAdvanceBinding

class ManageEmpAdvanceActivity :
    SuperCompatActivity<ActivityManageEmpAdvanceBinding, ManageEmpAdvanceViewModel>(),
    ManageEmpAdvanceNavigator {

    private lateinit var binding: ActivityManageEmpAdvanceBinding
    private lateinit var viewModel: ManageEmpAdvanceViewModel

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
        return R.layout.activity_manage_emp_advance
    }

    override fun getViewModel(): ManageEmpAdvanceViewModel {
        viewModel = ViewModelProvider(this).get(ManageEmpAdvanceViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        initBackWithTitle(binding.toolbar.toolParent, getString(R.string.manage_travel_request))

        binding.projectSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.isProjectEnabled.set(isChecked)
        }
    }

    override fun onCreate() {
        showSnack("Clicked create")
    }

    override fun onBack() {
        onBackPressed()
    }
}