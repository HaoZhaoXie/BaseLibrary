package com.cangjiedata.lib_widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cangjiedata.lib_widget.R;

import java.util.List;

public class SelectImageDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener {
    private SelectImageDialogListener mListener;
    private Activity mActivity;
    private TextView mMBtn_Cancel;
    private TextView mTv_Title;
    private List<String> mName;
    private String mTitle;
    private boolean mUseCustomColor = false;
    private int mFirstItemColor;
    private int mOtherItemColor;

    public interface SelectImageDialogListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }


    /**
     * 取消事件监听接口
     */
    private SelectImageDialogCancelListener mCancelListener;

    public interface SelectImageDialogCancelListener {
        public void onCancelClick(View v);
    }

    public SelectImageDialog(Activity activity, int theme,
                             SelectImageDialogListener listener, List<String> names) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        this.mName = names;

        setCanceledOnTouchOutside(true);
    }

    /**
     * @param activity       调用弹出菜单的activity
     * @param theme          主题
     * @param listener       菜单项单击事件
     * @param cancelListener 取消事件
     * @param names          菜单项名称
     */
    public SelectImageDialog(Activity activity, int theme, SelectImageDialogListener listener,
                             SelectImageDialogCancelListener cancelListener, List<String> names) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        mCancelListener = cancelListener;
        this.mName = names;

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
    public SelectImageDialog(Activity activity, int theme, SelectImageDialogListener listener,
                             List<String> names, String title) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        this.mName = names;
        mTitle = title;

        // 设置是否点击外围可解散
        setCanceledOnTouchOutside(true);
    }

    public SelectImageDialog(Activity activity, int theme, SelectImageDialogListener listener,
                             SelectImageDialogCancelListener cancelListener, List<String> names, String title) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        mCancelListener = cancelListener;
        this.mName = names;
        mTitle = title;

        // 设置是否点击外围可解散
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.layout_widget_select_image,
                null);
        setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.pop_anim_style);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = mActivity.getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        onWindowAttributesChanged(wl);

        initViews();
    }

    private void initViews() {
        DialogAdapter dialogAdapter = new DialogAdapter(mName);
        ListView dialogList = (ListView) findViewById(R.id.dialogList);
        dialogList.setOnItemClickListener(this);
        dialogList.setAdapter(dialogAdapter);
        mMBtn_Cancel = (TextView) findViewById(R.id.tvCancel);
        mTv_Title = (TextView) findViewById(R.id.tvTitle);


        mMBtn_Cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCancelListener != null) {
                    mCancelListener.onCancelClick(v);
                }
                dismiss();
            }
        });

        if (!TextUtils.isEmpty(mTitle) && mTv_Title != null) {
            mTv_Title.setVisibility(View.VISIBLE);
            mTv_Title.setText(mTitle);
        } else {
            mTv_Title.setVisibility(View.GONE);
        }
        dialogList.invalidate();
    }

    @Override
    public void onClick(View v) {
        dismiss();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        mListener.onItemClick(parent, view, position, id);
        dismiss();
    }

    private class DialogAdapter extends BaseAdapter {
        private List<String> mStrings;
        private Viewholder viewholder;
        private LayoutInflater layoutInflater;

        public DialogAdapter(List<String> strings) {
            this.mStrings = strings;
            this.layoutInflater = mActivity.getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mStrings == null ? 0 : mStrings.size();
        }

        @Override
        public Object getItem(int position) {
            return mStrings.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                viewholder = new Viewholder();
                convertView = layoutInflater.inflate(R.layout.view_dialog_item_imagepicker, null);
                viewholder.dialogItemButton = (TextView) convertView.findViewById(R.id.dialog_item_bt);
                convertView.setTag(viewholder);
            } else {
                viewholder = (Viewholder) convertView.getTag();
            }
            viewholder.dialogItemButton.setText(mStrings.get(position));
            if (!mUseCustomColor) {
                mFirstItemColor = mActivity.getResources().getColor(R.color.color_415BFD);
                mOtherItemColor = mActivity.getResources().getColor(R.color.color_415BFD);
            }
            if (1 == mStrings.size()) {
                viewholder.dialogItemButton.setTextColor(mFirstItemColor);
//                viewholder.dialogItemButton.setBackgroundResource(R.drawable.dialog_item_bg_only);
//            }
//            else if (position == 0) {
//                viewholder.dialogItemButton.setTextColor(mFirstItemColor);
//                viewholder.dialogItemButton.setBackgroundResource(R.drawable.select_dialog_item_bg_top);
//            } else if (position == mStrings.size() - 1) {
//                viewholder.dialogItemButton.setTextColor(mOtherItemColor);
//                viewholder.dialogItemButton.setBackgroundResource(R.drawable.select_dialog_item_bg_buttom);
//            } else {
//                viewholder.dialogItemButton.setTextColor(mOtherItemColor);
//                viewholder.dialogItemButton.setBackgroundResource(R.drawable.select_dialog_item_bg_center);
            }
            return convertView;
        }

    }

    public static class Viewholder {
        public TextView dialogItemButton;
    }

    /**
     * 设置列表项的文本颜色
     */
    public void setItemColor(int firstItemColor, int otherItemColor) {
        mFirstItemColor = firstItemColor;
        mOtherItemColor = otherItemColor;
        mUseCustomColor = true;
    }
}
