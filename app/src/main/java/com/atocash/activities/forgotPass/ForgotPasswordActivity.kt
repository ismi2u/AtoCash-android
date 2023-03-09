package com.atocash.activities.forgotPass

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity :
    SuperCompatActivity<ActivityForgotPasswordBinding, ForgotPasswordViewModel>(),
    ForgotPasswordNavigator {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var viewModel: ForgotPasswordViewModel

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
        return R.layout.activity_forgot_password
    }

    override fun getViewModel(): ForgotPasswordViewModel {
        viewModel = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {

    }

    override fun onBackClick() {
        onBackPressed()
    }

    override fun onSubmit() {

    }
}