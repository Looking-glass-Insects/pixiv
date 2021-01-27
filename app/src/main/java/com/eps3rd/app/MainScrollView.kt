package com.eps3rd.app

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView
import kotlin.math.abs

class MainScrollView(context: Context, attrs: AttributeSet?) : NestedScrollView(context, attrs) {
    private var mCallback: CallBack? = null
    private var mDownX: Float = 0f
    private var mDownY: Float = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = ev.x
                mDownY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                val curX = ev.x
                val curY = ev.y
                if (abs(curX - mDownX) > abs(curY - mDownY)){
                    return false
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    interface CallBack {
        fun isAtTop(): Boolean
    }
}