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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 年区间选择器
 */
public class YearDatePicker implements View.OnClickListener, PickerView.OnSelectListener {
    private Context mContext;
    private Callback mCallback;
    private Calendar mBeginTime, mEndTime, mSelectedStartTime, mSelectedEndTime;
    private boolean mCanDialogShow;

    private Dialog mPickerDialog;
    private PickerView mStartYearPv, mEndYearPv;
    private TextView tvTitle;
    private int mBeginYear, mEndYear;
    private List<String> mStartYearUnits = new ArrayList<>(), mEndYearUnits = new ArrayList<>();
    private DecimalFormat mDecimalFormat = new DecimalFormat("00");

    /**
     * 级联滚动延迟时间
     */
    private static final long LINKAGE_DELAY_DEFAULT = 100L;


    /**
     * 时间选择结果回调接口
     */
    public interface Callback {
        void onTimeSelected(String startTime, String endTime);
    }


    /**
     * 通过时间戳初始换时间选择器，毫秒级别
     *
     * @param context        Activity Context
     * @param callback       选择结果回调
     * @param beginTimestamp 毫秒级时间戳
     * @param endTimestamp   毫秒级时间戳
     */
    public YearDatePicker(Context context, Callback callback, long beginTimestamp, long endTimestamp) {
        if (context == null || callback == null || beginTimestamp < -28800000 || beginTimestamp >= endTimestamp) {
            mCanDialogShow = false;
            return;
        }
        mContext = context;
        mCallback = callback;
        mBeginTime = Calendar.getInstance();
        mBeginTime.setTimeInMillis(beginTimestamp);
        mEndTime = Calendar.getInstance();
        mEndTime.setTimeInMillis(endTimestamp);
        mSelectedStartTime = Calendar.getInstance();
        mSelectedEndTime = Calendar.getInstance();

        initView();
        initDateUnits();
        mCanDialogShow = true;
    }

    private void initView() {
        mPickerDialog = new Dialog(mContext, R.style.date_picker_dialog);
        mPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPickerDialog.setContentView(R.layout.dialog_year_picker);

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
        mStartYearPv = mPickerDialog.findViewById(R.id.dpv_year);
        mStartYearPv.setOnSelectListener(this);
        mEndYearPv = mPickerDialog.findViewById(R.id.dpv_month);
        mEndYearPv.setOnSelectListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cancel) {
        } else if (id == R.id.tv_confirm) {
            if (mCallback != null) {
                String start = DateFormatUtils.long2Str(mSelectedStartTime.getTimeInMillis(), false);
                String end = DateFormatUtils.long2Str(mSelectedEndTime.getTimeInMillis(), false);
                mCallback.onTimeSelected(start.substring(0, 4) + "年", end.substring(0, 4) + "年");
            }
        }

