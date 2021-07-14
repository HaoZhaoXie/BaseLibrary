/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: ShareDialog
 * Author: 星河
 * Date: 2021/1/9 10:37
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.cangjiedata.lib_widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cangjiedata.lib_widget.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.List;

/**
 * @ClassName: ShareDialog
 * @Description:
 * @Date: 2021/1/9 10:37
 */
public class ShareDialog extends Dialog {
    /**
     * item 点击事件
     */
    private ShareDialogItemListener mListener;

    /**
     * 取消事件监听接口
     */
    private ShareDialogCancelListener mCancelListener;

    private Activity mActivity;
    private List<DialogItemBean> beans;
    private String mTitle = "分享出去让更多好友看到";

    public ShareDialog(Activity activity, int theme,
                       ShareDialogItemListener listener, List<DialogItemBean> names) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        this.beans = names;
        setCanceledOnTouchOutside(true);
    }

    /**
     * @param activity       调用弹出菜单的activity
     * @param theme          主题
     * @param listener       菜单项单击事件
     * @param cancelListener 取消事件
     * @param names          菜单项名称
     */
    public ShareDialog(Activity activity, int theme, ShareDialogItemListener listener,
                       ShareDialogCancelListener cancelListener, List<DialogItemBean> names) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        mCancelListener = cancelListener;
        this.beans = names;

        // 设置是否点击外围不解散
        setCanceledOnTouchOutside(false);
    }

    /**
     * @param activity 调用弹出菜单的activity
     * @param theme    主题
     * @param listener 菜单项单击事件
     * @param names    菜单项名称
     * @param title    菜单标题文字
     */
    public ShareDialog(Activity activity, int theme, ShareDialogItemListener listener,
                       List<DialogItemBean> names, String title) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        this.beans = names;
        mTitle = title;

        // 设置是否点击外围可解散
        setCanceledOnTouchOutside(true);
    }

    public ShareDialog(Activity activity, int theme, ShareDialogItemListener listener,
                       ShareDialogCancelListener cancelListener, List<DialogItemBean> names, String title) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        mCancelListener = cancelListener;
        this.beans = names;
        mTitle = title;

        // 设置是否点击外围可解散
        setCanceledOnTouchOutside(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.layout_dialog_share, null);
        setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setUpWindow();
        initViews();
    }

    private void initViews() {
        RecyclerView recyclerView = findViewById(R.id.rv_share);
        TextView mTvTitle = findViewById(R.id.tvShareTitle);
        TextView mBtnCancel = findViewById(R.id.tv_cancel);
        GridLayoutManager manager = new GridLayoutManager(mActivity, 4);
        ShareDialogAdapter adapter = new ShareDialogAdapter();
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setList(beans);
        if (!TextUtils.isEmpty(mTitle) && mTvTitle != null) {
            mTvTitle.setVisibility(View.VISIBLE);
            mTvTitle.setText(mTitle);
        } else {
            mTvTitle.setVisibility(View.GONE);
        }
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCancelListener != null) {
                    mCancelListener.onCancelClick(view);
                }
                dismiss();
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (mListener != null) {
                    mListener.onItemClick(adapter, view, position);
                }
                dismiss();
            }
        });
        recyclerView.invalidate();
    }

    //设置Dialog显示的位置
    private void setUpWindow() {
        Window window = getWindow();
        // 设置显示动画
//        window.setWindowAnimations(R.style.CommonDialogStyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = mActivity.getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        onWindowAttributesChanged(wl);
    }


    public interface ShareDialogItemListener {
        void onItemClick(BaseQuickAdapter<?, ?> parent, View view, int position);
    }

    public interface ShareDialogCancelListener {
        public void onCancelClick(View v);
    }
}