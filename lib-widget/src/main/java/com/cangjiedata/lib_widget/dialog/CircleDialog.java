package com.cangjiedata.lib_widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cangjiedata.lib_widget.R;
import com.cangjiedata.lib_widget.databinding.LayoutDialogCircleUpdateBinding;

/**
 * 创建so
 * Created by ZQXQ-Developer on 2019/12/21.
 */

public class CircleDialog extends Dialog {

    private LayoutDialogCircleUpdateBinding updateBinding;
    private Context context = null;

    // 当前状态

    public CircleDialog(Context context) {
        super(context, R.style.CommonDialogStyle);
        this.context = context;
        updateBinding = LayoutDialogCircleUpdateBinding.inflate(getLayoutInflater());
        setContentView(updateBinding.getRoot());
        setUpWindow();
    }

    private void setUpWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
    }


    public void onCancelClickListener() {

        updateBinding.tvCreateSoCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShowing()) {
                    dismiss();
                }
            }
        });

    }

    /**
     * 设置取消button
     *
     * @param listener
     */
    public void setOkButton(View.OnClickListener listener) {
        updateBinding.btnGo2Edit.setOnClickListener(listener);
    }

    @Override
    public void show() {
        // 设置dialog
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        // 设置dialog
    }

}