package com.atocash.activities.login

import android.graphics.Paint
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.BuildConfig.PROD_SERVER_URL
import com.atocash.R
import com.atocash.activities.forgotPass.ForgotPasswordActivity
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityLoginBinding
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.network.request.LoginRequest
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.google.gson.Gson


class LoginActivity :
    SuperCompatActivity<ActivityLoginBinding, LoginViewModel>(),
    LoginNavigator {

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    override fun getBindingVariable(): Int {
        return BR.splashVm
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun getViewModel(): LoginViewModel {
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        binding.forgotPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        binding.loginBtn.setOnClickListener {
            onLoginClick()
        }

        viewModel.loginError.observe(this) {
            it?.let { error ->
                if (error.isNotEmpty()) {
                    printLog("login error")
                    cancelLoading()
                    showErrorResponse(null)
                    viewModel.loginError.value = ""
                }
            }
        }
    }

    override fun onForgotPasswordClick() {
        intentHelper.goTo(
            this@LoginActivity,
            ForgotPasswordActivity::class.java,
            null,
            false
        )
    }

    override fun onLoginClick() {
        if (isUserNameAndPasswordAvail()) {
            askForSubDomainLogin()
        }
    }

    var url = ""
    private fun askForSubDomainLogin() {
        printLog("Keys.Api.BASE_URL ${Keys.Api.BASE_URL}")
        printLog("Keys.Api.PROD_URL $PROD_SERVER_URL")

        url = RetrofitClient.instance.formBaseUrl(binding.userNameEd.text.toString().trim())
        printLog("reformed base url before login -> $url")
        proceedToLogin()
    }

    private fun proceedToLogin() {
        showLoading()
        viewModel.doLogin(getLoginRequest()).observe(this) {
            cancelLoading()
            when (it.code()) {
                200 -> {
                    val response = it.body()
                    response?.let { response_ ->
                        val role = response_.role
                        if (role.isEmpty().not()) {
                            showShortToast(getString(R.string.login_success))
                            dataStorage.saveBoolean(Keys.IS_LOGGED_IN, true)
                            dataStorage.saveString(Keys.BASE_URL, url)

                            //checking if the one of role is also an employee (user)
                            if (role.isNotEmpty() && role.size > 1) {
                                //has multiple roles
                                dataStorage.saveString(Keys.UserData.USER_ROLE, Gson().toJson(role))
                                dataStorage.saveBoolean(Keys.UserData.HAS_MULTIPLE_LOGINS, true)
                                if (role.contains(Keys.LoginType.EMPLOYEE)) {
                                    //has user roles also
                                    //saving boolean to show inbox in the home page
                                    dataStorage.saveBoolean(Keys.UserData.IS_ALSO_EMPLOYEE, true)
                                }
                            } else {
                                //only single role
                                dataStorage.saveString(Keys.UserData.USER_ROLE, role[0])
                                dataStorage.saveBoolean(Keys.UserData.HAS_MULTIPLE_LOGINS, false)
                            }

                            dataStorage.saveString(
                                Keys.UserData.AUTH,
                                "Bearer " + response_.token
                            )
                            dataStorage.saveString(
                                Keys.UserData.TOKEN,
                                "Bearer " + response_.token
                            )
                            dataStorage.saveString(
                                Keys.UserData.FIRST_NAME,
                                response_.firstName
                            )
                            dataStorage.saveString(Keys.UserData.LAST_NAME, response_.lastName)
                            dataStorage.saveString(Keys.UserData.EMAIL, response_.email)
                            dataStorage.saveString(
                                Keys.UserData.CURRENCY_CODE,
                                response_.currencyCode
                            )
                            dataStorage.saveInt(Keys.UserData.CURRENCY_ID, response_.currencyId)
                            dataStorage.saveString(Keys.UserData.ID, response_.empId)
                            openNextWithLoginType()
                        } else {
                            showShortToast(getString(R.string.no_roles_found))
                        }
                    }
                }
                405 -> showErrorResponse(getString(R.string.some_error_occurred))
                else -> showErrorResponse(it.errorBody()?.string())
            }
        }
    }

    override fun onApiError(message: String) {
        cancelLoading()
        showSnack(message)
    }

    private fun getLoginRequest(): LoginRequest {
        return LoginRequest(
            binding.userNameEd.text.toString().trim(),
            binding.passwordEd.text.toString().trim(),
            false
        )
    }

    private fun isUserNameAndPasswordAvail(): Boolean {
        AppHelper.hideKeyboard(this)

        val userName = binding.userNameEd.text.toString().trim()
        val password = binding.passwordEd.text.toString().trim()

        printLog("Username: $userName")
        printLog("Password: $password")

        if (userName.isEmpty()) {
            showSnack(getString(R.string.enter_user_name))
            return false
        }

        if (password.isEmpty()) {
            showSnack(getString(R.string.enter_password))
            return false
        }

        return true
    }


}
