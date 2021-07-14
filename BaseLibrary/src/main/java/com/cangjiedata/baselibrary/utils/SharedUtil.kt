package com.cangjiedata.baselibrary.utils

import android.content.Context
import android.content.SharedPreferences
import com.cangjiedata.baselibrary.constant.*
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

object SharedUtil {
    /**
     * 数据保存到  01010  文件
     */
    const val FILE_NAME = "SupperCity"

    /**
     * 设置 String
     *
     * @param context
     * @param key     保存名
     * @param str     保存内容
     */
    fun setString(context: Context, key: String?, str: String) {
        put(context, key, str)
    }

    /**
     * 获取 String
     *
     * @param context
     * @param key        保存名
     * @param strDefault 默认内容
     * @return
     */
    fun getString(context: Context, key: String?, strDefault: String?): String {
        return get(context, key, strDefault).toString()
    }

    /**
     * 设置 Boolean
     *
     * @param context
     * @param key     保存名
     * @param bool    保存内容
     */
    fun setBoolean(context: Context, key: String?, bool: Boolean) {
        put(context, key, bool)
    }

    /**
     * 获取 Boolean
     *
     * @param context
     * @param key       保存名
     * @param isDefault 默认内容
     * @return
     */
    fun getBoolean(context: Context, key: String?, isDefault: Boolean): Boolean {
        return get(context, key, isDefault) as Boolean
    }

    /**
     * 设置 Int
     *
     * @param context
     * @param key     保存名
     * @param in      保存内容
     */
    fun setInt(context: Context, key: String?, `in`: Int) {
        put(context, key, `in`)
    }

    /**
     * 获取 Int
     *
     * @param context
     * @param key        保存名
     * @param intDefault 默认内容
     * @return
     */
    fun getInt(context: Context, key: String?, intDefault: Int): Int {
        return get(context, key, intDefault).toString().toInt()
    }

    /**
     * 设置 Int
     *
     * @param context
     * @param key     保存名
     * @param in      保存内容
     */
    fun setLong(context: Context, key: String?, `in`: Long) {
        put(context, key, `in`)
    }

    /**
     * 获取 Int
     *
     * @param context
     * @param key        保存名
     * @param intDefault 默认内容
     * @return
     */
    fun getLong(context: Context, key: String?, intDefault: Long): Long {
        return SharedUtil[context, key, intDefault].toString().toLong()
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    fun put(context: Context, key: String?, `object`: Any) {
        val sp = context.getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = sp.edit()
        if (`object` is String) {
            editor.putString(key, `object`)
        } else if (`object` is Int) {
            editor.putInt(key, `object`)
        } else if (`object` is Boolean) {
            editor.putBoolean(key, `object`)
        } else if (`object` is Float) {
            editor.putFloat(key, `object`)
        } else if (`object` is Long) {
            editor.putLong(key, `object`)
        } else {
            editor.putString(key, `object`.toString())
        }
        SharedPreferencesCompat.apply(editor)
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    operator fun get(context: Context, key: String?, defaultObject: Any?): Any? {
        val sp = context.getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
        if (defaultObject is String) {
            return sp.getString(key, defaultObject as String?)
        } else if (defaultObject is Int) {
            return sp.getInt(key, (defaultObject as Int?)!!)
        } else if (defaultObject is Boolean) {
            return sp.getBoolean(key, (defaultObject as Boolean?)!!)
        } else if (defaultObject is Float) {
            return sp.getFloat(key, (defaultObject as Float?)!!)
        } else if (defaultObject is Long) {
            return sp.getLong(key, (defaultObject as Long?)!!)
        }
        return null
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    fun remove(context: Context, key: String?) {
        val sp = context.getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = sp.edit()
        editor.remove(key)
        SharedPreferencesCompat.apply(editor)
    }

    /**
     * 清除所有数据
     *
     * @param context
     */
    fun clear(context: Context) {
        val sp = context.getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = sp.edit()
        editor.clear()
        SharedPreferencesCompat.apply(editor)
    }

    /**
     * 清除用户登录信息
     */
    fun clearUserInfo(context: Context){
        val sp = context.getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = sp.edit()
        editor.putString(USER_BEAN, "")
        editor.putString(ACCESS_TOKEN, "")
        editor.putString(USER_ID, "")
        editor.putBoolean(IS_LOGIN, false)
        editor.putString(MERCHANT_ID, "")
        SharedPreferencesCompat.apply(editor)
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context
     * @param key
     * @return
     */
    fun contains(context: Context, key: String?): Boolean {
        val sp = context.getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
        return sp.contains(key)
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    fun getAll(context: Context): Map<String, *> {
        val sp = context.getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
        return sp.all
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private object SharedPreferencesCompat {
        private val sApplyMethod = findApplyMethod()

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        private fun findApplyMethod(): Method? {
            try {
                val clz: Class<*> = SharedPreferences.Editor::class.java
                return clz.getMethod("apply")
            } catch (e: NoSuchMethodException) {
            }
            return null
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        fun apply(editor: SharedPreferences.Editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor)
                    return
                }
            } catch (e: IllegalArgumentException) {
            } catch (e: IllegalAccessException) {
            } catch (e: InvocationTargetException) {
            }
            editor.commit()
        }
    }
}