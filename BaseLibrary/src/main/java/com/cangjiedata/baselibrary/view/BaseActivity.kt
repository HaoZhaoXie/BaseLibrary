package com.cangjiedata.baselibrary.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.cangjiedata.baselibrary.BaseApplication
import com.cangjiedata.baselibrary.R
import com.cangjiedata.baselibrary.constant.*
import com.cangjiedata.baselibrary.utils.*
import com.cangjiedata.baselibrary.utils.PermissionTool.Companion.onPermissionActivityResult
import com.cangjiedata.baselibrary.utils.PermissionTool.Companion.onPermissionsResult
import com.cangjiedata.baselibrary.vm.BaseViewModel
import com.cangjiedata.baselibrary.vm.VMFactory
import com.cangjiedata.baselibrary.weight.TitleBarView
import com.cangjiedata.lib_widget.dialog.DialogItemBean
import com.cangjiedata.lib_widget.dialog.LoadingDialog
import com.cangjiedata.lib_widget.dialog.SelectImageDialog
import com.cangjiedata.lib_widget.dialog.ShareDialog
import com.cangjiedata.lib_widget.slideback.SlideBack
import com.cangjiedata.share.lib.ShareConstant.*
import com.cangjiedata.share.lib.ShareHelper
import com.cangjiedata.share.lib.bean.ShareContentMiniProgram
import com.cangjiedata.share.lib.bean.ShareContentWebpage
import com.cangjiedata.share.lib.listener.OnShareHelperListener
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType
import java.util.*


/**
 * Create by Judge at 1/2/21
 */
abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    protected lateinit var viewBinding: T
    protected var openGPS = false
    private var loadingDialog: LoadingDialog? = null
    private val updateSingleData: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateTargetData(intent?.getStringExtra(KEY_TARGET) ?: "", intent?.getBundleExtra(KEY_TARGET_DATA) ?: Bundle())
        }
    }

    private val needGpsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.getBooleanExtra("Boolean", false) == true && BaseApplication.get().isTopActivity(this@BaseActivity.localClassName)) {
                showNeedGpsDialog()
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val type: ParameterizedType = javaClass.genericSuperclass as ParameterizedType
        val cls = type.actualTypeArguments[0] as Class<*>
        try {
            setStatusBar()
            val inflate = cls.getDeclaredMethod("inflate", LayoutInflater::class.java)
            viewBinding = inflate.invoke(null, layoutInflater) as T
            setContentView(viewBinding.root)
            ARouter.getInstance().inject(this)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        if (fitWindowSoftInputMode()) {
            GlobalScope.launch {
                AndroidBug5497Workaround.assistActivity(this@BaseActivity)
            }
        }
        initViews(savedInstanceState)
        initOnClick()
        val titleView = viewBinding.root.findViewWithTag<TitleBarView>(getString(R.string.BaseTitle))
        if (titleView != null) {
            titleView.setBackOnclick {
                onBackPressed()
            }
            titleView.onTitleRestListener = object : TitleBarView.OnTitleRestListener {
                override fun onReset() {
                    initData()
                }
            }
        } else {
            initData()
        }
        if (canSwipeBack()) {
            //开启滑动返回
            SlideBack.create().attachToActivity(this)
        }
        BroadcastManager.instance.register(this, ACTION_UPDATE_SINGLE_DATA, updateSingleData)
        BroadcastManager.instance.register(this, ACTION_NEED_GPS, needGpsReceiver)
    }

    protected open fun setStatusBar() {
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, false)
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this)
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, 0x55000000)
        }
    }

    protected open fun canSwipeBack(): Boolean {
        return true
    }

    /**
     * 是否键盘不布局顶上去
     */
    protected open fun fitWindowSoftInputMode(): Boolean {
        return true
    }

    abstract fun initViews(savedInstanceState: Bundle?)

    abstract fun initOnClick()

    open fun setTitle(title: String?) {
        try {
            viewBinding.root.findViewWithTag<TitleBarView>(getString(R.string.BaseTitle))?.setTitle(title)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun setBackIcon(resId: Int) {
        try {
            viewBinding.root.findViewWithTag<TitleBarView>(getString(R.string.BaseTitle))?.setBackIcon(resId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    abstract fun initData()

    fun <T : ViewModel?> getViewModel(modelClass: Class<T>, bundle: Bundle = Bundle()): T {
        val clazz = ViewModelProvider(this, VMFactory.getInstance(BaseApplication.get(), bundle)).get(modelClass)
        BaseApplication.get().saveAppViewModel("$this", modelClass.name, clazz as BaseViewModel)
        return clazz
    }

    override fun onDestroy() {
        BaseApplication.get().removeAppViewModel("$this")
        BroadcastManager.instance.unregister(this, updateSingleData)
        BroadcastManager.instance.unregister(this, needGpsReceiver)
        if (loadingDialog != null) {
            loadingDialog!!.dismiss()
        }
        super.onDestroy()
    }

    open fun <T> updateOtherModel(clazz: Class<T>) {
        BaseApplication.get().updateOtherModel(clazz)
    }

    open fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(this)
        }
        if (!loadingDialog!!.isShowing) {
            loadingDialog!!.show()
        }
    }

    open fun dismissLoading() {
        if (loadingDialog != null && loadingDialog!!.isShowing) {
            loadingDialog!!.dismiss()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onPermissionsResult(this, requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onPermissionActivityResult(this, requestCode)
    }

    open fun doPermissionSuccess(permissions: MutableList<String>) {}

    open fun doPermissionReject(permissions: MutableList<String>) {}

    open fun onUserInfoChange() {

    }

    open fun sendUpdateTargetData(action: String, bundle: Bundle) {
        BroadcastManager.instance.sendUpdateBroadcast(this, action, bundle)
    }

    open fun updateTargetData(action: String, bundle: Bundle) {

    }

    fun getShareType(targetType: String): String {
        return when (targetType) {
            "activity" -> "activityDetails" // 活动
            "heatedDebate" -> "hotTalk" // 热议话题
            "officialNews" -> "officialPublic" // 官方发布
            "news" -> "informationDetails" // 资讯
            "travel" -> "travelNotes" // 游记
            "recruit" -> "position" // 岗位
            "city_goods" -> "productDetails" // 商品
            "circle" -> "circleDetails" // 圈子
            "user" -> "userDetails"
            else -> "defaults"
        }
    }

    //type one 需要分享小程序的 包括 1 活动  2 热议 3 官方发布
    //pages/find/activicty/activityDetails/activityDetails?id= // 活动详情
    //pages/hicity/heatedDebate/heatedDebateDetail/heatedDebateDetail?id= //热议话题详情
    //pages/hicity/official/officialDetail/officialDetail?officialNewsId= // 官方发布详情
    fun getShareTypeMiniProgramUrl(targetType: String, targetId: String): String {
        return when (targetType) {
            "activity" -> "pages/find/activicty/activityDetails/activityDetails?id=${targetId}" // 活动
            "heatedDebate" -> "pages/hicity/heatedDebate/heatedDebateDetail/heatedDebateDetail?id=${targetId}" // 热议话题详情
            "officialNews" -> "pages/hicity/official/officialDetail/officialDetail?officialNewsId=${targetId}" // 官方发布
            "user" -> "pages/find/userCard/userCard?userId=${targetId}" //  个人名片
            "circle" -> "pages/find/circleCard/circleCard?circleId=${targetId}" //  圈子名片
            else -> "defaults"
        }
    }


    //type one 需要分享小程序的 包括 1 活动  2 热议 3 官方发布 4 圈子 5 好友
    //     two 分享H5的 包括 4 游记 5 资讯  6 岗位  7 城贝商品
    fun getShareTypeInt(targetType: String): Int {
        return when (targetType) {
            "activity", "heatedDebate", "officialNews", "user", "circle" -> 1 // 需要分享小程序的
//            "news" -> 2 // 资讯
            else -> 2
        }
    }

    fun getShareTypeContent(targetType: String, content: String?): String {
        return when (targetType) {
            "activity" -> "热门活动·${content}" //?: "我分享了一个" // 活动
            "heatedDebate" -> "热议话题·${content}"// 热议话题详情
            "travel" -> "热门游记·${content}" //
            "officialNews" -> "城市头条·${content}" // 官方发布
            "news" -> "新闻资讯·${content}" // 资讯
            "recruit" -> "我分享了一条职场动态" // 岗位详情
            "city_goods" -> content ?: "我分享了一个城贝商品"
            "user" -> "向您推荐${content}的名片"
            "circle" -> "${content}的名片，快加入我们圈子！"
            else -> "城市超级APP专家"
        }
    }

    fun accusationTarget(targetType: String, targetId: String) {
        val name: MutableList<String> = ArrayList()
        name.add("举报")
        showSelDialog({ parent: AdapterView<*>?, view1: View?, position: Int, id: Long ->
            when (position) {
                0 -> {
                    BaseApplication.get().accusationTarget(targetType, targetId)
                }
            }
        }, name)
    }

    /**
     * 咨询：type=informationDetails&id=
    游记：type=travelNotes&id=
    商品：type=productDetails&id=
    活动：type=activityDetails&id=
    热议：type=hotTalk&id=
    官方发布：type=officialPublic&id=
    岗位：type=position&id=
    个人名片：type=position&user=
    圈子名片：type=position&circle=
     */
    fun accusationTarget(targetType: String, targetId: String, title: String?, content: String?, iconPath: String?) {
        //type one 需要分享小程序的 包括 1 活动  2 热议 3 官方发布
        //     two 分享H5的 包括 4 游记 5 资讯  6 岗位  7 城贝商品

        // 目前有内部分享的 活动 好友 圈子 城市新闻 就业易 城市头条 城市热议 游记
        when (targetType) {
            "recruit" -> {
                val beans: MutableList<DialogItemBean> = ArrayList()
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_app, "好友"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_circle, "圈子"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_jubao, "举报"))

                //http://183.131.134.242:10173?type=&id=

                val dialog = ShareDialog(this, R.style.CommonDialogStyle, { parent: BaseQuickAdapter<*, *>?, view: View?, position: Int ->
                    when (position) {
                        0 -> {// 内部好友
                            ARouter.getInstance().build(Im.FRIENDLIST).withInt(KEY_TYPE, 2).withString(KEY_ID, targetId).withInt(KEY_TARGET, transformShareType(targetType))
                                .navigation()
                        }
                        1 -> {// 内部圈子
                            ARouter.getInstance().build(Im.GROUPFRIENDLIST).withInt(KEY_TYPE, 1).withString(KEY_ID, targetId)
                                .withInt(KEY_TARGET, transformShareType(targetType)).navigation()
                        }
                        2 -> {
                            BaseApplication.get().accusationTarget(targetType, targetId)
                        }

                    }
                }, beans)
                if (!this.isFinishing) {
                    dialog.show()
                }
            }
            "heatedDebate","travel", "city_goods", "informationDetails" -> {
                val beans: MutableList<DialogItemBean> = ArrayList()
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_wechat, "微信好友"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_wxcircle, "朋友圈"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_qq, "QQ"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_jubao, "举报"))

                val avatar = "https://woneng-oss.oss-cn-hangzhou.aliyuncs.com/wxapp/hicity/hicity_share.png"
                val path = BaseApplication.get().getWebUrl()

                val url = "${path}?type=${getShareType(targetType)}&id=${targetId}"

                val shareConstantMiniProgram =
                    ShareContentMiniProgram(getShareTypeContent(targetType, content), getShareTypeContent(targetType, content), url, iconPath ?: avatar,
                        getShareTypeMiniProgramUrl(targetType, targetId), R.mipmap.ic_share_logo)

                // title getShareTypeContent(targetType, content) 到底哪个作为标题
                val shareConstantH5 = ShareContentWebpage(title, getShareTypeContent(targetType, content), url, iconPath ?: "", R.mipmap.ic_share_logo)

                val dialog = ShareDialog(this, R.style.CommonDialogStyle, { parent: BaseQuickAdapter<*, *>?, view: View?, position: Int ->
                    when (position) {
                        0 -> {
                            when (getShareTypeInt(targetType)) {
                                1 -> {
                                    ShareHelper.getInstance().shareByMiniProgram(this, shareConstantMiniProgram, SHARE_TYPE_WECHAT_SESSION, onShareHelperListener)
                                }
                                2 -> {
                                    ShareHelper.getInstance().shareByWeChat(this, shareConstantH5, SHARE_TYPE_WECHAT_SESSION, onShareHelperListener)
                                }
                            }
                        }
                        1 -> {
                            ShareHelper.getInstance().shareByWeChat(this, shareConstantH5.shareConstantH5(url, 1), SHARE_TYPE_WECHAT_FRENDS_GROUP, onShareHelperListener)
                        }
                        2 -> {
                            ShareHelper.getInstance().shareByQQ(this, shareConstantH5, SHARE_TYPE_QQ_SESSION, onShareHelperListener)
                        }
                        3 -> {
                            BaseApplication.get().accusationTarget(targetType, targetId)
                        }

                    }
                }, beans)
                if (!this.isFinishing) {
                    dialog.show()
                }
            }
            else -> {
                val beans: MutableList<DialogItemBean> = ArrayList()
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_app, "好友"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_circle, "圈子"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_wechat, "微信好友"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_wxcircle, "朋友圈"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_qq, "QQ"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_jubao, "举报"))

                //http://183.131.134.242:10173?type=&id=

                val avatar = "https://woneng-oss.oss-cn-hangzhou.aliyuncs.com/wxapp/hicity/hicity_share.png"
                val path = BaseApplication.get().getWebUrl()

                val url = "${path}?type=${getShareType(targetType)}&id=${targetId}"

                val shareConstantMiniProgram =
                    ShareContentMiniProgram(getShareTypeContent(targetType, content), getShareTypeContent(targetType, content), url, iconPath ?: avatar,
                        getShareTypeMiniProgramUrl(targetType, targetId), R.mipmap.ic_share_logo)

                // title getShareTypeContent(targetType, content) 到底哪个作为标题
                val shareConstantH5 = ShareContentWebpage(title, getShareTypeContent(targetType, content), url, iconPath ?: "", R.mipmap.ic_share_logo)

                val dialog = ShareDialog(this, R.style.CommonDialogStyle, { parent: BaseQuickAdapter<*, *>?, view: View?, position: Int ->
                    when (position) {
                        0 -> {// 内部好友
                            ARouter.getInstance().build(Im.FRIENDLIST).withInt(KEY_TYPE, 2).withString(KEY_ID, targetId).withInt(KEY_TARGET, transformShareType(targetType))
                                .navigation()
                        }
                        1 -> {// 内部圈子
                            ARouter.getInstance().build(Im.GROUPFRIENDLIST).withInt(KEY_TYPE, 1).withString(KEY_ID, targetId)
                                .withInt(KEY_TARGET, transformShareType(targetType)).navigation()
                        }
                        2 -> {
                            when (getShareTypeInt(targetType)) {
                                1 -> {
                                    ShareHelper.getInstance().shareByMiniProgram(this, shareConstantMiniProgram, SHARE_TYPE_WECHAT_SESSION, onShareHelperListener)
                                }
                                2 -> {
                                    ShareHelper.getInstance().shareByWeChat(this, shareConstantH5, SHARE_TYPE_WECHAT_SESSION, onShareHelperListener)
                                }
                            }
                        }
                        3 -> {
                            ShareHelper.getInstance().shareByWeChat(this, shareConstantH5.shareConstantH5(url, 1), SHARE_TYPE_WECHAT_FRENDS_GROUP, onShareHelperListener)
                        }
                        4 -> {
                            ShareHelper.getInstance().shareByQQ(this, shareConstantH5, SHARE_TYPE_QQ_SESSION, onShareHelperListener)
                        }
                        5 -> {
                            BaseApplication.get().accusationTarget(targetType, targetId)
                        }

                    }
                }, beans)
                if (!this.isFinishing) {
                    dialog.show()
                }
            }
        }


    }

    val onShareHelperListener = object : OnShareHelperListener {
        override fun onStart() {
        }

        override fun onError(errorCode: Int, errorMessage: String?) {
            toasty(errorMessage)
        }

        override fun onCancel() {
        }

        override fun onComplete(type: String?) {
            Log.i("shareResult", "share 分享成功: $type")

        }

    }


    fun ShareContentWebpage.shareConstantH5(url: String, source: Int): ShareContentWebpage {
        this.setUrl(url + "&source=${source}")
        return this
    }

    /**
     * 单独分享小程序用
     * 个人名片：type=userCard&userId=
     * 圈子名片：type=userCard&circleId=
     */
    fun shareByMiniProgram(targetType: String, targetId: String, title: String?, iconPath: String?) {
        val userAvatar = "https://woneng-oss.oss-cn-hangzhou.aliyuncs.com/wxapp/hicity/bg_share_card.png"
        val circleAvatar = "https://woneng-oss.oss-cn-hangzhou.aliyuncs.com/wxapp/hicity/bg_share_circle_card.png"

        val avatar = when (targetType) {
            "user" -> userAvatar
            else -> circleAvatar
        }

        val path = BaseApplication.get().getWebUrl()

        val url = "${path}?type=${getShareType(targetType)}&id=${targetId}"

        val shareConstantMiniProgram =
            ShareContentMiniProgram(getShareTypeContent(targetType, title), null, url, iconPath ?: avatar, getShareTypeMiniProgramUrl(targetType, targetId),
                R.mipmap.ic_share_logo)

        ShareHelper.getInstance().shareByMiniProgram(this, shareConstantMiniProgram, SHARE_TYPE_WECHAT_SESSION, onShareHelperListener)
    }

    fun showSelDialog(listener: SelectImageDialog.SelectImageDialogListener, name: List<String>) {
        val dialog = SelectImageDialog(this, R.style.transparentFrameWindowStyle, listener, name)
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    open fun showNeedGpsDialog() {
        showAlertDialog("您的定位服务未开启，需要开启后使用获得最佳体验。", "去开启", {
            openGPS = true
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }, "取消", {
            rejectGps()
        })
    }

    open fun rejectGps() {

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.fontScale != 1f) {
            resources
        }
        super.onConfigurationChanged(newConfig)
    }

    override fun getResources(): Resources {
        val res = super.getResources()
        if (super.getResources().configuration.fontScale != 1f) {
            val newConfig = Configuration()
            newConfig.setToDefaults()
            res.updateConfiguration(newConfig, res.displayMetrics)
        }
        return res
    }


    open fun getView(recyclerView: RecyclerView, layoutId: Int): View {
        return LayoutInflater.from(recyclerView.context).inflate(layoutId, recyclerView.parent as ViewGroup, false)
    }
}