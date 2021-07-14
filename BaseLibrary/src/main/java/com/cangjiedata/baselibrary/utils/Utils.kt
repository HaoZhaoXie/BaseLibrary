package com.cangjiedata.baselibrary.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.text.TextUtils
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.hutool.core.codec.Base64
import cn.hutool.crypto.Mode
import cn.hutool.crypto.Padding
import cn.hutool.crypto.symmetric.AES
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.cangjiedata.baselibrary.BaseApplication
import com.cangjiedata.baselibrary.BuildConfig
import com.cangjiedata.baselibrary.R
import com.cangjiedata.baselibrary.bean.LetterBean
import com.cangjiedata.baselibrary.bean.UserBean
import com.cangjiedata.baselibrary.constant.Base.OtherAppPath
import com.cangjiedata.baselibrary.constant.Base.RouteError
import com.cangjiedata.baselibrary.constant.IS_LOGIN
import com.cangjiedata.baselibrary.constant.KEY_WEB_URL
import com.cangjiedata.baselibrary.constant.USER_BEAN
import com.cangjiedata.baselibrary.databinding.EmptyListBinding
import com.cangjiedata.baselibrary.view.BaseActivity
import com.cangjiedata.baselibrary.weight.SelectDialog
import com.cangjiedata.lib_widget.dialog.AlertDialog
import com.github.promeg.pinyinhelper.Pinyin
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lzy.imagepicker.ImagePicker
import com.lzy.imagepicker.bean.ImageItem
import com.lzy.imagepicker.ui.ImagePreviewDelActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.UnsupportedEncodingException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.abs
import kotlin.math.roundToInt


/**
 * Create by Judge at 1/4/21
 */

class Utils {


    companion object {
        private var oldMsg: String? = null
        private var toast: Toast? = null
        private var oneTime: Long = 0
        private var twoTime: Long = 0

        @JvmStatic
        fun toasty(s: String, int: Int) {
            if (toast == null) {
                toast = Toast.makeText(BaseApplication.get(), s, int)
                toast?.show()
                oneTime = System.currentTimeMillis()
            } else {
                twoTime = System.currentTimeMillis()
                if (s == oldMsg) {
                    if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                        toast!!.show()
                    }
                } else {
                    oldMsg = s
                    toast!!.setText(s)
                    toast!!.show()
                }
            }
            oneTime = twoTime
        }


        /**
         * 判断是否匹配正则
         *
         * @param regex 正则表达式
         * @param input 要匹配的字符串
         * @return `true`: 匹配<br></br>`false`: 不匹配
         */
        fun isMatch(regex: String?, input: CharSequence?): Boolean {
            return input != null && input.length > 0 && Pattern.matches(regex, input)
        }

        /**
         * Regex of email.
         */
        const val REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"

        /**
         * 判断是否为邮箱
         *
         * @param input
         * @return
         */
        fun isEmail(input: CharSequence?): Boolean {
            return isMatch(REGEX_EMAIL, input)
        }

        /**
         * 正则：手机号（精确）
         *
         * 移动：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188
         *
         * 联通：130、131、132、145、155、156、171、175、176、185、186
         *
         * 电信：133、153、173、177、180、181、189
         *
         * 全球星：1349
         *
         * 虚拟运营商：170
         */
        const val REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,1,3,4,5-8])|(18[0-9])|(166)|(162)|(19[0-9]))\\d{8}$"

        /**
         * 验证手机号（精确）
         *
         * @param input 待验证文本
         * @return `true`: 匹配<br></br>`false`: 不匹配
         */
        fun isMobileExact(input: CharSequence?): Boolean {
            return isMatch(REGEX_MOBILE_EXACT, input)
        }


        const val regex_letter_exact = "^[\\u4e00-\\u9fa5a-zA-Z0-9]+$"

        fun isLetterExact(input: CharSequence?): Boolean {
            return isMatch(regex_letter_exact, input)
        }

        fun isQQ(str: String?): Boolean {
            val regex = "[1-9][0-9]{5,11}"
            val p = Pattern.compile(regex)
            val m = p.matcher(str)
            return m.matches()
        }


        // 两次点击按钮之间的点击间隔不能少于1000毫秒
        private const val MIN_CLICK_DELAY_TIME = 1000
        private var lastClickTime: Long = 0

        fun isFastClick(): Boolean {
            var flag = false
            val curClickTime = System.currentTimeMillis()
            if (curClickTime - lastClickTime >= MIN_CLICK_DELAY_TIME) {
                flag = true
            }
            lastClickTime = curClickTime
            return flag
        }
    }
}

