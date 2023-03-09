package com.atocash.activities.admin.main

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.atocash.BR
import com.atocash.R
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityAdminMainBinding
import com.atocash.dialog.UserProfileDialog
import com.atocash.fragments.admin.dashboard.AdminDashboardFragment
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog

class AdminMainActivity : SuperCompatActivity<ActivityAdminMainBinding, AdminMainViewModel>(),
    AdminMainNavigator {

    private lateinit var binding: ActivityAdminMainBinding
    private lateinit var viewModel: AdminMainViewModel

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navController: NavController

    private val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_up)
        .setExitAnim(R.anim.slide_down)
        .setPopEnterAnim(R.anim.slide_up)
        .setPopExitAnim(R.anim.slide_down)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        actionBarToggle.onConfigurationChanged(newConfig)
    }

    private fun closeDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.admin_tool_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_account -> {
                askAndLogout()
            }
//            R.id.action_notification -> {
//                intentHelper.goTo(this, NotificationActivity::class.java, false)
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

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.admin_host_fragment)
        return NavigationUI.navigateUp(
            navController,
            appBarConfiguration
        ) || super.onSupportNavigateUp()
    }

    override fun getBindingVariable(): Int {
        return BR.adminMainVm
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_admin_main
    }

    override fun getViewModel(): AdminMainViewModel {
        viewModel = ViewModelProvider(this).get(AdminMainViewModel::class.java)
        return viewModel
    }

    fun setPageTitle(title: String) {
//        binding.toolbar.title = title
    }

    private fun initViewsAndClicks() {
        setSupportActionBar(binding.toolbar)
        actionBarToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        binding.drawerLayout.addDrawerListener(actionBarToggle)
        actionBarToggle.syncState()
        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowCustomEnabled(true)
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_report,
                R.id.nav_cost_center,
                R.id.nav_department,
                R.id.nav_roles,
                R.id.nav_employee_type,
                R.id.nav_expense_type,
                R.id.nav_approval_strategy,
                R.id.nav_employee,
                R.id.nav_project,
                R.id.nav_sub_project,
                R.id.nav_task,
                R.id.nav_user,
                R.id.nav_currency,
                R.id.nav_user_roles,
                R.id.nav_assign_roles,
                R.id.nav_assign_project
            ), binding.drawerLayout
        )

        navController = Navigation.findNavController(this, R.id.admin_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navView, navController)

        setMenuForRoles()

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            closeDrawer()
            Handler().postDelayed({
                when (menuItem.itemId) {
                    R.id.nav_home -> navController.navigate(R.id.nav_home, null, animationOptions)
                    R.id.nav_report -> navController.navigate(
                        R.id.nav_report,
                        null,
                        animationOptions
                    )
                    R.id.nav_cost_center -> navController.navigate(
                        R.id.nav_cost_center,
                        null,
                        animationOptions
                    )
                    R.id.nav_department -> navController.navigate(
                        R.id.nav_department,
                        null,
                        animationOptions
                    )
                    R.id.nav_roles -> navController.navigate(R.id.nav_roles, null, animationOptions)
                    R.id.nav_employee_type -> navController.navigate(
                        R.id.nav_employee_type,
                        null,
                        animationOptions
                    )
                    R.id.nav_expense_type -> navController.navigate(
                        R.id.nav_expense_type,
                        null,
                        animationOptions
                    )
                    R.id.nav_approval_strategy -> navController.navigate(
                        R.id.nav_approval_strategy,
                        null,
                        animationOptions
                    )
                    R.id.nav_employee -> navController.navigate(
                        R.id.nav_employee,
                        null,
                        animationOptions
                    )
                    R.id.nav_project -> navController.navigate(
                        R.id.nav_project,
                        null,
                        animationOptions
                    )
                    R.id.nav_sub_project -> navController.navigate(
                        R.id.nav_sub_project,
                        null,
                        animationOptions
                    )
                    R.id.nav_task -> navController.navigate(R.id.nav_task, null, animationOptions)
                    R.id.nav_user -> navController.navigate(R.id.nav_user, null, animationOptions)
                    R.id.nav_currency -> navController.navigate(
                        R.id.nav_currency,
                        null,
                        animationOptions
                    )
                    R.id.nav_user_roles -> navController.navigate(
                        R.id.nav_user_roles,
                        null,
                        animationOptions
                    )
                    R.id.nav_assign_roles -> navController.navigate(
                        R.id.nav_assign_roles,
                        null,
                        animationOptions
                    )
                    R.id.nav_assign_project -> navController.navigate(
                        R.id.nav_assign_project,
                        null,
                        animationOptions
                    )
                }
            }, 350)
            return@setNavigationItemSelectedListener false
        }
    }

    private fun setMenuForRoles() {
        val navMenu = binding.navView.menu

        if (!hasMultipleRoles()) {
            when (getRole()) {
                Keys.LoginType.EMPLOYEE -> setMenuItemsForEmployee(navMenu)
                Keys.LoginType.MANAGER -> setMenuItemForManager(navMenu)
                Keys.LoginType.FINANCE_MANAGER -> {
                }
                Keys.LoginType.ADMIN -> {
                }
                Keys.LoginType.ATOMINOS_ADMIN -> {
                }
            }
        } else {
            val roles = getRole().split(",")

            if(roles.contains(Keys.LoginType.EMPLOYEE)) {
                if(roles.contains(Keys.LoginType.MANAGER)) {
                    setMenuItemForManager(navMenu)
                } else {
                    setMenuItemsForEmployee(navMenu)
                }
            } else if(roles.contains(Keys.LoginType.MANAGER)) {
                setMenuItemForManager(navMenu)
            }
        }
    }

    private fun setMenuItemForManager(navMenu: Menu) {
        navMenu.findItem(R.id.nav_cost_center).isVisible = false
        navMenu.findItem(R.id.nav_department).isVisible = false
        navMenu.findItem(R.id.nav_roles).isVisible = false
        navMenu.findItem(R.id.nav_employee).isVisible = false
        navMenu.findItem(R.id.nav_employee_type).isVisible = false
        navMenu.findItem(R.id.nav_expense_type).isVisible = false
        navMenu.findItem(R.id.nav_approval_strategy).isVisible = false
        navMenu.findItem(R.id.nav_project).isVisible = false
        navMenu.findItem(R.id.nav_sub_project).isVisible = false
        navMenu.findItem(R.id.nav_task).isVisible = false
        navMenu.findItem(R.id.nav_user).isVisible = false
        navMenu.findItem(R.id.nav_user_roles).isVisible = false
        navMenu.findItem(R.id.nav_currency).isVisible = false
        navMenu.findItem(R.id.nav_assign_roles).isVisible = false
        navMenu.findItem(R.id.nav_assign_project).isVisible = false
    }

    private fun setMenuItemsForEmployee(navMenu: Menu) {
        navMenu.findItem(R.id.nav_cost_center).isVisible = false
        navMenu.findItem(R.id.nav_department).isVisible = false
        navMenu.findItem(R.id.nav_report).isVisible = false
        navMenu.findItem(R.id.nav_roles).isVisible = false
        navMenu.findItem(R.id.nav_employee).isVisible = false
        navMenu.findItem(R.id.nav_employee_type).isVisible = false
        navMenu.findItem(R.id.nav_expense_type).isVisible = false
        navMenu.findItem(R.id.nav_approval_strategy).isVisible = false
        navMenu.findItem(R.id.nav_project).isVisible = false
        navMenu.findItem(R.id.nav_sub_project).isVisible = false
        navMenu.findItem(R.id.nav_task).isVisible = false
        navMenu.findItem(R.id.nav_user).isVisible = false
        navMenu.findItem(R.id.nav_user_roles).isVisible = false
        navMenu.findItem(R.id.nav_currency).isVisible = false
        navMenu.findItem(R.id.nav_assign_roles).isVisible = false
        navMenu.findItem(R.id.nav_assign_project).isVisible = false
    }

    private var backPressed: Long = 0
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return
        } else {
            if (getFragment(AdminDashboardFragment::class.java) != null) {
                when {
                    backPressed == 0L -> {
                        backPressed = System.currentTimeMillis()
                        showSnack(getString(R.string.press_again_exit))
                    }
                    System.currentTimeMillis() - backPressed <= 3500 -> {
                        finish()
                        finishAffinity()
                    }
                    else -> {
                        backPressed = 0
                        onBackPressed()
                    }
                }
            } else {
                navController.navigate(R.id.nav_home, null, animationOptions)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <F : Fragment> getFragment(fragmentClass: Class<F>): F? {
        val navHostFragment = this.supportFragmentManager.fragments.first() as NavHostFragment
        navHostFragment.childFragmentManager.fragments.forEach {
            if (fragmentClass.isAssignableFrom(it.javaClass)) {
                return it as F
            }
        }
        return null
    }

    fun showInbox() {
//        getFragment(EmployeeHomeFragment::class.java)
    }

}