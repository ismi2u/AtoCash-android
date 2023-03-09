package com.atocash.utils.recycler

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * Created by geniuS on 6/11/2019.
 */
class RightSideDecoration //    @Retention(RetentionPolicy.SOURCE)
//    @IntDef({TOP, BOTTOM, RIGHT, LEFT})
//    public @interface SideSpec{}
//
//    public static final int TOP = 1;
//    public static final int RIGHT = 2;
//    public static final int BOTTOM = 3;
//    public static final int LEFT = 4;
//
//    private SideSpec side;
@JvmOverloads constructor(private val spacing: Int, private var displayMode: Int = -1) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildViewHolder(view).adapterPosition
        val itemCount = state.itemCount
        val layoutManager = parent.layoutManager
        setSpacingForDirection(outRect, layoutManager, position, itemCount)
    }

    private fun setSpacingForDirection(
        outRect: Rect,
        layoutManager: RecyclerView.LayoutManager?,
        position: Int,
        itemCount: Int
    ) { // Resolve display mode automatically
        if (displayMode == -1) {
            displayMode = resolveDisplayMode(layoutManager)
        }
        when (displayMode) {
            HORIZONTAL ->  //                outRect.left = spacing;
                outRect.right = if (position == itemCount - 1) spacing else 0
            VERTICAL ->  //                outRect.left = spacing;
                outRect.right = spacing
            GRID -> if (layoutManager is GridLayoutManager) {
                val cols = layoutManager.spanCount
                val rows = itemCount / cols
                //                    outRect.left = spacing;
                outRect.right = if (position % cols == cols - 1) spacing else 0
                //                    outRect.top = spacing;
//                    outRect.bottom = position / cols == rows - 1 ? spacing : 0;
            }
        }
    }

    private fun resolveDisplayMode(layoutManager: RecyclerView.LayoutManager?): Int {
        if (layoutManager is GridLayoutManager) return GRID
        return if (layoutManager!!.canScrollHorizontally()) HORIZONTAL else VERTICAL
    }

    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
        const val GRID = 2
    }

}