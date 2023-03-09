package com.atocash.activities.employee.main

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.notification.NotificationActivity
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityEmployeeMainBinding
import com.atocash.dialog.UserProfileDialog
import com.atocash.utils.IntentHelper

class EmployeeMainActivity :
    SuperCompatActivity<ActivityEmployeeMainBinding, EmployeeMainViewModel>(),
    EmployeeMainNavigator {

    private lateinit var binding: ActivityEmployeeMainBinding
    private lateinit var viewModel: EmployeeMainViewModel

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.white)))

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.employee_host_fragment)
        return NavigationUI.navigateUp(
            navController,
            appBarConfiguration
        ) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.admin_tool_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_account -> {
                showAccountDialog()
            }
//            R.id.action_notification -> {
//                IntentHelper(this).goTo(this, NotificationActivity::class.java, false)
//            }
        }
        return true
    }

    private fun showAccountDialog() {
        UserProfileDialog(dataStorage, object : UserProfileDialog.ProfileDialogCallback {
            override fun onHelp() {
                showSnack("show help and support")
            }

            override fun onLogout() {
                askAndLogout()
            }

        }).show(supportFragmentManager, "Profile Dialog")
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_employee_main
    }

    override fun getViewModel(): EmployeeMainViewModel {
        viewModel = ViewModelProvider(this).get(EmployeeMainViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        setSupportActionBar(binding.empToolbar)

        val navController = findNavController(R.id.employee_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_expense,
                R.id.navigation_advance,
                R.id.navigation_travel_request,
                R.id.navigation_report
            )
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }
}