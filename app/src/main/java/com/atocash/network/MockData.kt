package com.atocash.network

import com.atocash.fragments.admin.department.adapter.DepartmentModel
import com.atocash.network.response.*

object MockData {

    val userNames = arrayOf("admin", "manager", "user")

    fun getNotifications(): ArrayList<NotificationModel> {
        val notificationList = ArrayList<NotificationModel>()
        notificationList.add(
            NotificationModel(
                "You have received a new request and this is notification",
                "2 mins ago"
            )
        )
        notificationList.add(
            NotificationModel(
                "You have received a new notification from the admin and manager ",
                "5 mins ago"
            )
        )
        notificationList.add(NotificationModel("Payment request has been approved", "5 hrs ago"))
        notificationList.add(
            NotificationModel(
                "You have received a new notification from the admin and manager ",
                "15 hrs ago"
            )
        )
        return notificationList
    }
}