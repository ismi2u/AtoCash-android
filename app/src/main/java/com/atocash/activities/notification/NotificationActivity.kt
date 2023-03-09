package com.atocash.activities.notification

import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.notification.adapter.NotificationAdapter
import com.atocash.base.view.SuperCompatActivity
import com.atocash.databinding.ActivityNotificationBinding
import com.atocash.network.MockData
import com.atocash.network.response.NotificationModel
import com.atocash.utils.Keys
import com.atocash.utils.extensions.getBottomScaleAnim
import com.atocash.utils.extensions.visible


class NotificationActivity :
    SuperCompatActivity<ActivityNotificationBinding, NotificationViewModel>(),
    NotificationNavigator, NotificationAdapter.NotificationCallback {

    private lateinit var viewModel: NotificationViewModel
    private lateinit var binding: ActivityNotificationBinding

    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    override fun getBindingVariable(): Int {
        return BR.notificationVm
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_notification
    }

    override fun getViewModel(): NotificationViewModel {
        viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        initBackWithTitle(binding.toolbar.toolParent, getString(R.string.title_notifications))

        showLoading()
        Handler().postDelayed({
            cancelLoading()
            loadNotifications()
        }, Keys.SPLASH_DURATION)
    }

    private fun loadNotifications() {
        adapter = NotificationAdapter(this, MockData.getNotifications(), this)
        binding.notificationsRv.layoutAnimation = getBottomScaleAnim()

        binding.notificationsRv.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.notificationsRv.adapter = adapter
        binding.notificationsRv.scheduleLayoutAnimation()
        binding.notificationsRv.visible(true)
    }

    override fun onNotificationClick(item: NotificationModel?) {
        showSnack("clicked notification")
    }
}
