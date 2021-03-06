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
            //??????????????????
            SlideBack.create().attachToActivity(this)
        }
        BroadcastManager.instance.register(this, ACTION_UPDATE_SINGLE_DATA, updateSingleData)
        BroadcastManager.instance.register(this, ACTION_NEED_GPS, needGpsReceiver)
    }

    protected open fun setStatusBar() {
        //???FitsSystemWindows?????? true ?????????????????????????????????????????????????????? padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, false)
        //?????????????????????
        StatusBarUtil.setTranslucentStatus(this)
        //?????????????????????????????????????????????????????????, ???????????????????????????????????????, ?????????????????????????????????
        //??????????????????????????????,?????????????????????, ??????????????????????????????????????????, ???????????????????????????????????????if??????
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //????????????????????????????????? ???????????????????????????????????????????????????, ?????????????????????????????????????????????,
            //???????????????+???=???, ??????????????????????????????
            StatusBarUtil.setStatusBarColor(this, 0x55000000)
        }
    }

    protected open fun canSwipeBack(): Boolean {
        return true
    }

    /**
     * ??????????????????????????????
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
            "activity" -> "activityDetails" // ??????
            "heatedDebate" -> "hotTalk" // ????????????
            "officialNews" -> "officialPublic" // ????????????
            "news" -> "informationDetails" // ??????
            "travel" -> "travelNotes" // ??????
            "recruit" -> "position" // ??????
            "city_goods" -> "productDetails" // ??????
            "circle" -> "circleDetails" // ??????
            "user" -> "userDetails"
            else -> "defaults"
        }
    }

    //type one ???????????????????????? ?????? 1 ??????  2 ?????? 3 ????????????
    //pages/find/activicty/activityDetails/activityDetails?id= // ????????????
    //pages/hicity/heatedDebate/heatedDebateDetail/heatedDebateDetail?id= //??????????????????
    //pages/hicity/official/officialDetail/officialDetail?officialNewsId= // ??????????????????
    fun getShareTypeMiniProgramUrl(targetType: String, targetId: String): String {
        return when (targetType) {
            "activity" -> "pages/find/activicty/activityDetails/activityDetails?id=${targetId}" // ??????
            "heatedDebate" -> "pages/hicity/heatedDebate/heatedDebateDetail/heatedDebateDetail?id=${targetId}" // ??????????????????
            "officialNews" -> "pages/hicity/official/officialDetail/officialDetail?officialNewsId=${targetId}" // ????????????
            "user" -> "pages/find/userCard/userCard?userId=${targetId}" //  ????????????
            "circle" -> "pages/find/circleCard/circleCard?circleId=${targetId}" //  ????????????
            else -> "defaults"
        }
    }


    //type one ???????????????????????? ?????? 1 ??????  2 ?????? 3 ???????????? 4 ?????? 5 ??????
    //     two ??????H5??? ?????? 4 ?????? 5 ??????  6 ??????  7 ????????????
    fun getShareTypeInt(targetType: String): Int {
        return when (targetType) {
            "activity", "heatedDebate", "officialNews", "user", "circle" -> 1 // ????????????????????????
//            "news" -> 2 // ??????
            else -> 2
        }
    }

    fun getShareTypeContent(targetType: String, content: String?): String {
        return when (targetType) {
            "activity" -> "??????????????${content}" //?: "??????????????????" // ??????
            "heatedDebate" -> "??????????????${content}"// ??????????????????
            "travel" -> "??????????????${content}" //
            "officialNews" -> "??????????????${content}" // ????????????
            "news" -> "??????????????${content}" // ??????
            "recruit" -> "??????????????????????????????" // ????????????
            "city_goods" -> content ?: "??????????????????????????????"
            "user" -> "????????????${content}?????????"
            "circle" -> "${content}????????????????????????????????????"
            else -> "????????????APP??????"
        }
    }

    fun accusationTarget(targetType: String, targetId: String) {
        val name: MutableList<String> = ArrayList()
        name.add("??????")
        showSelDialog({ parent: AdapterView<*>?, view1: View?, position: Int, id: Long ->
            when (position) {
                0 -> {
                    BaseApplication.get().accusationTarget(targetType, targetId)
                }
            }
        }, name)
    }

    /**
     * ?????????type=informationDetails&id=
    ?????????type=travelNotes&id=
    ?????????type=productDetails&id=
    ?????????type=activityDetails&id=
    ?????????type=hotTalk&id=
    ???????????????type=officialPublic&id=
    ?????????type=position&id=
    ???????????????type=position&user=
    ???????????????type=position&circle=
     */
    fun accusationTarget(targetType: String, targetId: String, title: String?, content: String?, iconPath: String?) {
        //type one ???????????????????????? ?????? 1 ??????  2 ?????? 3 ????????????
        //     two ??????H5??? ?????? 4 ?????? 5 ??????  6 ??????  7 ????????????

        // ???????????????????????? ?????? ?????? ?????? ???????????? ????????? ???????????? ???????????? ??????
        when (targetType) {
            "recruit" -> {
                val beans: MutableList<DialogItemBean> = ArrayList()
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_app, "??????"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_circle, "??????"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_jubao, "??????"))

                //http://183.131.134.242:10173?type=&id=

                val dialog = ShareDialog(this, R.style.CommonDialogStyle, { parent: BaseQuickAdapter<*, *>?, view: View?, position: Int ->
                    when (position) {
                        0 -> {// ????????????
                            ARouter.getInstance().build(Im.FRIENDLIST).withInt(KEY_TYPE, 2).withString(KEY_ID, targetId).withInt(KEY_TARGET, transformShareType(targetType))
                                .navigation()
                        }
                        1 -> {// ????????????
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
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_wechat, "????????????"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_wxcircle, "?????????"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_qq, "QQ"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_jubao, "??????"))

                val avatar = "https://woneng-oss.oss-cn-hangzhou.aliyuncs.com/wxapp/hicity/hicity_share.png"
                val path = BaseApplication.get().getWebUrl()

                val url = "${path}?type=${getShareType(targetType)}&id=${targetId}"

                val shareConstantMiniProgram =
                    ShareContentMiniProgram(getShareTypeContent(targetType, content), getShareTypeContent(targetType, content), url, iconPath ?: avatar,
                        getShareTypeMiniProgramUrl(targetType, targetId), R.mipmap.ic_share_logo)

                // title getShareTypeContent(targetType, content) ????????????????????????
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
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_app, "??????"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_circle, "??????"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_wechat, "????????????"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_wxcircle, "?????????"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_qq, "QQ"))
                beans.add(DialogItemBean(R.mipmap.umeng_socialize_jubao, "??????"))

                //http://183.131.134.242:10173?type=&id=

                val avatar = "https://woneng-oss.oss-cn-hangzhou.aliyuncs.com/wxapp/hicity/hicity_share.png"
                val path = BaseApplication.get().getWebUrl()

                val url = "${path}?type=${getShareType(targetType)}&id=${targetId}"

                val shareConstantMiniProgram =
                    ShareContentMiniProgram(getShareTypeContent(targetType, content), getShareTypeContent(targetType, content), url, iconPath ?: avatar,
                        getShareTypeMiniProgramUrl(targetType, targetId), R.mipmap.ic_share_logo)

                // title getShareTypeContent(targetType, content) ????????????????????????
                val shareConstantH5 = ShareContentWebpage(title, getShareTypeContent(targetType, content), url, iconPath ?: "", R.mipmap.ic_share_logo)

                val dialog = ShareDialog(this, R.style.CommonDialogStyle, { parent: BaseQuickAdapter<*, *>?, view: View?, position: Int ->
                    when (position) {
                        0 -> {// ????????????
                            ARouter.getInstance().build(Im.FRIENDLIST).withInt(KEY_TYPE, 2).withString(KEY_ID, targetId).withInt(KEY_TARGET, transformShareType(targetType))
                                .navigation()
                        }
                        1 -> {// ????????????
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
            Log.i("shareResult", "share ????????????: $type")

        }

    }


    fun ShareContentWebpage.shareConstantH5(url: String, source: Int): ShareContentWebpage {
        this.setUrl(url + "&source=${source}")
        return this
    }

    /**
     * ????????????????????????
     * ???????????????type=userCard&userId=
     * ???????????????type=userCard&circleId=
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
        showAlertDialog("????????????????????????????????????????????????????????????????????????", "?????????", {
            openGPS = true
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }, "??????", {
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