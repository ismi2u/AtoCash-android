package com.atocash.activities.resetPass

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atocash.R
import com.atocash.BR
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity :
    SuperCompatActivity<ActivityResetPasswordBinding, ResetPasswordViewModel>(),
    ResetPasswordNavigator {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var viewModel: ResetPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    override fun getBindingVariable(): Int {
        return BR.resetViewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_reset_password
    }

    override fun getViewModel(): ResetPasswordViewModel {
        viewModel = ViewModelProvider(this).get(ResetPasswordViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {

    }
}