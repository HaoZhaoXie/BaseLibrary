package com.cangjiedata.lib_widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.widget.NestedScrollView

/**
 * Create by Judge at 2021/6/15
 */
class LetterNestedScrollView : NestedScrollView {
    private val TAG = "LetterNestedScrollView"
    var letterWidth = 0
    constructor(@NonNull context: Context) : super(context) {}
    constructor(@NonNull context: Context, @Nullable attrs: AttributeSet?) : super(context, attrs) {}
    constructor(@NonNull context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.e(TAG, "onInterceptTouchEvent: ${ev.x}  ${ev.rawX}" )
        return if(ev.x < resources.displayMetrics.widthPixels - letterWidth) {
            super.onInterceptTouchEvent(ev)
        }else{
            false
        }
    }
}
