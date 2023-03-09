package com.atocash.activities.notification.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.atocash.R
import com.atocash.activities.notification.adapter.NotificationAdapter.Notifications
import com.atocash.network.response.NotificationModel
import java.util.*

/**
 * Created by geniuS on 5/20/2019.
 */
class NotificationAdapter(
    private val context: Context, private val items: ArrayList<NotificationModel>?,
    private val callback: NotificationCallback
) : RecyclerView.Adapter<Notifications>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Notifications {
        return Notifications(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notification, parent, false)
        )
    }

    override fun onBindViewHolder(holder: Notifications, position: Int) {
        holder.bindViews(position)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    inner class Notifications(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private var notificationCv: CardView = itemView.findViewById(R.id.notification_item)
        private var notifyTextTv: TextView = itemView.findViewById(R.id.notification_msg)
        private var notifyDateTv: TextView = itemView.findViewById(R.id.notification_time)

        fun bindViews(position: Int) {
            val item = items!![position]

            notifyDateTv.text = item.time
            notifyTextTv.text = item.message
            notificationCv.setOnClickListener { v: View? ->
                callback.onNotificationClick(
                    item
                )
            }
        }

    }

    interface NotificationCallback {
        fun onNotificationClick(item: NotificationModel?)
    }

}