package com.cangjiedata.lib_widget.nested

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.*
import kotlin.math.max

/**
 * Create by Judge at 2021/6/15
 */
class NestedConstraintLayout : ConstraintLayout, NestedScrollingParent3, NestedScrollingChild3, ScrollingView {
    private var mParentHelper: NestedScrollingParentHelper? = null
    private var mChildHelper: NestedScrollingChildHelper? = null

    constructor(@NonNull context: Context) : super(context) {}
    constructor(@NonNull context: Context, @Nullable attrs: AttributeSet?) : super(context, attrs) {}
    constructor(@NonNull context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mParentHelper = NestedScrollingParentHelper(this)
        mChildHelper = NestedScrollingChildHelper(this)
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return mChildHelper!!.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        mChildHelper!!.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return mChildHelper!!.hasNestedScrollingParent(type)
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int, consumed: IntArray) {
        mChildHelper!!.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed)
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int): Boolean {
        return mChildHelper!!.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?, type: Int): Boolean {
        return mChildHelper!!.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        mParentHelper!!.onNestedScrollAccepted(child, target, axes, type)
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        mParentHelper!!.onStopNestedScroll(target, type)
        stopNestedScroll(type)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray) {
        onNestedScrollInternal(dyUnconsumed, type, consumed)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        onNestedScrollInternal(dyUnconsumed, ViewCompat.TYPE_TOUCH, null)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        dispatchNestedPreScroll(dx, dy, consumed, null, type)
    }

    private fun onNestedScrollInternal(dyUnconsumed: Int, type: Int, consumed: IntArray?) {
        val oldScrollY = scrollY
        scrollBy(0, dyUnconsumed)
        val myConsumed = scrollY - oldScrollY
        if (consumed != null) {
            consumed[1] += myConsumed
        }
        val myUnconsumed = dyUnconsumed - myConsumed
        mChildHelper!!.dispatchNestedScroll(0, myConsumed, 0, myUnconsumed, null, type, consumed)
    }

    override fun computeHorizontalScrollExtent(): Int {//1
        return super.computeHorizontalScrollExtent()
    }

    override fun computeHorizontalScrollOffset(): Int {//1
        return super.computeHorizontalScrollOffset()
    }

    override fun computeHorizontalScrollRange(): Int {//1
        return super.computeHorizontalScrollRange()
    }

    override fun computeVerticalScrollExtent(): Int {//1
        return super.computeVerticalScrollExtent()
    }

    override fun computeVerticalScrollOffset(): Int {//1
        return super.computeVerticalScrollOffset()
    }

    override fun computeVerticalScrollRange(): Int {//1
        val count = childCount
        val parentSpace = height - paddingBottom - paddingTop
        if (count == 0) {
            return parentSpace
        }

        val child = getChildAt(0)
        val lp = child.layoutParams as LayoutParams
        var scrollRange = child.bottom + lp.bottomMargin
        val scrollY = scrollY
        val overscrollBottom = max(0, scrollRange - parentSpace)
        if (scrollY < 0) {
            scrollRange -= scrollY
        } else if (scrollY > overscrollBottom) {
            scrollRange += scrollY - overscrollBottom
        }

        return scrollRange
    }
}