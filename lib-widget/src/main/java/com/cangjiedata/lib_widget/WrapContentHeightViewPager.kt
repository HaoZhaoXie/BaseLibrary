package com.cangjiedata.lib_widget

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

/**
 * Create by Judge at 1/12/21
 */
class WrapContentHeightViewPager : ViewPager {
    /**
     * Constructor * * @param context the context
     */
    constructor(context: Context?) : super(context!!) {}

    /**
     * * Constructor * * @param context the context * @param attrs the attribute
     * set
     */
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasure: Int
        var height = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            val h = child.measuredHeight
            if (h > height) height = h
        }
        heightMeasure = MeasureSpec.makeMeasureSpec(
            height,
            MeasureSpec.EXACTLY
        )
        super.onMeasure(widthMeasureSpec, heightMeasure)
    }

    companion object {
        private const val TAG = "WrapContentHeightViewPager"
    }
}