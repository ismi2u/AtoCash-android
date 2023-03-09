package com.atocash.utils.locale

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import com.atocash.utils.extensions.printLog
import java.util.*


object LocaleUtil {
    private lateinit var locale: Locale

    fun setLocalAndConfig(locale: Locale, context: Context) {
        this.locale = locale
        Locale.setDefault(LocaleUtil.locale)
        val res: Resources = context.resources
        val dm: DisplayMetrics = res.displayMetrics
        val configuration: Configuration = res.configuration
        configuration.setLocale(LocaleUtil.locale)
        res.updateConfiguration(configuration, dm)
    }

    fun setLocale(localeIn: Locale) {
        locale = localeIn
        Locale.setDefault(locale)
        printLog("localeIn.language ${localeIn.language}")
    }

    fun setConfigChange(context: Context) {
        Locale.setDefault(locale)
        val res: Resources = context.resources
        val dm: DisplayMetrics = res.displayMetrics
        val configuration: Configuration = res.configuration
        configuration.setLocale(locale)
        res.updateConfiguration(configuration, dm)
    }
}
