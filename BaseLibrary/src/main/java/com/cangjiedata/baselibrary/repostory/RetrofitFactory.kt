package com.cangjiedata.baselibrary.repostory

import android.text.TextUtils
import com.cangjiedata.baselibrary.BaseApplication
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Create by Judge at 1/4/21
 */
class RetrofitFactory {
    private var mRetrofit: Retrofit


    companion object {
        val instance: RetrofitFactory by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitFactory()
        }
    }

    fun <T> getService(service: Class<T>, baseUrl: String = ""): T {
        return instance.getRetrofitBuilder().apply {
            if(!TextUtils.isEmpty(baseUrl)){
                baseUrl(baseUrl)
            }
        }.build().create(service)
    }

    init {
        this.mRetrofit = getRetrofit()
    }

    private fun getOkHttpClientBuilder(): OkHttpClient.Builder {
        return BaseApplication.get().onConfigHttpClient()
    }

    private fun getRetrofit(): Retrofit {
        getOkHttpClientBuilder()
        // 创建Retrofit
        return getRetrofitBuilder().build()
    }

    private fun getRetrofitBuilder(): Retrofit.Builder {
        val builder = getOkHttpClientBuilder()
        return Retrofit.Builder()
            .client(builder.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BaseApplication.get().getBaseUrl())
    }
}