fun encryptedData(data: String): String? {
    var psdEncode = ""
    try {
        psdEncode = encodeURI(data)
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }
    val password = "gdscloudprisbest"
    val aes = AES(Mode.CBC, Padding.ZeroPadding, SecretKeySpec(password.toByteArray(), "AES"), IvParameterSpec(password.toByteArray()))
    return Base64.encode(aes.encrypt(psdEncode))
}

fun encodeURI(str: String): String {
    val isoStr = String(str.toByteArray(charset("UTF8")), Charsets.ISO_8859_1)
    val chars = isoStr.toCharArray()
    val sb = StringBuffer()
    for (i in chars.indices) {
        if (chars[i] in 'a'..'z' || chars[i] in 'A'..'Z' || chars[i] == '-' || chars[i] == '_' || chars[i] == '.' || chars[i] == '!' || chars[i] == '~' || chars[i] == '*' || chars[i] == '\'' || chars[i] == '(' || chars[i] == ')' || chars[i] == ';' || chars[i] == '/' || chars[i] == '?' || chars[i] == ':' || chars[i] == '@' || chars[i] == '&' || chars[i] == '=' || chars[i] == '+' || chars[i] == '$' || chars[i] == ',' || chars[i] == '#' || chars[i] in '0'..'9') {
            sb.append(chars[i])
        } else {
            sb.append("%")
            sb.append(Integer.toHexString(chars[i].toInt()))
        }
    }
    return sb.toString()
}

fun toasty(string: String?, int: Int = Toast.LENGTH_SHORT) {
    Utils.toasty("$string", int)
}

