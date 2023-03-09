package com.atocash.activities.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.activities.language.ChooseLanguageActivity
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivitySplashBinding
import com.atocash.network.connectivity.RetrofitClient
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog
import com.atocash.utils.locale.LocaleManager
import com.atocash.utils.locale.LocaleUtil
import java.util.*


class SplashActivity :
    SuperCompatActivity<ActivitySplashBinding, SplashViewModel>(),
    SplashNavigator {

    private lateinit var viewModel: SplashViewModel
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        setUpBaseUrl()
    }

    override fun getBindingVariable(): Int {
        return BR.splashVm
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun getViewModel(): SplashViewModel {
        viewModel = ViewModelProvider(this)[SplashViewModel::class.java]
        return viewModel
    }

    private fun setUpBaseUrl() {
        if(!dataStorage.getBoolean(Keys.IS_LOGGED_IN)) {
            setBaseUrlAndOpenNextScreen()
            return
        }

        if(dataStorage.getString(Keys.BASE_URL).isNullOrEmpty()) {
            setBaseUrlAndOpenNextScreen()
            return
        }

        RetrofitClient.instance.setBaseUrlForLoggedInUser(dataStorage.getString(Keys.BASE_URL).toString())
        setLanguageAndOpen()
    }

    private fun setBaseUrlAndOpenNextScreen() {
        val url = RetrofitClient.instance.formBaseUrl("")
        dataStorage.saveString(Keys.BASE_URL, url)
        setLanguageAndOpen()
    }

    private fun setLanguageAndOpen() {
        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                if (isLoggedIn()) {
                    printLog("Chosen language: ${LocaleManager.getLanguagePref(this)}")
                    val appLocale = Locale(LocaleManager.getLanguagePref(this))
                    LocaleUtil.setLocale(appLocale)
                    LocaleUtil.setConfigChange(this)

                    intentHelper.goTo(
                        this,
                        AdminMainActivity::class.java,
                        true
                    )
                } else {
                    intentHelper.goTo(this@SplashActivity, ChooseLanguageActivity::class.java, true)
                }
            }, Keys.SPLASH_DURATION)
        }
    }
}
