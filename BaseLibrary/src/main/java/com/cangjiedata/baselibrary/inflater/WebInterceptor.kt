package com.cangjiedata.baselibrary.inflater

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.exception.NoRouteFoundException
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.service.PretreatmentService
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.alibaba.android.arouter.launcher.ARouter
import com.cangjiedata.baselibrary.BaseApplication
import com.cangjiedata.baselibrary.constant.Base
import com.cangjiedata.baselibrary.constant.KEY_WEB_URL
import com.cangjiedata.baselibrary.constant.WEB
import com.cangjiedata.baselibrary.utils.buildIntent
import com.cangjiedata.baselibrary.utils.doIntent

/**
 * Create by Judge at 1/13/21
 */
@Interceptor(priority = 2, name = "网页拦截器")
class WebInterceptor : IInterceptor {
    private var mContext: Context? = null
    override fun process(postcard: Postcard?, callback: InterceptorCallback?) {
        callback?.let {
            Log.i("result", "process  path: " + postcard!!.path)
            if (isExist(BaseApplication.get(), postcard.path)) {
                if (TextUtils.equals(postcard.path, WEB.PAGER_WEB)) {
                    if(postcard.uri != null){
                        if(postcard.uri.queryParameterNames.contains(KEY_WEB_URL)){
                            postcard.extras.putString(KEY_WEB_URL, postcard.uri.toString().split("${KEY_WEB_URL}=")[1])
                        }else{
                            postcard.extras.putString(KEY_WEB_URL, postcard.uri.toString())
                        }
//                        if (TextUtils.isEmpty(postcard.extras[KEY_WEB_URL] as String?)) {
//                            postcard.extras.putString(KEY_WEB_URL, postcard.uri.toString())
//                        }
                    }
                }else{
                    if(postcard.uri != null){
                        postcard.uri.queryParameterNames.forEach { key ->
                            postcard.extras.putString(key, postcard.uri.getQueryParameter(key))
                        }
                    }
                }
                it.onContinue(postcard)// 处理完成，交还控制权
            } else {
                if(postcard.uri == null) {
                    mContext?.doIntent(buildIntent(Base.RouteError))
                }else{
                    mContext?.doIntent(buildIntent(Base.OtherAppPath).withString(KEY_WEB_URL, postcard.uri.toString()))
                }
            }
        }
        // 以上两种至少需要调用其中一种，否则不会继续路由
    }

    override fun init(context: Context?) {
        mContext = context
    }
}

fun isExist(context: Context, path: String): Boolean {
    val pretreatmentService = ARouter.getInstance().navigation(PretreatmentService::class.java)
    if (null != pretreatmentService && !pretreatmentService.onPretreatment(context, ARouter.getInstance().build(path))) {
        return false
    }
    try {
        LogisticsCenter.completion(ARouter.getInstance().build(path))
    } catch (e: NoRouteFoundException) {
        return false
    }
    return true
}