fun getVersion(context: Context): String {
    return try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun getVersionCode(context: Context): String {
    return try {
        "${context.packageManager.getPackageInfo(context.packageName, 0).versionCode}"
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun getChannel(context: Context): String? {
    var channelNumber: String? = null
    try {
        val packageManager: PackageManager = context.getPackageManager()
        if (packageManager != null) {
            val applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
            if (applicationInfo != null) {
                if (applicationInfo.metaData != null) {
                    channelNumber = applicationInfo.metaData.getString("UMENG_CHANNEL")
                }
            }
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return channelNumber
}

fun dip2px(float: Float): Int {
    return (BaseApplication.get().resources.displayMetrics.density * float + 0.5f).toInt()
}

fun sp2px(sp: Float): Int {
    val scale: Float = BaseApplication.get().resources.displayMetrics.scaledDensity
    return (sp * scale + 0.5f).toInt()
}

fun getScreenWidth(): Int {
    return BaseApplication.get().resources.displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return BaseApplication.get().resources.displayMetrics.heightPixels
}

fun initLinearRecyclerView(recyclerView: RecyclerView, @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL): RecyclerView {
    recyclerView.layoutManager = LinearLayoutManager(BaseApplication.get(), orientation, false)
    return setUpRecyclerView(recyclerView)
}

fun initGridRecyclerView(recyclerView: RecyclerView, count: Int = 2): RecyclerView {
    recyclerView.layoutManager = GridLayoutManager(BaseApplication.get(), count)
    return setUpRecyclerView(recyclerView)
}

fun initStaggeredGridLayoutManager(recyclerView: RecyclerView, orientation: Int = StaggeredGridLayoutManager.VERTICAL, count: Int = 2): RecyclerView {
    recyclerView.layoutManager = StaggeredGridLayoutManager(count, orientation)
    return setUpRecyclerView(recyclerView)
}

fun setUpRecyclerView(recyclerView: RecyclerView): RecyclerView {
    recyclerView.itemAnimator = null
    recyclerView.setRecycledViewPool(RecyclerView.RecycledViewPool().apply {
        setMaxRecycledViews(0, 20)
    })
    return recyclerView
}

fun getUserPosition(position: String?): String? {
    var string = ""
    if (TextUtils.isEmpty(position)) {
        return string
    }
    if (position!!.contains("-")) { //截取职位最后一个
        val job = position.split("-").toTypedArray()
        if (job.isNotEmpty()) {
            string = job[job.lastIndex]
        }
    } else {
        return position
    }
    return string
}

fun loge(str: String) {
    if (BuildConfig.DEBUG) {
        Log.e("hiCity", str)
    }
}

fun logi(str: String) {
    if (BuildConfig.DEBUG) {
        Log.i("hiCity", str)
    }
}


fun getLocalUser(): UserBean {
    val str: String = SharedUtil.getString(BaseApplication.get(), USER_BEAN, "")
    return if (TextUtils.isEmpty(str) || TextUtils.equals(str, "null")) {
        UserBean()
    } else {
        Gson().fromJson(SharedUtil.getString(BaseApplication.get(), USER_BEAN, ""), UserBean::class.java);
    }
}


fun buildIntent(path: String): Postcard {
    return if (path.startsWith("http")) {
        try {
            ARouter.getInstance().build(Uri.parse(path))
        } catch (e: java.lang.Exception) {
            ARouter.getInstance().build(OtherAppPath).withString(KEY_WEB_URL, path)
        }
    } else {
        ARouter.getInstance().build(if (TextUtils.isEmpty(path)) {
            RouteError
        } else {
            path
        })
    }
}

fun Context.doIntent(intent: Postcard) {
    doIntent(intent, this)
}

fun doIntent(intent: Postcard, context: Context) {
    if (TextUtils.equals(intent.path, RouteError)) {
        return
    }
    intent.navigation(context, object : NavigationCallback {
        override fun onFound(postcard: Postcard?) {

        }

        override fun onLost(postcard: Postcard?) {
            buildIntent(RouteError).navigation()
        }

        override fun onArrival(postcard: Postcard?) {

        }

        override fun onInterrupt(postcard: Postcard?) {
        }

    })
}

fun doIntent(activity: Activity, intent: Postcard, requestCode: Int = 0) {
    if (TextUtils.equals(intent.path, RouteError)) {
        return
    }
    intent.navigation(activity, requestCode, object : NavigationCallback {
        override fun onFound(postcard: Postcard?) {

        }

        override fun onLost(postcard: Postcard?) {
            buildIntent(RouteError).navigation()
        }

        override fun onArrival(postcard: Postcard?) {

        }

        override fun onInterrupt(postcard: Postcard?) {
            if (activity is BaseActivity<*>) {
                activity.showLoading()
            }
        }

    })
}

@SuppressLint("ClickableViewAccessibility")
fun fixEditAndScrollBug(editText: EditText) {
    editText.setOnTouchListener { v, event ->
        if (MotionEvent.ACTION_DOWN == event.action) {
            v.parent.requestDisallowInterceptTouchEvent(true)
        } else if (MotionEvent.ACTION_UP == event.action) {
            v.parent.requestDisallowInterceptTouchEvent(false)
        }
        false
    }
}

fun isNumber(input: CharSequence): Boolean {
    val haveDigit = true //数字
    for (element in input) {
        if (!Character.isDigit(element)) {
            return false
        }
    }
    return haveDigit
}

const val M = (1000 * 60).toLong()
const val H = M * 60
const val DAY = H * 24
const val WEEK = 7 * DAY

fun betweenTime(createTime: String?): String? {
    try {
        val format = "yyyy-MM-dd HH:mm:ss"
        val sdf = SimpleDateFormat(format)
        val time = sdf.parse(createTime).time
        val now = System.currentTimeMillis()
        val timeLag = now - time
        val timeCalender = Calendar.getInstance()
        timeCalender.timeInMillis = time
        val nowCalender = Calendar.getInstance()
        nowCalender.timeInMillis = now
        return if (timeCalender[Calendar.YEAR] == nowCalender[Calendar.YEAR]) {
            if (timeCalender[Calendar.DAY_OF_YEAR] == nowCalender[Calendar.DAY_OF_YEAR]) {
                when {
                    timeLag < M -> {
                        "刚刚"
                    }
                    timeLag < H -> {
                        "${timeLag.toInt().div(M)}分钟前"
                    }
                    else -> {
                        "${timeLag.toInt().div(H)}小时前"
                    }
                }
            } else {
                DateFormat.format("MM-dd HH:mm", time).toString()
            }
        } else {
            DateFormat.format("yyyy-MM-dd HH:mm", time).toString()
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return createTime
}

fun betweenTime2(createTime: String?): String? {
    try {
        val format = "yyyy-MM-dd HH:mm:ss"
        val sdf = SimpleDateFormat(format)
        val time = sdf.parse(createTime).time
        val now = System.currentTimeMillis()
        val timeLag = now - time
        val timeCalender = Calendar.getInstance()
        timeCalender.timeInMillis = time
        val nowCalender = Calendar.getInstance()
        nowCalender.timeInMillis = now
        return if (timeCalender[Calendar.YEAR] == nowCalender[Calendar.YEAR]) {
            if (timeCalender[Calendar.DAY_OF_YEAR] == nowCalender[Calendar.DAY_OF_YEAR]) {
                when {
                    timeLag < M * 5 -> {
                        "刚刚"
                    }
                    timeLag < H -> {
                        "${timeLag.toInt().div(M)}分钟前"
                    }
                    else -> {
                        "${timeLag.toInt().div(H)}小时前"
                    }
                }
            } else if (timeCalender[Calendar.DAY_OF_YEAR] > nowCalender[Calendar.DAY_OF_YEAR] - 7) {
                "${nowCalender[Calendar.DAY_OF_YEAR] - timeCalender[Calendar.DAY_OF_YEAR]}天前"
            } else {
                DateFormat.format("MM-dd HH:mm", time).toString()
            }
        } else {
            DateFormat.format("yyyy-MM-dd HH:mm", time).toString()
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return createTime
}

fun betweenTime3(createTime: String?): String? {
    try {
        val format = "yyyy-MM-dd HH:mm:ss"
        val sdf = SimpleDateFormat(format)
        val time = sdf.parse(createTime).time
        val now = System.currentTimeMillis()
        val timeLag = now - time
        val timeCalender = Calendar.getInstance()
        timeCalender.timeInMillis = time
        val nowCalender = Calendar.getInstance()
        nowCalender.timeInMillis = now
        return if (timeCalender[Calendar.YEAR] == nowCalender[Calendar.YEAR]) {
            if (timeCalender[Calendar.DAY_OF_YEAR] == nowCalender[Calendar.DAY_OF_YEAR]) {
                when {
                    timeLag < M -> {
                        "刚刚"
                    }
                    timeLag < H -> {
                        "${timeLag.toInt().div(M)}分钟前"
                    }
                    else -> {
                        "${timeLag.toInt().div(H)}小时前"
                    }
                }
            } else {
                val day = abs(timeCalender[Calendar.DAY_OF_YEAR] - nowCalender[Calendar.DAY_OF_YEAR])
                if (abs(day) < 7) {
                    "${abs(day)}天前"
                } else {
                    DateFormat.format("MM-dd", time).toString()
                }
            }
        } else {
            DateFormat.format("yyyy-MM-dd", time).toString()
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return createTime
}

fun timeToDHM(createTime: String?): String? {
    try {
        val format = "yyyy-MM-dd HH:mm:ss"
        val sdf = SimpleDateFormat(format)
        val time = sdf.parse(createTime).time
        val now = System.currentTimeMillis()
        val timeLag = now - time
        val timeCalender = Calendar.getInstance()
        timeCalender.timeInMillis = time
        val nowCalender = Calendar.getInstance()
        nowCalender.timeInMillis = now
        return if (timeCalender[Calendar.YEAR] == nowCalender[Calendar.YEAR]) {
            DateFormat.format("MM-dd HH:mm", time).toString()
        } else {
            DateFormat.format("yyyy-MM-dd HH:mm", time).toString()
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return createTime
}

fun betweenDayWithTime(createTime: String?): String? {
    try {
        val format = "yyyy-MM-dd HH:mm:ss"
        val sdf = SimpleDateFormat(format)
        val time = sdf.parse(createTime).time
        val now = System.currentTimeMillis()
        val timeLag = now - time
        val timeCalender = Calendar.getInstance()
        timeCalender.timeInMillis = time
        val nowCalender = Calendar.getInstance()
        nowCalender.timeInMillis = now
        return if (timeCalender[Calendar.YEAR] == nowCalender[Calendar.YEAR]) {
            when {
                timeCalender[Calendar.DAY_OF_YEAR] == nowCalender[Calendar.DAY_OF_YEAR] -> {
                    "今天 ${DateFormat.format("HH:mm", time)}"
                }
                timeCalender[Calendar.DAY_OF_YEAR] == nowCalender[Calendar.DAY_OF_YEAR] - 1 -> {
                    "昨天 ${DateFormat.format("HH:mm", time)}"
                }
                timeCalender[Calendar.DAY_OF_YEAR] == nowCalender[Calendar.DAY_OF_YEAR] - 2 -> {
                    "前天 ${DateFormat.format("HH:mm", time)}"
                }
                else -> {
                    DateFormat.format("MM月dd日 HH:mm", time).toString()
                }
            }
        } else {
            DateFormat.format("yyyy年MM月dd日 HH:mm", time).toString()
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return createTime
}

fun getPinYin(str: String): String {
    return Pinyin.toPinyin(str, "").toUpperCase(Locale.ROOT)
}

/**
 * 判断字符是否是字母
 */
fun isLetter(s: String): Boolean {
    val c = s[0]
    val i = c.toInt()
    return i in 65..90 || i in 97..122
}

/**
 * Create by Judge at 1/22/21
 */
/**
 * 把按照a b c升序排列
 */
fun <T : LetterBean> compareLetter(letterList: List<T>): List<T> {
    Collections.sort(letterList, object : Comparator<T> {
        override fun compare(o1: T?, o2: T?): Int {
            o1?.let { one ->
                o2?.let { two ->
                    if (!TextUtils.isEmpty(one.letter) && !TextUtils.isEmpty(two.letter)) {
                        if (TextUtils.equals(one.letter, "#") || TextUtils.equals(one.letter, "@")) {
                            return -1
                        } else if (TextUtils.equals(two.letter, "#") || TextUtils.equals(two.letter, "@")) {
                            return 1
                        }
                        return one.letter!!.compareTo(two.letter!!)
                    }
                }
            }
            return -1
        }
    })
    return letterList
}


class FunctionUtils {

    private var queueFun = LinkedList<Function>()
    private var currentFun: (() -> Unit?)? = null // 当前任务

    fun addFunc(function: () -> Unit?) {
        val func = Function(function)
        doFunc(func)
    }

    fun finishFunc() {
        doFunc(null)
    }

    private fun doFunc(func: Function?) {
        if (func != null) {
            queueFun.offer(func)
        } else {
            currentFun = null
        }
        if (currentFun == null) {
            if (queueFun.size != 0) {
                val funNow = queueFun.poll()
                funNow?.let {
                    currentFun = funNow.function
                    currentFun?.invoke()
                }
            }
        }
    }

    fun getFuncCount(): Int {
        return queueFun.size
    }

    data class Function(var function: () -> Unit? = {})
}

fun checkIsLogin(): Boolean {
    return SharedUtil.getBoolean(BaseApplication.get(), IS_LOGIN, false)
}

fun getEmptyCollectView(): View {
    return getEmptyListView(R.mipmap.pic_no_collect, "您还没有收藏的项目")
}

fun getEmptyCommentView(): View {
    return getEmptyListView(R.mipmap.pic_no_comment, "暂无评论内容")
}

fun getEmptyMessageView(): View {
    return getEmptyListView(R.mipmap.pic_no_message, "暂无任何消息")
}

fun getEmptyNetView(): View {
    return getEmptyListView(R.mipmap.pic_no_net, "加载超时了")
}


fun getEmptyOrderView(): View {
    return getEmptyListView(R.mipmap.pic_no_order, "暂无订单记录")
}

fun getEmptySearchView(): View {
    return getEmptyListView(R.mipmap.pic_no_search, "没有搜索结果")
}

fun getEmptyTicketView(): View {
    return getEmptyListView(R.mipmap.pic_no_ticket, "暂无订单记录")
}

fun getEmptyDataView(): View {
    return getEmptyListView()
}

fun getEmptyListView(resId: Int = R.mipmap.icon_no_data, label: String = "暂无内容"): View {
    val emptyView = LayoutInflater.from(BaseApplication.get()).inflate(R.layout.empty_list, null)
    val viewBinding = EmptyListBinding.bind(emptyView)
    viewBinding.ivIcon.setImageResource(resId)
    viewBinding.tvLabel.text = label
    return emptyView
}

fun getAlphaStr(float: Float): String {
    val temp: Float = 255 * float * 1.0f / 100f
    val alpha = temp.roundToInt()
    var hexStr = Integer.toHexString(alpha)
    if (hexStr.length < 2) hexStr = "0$hexStr"
    return hexStr.toUpperCase(Locale.ROOT)
}

//复制到剪切板
fun copyToClip(str: String) {
    //获取剪贴板管理器：
    val cm = BaseApplication.get().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
    // 创建普通字符型ClipData
    val mClipData = ClipData.newPlainText("Label", str)
    // 将ClipData内容放到系统剪贴板里。
    cm.setPrimaryClip(mClipData)
    toasty("复制成功")
}

fun paramBigSmallTime(createTime: String?): ArrayList<String> {
    val bigSmall = arrayListOf<String>()
    try {
        val format = "yyyy-MM-dd HH:mm:ss"
        val sdf = SimpleDateFormat(format)
        val time = sdf.parse(createTime).time
        val now = System.currentTimeMillis()
        val timeLag = now - time
        val timeCalender = Calendar.getInstance()
        timeCalender.timeInMillis = time
        val nowCalender = Calendar.getInstance()
        nowCalender.timeInMillis = now
//        if (timeCalender[Calendar.YEAR] == nowCalender[Calendar.YEAR]) {
        if (timeCalender[Calendar.DAY_OF_YEAR] == nowCalender[Calendar.DAY_OF_YEAR]) {
            when {
                timeLag < M -> {
                    bigSmall.add("刚刚")
                    bigSmall.add("")
                }
                timeLag < H -> {
                    bigSmall.add(0, "${timeLag.toInt().div(M)}")
                    bigSmall.add(1, "分钟前")
                }
                else -> {
                    bigSmall.add(0, "${timeLag.toInt().div(H)}")
                    bigSmall.add(1, "小时前")
                }
            }
        } else {
            bigSmall.add(0, "${timeCalender[Calendar.DAY_OF_MONTH]}")
            bigSmall.add(1, "${timeCalender[Calendar.MONTH] + 1}月")
        }
//        } else {
//            DateFormat.format("yyyy-MM-dd HH:mm", time).toString()
//        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return bigSmall
}

fun createRequestBody(bean: Any): RequestBody {
    return Gson().toJson(bean).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
}

fun toDBC(input: String): String? {
    val c = input.toCharArray()
    for (i in c.indices) {
        if (c[i].toInt() == 12288) {
            // 全角空格为12288，半角空格为32
            c[i] = 32.toChar()
            continue
        }
        if (c[i].toInt() in 65281..65374) // 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
            c[i] = (c[i] - 65248)
    }
    return String(c)
}

fun captureView(view: View): Bitmap? {
    view.isDrawingCacheEnabled = true
    view.buildDrawingCache()
    // 重新测量一遍View的宽高
    view.measure(View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(view.height, View.MeasureSpec.EXACTLY))
    // 确定View的位置
    view.layout(view.x.toInt(), view.y.toInt(), view.x.toInt() + view.measuredWidth, view.y.toInt() + view.measuredHeight)
    // 生成View宽高一样的Bitmap
    val bitmap = Bitmap.createBitmap(view.drawingCache, 0, 0, view.measuredWidth, view.measuredHeight)
    view.isDrawingCacheEnabled = false
    view.destroyDrawingCache()
    return bitmap
}

fun Context.showAlertDialog(msg: String, positive: String, positiveOperate: () -> Unit, negative: String, negativeOperate: () -> Unit) {
    val dialog: AlertDialog = AlertDialog(this).builder()
    dialog.setMsg(msg)
    dialog.setPositiveButton(positive) {
        dialog.dismiss()
        positiveOperate()
    }
    dialog.setNegativeButton(negative) {
        dialog.dismiss()
        negativeOperate()
    }
    dialog.show()
}

fun <T> coverJsonObjectData(data: MutableList<JsonObject>, clazz: Class<T>): ArrayList<T> {
    val arrayList = arrayListOf<T>()
    data.forEach {
        arrayList.add(Gson().fromJson(it, clazz))
    }
    return arrayList
}

fun cutPrice(price: String?): String {
    price?.let {
        return if (price.contains(".")) {
            when {
                price.endsWith("0") || price.endsWith(".") -> {
                    cutPrice(price.substring(0, price.lastIndex))
                }
                else -> {
                    price
                }
            }
        } else {
            price
        }
    }
    return ""
}

fun intervalsDays(startTime: String?, endTime: String): Int {
    try {
        val format = "yyyy-MM-dd HH:mm:ss"
        val sdf = SimpleDateFormat(format)
        val start = sdf.parse(startTime).time
        val end = sdf.parse(endTime).time
        val startCalendar = Calendar.getInstance()
        startCalendar.timeInMillis = start
        val endCalender = Calendar.getInstance()
        endCalender.timeInMillis = end
        return abs(endCalender[Calendar.DAY_OF_YEAR] - startCalendar[Calendar.DAY_OF_YEAR])
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return 0
}


/**
 * 条件选择弹窗
 */
fun showTypeSelDialog(context: Activity, listener: SelectDialog.SelectImageDialogListener, names: MutableList<String>, title: String): SelectDialog {
    return SelectDialog(context, R.style.transparentFrameWindowStyle, listener, names, title)
}


/**
 * 条件选择弹窗 - 需要显示活着隐藏底部取消按钮
 */
fun showTypeSelDialog(context: Activity, listener: SelectDialog.SelectImageDialogListener, names: MutableList<String>, title: String, boolean: Boolean): SelectDialog {
    return SelectDialog(context, R.style.transparentFrameWindowStyle, listener, names, title, boolean)
}

/**
 * 条件选择弹窗 - 显示取消按钮，
 */
fun showTypeSelDialog(context: Activity, listener: SelectDialog.SelectImageDialogListener, names: MutableList<String>, title: String, sleStr: String): SelectDialog {
    return SelectDialog(context, R.style.transparentFrameWindowStyle, listener, names, title, true, sleStr)
}


fun Context.preview(data: MutableList<ImageItem>, position: Int) {
    //打开预览
    val intentPreview = Intent(this, ImagePreviewDelActivity::class.java)
    intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, ArrayList(data))
    intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position)
    intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true)
    intentPreview.putExtra(ImagePicker.EXTRA_CAN_EDIT, false)
    intentPreview.putExtra(ImagePicker.EXTRA_FROM_LONG_CLICK_ITEMS, true)
    startActivity(intentPreview)
}

/**
 * 分享类型转换
 */
fun transformShareType(targetType: String): Int {
    return when (targetType) { //1: 好友 , 2: 圈子 , 3:活动，4：城市新闻 5 就业易
        "activity" -> 3 // 活动
        "news", "officialNews" -> 4 // 资讯
        "user" -> 1
        "circle" -> 2 // 圈子
        "recruit" -> 5 // 就业易
        "heatedDebate" -> 6 //全城热议
        else -> 0
    }
}

/**
 * 数组转Set去重
 */
fun <T> judgeListAsSet(list: MutableList<T>): MutableList<T> {
    val set: LinkedHashSet<T> = LinkedHashSet(list.size)
    set.addAll(list)
    list.clear()
    list.addAll(set)
    return list
}

fun matcherSearchTitle(color:Int, text:String, keyword:String) : SpannableString {
    val string = text.toLowerCase(Locale.getDefault())
    val key = keyword.toLowerCase(Locale.getDefault())
    val pattern = Pattern.compile(key)
    val matcher = pattern.matcher(string)
    val sb = SpannableString(text)
    while (matcher.find()){
        val start = matcher.start()
        val end = matcher.end()
        sb.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return sb
}
