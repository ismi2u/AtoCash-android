package com.atocash.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager

/**
 * Created by geniuS on 12/19/2019.
 */
class NoSwipeViewPager : ViewPager {

    constructor(context: Context?) : super(context!!) {
        setUpScroller()
    }

    constructor(
        context: Context?,
        attributeSet: AttributeSet?
    ) : super(context!!, attributeSet) {
        setUpScroller()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return false
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return false
    }

    override fun canScroll(
        view: View,
        checkV: Boolean,
        dx: Int,
        x: Int,
        y: Int
    ): Boolean {
        return view !== this || view !is ViewPager
    }

    private fun setUpScroller() {
        try {
            val viewPager: Class<*> = ViewPager::class.java
            val scroller = viewPager.getDeclaredField("mScroller")
            scroller.isAccessible = true
            scroller[this] = MyScroller(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class MyScroller internal constructor(context: Context?) :
        Scroller(context) {
        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            super.startScroll(startX, startY, dx, dy)
        }

        override fun startScroll(
            startX: Int,
            startY: Int,
            dx: Int,
            dy: Int,
            duration: Int
        ) {
            super.startScroll(startX, startY, dx, dy, duration)
        }
    }
}