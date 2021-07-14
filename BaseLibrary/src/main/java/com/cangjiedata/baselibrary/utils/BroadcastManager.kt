package com.cangjiedata.baselibrary.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
import com.cangjiedata.baselibrary.constant.ACTION_UPDATE_SINGLE_DATA
import com.cangjiedata.baselibrary.constant.KEY_TARGET
import com.cangjiedata.baselibrary.constant.KEY_TARGET_DATA
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.Serializable
import java.util.*

class BroadcastManager private constructor() {
    /**
     * 添加
     */
    fun register(mContext: Context, action: String, receiver: BroadcastReceiver?) {
        try {
            val filter = IntentFilter()
            filter.addAction(action)
            mContext.registerReceiver(receiver, filter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 发送广播
     *
     * @param action 唯一码
     * @param obj    参数
     */
    fun sendBroadcastWithObject(mContext: Context, action: String, obj: Any?) {
        try {
            val intent = Intent()
            intent.action = action
            intent.putExtra("result", Gson().toJson(obj))
            mContext.sendBroadcast(intent)
        } catch (e: HttpException) {
            e.printStackTrace()
        }
    }

    fun sendBroadcast(mContext: Context, action: String, obj: Parcelable?) {
        val intent = Intent()
        intent.action = action
        intent.putExtra("result", obj)
        mContext.sendBroadcast(intent)
    }

    fun sendBroadcast(mContext: Context, action: String, obj: Serializable?) {
        val intent = Intent()
        intent.action = action
        intent.putExtra("result", obj)
        mContext.sendBroadcast(intent)
    }
    /**
     * 发送参数为 String 的数据广播
     *
     * @param action
     * @param s
     */
    /**
     * 发送广播
     *
     * @param action 唯一码
     */
    @JvmOverloads
    fun sendBroadcast(mContext: Context, action: String, s: String? = "") {
        val intent = Intent()
        intent.action = action
        intent.putExtra("String", s)
        mContext.sendBroadcast(intent)
    }

    /**
     * 发送参数为 int 的数据广播
     *
     * @param action
     * @param s
     */
    fun sendBroadcast(mContext: Context, action: String, s: Int) {
        val intent = Intent()
        intent.action = action
        intent.putExtra("INT", s)
        mContext.sendBroadcast(intent)
    }

    /**
     * 发送参数为 String 的数据广播
     *
     * @param action
     * @param s
     */
    fun sendBroadcast(mContext: Context, action: String, s: Boolean) {
        val intent = Intent()
        intent.action = action
        intent.putExtra("Boolean", s)
        mContext.sendBroadcast(intent)
    }

    fun sendUpdateBroadcast(mContext: Context, action: String, bundle: Bundle) {
        val intent = Intent()
        intent.action = ACTION_UPDATE_SINGLE_DATA
        intent.putExtra(KEY_TARGET, action)
        intent.putExtra(KEY_TARGET_DATA, bundle)
        mContext.sendBroadcast(intent)
    }

    /**
     * 销毁广播
     *
     * @param receiver
     */
    fun unregister(mContext: Context, receiver: BroadcastReceiver) {
        try {
            mContext.unregisterReceiver(receiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        val instance: BroadcastManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { BroadcastManager() }
    }
}