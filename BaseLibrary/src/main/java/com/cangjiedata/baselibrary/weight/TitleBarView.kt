package com.cangjiedata.baselibrary.weight

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.cangjiedata.baselibrary.R
import java.util.*

/**
 * 类描述： 统一的标题及返回按钮的view，使用时配套使用style TitleBar
 * viewBinding无法查找到重绘view，改为tag查找当前view
 *
 * 创建时间: 2016/1/15 10:20.
 */
class TitleBarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private var title: String? = ""

    @DrawableRes
    private var backIcon: Int
    private var canBack: Boolean
    private var reInit = false
    private var barBackView: TextView? = null
    private var barTitleView: TextView? = null
    private var titleTextColorId: Int = R.color.color_272727
    private var clickListener: OnClickListener? = null
    private var fitSystem = true
    private var statusBarHeight = 0
    var onTitleRestListener: OnTitleRestListener? = null
        set(value) {
            field = value
        }

    init {
        setWillNotDraw(false)
        val a = context.obtainStyledAttributes(attrs, R.styleable.TitleBarView, defStyleAttr, 0)
        val titleID = a.getResourceId(R.styleable.TitleBarView_title, 0)
        title = if (titleID == 0) {
            a.getString(R.styleable.TitleBarView_title)
        } else {
            resources.getString(titleID)
        }
        backIcon = a.getResourceId(R.styleable.TitleBarView_backIcon, R.mipmap.icon_titlebar_back_dark)
        titleTextColorId = a.getResourceId(R.styleable.TitleBarView_titleBarColor, R.color.color_272727)
        canBack = a.getBoolean(R.styleable.TitleBarView_canBack, true)
        fitSystem = a.getBoolean(R.styleable.TitleBarView_fitSystem, true)
        if (fitSystem) {
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (resourceId > 0) {
                    statusBarHeight = resources.getDimensionPixelSize(resourceId)
                }
            } else {
                //低版本 直接设置0
                statusBarHeight = 0
            }
        }
        a.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!reInit) {
            reInitView()
        }
    }

    private fun reInitView() {
        val reInitLayout = RelativeLayout(context)
        var reInitLayoutParams = LayoutParams(layoutParams.width, layoutParams.height)
        if (fitSystem) {
            reInitLayoutParams.topMargin = statusBarHeight
            layoutParams.height += statusBarHeight
        }
        reInitLayout.layoutParams = reInitLayoutParams
        //处理当前布局
        val contentView = RelativeLayout(context)
        if (childCount > 0) {
            val child: MutableList<View> = ArrayList()
            for (i in 0 until childCount) {
                child.add(getChildAt(i))
            }
            removeAllViews()
            for (i in child.indices) {
                contentView.addView(child[i])
            }
        }
        //处理返回按钮
        val backView = LayoutInflater.from(context).inflate(R.layout.title_bar_back, null)
        barBackView = backView.findViewById(R.id.tvTitleBarBack)
        barBackView?.also {
            if (canBack) {
                it.setCompoundDrawablesRelativeWithIntrinsicBounds(backIcon, 0, 0, 0)
            }
            it.tag = canBack
            val defaultViewParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            defaultViewParams.addRule(CENTER_VERTICAL)
            backView.layoutParams = defaultViewParams
            reInitLayout.addView(backView, 0)
            if (TextUtils.isEmpty(title) && !canBack) {
                it.visibility = GONE
            }
            it.setOnClickListener { view ->
                if (canBack) {
                    clickListener?.onClick(view)
                }
            }
        }

        //处理标题
        val titleView = LayoutInflater.from(context).inflate(R.layout.title_bar_title, null)
        barTitleView = titleView.findViewById(R.id.tvTitleBarTitle)
        barTitleView?.also {
            it.text = title
            it.setTextColor(resources.getColor(titleTextColorId))
            val defaultViewParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            defaultViewParams.addRule(CENTER_IN_PARENT)
            titleView.layoutParams = defaultViewParams
            reInitLayout.addView(titleView, 1)
        }
        val contentViewParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        contentViewParams.addRule(CENTER_VERTICAL)
        if(TextUtils.isEmpty(title)){
            if(canBack){
                contentViewParams.addRule(END_OF, backView.id)
            }
        }else{
            contentViewParams.addRule(END_OF, titleView.id)
        }
        contentView.layoutParams = contentViewParams
        reInitLayout.addView(contentView, 2)
        addView(reInitLayout)
        reInit = true
        tag = "BaseTitle"
        postInvalidate()
        requestLayout()
        onTitleRestListener?.onReset()
    }

    fun setTitle(title: String?) {
        this.title = title
        barTitleView?.let {
            it.text = title
            it.visibility = if (TextUtils.isEmpty(title)) GONE else VISIBLE
        }
    }

    fun setBackIcon(resId: Int) {
        this.backIcon = resId
        this.canBack = true
        barBackView?.let {
            it.setCompoundDrawablesRelativeWithIntrinsicBounds(backIcon, 0, 0, 0)
        }
    }

    fun setTitleColor(resId: Int) {
        this.titleTextColorId = resId;
        barTitleView?.let {
            it.setTextColor(context.resources.getColor(resId))
        }
    }

    open fun setBackOnclick(clickListener: OnClickListener) {
        this.clickListener = clickListener
    }

    interface OnTitleRestListener {
        fun onReset()
    }
}