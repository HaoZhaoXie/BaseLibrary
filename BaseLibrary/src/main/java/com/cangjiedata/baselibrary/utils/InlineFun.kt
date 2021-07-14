package com.cangjiedata.baselibrary.utils

import android.view.View
import com.cangjiedata.baselibrary.R

/**
 * Create by Judge at 1/13/21
 */
inline fun <reified T> isThis(value: Any) = value is T

inline fun <T: View> T.setOnClickListenerSingle(delay:Long = 250, crossinline block:(T) ->Unit){
    triggerDelay = delay
    setOnClickListener {
        if (clickEnable()) {
            block(this)
        }
    }
}

/**
 * 给view添加一个延迟的属性（用来屏蔽连击操作）
 */
var <T : View> T.triggerDelay: Long
    get() = if (getTag(R.id.triggerDelayKey) != null) getTag(R.id.triggerDelayKey) as Long else -1
    set(value) {
        setTag(R.id.triggerDelayKey, value)
    }


/**
 * 给view添加一个上次触发时间的属性（用来屏蔽连击操作）
 */
var <T : View>T.triggerLastTime: Long
    get() = if (getTag(R.id.triggerLastTimeKey) != null) getTag(R.id.triggerLastTimeKey) as Long else 0
    set(value) {
        setTag(R.id.triggerLastTimeKey, value)
    }


/**
 * 判断时间是否满足再次点击的要求（控制点击）
 */
fun <T : View> T.clickEnable(): Boolean {
    var clickable = false
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= triggerDelay) {
        clickable = true
    }
    triggerLastTime = currentClickTime
    return clickable
}