        if (mPickerDialog != null && mPickerDialog.isShowing()) {
            mPickerDialog.dismiss();
        }
    }

    @Override
    public void onSelect(View view, String selected) {
        if (view == null || TextUtils.isEmpty(selected)) return;
        int timeUnit = 0;
        try {
            if (selected.contains("年")) {
                timeUnit = Integer.parseInt(selected.replace("年", ""));
            } else {
                timeUnit = Integer.parseInt(selected);
            }
        } catch (Throwable ignored) {

            return;
        }

        int id = view.getId();
        if (id == R.id.dpv_year) {
            mSelectedStartTime.set(Calendar.YEAR, timeUnit);
            linkageEndYearUnit(true, LINKAGE_DELAY_DEFAULT);
        } else if (id == R.id.dpv_month) {
            mSelectedEndTime.set(Calendar.YEAR, timeUnit);
        }
    }


    private void initDateUnits() {
        mBeginYear = mBeginTime.get(Calendar.YEAR);
        mEndYear = mEndTime.get(Calendar.YEAR);

        mSelectedStartTime.setTimeInMillis(mBeginTime.getTimeInMillis());
        mSelectedEndTime.setTimeInMillis(mEndTime.getTimeInMillis());
        for (int i = mBeginYear; i <= DateFormatUtils.getCurrentYear(); i++) {
            mStartYearUnits.add(String.valueOf(i) + "年");
        }
        for (int i = mBeginYear; i <= DateFormatUtils.getCurrentYear() + 7; i++) {
            mEndYearUnits.add(mDecimalFormat.format(i) + "年");
        }
        mStartYearPv.setDataList(mStartYearUnits);
        mStartYearPv.setSelected(0);
        mEndYearPv.setDataList(mEndYearUnits);
        mEndYearPv.setSelected(0);
        setCanScroll();
    }

    private void setCanScroll() {
        mStartYearPv.setCanScroll(mStartYearUnits.size() > 1);
        mEndYearPv.setCanScroll(mEndYearUnits.size() > 1);
    }

    /**
     * 联动结束年的变化
     *
     * @param showAnim 是否展示滚动动画
     * @param delay    联动下一级延迟时间
     */
    private void linkageEndYearUnit(final boolean showAnim, final long delay) {
        int minYear;
        int maxYear;

        minYear = mSelectedStartTime.get(Calendar.YEAR);
        maxYear = DateFormatUtils.getCurrentYear() + 7;

        // 重新初始化时间单元容器
        mEndYearUnits.clear();
        for (int i = minYear; i <= maxYear; i++) {
            mEndYearUnits.add(i + "年");
        }
        mEndYearPv.setDataList(mEndYearUnits);
        // 确保联动时不会溢出或改变关联选中值
        int selectedEndYear = getValueInRange(mSelectedEndTime.get(Calendar.YEAR), minYear, maxYear);
        mSelectedEndTime.set(Calendar.YEAR, selectedEndYear);
        mEndYearPv.setSelected(selectedEndYear - minYear);
        if (showAnim) {
            mEndYearPv.startAnim();
        }
    }

    private int getValueInRange(int value, int minValue, int maxValue) {
        if (value < minValue) {
            return minValue;
        } else if (value > maxValue) {
            return maxValue;
        } else {
            return value;
        }
    }

    /**
     * 展示时间选择器
     *
     * @param
     */
    public void show(String dateStr, String dateEnd, boolean showFar) {
        if (!canShow() || TextUtils.isEmpty(dateStr)) return;
        // 弹窗时，考虑用户体验，不展示滚动动画
        if (setSelectedTime(dateStr, dateEnd, false, showFar)) {
            mPickerDialog.show();
        }
    }

    private boolean canShow() {
        return mCanDialogShow && mPickerDialog != null;
    }

    /**
     * 设置日期选择器的选中时间
     *
     * @param dateStr  日期字符串
     * @param showAnim 是否展示动画
     * @return 是否设置成功
     */
    public boolean setSelectedTime(String dateStr, String endtime, boolean showAnim, boolean showFar) {
        return canShow() && !TextUtils.isEmpty(dateStr)
                && setSelectedTime(DateFormatUtils.year2Long(dateStr), DateFormatUtils.year2Long(endtime), showAnim, showFar);
    }

    /**
     * 展示时间选择器
     *
     * @param timestamp 时间戳，毫秒级别
     */
    public void show(long timestamp, long endtime, boolean showSoFar) {
        if (!canShow()) return;

        if (setSelectedTime(timestamp, endtime, false, showSoFar)) {
            mPickerDialog.show();
        }
    }

    /**
     * 设置日期选择器的选中时间
     *
     * @param stimestamp 毫秒级开始时间戳
     * @param etimestamp 毫秒级结束时间戳
     * @param showAnim   是否展示动画
     * @param showFar    是否显示至今
     * @return 是否设置成功
     */
    public boolean setSelectedTime(long stimestamp, long etimestamp, boolean showAnim, boolean showFar) {
        if (!canShow()) return false;

        if (stimestamp < mBeginTime.getTimeInMillis()) {
            stimestamp = mBeginTime.getTimeInMillis();
        } else if (stimestamp > mEndTime.getTimeInMillis()) {
            stimestamp = mEndTime.getTimeInMillis();
        }
        mSelectedStartTime.setTimeInMillis(stimestamp);

        mSelectedEndTime.setTimeInMillis(etimestamp);
        mStartYearUnits.clear();
        if (showFar) {
            for (int i = mBeginYear; i <= mEndYear; i++) {
                mStartYearUnits.add(i + "年");
            }
        } else {
            for (int i = mBeginYear; i <= mEndYear; i++) {
                mStartYearUnits.add(i + "年");
            }
        }
        mStartYearPv.setDataList(mStartYearUnits);
        mStartYearPv.setSelected(mSelectedStartTime.get(Calendar.YEAR) - mBeginYear);

        linkageEndYearUnit(showAnim, showAnim ? LINKAGE_DELAY_DEFAULT : 0);
        return true;
    }

    /**
     * 设置是否允许点击屏幕或物理返回键关闭
     */
    public void setCancelable(boolean cancelable) {
        if (!canShow()) return;
        mPickerDialog.setCancelable(cancelable);
    }

    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            tvTitle.setText("");
        } else {
            tvTitle.setText(title);
        }
    }

    /**
     * 设置日期控件是否可以循环滚动
     */
    public void setScrollLoop(boolean canLoop) {
        if (!canShow()) return;

        mStartYearPv.setCanScrollLoop(canLoop);
        mEndYearPv.setCanScrollLoop(canLoop);
    }

    /**
     * 设置日期控件是否展示滚动动画
     */
    public void setCanShowAnim(boolean canShowAnim) {
        if (!canShow()) return;
        mStartYearPv.setCanShowAnim(canShowAnim);
        mEndYearPv.setCanShowAnim(canShowAnim);
    }

    /**
     * 销毁弹窗
     */
    public void onDestroy() {
        if (mPickerDialog != null) {
            mPickerDialog.dismiss();
            mPickerDialog = null;
            mStartYearPv.onDestroy();
            mEndYearPv.onDestroy();
        }
    }
}
