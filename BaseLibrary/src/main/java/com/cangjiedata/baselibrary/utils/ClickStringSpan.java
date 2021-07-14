package com.cangjiedata.baselibrary.utils;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;


/**
 * 显示可点击的@某人
 */
public class ClickStringSpan extends ClickableSpan {

    private int color;
    private SpanStringCallBack spanClickCallBack;
    private String str;

    public ClickStringSpan(String str, int color, SpanStringCallBack spanClickCallBack) {
        this.str = str;
        this.color = color;
        this.spanClickCallBack = spanClickCallBack;
    }

    @Override
    public void onClick(View widget) {
        if (spanClickCallBack != null) {
            spanClickCallBack.onClick(widget, str);
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        /** 给文字染色 **/
        ds.setColor(color);
        /** 去掉下划线 ， 默认自带下划线 **/
        ds.setUnderlineText(false);
    }

}