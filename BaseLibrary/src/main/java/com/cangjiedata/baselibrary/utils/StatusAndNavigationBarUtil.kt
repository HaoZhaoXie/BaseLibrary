package com.cangjiedata.baselibrary.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout

/**
* 设置Status和 NavigationBar颜色
*/
object StatusAndNavigationBarUtil {

    fun setStatusBarColor(activity: Activity, color: Int) {
        when {
            Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP -> {
                //5.0以上可以直接设置 statusbar颜色
                activity.window.statusBarColor = color
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                //4.4~5.0之间自己设置
                //设置statusbar隐藏
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                //创建statusbarview设置背景 高度等于系统的statusbar高度
                val statusBarView = getStatusBarView(activity)
                statusBarView.setBackgroundColor(color)
                //获得contentview 并添加创建的statusbarview
                val decorView = activity.window.decorView as ViewGroup
                val params = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity))
                params.gravity = Gravity.TOP
                statusBarView.layoutParams = params
                decorView.addView(statusBarView)
            }
            else -> {
                //4.4以下无法设置statusbar颜色
            }
        }
    }

    private fun getStatusBarView(activity: Activity?): View {
        return View(activity)
    }

    fun getStatusBarHeight(context: Context): Int {
        var height = 0
        val id = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (id > 0) {
            height = context.resources.getDimensionPixelSize(id)
        }
        return height
    }

    fun setNavigationBarColor(activity: Activity, color: Int) {
        when {
            Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP -> {
                //5.0以上可以直接设置 navigation颜色
                activity.window.navigationBarColor = color
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                // android:clipToPadding="false"
                //android:fitsSystemWindows="true"
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                val decorView = activity.window.decorView as ViewGroup
                val mNavigationBar = getNavigationBarView(activity)
                val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, getNavigationBarHeight(activity))
                params.gravity = Gravity.BOTTOM
                mNavigationBar.layoutParams = params
                mNavigationBar.setBackgroundColor(color)
                decorView.addView(mNavigationBar)
            }
            else -> {
                //4.4以下无法设置navigationbar颜色
            }
        }
    }

    private fun getNavigationBarView(activity: Activity?): View {
        return View(activity)
    }

    fun getNavigationBarHeight(context: Context): Int {
        var height = 0
        val id = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (id > 0) {
            height = context.resources.getDimensionPixelSize(id)
        }
        return height
    }
}