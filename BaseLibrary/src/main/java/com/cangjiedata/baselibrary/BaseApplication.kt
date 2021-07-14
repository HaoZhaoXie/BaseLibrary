package com.cangjiedata.baselibrary

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.cangjiedata.baselibrary.constant.*
import com.cangjiedata.baselibrary.utils.*
import com.cangjiedata.baselibrary.view.BaseActivity
import com.cangjiedata.baselibrary.vm.BaseViewModel
import com.cangjiedata.share.lib.ShareHelper
import com.cangjiedata.share.lib.ShareRegisterConstant
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


/**
 * Create by Judge at 12/31/20
 *
 */
open class BaseApplication : Application() {
    private var userToken = ""
    private val defaultTimeOut: Long = 30
    private var viewModeStore = HashMap<String, BaseViewModel>()
    private var baseViewModel: BaseViewModel? = null
    private var topActivityName = ""
    private var oldToken = HashMap<String, String>()//"创建activity时获取的token"

    companion object {
        private var baseApplication: BaseApplication? = null
        fun get(): BaseApplication {
            if (baseApplication == null) {
                synchronized(BaseApplication()) {
                    if (baseApplication == null) {
                        BaseApplication()
                    }
                }
            }
            return baseApplication!!
        }

        init {
            //设置全局的Header构建器
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
                layout.setPrimaryColorsId(R.color.background, R.color.color_999999) //全局设置主题颜色
                layout.setEnableOverScrollDrag(false) //禁止越界拖动（1.0.4以上版本）
                layout.setEnableOverScrollBounce(false) //关闭越界回弹功能
                ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
            //设置全局的Footer构建器
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
                //指定为经典Footer，默认是 BallPulseFooter
                layout.setFooterHeight(40f)
                BallPulseFooter(context).apply {
                    setBackgroundResource(R.color.background)
                    setNormalColor(resources.getColor(R.color.color_999999))
                    setAnimatingColor(resources.getColor(R.color.color_999999))
                }
            }
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    open fun getBaseUrl(): String {
        return if (BuildConfig.IS_TEST) {
            "http://183.131.134.242:10167/api/"//测试站
//            "http://192.168.0.153:9999/"//辜鹏
//            "http://192.168.0.155:9999" // 杜
//            "http://192.168.0.107:9999" // 旭
//            "http://192.168.0.134:9999/"//王建清
//            "http://192.168.0.116:9999/"//生
//            "http://192.168.0.181:9999/"//司
        } else {
            "https://admin.wecan.vip/api/"//正式站
        }
    }

    open fun getAgreement(): String {
        return getWebUrl() + "?type=agreement"
    }

    open fun getPrivacy(): String {
        return getWebUrl() + "?type=privacy"
    }

    //    服务协议（正式站）：https://h5.wecan.vip/?type=agreement
//    隐私条款（正式站）：https://h5.wecan.vip/?type=privacy
//    服务协议（测试站）：http://183.131.134.242:10173/?type=agreement
//    隐私条款（测试站）：http://183.131.134.242:10173/?type=privacy
//    open fun getWebUrl(): String = "https://h5.wecan.vip/"QQ
    open fun getWebUrl(): String = if (BuildConfig.IS_TEST) {
        "http://192.168.0.159:8888/"
//        "http://183.131.134.242:10173/"
    } else {
        "https://h5.wecan.vip/"
    }

    open fun isMerchantApp(): Boolean {
        return false;
    }

    open fun <T> updateOtherModel(clazz: Class<T>) {
        viewModeStore.forEach {
            if (it.key.contains(clazz.name) && it !== clazz) {
                try {
                    it.value.needRefresh.postValue(true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    open fun <T : BaseViewModel> saveAppViewModel(path: String, canonicalName: String, modelClass: T) {
        /*
        如果没有存储，就赋值，如果有，就按时间戳再次添加
         */
        val name = path + canonicalName
        loge(name)
        if (viewModeStore[name] == null) {
            viewModeStore[name] = modelClass
        } else {
            viewModeStore["$name${System.currentTimeMillis()}"] = modelClass
        }
    }

    open fun removeAppViewModel(path: String) {
        val removeKeys = ArrayList<String>()
        for (key in viewModeStore.keys) {
            if (key.startsWith(path)) {
                removeKeys.add(key)
            }
        }
        removeKeys.forEach {
            viewModeStore.remove(it)
        }
    }

    open fun onConfigHttpClient(): OkHttpClient.Builder {
        return OkHttpClient.Builder().apply {
            connectTimeout(defaultTimeOut, TimeUnit.SECONDS)//连接 超时时间
            writeTimeout(defaultTimeOut, TimeUnit.SECONDS)//写操作 超时时间
            readTimeout(defaultTimeOut, TimeUnit.SECONDS)//读操作 超时时间
            retryOnConnectionFailure(true)//错误重连
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallback()
        baseApplication = this
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)

        initApp()
        initShare()
    }

    fun setUserToken(token: String) {
        this.userToken = token
    }

    fun getUserToken(): String {
        return SharedUtil.getString(get(), ACCESS_TOKEN, "")
    }

    open fun initApp() {}

    fun getBaseViewModel(): BaseViewModel {
        if (baseViewModel == null) {
            baseViewModel = BaseViewModel(this, Bundle())
        }
        return baseViewModel!!
    }

    private fun registerActivityLifecycleCallback() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                oldToken["$activity"] = getUserToken()
                ActivityManager.getInstance().addActivity(activity) //添加到栈中

            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {
                topActivityName = activity.localClassName
                if (!TextUtils.equals(oldToken["$activity"], getUserToken())) {//如果前后存储的token不一致，表示登录信息更新
                    oldToken["$activity"] = getUserToken()
                    if (activity is BaseActivity<*>) {
                        activity.onUserInfoChange()
                    }
                }
            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {
                oldToken.remove("$activity")
                ActivityManager.getInstance().finishActivity(activity) //从栈中移除
            }
        })
    }

    fun isTopActivity(name: String): Boolean {
        if (TextUtils.isEmpty(topActivityName)) {
            return false
        }
        return topActivityName.contains(name)
    }

    open fun startChatActivity(type: Int, id: String?, title: String?) {}

    open fun accusationTarget(targetType: String, targetId: String) {
        doIntent(buildIntent(DictionaryLib.Accusation).withString(KEY_TYPE, targetType).withString(KEY_ID, targetId))
    }


    fun initShare() {
        ShareHelper.getInstance().init(
            ShareHelper.Builder().setContext(this).setRegisterShareType(ShareRegisterConstant.WEIXIN, ShareRegisterConstant.QQ, ShareRegisterConstant.MINI_PROGRAM)
                .setWeixinAppID(WXAPPID).setQQAppId(QQAppID).setMiniProgramId(WXMINIProgramId))
    }
}