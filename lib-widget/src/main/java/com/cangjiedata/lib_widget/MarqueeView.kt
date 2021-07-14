package com.cangjiedata.lib_widget

import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.PointF
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

/**
 * Create by Judge at 2021/6/2
 */
class MarqueeView : LinearLayout {
    private var mContext: Context? = null
    private var mRv: RecyclerView? = null
    private var marqueeRunnable: MarqueeRunnable? = null
    private var mStandDuration = MILLISECONDS_MARQUEE_STAND
    private var mTurningDuration = MILLISECONDS_MARQUEE_TURNING
    private var mMarqueeHeight = 100
    private var mScrollItemCount = 1
    private var mShowItemCount = 1
//    private val TAG = "MarqueeTag"
    private var marqueeScrollListener: MarqueeScrollListener? = null
    private var layoutManager: SmoothScrollLinearLayoutManager? = null

    constructor(context: Context) : this(context, null) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        mContext = context
        if (attrs != null) {
            val systemAttrs = intArrayOf(android.R.attr.layout_height)
            var a: TypedArray = context.obtainStyledAttributes(attrs, systemAttrs)
            val height: Int = a.getDimensionPixelSize(0, ViewGroup.LayoutParams.MATCH_PARENT)
            mMarqueeHeight = height
            a = context.obtainStyledAttributes(attrs, R.styleable.MarqueeViewStyle)
            mScrollItemCount = a.getInteger(R.styleable.MarqueeViewStyle_scrollItemCount, 1)
            mShowItemCount = a.getInteger(R.styleable.MarqueeViewStyle_showItemCount, 1)
            mStandDuration = a.getInteger(R.styleable.MarqueeViewStyle_standDuration, MILLISECONDS_MARQUEE_STAND)
            mTurningDuration = a.getInteger(R.styleable.MarqueeViewStyle_turningDuration, MILLISECONDS_MARQUEE_TURNING)
            a.recycle()
        }
//        if (BuildConfig.DEBUG) {
//            Log.i(TAG, "item count $mScrollItemCount")
//        }
        mRv = RecyclerView(context)
        mRv?.setHasFixedSize(true)
        layoutManager = SmoothScrollLinearLayoutManager(context)
        layoutManager?.orientation = LinearLayoutManager.VERTICAL
        mRv?.layoutManager = layoutManager
        marqueeScrollListener = MarqueeScrollListener()
        mRv?.clearOnScrollListeners()
        mRv?.addOnScrollListener(marqueeScrollListener!!)
        marqueeRunnable = MarqueeRunnable()
        synchronized(this) {
            if (marqueeHandler == null) {
                marqueeHandler = Handler()
            }
        }
        addView(mRv)
        mRv?.layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        mRv?.layoutParams?.height = mMarqueeHeight
    }

    fun startScroll() {
        val llm: LinearLayoutManager = mRv?.layoutManager as LinearLayoutManager
        if (llm.itemCount > mShowItemCount) {
            if (marqueeHandler != null && marqueeRunnable != null) {
                marqueeHandler?.removeCallbacks(marqueeRunnable!!)
                marqueeHandler?.postDelayed(marqueeRunnable!!, mStandDuration.toLong())
            }
        } else {
            marqueeRunnable?.let { marqueeHandler?.removeCallbacks(it) }
        }
    }

    fun setAdapter(adapter: Adapter<*>?) {
        mRv?.adapter = adapter
    }

    private inner class MarqueeScrollListener internal constructor() : RecyclerView.OnScrollListener() {
       override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> {
//                    if (BuildConfig.DEBUG) {
//                        Log.i(TAG, " sroll state idle ")
//                    }
                    val llm: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val itemCount: Int = llm.itemCount
//                    if (BuildConfig.DEBUG) {
//                        Log.i(TAG, "itemcount " + itemCount + " showitemcout " + mShowItemCount + " lastvisiable " + llm.findLastVisibleItemPosition())
//                    }
                    if (itemCount > mShowItemCount) {
//                        if (itemCount == llm.findLastVisibleItemPosition() + 1) {
//                            recyclerView.scrollToPosition(0)
//                        }
                        startScroll()
                    }
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

        }
    }

    private fun smoothNextPosition(rv: RecyclerView?) {
        if (rv != null) {
            val llm: LinearLayoutManager = rv.layoutManager as LinearLayoutManager
            val lastVisibleItemPosition: Int = llm.findLastVisibleItemPosition()
            val totalCount: Int = llm.itemCount
            if (totalCount == lastVisibleItemPosition + 1) {
                rv.scrollToPosition(0)
                rv.smoothScrollToPosition(0)
            }else{
                val nextPosition: Int = llm.findLastVisibleItemPosition() + mScrollItemCount
                if (nextPosition < totalCount) {
                    rv.smoothScrollToPosition(nextPosition)
                }
            }
        }
    }

    private inner class MarqueeRunnable : Runnable {
        override fun run() {
            smoothNextPosition(mRv)
//            if (BuildConfig.DEBUG) {
//                Log.i(TAG, "smooth next position ")
//            }
        }
    }

   override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP) {
            ev.action = MotionEvent.ACTION_DOWN
            mRv?.dispatchTouchEvent(ev)
            ev.action = MotionEvent.ACTION_UP
            mRv?.dispatchTouchEvent(ev)
        }
        super.dispatchTouchEvent(ev)
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (ev.action == MotionEvent.ACTION_DOWN) {
            true
        } else super.onInterceptTouchEvent(ev)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (marqueeHandler != null) {
            marqueeRunnable?.let { marqueeHandler?.removeCallbacks(it) }
            mRv?.scrollToPosition(0)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
//        if (BuildConfig.DEBUG) {
//            Log.i(TAG, "onacctached")
//        }
        startScroll()
    }

    companion object {
        private var marqueeHandler: Handler? = null
        private const val MILLISECONDS_MARQUEE_STAND = 3000
        private const val MILLISECONDS_MARQUEE_TURNING = 3000
    }
}

class SmoothScrollLinearLayoutManager(var context: Context) : LinearLayoutManager(context) {
    private var MILLISECONDS_PER_INCH = 10f
    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
        val linearSmoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return this@SmoothScrollLinearLayoutManager.computeScrollVectorForPosition(targetPosition)
            }

            //This returns the milliseconds it takes to
            //scroll one pixel.
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return MILLISECONDS_PER_INCH / displayMetrics.density
                //返回滑动一个pixel需要多少毫秒
            }
        }
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }

    fun setSpeedSlow() {
        //自己在这里用density去乘，希望不同分辨率设备上滑动速度相同
        //0.3f是自己估摸的一个值，可以根据不同需求自己修改
        MILLISECONDS_PER_INCH = context.resources.displayMetrics.density * 0.3f
    }

    fun setSpeedFast() {
        MILLISECONDS_PER_INCH = context.resources.displayMetrics.density * 0.03f
    }

    private var isScrollEnabled = true
    fun setScrollEnabled(flag: Boolean) {
        isScrollEnabled = flag
    }

    override fun canScrollVertically(): Boolean {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically()
    }

}