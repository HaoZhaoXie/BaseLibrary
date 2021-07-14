package com.cangjiedata.lib_widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cangjiedata.lib_widget.R;

/**
 * 活动拒绝的dialog
 * Created by ZQXQ-Developer on 2019/12/21.
 */

public class MeetingRejectDialog {

    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private TextView txt_msg;
    private ImageView btn_pos;
    private Display display;

    public MeetingRejectDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public MeetingRejectDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_meeting_reject, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);

        txt_msg = (TextView) view.findViewById(R.id.txt_msg);

        btn_pos = (ImageView) view.findViewById(R.id.ivRejectCancel);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.CommonDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85), LinearLayout.LayoutParams.WRAP_CONTENT));
        return this;
    }


    public MeetingRejectDialog setMessageGravity(int gravity) {
        txt_msg.setGravity(gravity);
        return this;
    }


    public MeetingRejectDialog setMsg(String msg) {
        if ("".equals(msg)) {
            txt_msg.setText("内容");
        } else {
            txt_msg.setText(msg);
        }
        return this;
    }

    public MeetingRejectDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public MeetingRejectDialog setPositiveButton( final View.OnClickListener listener) {
        btn_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }


    private void setLayout() {

        btn_pos.setVisibility(View.VISIBLE);
        btn_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void show() {
        setLayout();
        if (dialog.isShowing()) {
            return;
        }
        dialog.show();
    }
}