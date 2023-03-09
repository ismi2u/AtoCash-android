package com.atocash.activities.language

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.login.LoginActivity
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityChooseLanguageBinding
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.showShortToast
import com.atocash.utils.locale.LocaleManager
import com.atocash.utils.locale.LocaleUtil
import java.util.*

class ChooseLanguageActivity :
    SuperCompatActivity<ActivityChooseLanguageBinding, ChooseLanguageViewModel>(),
    ChooseLanguageNavigator {

    private lateinit var viewModel: ChooseLanguageViewModel
    private lateinit var binding: ActivityChooseLanguageBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_choose_language
    }

    override fun getViewModel(): ChooseLanguageViewModel {
        viewModel = ViewModelProvider(this).get(ChooseLanguageViewModel::class.java)
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    private fun initViewsAndClicks() {

    }

    override fun onLanguageChosen() {
        when {
            binding.rbArab.isChecked -> {
                setLangAs(LocaleManager.ARABIC)
            }
            binding.rbEng.isChecked -> {
                setLangAs(LocaleManager.ENGLISH)
            }
            else -> {
                showShortToast(getString(R.string.choose_language))
            }
        }
    }

    private fun setLangAs(language: String) {
        printLog("Chosen language: $language")
        dataStorage.saveString(Keys.LANGUAGE_PREF, language)
        LocaleManager.setNewLocale(this, language)
        val appLocale = Locale(LocaleManager.getLanguagePref(this))
        LocaleUtil.setLocale(appLocale)
        LocaleUtil.setConfigChange(this)
        intentHelper.goTo(this, LoginActivity::class.java, null, false)
    }
}