package com.eps3rd.app

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout

class DrawerCollapseBehavior : CoordinatorLayout.Behavior<View> {
    constructor() : super()
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        mCollapsedSize =
            context?.resources?.getDimensionPixelSize(R.dimen.drawer_user_bar_size) ?: 0
    }

    private var mCollapsedSize: Int = 0

    companion object {
        const val TAG = "DrawerCollapseBehavior"
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return child is ConstraintLayout && axes == View.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        if (dy == 0) return
        if (dy > 0) {
            if (child.height + child.translationY - dy <= mCollapsedSize) {
                child.translationY = (mCollapsedSize - child.height).toFloat()
                target.translationY = mCollapsedSize.toFloat()
                return
            }
            child.translationY -= dy
            target.translationY -= dy
            consumed[1] = dy
        }/* else {
            if (child.translationY >= 0) {
                child.translationY = 0f
                target.translationY = child.height.toFloat()
                return
            }
            child.translationY -= dy
            target.translationY -= dy
            consumed[1] = dy
        }*/
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        if (dyUnconsumed < 0){
            if (child.translationY - dyUnconsumed >= 0) {
                child.translationY = 0f
                target.translationY = child.height.toFloat()
                return
            }
            child.translationY -= dyUnconsumed
            target.translationY -= dyUnconsumed
        }
    }



}