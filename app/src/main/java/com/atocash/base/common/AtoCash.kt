package com.atocash.base.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.atocash.R
import com.atocash.utils.locale.LocaleManager

/**
 * Created by geniuS on 26/03/2020.
 */
class AtoCash : MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleManager.setLocale(base))
        MultiDex.install(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }

    override fun onCreate() {
        super.onCreate()

        //create notification channel above oreo
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val description = getString(R.string.default_notification_channel_id)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(
                description,
                name, importance
            )
            mChannel.description = description
            val notificationManager = (getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager)
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    companion object {
        private fun get(context: Context): AtoCash {
            return context.applicationContext as AtoCash
        }

        fun init(context: Context): AtoCash {
            return get(context)
        }
    }

}