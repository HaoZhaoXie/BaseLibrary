/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: LoadingDialog
 * Author: 星河
 * Date: 2021/4/15 10:17
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.cangjiedata.lib_widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.cangjiedata.lib_widget.R;


/**
 * @ClassName: LoadingDialog
 * @Description:
 * @Author: 星河
 * @Date: 2021/4/15 10:17
 */
public class LoadingDialog extends Dialog {
    private Context context;

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.LoadingDialog);
        this.context = context;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_widget_dialog_loading, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        setContentView(view);
        setCanceledOnTouchOutside(false);
    }
}