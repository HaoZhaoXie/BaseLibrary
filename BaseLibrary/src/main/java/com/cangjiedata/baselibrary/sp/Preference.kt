package com.cangjiedata.baselibrary.sp

import android.content.Context
import com.cangjiedata.baselibrary.BaseApplication
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 通过 Kotlin 中的委托功能，实现的一个 SharedPreference 的代理类
 * 实现基本数据类型的存取
 */
class Preference<T>(
    private val name: String?,
    private val default: T,
    /**
     * 数据保存到  01010  文件
     */
    private val FILE_NAME: String? = "SupperCity"
) :
    ReadWriteProperty<Any?, T> {
    private val prefs by lazy {
        BaseApplication.get().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = with(prefs) {

        val res: Any? = when (default) {
            is Long -> {
                getLong(name, 0)
            }
            is String -> {
                getString(name, default)
            }
            is Float -> {
                getFloat(name, default)
            }
            is Int -> {
                getInt(name, default)
            }
            is Boolean -> {
                getBoolean(name, default)
            }
            else -> {
                throw IllegalArgumentException("This type can't be saved into Preferences")
            }
        }
        res as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Float -> putFloat(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            else -> {
                throw IllegalArgumentException("This type can't be saved into Preferences")
            }
        }.apply()
    }

    fun clearSp() = with(prefs.edit()) {
        clear()
        commit()
    }

    fun remove() = with(prefs.edit()) {
        remove(name)
        commit()
    }

}

//private var userId: String by Preference(this, "userIdName", "")
//
//private fun testUserId() {
//    if (userId.isEmpty()) {
//        println("userId is empty")
//        userId = "default userId2"// 这里已经写入了SharedPreference;
//    } else {
//        println("userId is $userId")
//    }
//}