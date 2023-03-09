package com.atocash.base.view

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.atocash.R
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.base.common.BaseViewModel
import com.atocash.utils.AppHelper
import com.atocash.utils.DataStorage
import com.atocash.utils.IntentHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.roundOffToTwoDecimal
import com.atocash.utils.extensions.showShortSnack
import com.atocash.utils.locale.LocaleManager
import com.atocash.utils.locale.LocaleUtil
import com.atocash.utils.permissions.PermissionsHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

abstract class SuperCompatActivity<T : ViewDataBinding, V : BaseViewModel<*>> :
    AppCompatActivity() {

    private lateinit var viewDataBinding: T
    private lateinit var viewModel: V

    lateinit var dataStorage: DataStorage
    lateinit var intentHelper: IntentHelper
    lateinit var permissionsHelper: PermissionsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        performDataBinding()
        init()
    }

    private fun performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        viewModel = getViewModel()
        viewDataBinding.setVariable(getBindingVariable(), viewModel)
        viewDataBinding.lifecycleOwner = this
        viewDataBinding.executePendingBindings()
    }

    abstract fun getBindingVariable(): Int

    open fun getViewDataBinding(): T {
        return viewDataBinding
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun getViewModel(): V

    private lateinit var unAuthDialog: AlertDialog
    private fun init() {
        dataStorage = DataStorage(this)
        intentHelper = IntentHelper(this)
        permissionsHelper = PermissionsHelper(this)
        unAuthDialog = AlertDialog.Builder(this).create()
    }

    val isNetworkConnected: Boolean
        get() = AppHelper.isNetworkConnected(this)

    fun isLoggedIn(): Boolean {
        return dataStorage.getBoolean(Keys.IS_LOGGED_IN)
    }

    fun getToken(): String {
        return dataStorage.getString(Keys.UserData.TOKEN).toString()
    }

    fun hasMultipleRoles(): Boolean {
        return dataStorage.getBoolean(Keys.UserData.HAS_MULTIPLE_LOGINS)
    }

    fun getRole(): String {
        return dataStorage.getString(Keys.UserData.USER_ROLE).toString()
    }

    fun isAlsoAnEmployee(): Boolean {
        return if (hasMultipleRoles()) {
            val rolesStr = dataStorage.getString(Keys.UserData.USER_ROLE).toString()
            val token: TypeToken<ArrayList<String>> =
                object : TypeToken<ArrayList<String>>() {}
            val roles: ArrayList<String> = Gson().fromJson(rolesStr, token.type)
            roles.contains(Keys.LoginType.EMPLOYEE)
        } else {
            dataStorage.getString(Keys.UserData.USER_ROLE) == Keys.LoginType.EMPLOYEE
        }
    }

    fun openNextWithLoginType() {
//        if (!hasMultipleRoles()) {
//            if (getRole() == Keys.LoginType.EMPLOYEE) {
//                //show user view
//                intentHelper.goTo(
//                    this,
//                    EmployeeMainActivity::class.java,
//                    true
//                )
//            } else {
//                //show admin view
//                openAdmin()
//            }
//        } else {
//            //show admin view
//            openAdmin()
//        }
        openAdmin()
    }

    private fun openAdmin() {
        intentHelper.goTo(
            this,
            AdminMainActivity::class.java,
            true
        )
    }

    fun getUserId(): String {
        return dataStorage.getString(Keys.UserData.ID).toString()
    }

    fun withCurrency(amount: Float): String {
        val roundedValue = amount.roundOffToTwoDecimal().toString()
        return roundedValue + " " + dataStorage.getString(Keys.UserData.CURRENCY_CODE).toString()
    }

    fun showSnack(message: String?) {
        val view = findViewById<View>(android.R.id.content)
        message?.let { view.showShortSnack(it) }
    }

    private var lastAppliedColor: Int = R.color.black
    fun changeStatusBarColor(@ColorRes barColor: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (lastAppliedColor != barColor) {
                window.apply {
                    printLog("status bar color applied")
                    statusBarColor = getColorFromRes(barColor)
                    lastAppliedColor = getColorFromRes(barColor)
                }
            }
        }
    }

    private fun getColorFromRes(barColor: Int): Int {
        return ContextCompat.getColor(this, barColor)
    }

    private var mProgressDialog: ProgressDialog? = null

    fun showLoading(msg: String) {
        cancelLoading()
        mProgressDialog = AppHelper.showLoadingDialog(this, msg)
    }

    fun showLoading() {
        cancelLoading()
        mProgressDialog = AppHelper.showLoadingDialog(this, getString(R.string.loading))
    }

    fun cancelLoading() {
        mProgressDialog?.let {
            if (it.isShowing) it.cancel()
        }
    }

    fun initBackWithTitle(view: View, title: String) {
        view.findViewById<TextView>(R.id.tool_tv).text = title
        view.findViewById<ImageView>(R.id.back_iv).setOnClickListener {
            onBackPressed()
        }
    }

    fun showUnAuthDialog() {
        if (::unAuthDialog.isInitialized) {
            if (unAuthDialog.isShowing) return
            unAuthDialog.setMessage("Your session has been expired, login again!")
            unAuthDialog.setButton(
                AlertDialog.BUTTON_POSITIVE, "Ok"
            ) { dialog, which ->
                proceedToLogout()
                dialog.dismiss()
            }
            unAuthDialog.setCancelable(false)
            unAuthDialog.show()
        }
    }

    fun askAndLogout() {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setMessage(getString(R.string.ask_and_logout))
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            getString(R.string.yes)
        ) { dialog_, which ->
            dialog_?.dismiss()
            proceedToLogout()
        }
        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            getString(R.string.no)
        ) { dialog_, which ->
            dialog_?.dismiss()
        }
        dialog.show()
    }

    fun proceedToLogout() {
        dataStorage.removeAllData()
        intentHelper.openSplash(this)
    }

    open fun setNewLocale(
        localeIntent: Intent,
        language: String
    ) {
        printLog("Chosen language: $language")
        dataStorage.saveString(Keys.LANGUAGE_PREF, language)
        LocaleManager.setNewLocale(this, language)
        startActivity(localeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}