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

        fun hide(view:View){
            view.animate()
                .alpha(0f)
                .translationYBy(80f)
                .withEndAction { view.visibility = View.GONE }
                .start()
        }

        fun show(view:View){
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .withStartAction { view.visibility = View.VISIBLE }
                .start()
        }

    }

    constructor() : super()
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    var mY = -1f

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        Log.d(TAG,"layoutDependsOn:$dependency")
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {

//        Log.d(TAG,"onDependentViewChanged:$dependency")

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