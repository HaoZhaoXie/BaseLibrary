/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: PayDialogFragment
 * Author: 星河
 * Date: 2021/3/15 19:02
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.cangjiedata.lib_widget.pawd;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cangjiedata.lib_widget.R;

/**
 * @ClassName: PayDialogFragment
 * @Description:
 * @Author: 星河
 * @Date: 2021/3/15 19:02
 */
public class PayDialogFragment extends DialogFragment implements PasswordEditText.OnTextInputListener {

    private static final String TAG = "PayDialogFragment";

    private OnKeyBoardListener onKeyBoardListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        //去掉dialog的标题，需要在setContentView()之前
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        View view = inflater.inflate(R.layout.layout_pay_dialog, null);
        TextView exitImgView = view.findViewById(R.id.tv_exit);
        TextView weixinView = view.findViewById(R.id.tv_wx_pay);
        weixinView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onKeyBoardListener != null) {
                    onKeyBoardListener.onWeiXinPayListener();
                }
                PayDialogFragment.this.dismiss();
            }
        });
        exitImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayDialogFragment.this.dismiss();
            }
        });


        final PasswordEditText editText = view.findViewById(R.id.et_input);
        editText.setOnTextInputListener(this);
        PasswordKeyboardView keyboardView = view.findViewById(R.id.key_board);
        keyboardView.setOnKeyListener(new PasswordKeyboardView.OnKeyListener() {
            @Override
            public void onInput(String text) {
                Log.d(TAG, "onInput: text = " + text);
                editText.append(text);
                String content = editText.getText().toString();
                Log.d(TAG, "onInput: content = " + content);
            }

            @Override
            public void onDelete() {
                Log.d(TAG, "onDelete: ");
                String content = editText.getText().toString();
                if (content.length() > 0) {
                    editText.setText(content.substring(0, content.length() - 1));
                }
            }

            @Override
            public void onForget() {
                if (onKeyBoardListener != null) {
                    onKeyBoardListener.onForgetPasswordListener();
                }
                PayDialogFragment.this.dismiss();
            }
        });
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.windowAnimations = R.style.DialogFragmentAnimation;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置dialog的位置在底部
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    @Override
    public void onComplete(String result) {
        if (onKeyBoardListener != null) {
            onKeyBoardListener.onCompleteListener(result);
        }
        PayDialogFragment.this.dismiss();
    }

    public OnKeyBoardListener getOnKeyBoardListener() {
        return onKeyBoardListener;
    }

    public void setOnKeyBoardListener(OnKeyBoardListener onKeyBoardListener) {
        this.onKeyBoardListener = onKeyBoardListener;
    }
}