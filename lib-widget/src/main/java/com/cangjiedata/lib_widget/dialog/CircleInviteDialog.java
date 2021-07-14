package com.cangjiedata.lib_widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cangjiedata.lib_widget.R;

/**
 * 创建so
 * Created by ZQXQ-Developer on 2019/12/21.
 */

public class CircleInviteDialog {
    private EditText mReason;
    private Context context;
    LinearLayout relativeLayout;
    private Dialog dialog;
    private TextView btn_neg;
    private TextView btn_pos;
    //    private ImageView img_line;
    private Display display;
    private boolean showTitle = false;
    private boolean showMsg = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;


    public interface LoginInputListener {
        void onInviteInputComplete(String username);
    }

    public CircleInviteDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public CircleInviteDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_dialog_circle_invite, null);
        mReason = view.findViewById(R.id.etInputReason);
        btn_neg = (TextView) view.findViewById(R.id.tvCancel);
        btn_neg.setVisibility(View.GONE);
        btn_pos = (TextView) view.findViewById(R.id.tvOk);
        btn_pos.setVisibility(View.GONE);
        relativeLayout = view.findViewById(R.id.rlReasonBg);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.CommonDialogStyle);
        dialog.setContentView(view);

        relativeLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85), LinearLayout.LayoutParams.WRAP_CONTENT));
        return this;
    }


    public CircleInviteDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public CircleInviteDialog setPositiveButton(String text, final LoginInputListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText("确定");
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason = mReason.getText().toString();
                if (!TextUtils.isEmpty(reason)) {
                    listener.onInviteInputComplete(reason);
                } else {
                    listener.onInviteInputComplete("");
                }
                dialog.dismiss();

            }
        });
        return this;
    }

    public CircleInviteDialog setPositiveButtons(String text,
                                                 final View.OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText("确定");
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public CircleInviteDialog setNegativeButton(String text,
                                                final View.OnClickListener listener) {
        showNegBtn = true;
        if ("".equals(text)) {
            btn_neg.setText("取消");
        } else {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    private void setLayout() {
        if (!showPosBtn && !showNegBtn) {
            btn_pos.setText("确定");
            btn_pos.setVisibility(View.VISIBLE);
//            btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
            btn_pos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        if (showPosBtn && showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
//            btn_pos.setBackgroundResource(R.drawable.alertdialog_right_selector);
            btn_neg.setVisibility(View.VISIBLE);
//            btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
//            img_line.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
//            btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }

        if (!showPosBtn && showNegBtn) {
            btn_neg.setVisibility(View.VISIBLE);
//            btn_neg.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }
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