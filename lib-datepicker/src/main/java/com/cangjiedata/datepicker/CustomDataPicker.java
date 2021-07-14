package com.cangjiedata.datepicker;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.datepicker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：自定义时间选择器
 */
public class CustomDataPicker implements View.OnClickListener, PickerView.OnSelectListener {

    private Context mContext;
    private Callback mCallback;
    private CallbackPosition mCallbackPosition;
    private boolean mCanDialogShow;
    private Dialog mPickerDialog;
    private PickerView mDpvData;
    private TextView tvTitle;
    private String mSelectedData;
    private List<String> data = new ArrayList<>();

    /**
     * 级联滚动延迟时间
     */
    private static final long LINKAGE_DELAY_DEFAULT = 100L;

    public void setTitle(String title) {
        if (tvTitle == null) {
            return;
        }
        if (TextUtils.isEmpty(title)) {
            tvTitle.setText("");
        } else {
            tvTitle.setText(title);
        }
    }

    /**
     * 时间选择结果回调接口
     */
    public interface Callback {
        void onDateSelected(String tamp);
    }

    /**
     * 时间选择结果回调接口
     */
    public interface CallbackPosition {
        void onDateSelected(String tamp, int position);
    }

    /**
     * 通过时间戳初始换时间选择器，毫秒级别
     *
     * @param context  Activity Context
     * @param callback 选择结果回调
     * @param data     初始化数据
     */
    public CustomDataPicker(Context context, Callback callback, List<String> data) {
        if (context == null || callback == null || data == null) {
            mCanDialogShow = false;
            return;
        }

        mContext = context;
        mCallback = callback;
        this.data = data;
        initView();
        initData();
        mCanDialogShow = true;
    }

    /**
     * 通过时间戳初始换时间选择器，毫秒级别
     *
     * @param context  Activity Context
     * @param callback 选择结果回调
     * @param data     初始化数据
     */
    public CustomDataPicker(Context context, CallbackPosition callback, List<String> data) {
        if (context == null || callback == null || data == null) {
            mCanDialogShow = false;
            return;
        }

        mContext = context;
        mCallbackPosition = callback;
        this.data = data;
        initView();
        initData();
        mCanDialogShow = true;
    }


    private void initView() {
        mPickerDialog = new Dialog(mContext, R.style.date_picker_dialog);
        mPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPickerDialog.setContentView(R.layout.dialog_data_picker);

        Window window = mPickerDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }

        mPickerDialog.findViewById(R.id.tv_cancel).setOnClickListener(this);
        mPickerDialog.findViewById(R.id.tv_confirm).setOnClickListener(this);
        tvTitle = mPickerDialog.findViewById(R.id.tv_title);

        mDpvData = mPickerDialog.findViewById(R.id.datePicker);
        mDpvData.setOnSelectListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cancel) {
        } else if (id == R.id.tv_confirm) {
            if (mCallback != null) {
                mCallback.onDateSelected(mSelectedData);
            }

            if (mCallbackPosition != null) {
                mCallbackPosition.onDateSelected(mSelectedData, data.indexOf(mSelectedData));
            }
        }

        if (mPickerDialog != null && mPickerDialog.isShowing()) {
            mPickerDialog.dismiss();
        }
    }

    @Override
    public void onSelect(View view, String selected) {
        if (view == null || TextUtils.isEmpty(selected)) return;
        int id = view.getId();
        if (id == R.id.datePicker) {
            mSelectedData = selected;
        }
    }

    private void initData() {
        mSelectedData = "";
        mDpvData.setDataList(data);
        mDpvData.setSelected(0);
        mDpvData.setCanScroll(data.size() > 1);
    }

    /**
     * 展示时间选择器
     *
     * @param dateStr 字符串
     */
    public void show(String dateStr) {
        if (!canShow() || TextUtils.isEmpty(dateStr)) return;
        // 弹窗时，考虑用户体验，不展示滚动动画
        if (setSelectedData(dateStr, false)) {
            mPickerDialog.show();
        }
    }

    private boolean canShow() {
        return mCanDialogShow && mPickerDialog != null;
    }


    /**
     * 设置日期选择器的选中数据
     *
     * @param timestamp 毫秒级时间戳
     * @param showAnim  是否展示动画
     * @return 是否设置成功
     */
    public boolean setSelectedData(String timestamp, boolean showAnim) {
        if (!canShow()) return false;
        mSelectedData = timestamp;
        mDpvData.setDataList(data);
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).equals(timestamp)) {
                mDpvData.setSelected(i);
                break;
            }
        }
        return true;
    }

    /**
     * 设置是否允许点击屏幕或物理返回键关闭
     */
    public void setCancelable(boolean cancelable) {
        if (!canShow()) return;

        mPickerDialog.setCancelable(cancelable);
    }

    /**
     * 设置日期控件是否可以循环滚动
     */
    public void setScrollLoop(boolean canLoop) {
        if (!canShow()) return;

        mDpvData.setCanScrollLoop(canLoop);
    }

    /**
     * 设置日期控件是否展示滚动动画
     */
    public void setCanShowAnim(boolean canShowAnim) {
        if (!canShow()) return;
        mDpvData.setCanShowAnim(canShowAnim);
    }

    /**
     * 销毁弹窗
     */
    public void onDestroy() {
        if (mPickerDialog != null) {
            mPickerDialog.dismiss();
            mPickerDialog = null;
            mDpvData.onDestroy();
        }
    }

}
