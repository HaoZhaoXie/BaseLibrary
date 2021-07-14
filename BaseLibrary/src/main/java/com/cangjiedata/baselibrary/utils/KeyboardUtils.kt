package com.cangjiedata.baselibrary.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * 键盘相关工具类
 */
object KeyboardUtils {

    /**
     * 动态显示软键盘
     *
     * @param activity activity
     */
    fun showSoftInput(activity: Activity) {
        var view = activity.currentFocus
        if (view == null) view = View(activity)
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            ?: return
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    /**
     * 动态隐藏软键盘
     *
     * @param activity activity
     */
    fun hideSoftInput(activity: Activity) {
        var view = activity.currentFocus
        if (view == null) view = View(activity)
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            ?: return
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}