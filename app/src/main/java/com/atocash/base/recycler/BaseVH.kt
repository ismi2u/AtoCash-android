package com.atocash.base.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by geniuS on 12/12/2019.
 */
abstract class BaseVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onBind(position: Int)
}