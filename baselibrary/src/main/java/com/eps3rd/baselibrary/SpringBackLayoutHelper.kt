package com.eps3rd.baselibrary

import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup

class SpringBackLayoutHelper(private val mTarget: ViewGroup, var mTargetScrollOrientation: Int) {
    private val mTouchSlop: Int
    var mInitialDownY = 0f
    var mInitialDownX = 0f
    var mActivePointerId = -1
    var mScrollOrientation = 0
    fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        checkOrientation(ev)
        return if (mScrollOrientation != 0 && mScrollOrientation != mTargetScrollOrientation) {
            mTarget.requestDisallowInterceptTouchEvent(true)
            false
        } else {
            mTarget.requestDisallowInterceptTouchEvent(false)
            true
        }
    }

    fun checkOrientation(ev: MotionEvent) {
        val action = ev.actionMasked
        val pointerIndex: Int
        when (action) {
            0 -> {
                mActivePointerId = ev.getPointerId(0)
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    return
                }
                mInitialDownY = ev.getY(pointerIndex)
                mInitialDownX = ev.getX(pointerIndex)
                mScrollOrientation = 0
            }
            1, 3 -> {
                mScrollOrientation = 0
                mTarget.requestDisallowInterceptTouchEvent(false)
            }
            2 -> {
                if (mActivePointerId == -1) {
                    return
                }
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    return
                }
                val y = ev.getY(pointerIndex)
                val x = ev.getX(pointerIndex)
                val yDiff = y - mInitialDownY
                val xDiff = x - mInitialDownX
                if (Math.abs(xDiff) > mTouchSlop.toFloat() || Math.abs(
                        yDiff
                    ) > mTouchSlop.toFloat()
                ) {
                    mScrollOrientation =
                        if (Math.abs(xDiff) > Math.abs(yDiff)) 1 else 2
                }
            }
        }
    }

    companion object {
        private const val INVALID_POINTER = -1
    }

    init {
        mTouchSlop = ViewConfiguration.get(mTarget.context).scaledTouchSlop
    }
}