package com.eps3rd.baselibrary

import android.view.animation.AnimationUtils

class SpringScroller {
    private var mStartTime: Long = 0
    private var mCurrentTime: Long = 0
     var mCurrX = 0.0
     var mCurrY = 0.0
    private var mSpringOperator: SpringOperator? = null
    private var mEndX = 0.0
    private var mStartX = 0.0
    private var mOriginStartX = 0.0
    private var mEndY = 0.0
    private var mStartY = 0.0
    private var mOriginStartY = 0.0
    private var mOriginVelocity = 0.0
    private var mVelocity = 0.0
    private var mOrientation = 0
    var isFinished = true
        private set
    private var mLastStep = false
    fun scrollByFling(
        startX: Float,
        endX: Float,
        startY: Float,
        endY: Float,
        velocity: Float,
        orientation: Int
    ) {
        isFinished = false
        mLastStep = false
        mStartX = startX.toDouble()
        mOriginStartX = startX.toDouble()
        mEndX = endX.toDouble()
        mStartY = startY.toDouble()
        mOriginStartY = startY.toDouble()
        mCurrY = mStartY.toInt().toDouble()
        mEndY = endY.toDouble()
        mOriginVelocity = velocity.toDouble()
        mVelocity = velocity.toDouble()
        if (Math.abs(mVelocity) <= 5000.0) {
            mSpringOperator = SpringOperator(0.9f, 0.3f)
        } else {
            mSpringOperator = SpringOperator(0.65f, 0.3f)
        }
        mOrientation = orientation
        mStartTime = AnimationUtils.currentAnimationTimeMillis()
    }

    fun computeScrollOffset(): Boolean {
        return if (mSpringOperator != null && !isFinished) {
            if (mLastStep) {
                isFinished = true
                true
            } else {
                mCurrentTime =
                    AnimationUtils.currentAnimationTimeMillis()
                var delta =
                    (mCurrentTime - mStartTime).toFloat() / 1000.0f
                delta = Math.min(delta, 0.016f)
                delta = if (delta == 0.0f) 0.016f else delta
                mStartTime = mCurrentTime
                val currentVelocity: Double
                if (mOrientation == 2) {
                    currentVelocity = mSpringOperator!!.updateVelocity(
                        mVelocity,
                        delta,
                        mEndY,
                        mStartY
                    )
                    mCurrY = mStartY + currentVelocity * delta.toDouble()
                    mVelocity = currentVelocity
                    if (isAtEquilibrium(mCurrY, mOriginStartY, mEndY)) {
                        mLastStep = true
                        mCurrY = mEndY
                    } else {
                        mStartY = mCurrY
                    }
                } else {
                    currentVelocity = mSpringOperator!!.updateVelocity(
                        mVelocity,
                        delta,
                        mEndX,
                        mStartX
                    )
                    mCurrX = mStartX + currentVelocity * delta.toDouble()
                    mVelocity = currentVelocity
                    if (isAtEquilibrium(mCurrX, mOriginStartX, mEndX)) {
                        mLastStep = true
                        mCurrX = mEndX
                    } else {
                        mStartX = mCurrX
                    }
                }
                true
            }
        } else {
            false
        }
    }

    fun isAtEquilibrium(
        current: Double,
        start: Double,
        end: Double
    ): Boolean {
        return if (start < end && current > end) {
            true
        } else if (start > end && current < end) {
            true
        } else if (start == end && Math.signum(mOriginVelocity) != Math.signum(
                current
            )
        ) {
            true
        } else {
            Math.abs(current - end) < 1.0
        }
    }

    val currX: Int
        get() = mCurrX.toInt()

    val currY: Int
        get() = mCurrY.toInt()

    companion object {
        private const val VALUE_THRESHOLD = 1.0f
        private const val MAX_DELTA_TIME = 0.016f
    }
}