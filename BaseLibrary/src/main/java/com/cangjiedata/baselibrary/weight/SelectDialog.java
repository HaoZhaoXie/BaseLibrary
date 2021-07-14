package com.cangjiedata.baselibrary.weight;

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

import com.cangjiedata.baselibrary.R;

import java.util.List;

public class SelectDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final SelectImageDialogListener mListener;
    private final Activity mActivity;
    private List<String> mName;
    private String mTitle;
    private String mSelStr;
    private boolean mUseCustomColor = false; // 是否指定最后一条item和是否显示取消按钮
    private int mFirstItemColor;
    private int mOtherItemColor;

    public interface SelectImageDialogListener {
        void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }


    /**
     * 取消事件监听接口
     */
    private SelectImageDialogCancelListener mCancelListener;

    public interface SelectImageDialogCancelListener {
        void onCancelClick(View v);
    }

    public SelectDialog(Activity activity, int theme,
                        SelectImageDialogListener listener, List<String> names, String title) {
        this(activity, theme, listener, names, title, false);
    }

    public SelectDialog(Activity activity, int theme,
                        SelectImageDialogListener listener, List<String> names, String title, boolean mUseCustomColor) {
        this(activity, theme, listener, names, title, mUseCustomColor, names.get(names.size() - 1));
    }

    public SelectDialog(Activity activity, int theme,
                        SelectImageDialogListener listener, List<String> names, String title, boolean mUseCustomColor, String selStr) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        this.mName = names;
        this.mTitle = title;
        this.mUseCustomColor = mUseCustomColor;
        if(names.size() == 1){
            this.mSelStr = "";
        }else {
            this.mSelStr = selStr;
        }
        setCanceledOnTouchOutside(true);
    }


    /**
     * @param activity       调用弹出菜单的activity
     * @param theme          主题
     * @param listener       菜单项单击事件
     * @param cancelListener 取消事件
     * @param names          菜单项名称
     */
    public SelectDialog(Activity activity, int theme, SelectImageDialogListener listener,
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
     */
    public SelectDialog(Activity activity, int theme, SelectImageDialogListener listener,
                        List<String> names) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        this.mName = names;

        // 设置是否点击外围可解散
        setCanceledOnTouchOutside(true);
    }

    public SelectDialog(Activity activity, int theme, SelectImageDialogListener listener,
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
        View view = getLayoutInflater().inflate(R.layout.view_dialog_select,
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
        ListView dialogList = findViewById(R.id.dialog_list);
        dialogList.setOnItemClickListener(this);
        dialogList.setAdapter(dialogAdapter);
        TextView mTv_Title = findViewById(R.id.mTv_Title);
        if (!TextUtils.isEmpty(mTitle) && mTv_Title != null) {
            mTv_Title.setVisibility(View.VISIBLE);
            mTv_Title.setText(mTitle);
        } else {
            mTv_Title.setVisibility(View.GONE);
        }
        View mTv_Line = findViewById(R.id.line1);
        TextView mTv_Cancel = findViewById(R.id.tv_cancel);
        mTv_Cancel.setOnClickListener(v -> dismiss());
        if (mUseCustomColor) {
            mTv_Line.setVisibility(View.VISIBLE);
            mTv_Cancel.setVisibility(View.VISIBLE);
        } else {
            mTv_Line.setVisibility(View.GONE);
            mTv_Cancel.setVisibility(View.GONE);
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
                convertView = layoutInflater.inflate(R.layout.view_dialog_item, null);
                viewholder.dialogItemButton = (TextView) convertView.findViewById(R.id.dialog_item_bt);
                convertView.setTag(viewholder);
            } else {
                viewholder = (Viewholder) convertView.getTag();
            }
            viewholder.dialogItemButton.setText(mStrings.get(position));
            mFirstItemColor = mActivity.getResources().getColor(R.color.color_272727);
            viewholder.dialogItemButton.setTextColor(mFirstItemColor);
            if (mUseCustomColor) {
                mOtherItemColor = mActivity.getResources().getColor(R.color.color_FF1D1D);
                int color = mFirstItemColor;
                if (TextUtils.equals(mStrings.get(position), mSelStr)) {
                    color = mOtherItemColor;
                }
                viewholder.dialogItemButton.setTextColor(color);
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
