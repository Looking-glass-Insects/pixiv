package com.eps3rd.app

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

class BottomAreaBehavior : CoordinatorLayout.Behavior<View> {

    companion object{
        const val TAG = "BottomAreaBehavior"
        var mAnim = false

        fun hide(view:View){
            if (view.visibility == View.GONE || mAnim)return
            view.animate()
                .alpha(0f)
                .translationY(80f)
                .withStartAction {
                    mAnim = true
                }
                .withEndAction {
                    view.visibility = View.GONE
                    mAnim = false
                }
                .start()
        }

        fun show(view:View){
            if (view.visibility == View.VISIBLE || mAnim)return
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .withStartAction {
                    view.visibility = View.VISIBLE
                    mAnim = true
                }
                .withEndAction { mAnim = false }
                .start()
        }

    }

    constructor() : super()
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    var mY = -1f

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return !mAnim
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
        if (dyConsumed > 0 || dyUnconsumed > 0){
            hide(child)
        }
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        if (mY == -1f){
            mY = dependency.y
            return false
        }

        if (mY > dependency.y){ // up
            Log.d(TAG,"UP")
            hide(child)
        }else if (mY < dependency.y) {
            Log.d(TAG,"DOWN")
            show(child)
        }
        mY = dependency.y
        return false
    }
}