package com.eps3rd.baselibrary

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Scroller
import androidx.core.widget.ListViewCompat
import java.util.*

class SpringBackLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null as AttributeSet?
) :
    ViewGroup(context, attrs) {
    private var mTarget: View? = null
    private val mTargetId: Int
    private val mTouchSlop: Int
    private var mInitialDownY = 0f
    private var mInitialMotionY = 0f
    private var mInitialDownX = 0f
    private var mInitialMotionX = 0f
    private var mIsBeingDragged = false
    private var mActivePointerId: Int
    private var mScrollOrientation = 0
    private var mOriginScrollOrientation: Int
    var springBackMode: Int
    private val mScroller: Scroller
    private val mSpringScroller: SpringScroller
    private val mHelper: SpringBackLayoutHelper
    private var mScreenWith = 0
    private var mScreenHeight = 0
    private val mOnScrollListeners: MutableList<OnScrollListener?>
    private var mOnSpringListener: OnSpringListener? = null
    private var mScrollState: Int
    override fun onFinishInflate() {
        super.onFinishInflate()
        ensureTarget()
    }

    fun setScrollOrientation(scrollOrientation: Int) {
        mOriginScrollOrientation = scrollOrientation
        mHelper.mTargetScrollOrientation = scrollOrientation
    }

    private fun supportTopSpringBackMode(): Boolean {
        return springBackMode and 1 != 0
    }

    private fun supportBottomSpringBackMode(): Boolean {
        return springBackMode and 2 != 0
    }

    fun setTarget(target: View?) {
        mTarget = target
    }

    private fun ensureTarget() {
        if (mTarget == null) {
            require(mTargetId != -1) { "invalid target Id" }
            mTarget = findViewById(mTargetId)
        }
        requireNotNull(mTarget) { "fail to get target" }
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        val width = this.measuredWidth
        val height = this.measuredHeight
        val childLeft = this.paddingLeft
        val childTop = this.paddingTop
        val childWidth = width - this.paddingLeft - this.paddingRight
        val childHeight = height - this.paddingTop - this.paddingBottom
        mTarget!!.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        var sizeHeight = MeasureSpec.getSize(heightMeasureSpec)
        measureChild(mTarget, widthMeasureSpec, heightMeasureSpec)
        if (sizeWidth > mTarget!!.measuredWidth) {
            sizeWidth = mTarget!!.measuredWidth
        }
        if (sizeHeight > mTarget!!.measuredHeight) {
            sizeHeight = mTarget!!.measuredHeight
        }
        setMeasuredDimension(
            if (widthMode == 1073741824) sizeWidth else mTarget!!.measuredWidth,
            if (heightMode == 1073741824) sizeHeight else mTarget!!.measuredHeight
        )
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            if (!mScroller.isFinished) {
                this.postInvalidateOnAnimation()
            } else {
                dispatchScrollState(0)
            }
        } else if (mSpringScroller.computeScrollOffset()) {
            scrollTo(mSpringScroller.currX, mSpringScroller.currY)
            if (!mSpringScroller.isFinished) {
                this.postInvalidateOnAnimation()
            }
        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        val var5: Iterator<*> = mOnScrollListeners.iterator()
        while (var5.hasNext()) {
            val listener: OnScrollListener =
                var5.next() as OnScrollListener
            listener.onScrolled(this, l - oldl, t - oldt)
        }
    }

    private val isVerticalTargetScrollToTop: Boolean
        private get() = if (mTarget is ListView) {
            !ListViewCompat.canScrollList(mTarget as ListView, -1)
        } else {
            !mTarget!!.canScrollVertically(-1)
        }

    private val isHorizontalTargetScrollToTop: Boolean
        private get() = !mTarget!!.canScrollHorizontally(-1)

    private fun isTargetScrollOrientation(orientation: Int): Boolean {
        return mScrollOrientation == orientation
    }

    private fun isTargetScrollToTop(orientation: Int): Boolean {
        return if (orientation == 2) {
            if (mTarget is ListView) {
                !ListViewCompat.canScrollList(mTarget as ListView, -1)
            } else {
                !mTarget!!.canScrollVertically(-1)
            }
        } else {
            !mTarget!!.canScrollHorizontally(-1)
        }
    }

    private fun isTargetScrollToBottom(orientation: Int): Boolean {
        return if (orientation == 2) {
            if (mTarget is ListView) {
                !ListViewCompat.canScrollList(mTarget as ListView, 1)
            } else {
                !mTarget!!.canScrollVertically(1)
            }
        } else {
            !mTarget!!.canScrollHorizontally(1)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.actionMasked == 0 && mScrollState == 2) {
            dispatchScrollState(1)
        }
        val handled = super.dispatchTouchEvent(ev)
        if (ev.actionMasked == 1 && mScrollState != 2) {
            dispatchScrollState(0)
        }
        return handled
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        if (!mScroller.isFinished && action == 0) {
            mScroller.forceFinished(true)
        }
        return if (this.isEnabled && mScroller.isFinished) {
            if (!supportTopSpringBackMode() && !supportBottomSpringBackMode()) {
                false
            } else {
                if (mOriginScrollOrientation and 4 != 0) {
                    checkOrientation(ev)
                    if (isTargetScrollOrientation(2) && mOriginScrollOrientation and 1 != 0) {
                        return false
                    }
                    if (isTargetScrollOrientation(1) && mOriginScrollOrientation and 2 != 0) {
                        return false
                    }
                    if (isTargetScrollOrientation(1) || isTargetScrollOrientation(2)) {
                        disallowParentInterceptTouchEvent(true)
                    }
                } else {
                    mScrollOrientation = mOriginScrollOrientation
                }
                if (isTargetScrollOrientation(2)) {
                    onVerticalInterceptTouchEvent(ev)
                } else {
                    if (isTargetScrollOrientation(1)) onHorizontalInterceptTouchEvent(
                        ev
                    ) else false
                }
            }
        } else {
            false
        }
    }

    private fun disallowParentInterceptTouchEvent(disallow: Boolean) {
        val parent = this.parent
        parent?.requestDisallowInterceptTouchEvent(disallow)
    }

    private fun checkOrientation(ev: MotionEvent) {
        mHelper.checkOrientation(ev)
        val action = ev.actionMasked
        when (action) {
            0 -> {
                mInitialDownY = mHelper.mInitialDownY
                mInitialDownX = mHelper.mInitialDownX
                mActivePointerId = mHelper.mActivePointerId
                mScrollOrientation = 0
            }
            1, 3 -> disallowParentInterceptTouchEvent(false)
            2 -> if (mScrollOrientation == 0 && mHelper.mScrollOrientation !== 0) {
                mScrollOrientation = mHelper.mScrollOrientation
            }
            4, 5 -> {
            }
            6 -> onSecondaryPointerUp(ev)
            else -> {
            }
        }
    }

    private fun onVerticalInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (!isTargetScrollToTop(2) && !isTargetScrollToBottom(2)) {
            false
        } else if (isTargetScrollToTop(2) && !supportTopSpringBackMode()) {
            false
        } else if (isTargetScrollToBottom(2) && !supportBottomSpringBackMode()) {
            false
        } else {
            val action = ev.actionMasked
            val pointerIndex: Int
            when (action) {
                0 -> {
                    scrollTo(this.scrollX, 0)
                    mActivePointerId = ev.getPointerId(0)
                    mIsBeingDragged = false
                    pointerIndex = ev.findPointerIndex(mActivePointerId)
                    if (pointerIndex < 0) {
                        return false
                    }
                    mInitialDownY = ev.getY(pointerIndex)
                }
                1, 3 -> {
                    mIsBeingDragged = false
                    mActivePointerId = -1
                }
                2 -> {
                    if (mActivePointerId == -1) {
                        Log.e(
                            "SpringBackLayout",
                            "Got ACTION_MOVE event but don't have an active pointer id."
                        )
                        return false
                    }
                    pointerIndex = ev.findPointerIndex(mActivePointerId)
                    if (pointerIndex < 0) {
                        Log.e(
                            "SpringBackLayout",
                            "Got ACTION_MOVE event but have an invalid active pointer id."
                        )
                        return false
                    }
                    val y = ev.getY(pointerIndex)
                    var yDiff = 0.0f
                    val bidirectional =
                        isTargetScrollToBottom(2) && isTargetScrollToTop(2)
                    if (!bidirectional && isTargetScrollToTop(2) || bidirectional && y > mInitialDownY) {
                        yDiff = y - mInitialDownY
                        if (yDiff > mTouchSlop.toFloat() && !mIsBeingDragged) {
                            mIsBeingDragged = true
                            dispatchScrollState(1)
                            mInitialMotionY = y
                        }
                    } else {
                        yDiff = mInitialDownY - y
                        if (yDiff > mTouchSlop.toFloat() && !mIsBeingDragged) {
                            mIsBeingDragged = true
                            dispatchScrollState(1)
                            mInitialMotionY = y
                        }
                    }
                }
                4, 5 -> {
                }
                6 -> onSecondaryPointerUp(ev)
                else -> {
                }
            }
            mIsBeingDragged
        }
    }

    private fun onHorizontalInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (!isTargetScrollToTop(1) && !isTargetScrollToBottom(1)) {
            false
        } else if (isTargetScrollToTop(1) && !supportTopSpringBackMode()) {
            false
        } else if (isTargetScrollToBottom(1) && !supportBottomSpringBackMode()) {
            false
        } else {
            val action = ev.actionMasked
            val pointerIndex: Int
            when (action) {
                0 -> {
                    scrollTo(0, this.scrollY)
                    mActivePointerId = ev.getPointerId(0)
                    mIsBeingDragged = false
                    pointerIndex = ev.findPointerIndex(mActivePointerId)
                    if (pointerIndex < 0) {
                        return false
                    }
                    mInitialDownX = ev.getX(pointerIndex)
                }
                1, 3 -> {
                    mIsBeingDragged = false
                    mActivePointerId = -1
                }
                2 -> {
                    if (mActivePointerId == -1) {
                        Log.e(
                            "SpringBackLayout",
                            "Got ACTION_MOVE event but don't have an active pointer id."
                        )
                        return false
                    }
                    pointerIndex = ev.findPointerIndex(mActivePointerId)
                    if (pointerIndex < 0) {
                        Log.e(
                            "SpringBackLayout",
                            "Got ACTION_MOVE event but have an invalid active pointer id."
                        )
                        return false
                    }
                    val x = ev.getX(pointerIndex)
                    var xDiff = 0.0f
                    val bidirectional =
                        isTargetScrollToBottom(1) && isTargetScrollToTop(1)
                    if (!bidirectional && isTargetScrollToTop(1) || bidirectional && x > mInitialDownX) {
                        xDiff = x - mInitialDownX
                        if (xDiff > mTouchSlop.toFloat() && !mIsBeingDragged) {
                            mIsBeingDragged = true
                            dispatchScrollState(1)
                            mInitialMotionX = x
                        }
                    } else {
                        xDiff = mInitialDownX - x
                        if (xDiff > mTouchSlop.toFloat() && !mIsBeingDragged) {
                            mIsBeingDragged = true
                            dispatchScrollState(1)
                            mInitialMotionX = x
                        }
                    }
                }
                4, 5 -> {
                }
                6 -> onSecondaryPointerUp(ev)
                else -> {
                }
            }
            mIsBeingDragged
        }
    }

    override fun requestDisallowInterceptTouchEvent(b: Boolean) {}
    fun internalRequestDisallowInterceptTouchEvent(b: Boolean) {
        super.requestDisallowInterceptTouchEvent(b)
    }

    fun requestDisallowParentInterceptTouchEvent(b: Boolean) {
        var parent = this.parent
        parent!!.requestDisallowInterceptTouchEvent(b)
        while (parent != null) {
            if (parent is SpringBackLayout) {
                parent.internalRequestDisallowInterceptTouchEvent(b)
            }
            parent = parent.parent
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        if (!mScroller.isFinished && action == 0) {
            mScroller.forceFinished(true)
        }
        return if (this.isEnabled && mScroller.isFinished) {
            if (isTargetScrollOrientation(2)) {
                onVerticalTouchEvent(ev)
            } else {
                if (isTargetScrollOrientation(1)) onHorizontalTouchEvent(ev) else false
            }
        } else {
            false
        }
    }

    private fun onHorizontalTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        return if (!isTargetScrollToTop(1) && !isTargetScrollToBottom(1)) {
            false
        } else if (isTargetScrollToTop(1) && isTargetScrollToBottom(1)) {
            onScrollEvent(ev, action, 1)
        } else {
            if (isTargetScrollToBottom(1)) onScrollUpEvent(
                ev,
                action,
                1
            ) else onScrollDownEvent(ev, action, 1)
        }
    }

    private fun onVerticalTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        return if (!isTargetScrollToTop(2) && !isTargetScrollToBottom(2)) {
            false
        } else if (isTargetScrollToTop(2) && isTargetScrollToBottom(2)) {
            onScrollEvent(ev, action, 2)
        } else {
            if (isTargetScrollToBottom(2)) onScrollUpEvent(
                ev,
                action,
                2
            ) else onScrollDownEvent(ev, action, 2)
        }
    }

    private fun onScrollEvent(ev: MotionEvent, action: Int, orientation: Int): Boolean {
        var pointerIndex: Int
        var overscroll: Float
        val scrollY: Float
        when (action) {
            0 -> {
                mActivePointerId = ev.getPointerId(0)
                mIsBeingDragged = false
            }
            1 -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(
                        "SpringBackLayout",
                        "Got ACTION_UP event but don't have an active pointer id."
                    )
                    return false
                }
                if (mIsBeingDragged) {
                    mIsBeingDragged = false
                    springBack(orientation)
                }
                mActivePointerId = -1
                return false
            }
            2 -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(
                        "SpringBackLayout",
                        "Got ACTION_MOVE event but have an invalid active pointer id."
                    )
                    return false
                }
                if (mIsBeingDragged) {
                    overscroll = 0.0f
                    if (orientation == 2) {
                        scrollY = ev.getY(pointerIndex)
                        overscroll =
                            Math.signum(scrollY - mInitialMotionY) * obtainSpringBackDistance(
                                scrollY - mInitialMotionY,
                                orientation
                            )
                    } else {
                        scrollY = ev.getX(pointerIndex)
                        overscroll =
                            Math.signum(scrollY - mInitialMotionX) * obtainSpringBackDistance(
                                scrollY - mInitialMotionX,
                                orientation
                            )
                    }
                    requestDisallowParentInterceptTouchEvent(true)
                    moveTarget(overscroll, orientation)
                }
            }
            3 -> return false
            4 -> {
            }
            5 -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(
                        "SpringBackLayout",
                        "Got ACTION_POINTER_DOWN event but have an invalid active pointer id."
                    )
                    return false
                }
                val currentPointerY: Float
                if (orientation == 2) {
                    overscroll = ev.getY(pointerIndex)
                    scrollY = overscroll - mInitialDownY
                    pointerIndex = ev.actionIndex
                    if (pointerIndex < 0) {
                        Log.e(
                            "SpringBackLayout",
                            "Got ACTION_POINTER_DOWN event but have an invalid action index."
                        )
                        return false
                    }
                    currentPointerY = ev.getY(pointerIndex)
                    mInitialDownY = currentPointerY - scrollY
                    mInitialMotionY = mInitialDownY
                } else {
                    overscroll = ev.getX(pointerIndex)
                    scrollY = overscroll - mInitialDownX
                    pointerIndex = ev.actionIndex
                    if (pointerIndex < 0) {
                        Log.e(
                            "SpringBackLayout",
                            "Got ACTION_POINTER_DOWN event but have an invalid action index."
                        )
                        return false
                    }
                    currentPointerY = ev.getX(pointerIndex)
                    mInitialDownX = currentPointerY - scrollY
                    mInitialMotionX = mInitialDownX
                }
                mActivePointerId = ev.getPointerId(pointerIndex)
            }
            6 -> onSecondaryPointerUp(ev)
            else -> {
            }
        }
        return true
    }

    private fun onScrollDownEvent(ev: MotionEvent, action: Int, orientation: Int): Boolean {
        var pointerIndex: Int
        var overscrollTop: Float
        val scrollY: Float
        when (action) {
            0 -> {
                mActivePointerId = ev.getPointerId(0)
                mIsBeingDragged = false
            }
            1 -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(
                        "SpringBackLayout",
                        "Got ACTION_UP event but don't have an active pointer id."
                    )
                    return false
                }
                if (mIsBeingDragged) {
                    mIsBeingDragged = false
                    springBack(orientation)
                }
                mActivePointerId = -1
                return false
            }
            2 -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(
                        "SpringBackLayout",
                        "Got ACTION_MOVE event but have an invalid active pointer id."
                    )
                    return false
                }
                if (mIsBeingDragged) {
                    overscrollTop = 0.0f
                    if (orientation == 2) {
                        scrollY = ev.getY(pointerIndex)
                        overscrollTop =
                            Math.signum(scrollY - mInitialMotionY) * obtainSpringBackDistance(
                                scrollY - mInitialMotionY,
                                orientation
                            )
                    } else {
                        scrollY = ev.getX(pointerIndex)
                        overscrollTop =
                            Math.signum(scrollY - mInitialMotionX) * obtainSpringBackDistance(
                                scrollY - mInitialMotionX,
                                orientation
                            )
                    }
                    if (overscrollTop <= 0.0f) {
                        return false
                    }
                    requestDisallowParentInterceptTouchEvent(true)
                    moveTarget(overscrollTop, orientation)
                }
            }
            3 -> return false
            4 -> {
            }
            5 -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(
                        "SpringBackLayout",
                        "Got ACTION_POINTER_DOWN event but have an invalid active pointer id."
                    )
                    return false
                }
                val currentPointerY: Float
                if (orientation == 2) {
                    overscrollTop = ev.getY(pointerIndex)
                    scrollY = overscrollTop - mInitialDownY
                    pointerIndex = ev.actionIndex
                    if (pointerIndex < 0) {
                        Log.e(
                            "SpringBackLayout",
                            "Got ACTION_POINTER_DOWN event but have an invalid action index."
                        )
                        return false
                    }
                    currentPointerY = ev.getY(pointerIndex)
                    mInitialDownY = currentPointerY - scrollY
                    mInitialMotionY = mInitialDownY
                } else {
                    overscrollTop = ev.getX(pointerIndex)
                    scrollY = overscrollTop - mInitialDownX
                    pointerIndex = ev.actionIndex
                    if (pointerIndex < 0) {
                        Log.e(
                            "SpringBackLayout",
                            "Got ACTION_POINTER_DOWN event but have an invalid action index."
                        )
                        return false
                    }
                    currentPointerY = ev.getX(pointerIndex)
                    mInitialDownX = currentPointerY - scrollY
                    mInitialMotionX = mInitialDownX
                }
                mActivePointerId = ev.getPointerId(pointerIndex)
            }
            6 -> onSecondaryPointerUp(ev)
            else -> {
            }
        }
        return true
    }

    private fun moveTarget(overscrollTop: Float, orientation: Int) {
        if (orientation == 2) {
            scrollTo(0, (-overscrollTop).toInt())
        } else {
            scrollTo((-overscrollTop).toInt(), 0)
        }
    }

    private fun springBack(orientation: Int) {
        if (mOnSpringListener == null || mOnSpringListener?.onSpringBack() != true) {
            mSpringScroller.scrollByFling(
                this.scrollX.toFloat(),
                0.0f,
                this.scrollY.toFloat(),
                0.0f,
                0.0f,
                orientation
            )
            this.postInvalidateOnAnimation()
        }
    }

    private fun onScrollUpEvent(ev: MotionEvent, action: Int, orientation: Int): Boolean {
        var pointerIndex: Int
        val overscrollBottom: Float
        val scrollY: Float
        when (action) {
            0 -> {
                mActivePointerId = ev.getPointerId(0)
                mIsBeingDragged = false
            }
            1 -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(
                        "SpringBackLayout",
                        "Got ACTION_UP event but don't have an active pointer id."
                    )
                    return false
                }
                if (mIsBeingDragged) {
                    mIsBeingDragged = false
                    springBack(orientation)
                }
                mActivePointerId = -1
                return false
            }
            2 -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(
                        "SpringBackLayout",
                        "Got ACTION_MOVE event but have an invalid active pointer id."
                    )
                    return false
                }
                if (mIsBeingDragged) {
                    if (orientation == 2) {
                        scrollY = ev.getY(pointerIndex)
                        overscrollBottom =
                            Math.signum(mInitialMotionY - scrollY) * obtainSpringBackDistance(
                                mInitialMotionY - scrollY,
                                orientation
                            )
                    } else {
                        scrollY = ev.getX(pointerIndex)
                        overscrollBottom =
                            Math.signum(mInitialMotionX - scrollY) * obtainSpringBackDistance(
                                mInitialMotionX - scrollY,
                                orientation
                            )
                    }
                    if (overscrollBottom <= 0.0f) {
                        return false
                    }
                    requestDisallowParentInterceptTouchEvent(true)
                    moveTarget(-overscrollBottom, orientation)
                }
            }
            3 -> return false
            4 -> {
            }
            5 -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(
                        "SpringBackLayout",
                        "Got ACTION_POINTER_DOWN event but have an invalid active pointer id."
                    )
                    return false
                }
                val currentPointerY: Float
                if (orientation == 2) {
                    overscrollBottom = ev.getY(pointerIndex)
                    scrollY = overscrollBottom - mInitialDownY
                    pointerIndex = ev.actionIndex
                    if (pointerIndex < 0) {
                        Log.e(
                            "SpringBackLayout",
                            "Got ACTION_POINTER_DOWN event but have an invalid action index."
                        )
                        return false
                    }
                    currentPointerY = ev.getY(pointerIndex)
                    mInitialDownY = currentPointerY - scrollY
                    mInitialMotionY = mInitialDownY
                } else {
                    overscrollBottom = ev.getX(pointerIndex)
                    scrollY = overscrollBottom - mInitialDownX
                    pointerIndex = ev.actionIndex
                    if (pointerIndex < 0) {
                        Log.e(
                            "SpringBackLayout",
                            "Got ACTION_POINTER_DOWN event but have an invalid action index."
                        )
                        return false
                    }
                    currentPointerY = ev.getX(pointerIndex)
                    mInitialDownX = currentPointerY - scrollY
                    mInitialMotionX = mInitialDownX
                }
                mActivePointerId = ev.getPointerId(pointerIndex)
            }
            6 -> onSecondaryPointerUp(ev)
            else -> {
            }
        }
        return true
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.actionIndex
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == mActivePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mActivePointerId = ev.getPointerId(newPointerIndex)
        }
    }

    private fun obtainSpringBackDistance(distance: Float, orientation: Int): Float {
        val range = if (orientation == 2) mScreenHeight else mScreenWith
        return if (range == 0) {
            Math.abs(distance) * 0.5f
        } else {
            val per =
                Math.min(Math.abs(distance) / range.toFloat(), 1.0f)
            (Math.pow(
                per.toDouble(),
                3.0
            ) / 3.0 - Math.pow(
                per.toDouble(),
                2.0
            ) + per.toDouble()).toFloat() * range.toFloat()
        }
    }

    fun smoothScrollTo(x: Int, y: Int) {
        smoothScrollBy(x - this.scrollX, y - this.scrollY)
    }

    fun smoothScrollBy(dx: Int, dy: Int) {
        if (dx != 0 || dy != 0) {
            mScroller.forceFinished(true)
            mScroller.startScroll(this.scrollX, this.scrollY, dx, dy, 350)
            dispatchScrollState(2)
            this.postInvalidateOnAnimation()
        }
    }

    private fun dispatchScrollState(state: Int) {
        if (mScrollState != state) {
            val var2: Iterator<*> = mOnScrollListeners.iterator()
            while (var2.hasNext()) {
                val listener: OnScrollListener =
                    var2.next() as OnScrollListener
                listener.onStateChanged(mScrollState, state)
            }
            mScrollState = state
        }
    }

    fun addOnScrollListener(onScrollListener: OnScrollListener?) {
        mOnScrollListeners.add(onScrollListener)
    }

    fun removeOnScrollListener(onScrollListener: OnScrollListener?) {
        mOnScrollListeners.remove(onScrollListener)
    }

    fun setOnSpringListener(onSpringListener: OnSpringListener?) {
        mOnSpringListener = onSpringListener
    }

    fun hasSpringListener(): Boolean {
        return mOnSpringListener != null
    }

    interface OnScrollListener {
        fun onStateChanged(var1: Int, var2: Int)
        fun onScrolled(var1: SpringBackLayout?, var2: Int, var3: Int)
    }

    interface OnSpringListener {
        fun onSpringBack(): Boolean
    }

    companion object {
        private const val TAG = "SpringBackLayout"
        const val UNCHECK_ORIENTATION = 0
        const val HORIZONTAL = 1
        const val VERTICAL = 2
        const val ANGLE = 4
        const val SPRING_BACK_TOP = 1
        const val SPRING_BACK_BOTTOM = 2
        private const val INVALID_ID = -1
        private const val INVALID_POINTER = -1
        private const val DRAG_RATE = 0.5f
        private const val SPRING_BACK_DURATION = 350
        const val STATE_IDLE = 0
        const val STATE_DRAGGING = 1
        const val STATE_SETTLING = 2
    }

    init {
        mActivePointerId = -1
        mOnScrollListeners = ArrayList()
        mScrollState = 0
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        val a = context.obtainStyledAttributes(attrs, R.styleable.SpringBackLayout)
        mTargetId = a.getResourceId(R.styleable.SpringBackLayout_scrollableView, -1)
        mOriginScrollOrientation = a.getInt(R.styleable.SpringBackLayout_scrollOrientation, 2)
        springBackMode = a.getInt(R.styleable.SpringBackLayout_springBackMode, 3)
        a.recycle()
        mScroller = Scroller(context)
        mSpringScroller = SpringScroller()
        mHelper = SpringBackLayoutHelper(this, mOriginScrollOrientation)
        val dm = DisplayMetrics()
        if (context is Activity) {
            context.windowManager.defaultDisplay.getMetrics(dm)
            mScreenWith = dm.widthPixels
            mScreenHeight = dm.heightPixels
        } else {
            mScreenWith = 0
            mScreenHeight = 0
        }
    }
}