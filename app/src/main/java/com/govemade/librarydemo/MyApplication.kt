package com.govemade.librarydemo

import com.cangjiedata.baselibrary.BaseApplication
import com.cangjiedata.baselibrary.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Create by Judge at 1/4/21
 */
class MyApplication : BaseApplication() {
    override fun onConfigHttpClient(): OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
        return super.onConfigHttpClient().addInterceptor(logging)
    }

    override fun initApp() {
        super.initApp()
    }
}