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

public class MonthDatePicker implements View.OnClickListener, PickerView.OnSelectListener {
    private Context mContext;
    private Callback mCallback;
    private Calendar mBeginTime, mEndTime, mSelectedTime;
    private boolean mCanDialogShow;

    private Dialog mPickerDialog;
    private PickerView mDpvYear, mDpvMonth;
    private TextView tvTitle;
    private int mBeginYear, mBeginMonth, mEndYear, mEndMonth;
    private List<String> mYearUnits = new ArrayList<>(), mMonthUnits = new ArrayList<>();
    private DecimalFormat mDecimalFormat = new DecimalFormat("00");
    private static final int MAX_MONTH_UNIT = 12;
    private boolean mCanShowPreciseTime;

    /**
     * 级联滚动延迟时间
     */
    private static final long LINKAGE_DELAY_DEFAULT = 100L;


    /**
     * 时间选择结果回调接口
     */
    public interface Callback {
        void onTimeSelected(String timestamp);
    }

    /**
     * 通过日期字符串初始换时间选择器
     *
     * @param context      Activity Context
     * @param callback     选择结果回调
     * @param beginDateStr 日期字符串，格式为 yyyy-MM-dd HH:mm
     * @param endDateStr   日期字符串，格式为 yyyy-MM-dd HH:mm
     */
    public MonthDatePicker(Context context, Callback callback, String beginDateStr, String endDateStr) {
        this(context, callback, DateFormatUtils.str2Long(beginDateStr, true),
                DateFormatUtils.str2Long(endDateStr, true));
    }

    /**
     * 通过时间戳初始换时间选择器，毫秒级别
     *
     * @param context        Activity Context
     * @param callback       选择结果回调
     * @param beginTimestamp 毫秒级时间戳
     * @param endTimestamp   毫秒级时间戳
     */
    public MonthDatePicker(Context context, Callback callback, long beginTimestamp, long endTimestamp) {
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
        mSelectedTime = Calendar.getInstance();


        initView();
        initData();
        mCanDialogShow = true;
    }

    private void initView() {
        mPickerDialog = new Dialog(mContext, R.style.date_picker_dialog);
        mPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPickerDialog.setContentView(R.layout.dialog_month_picker);

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
        mDpvYear = mPickerDialog.findViewById(R.id.dpv_year);
        mDpvYear.setOnSelectListener(this);
        mDpvMonth = mPickerDialog.findViewById(R.id.dpv_month);
        mDpvMonth.setOnSelectListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cancel) {
        } else if (id == R.id.tv_confirm) {
            if (mCallback != null) {
                String time = DateFormatUtils.long2Str(mSelectedTime.getTimeInMillis(), false);
                if (time.equals("3030-01")) {
                    mCallback.onTimeSelected("至今");
                } else {
                    mCallback.onTimeSelected(time);
                }
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
            } else if (selected.contains("至今")) {
                timeUnit = 3030;
            } else if (selected.contains("月")) {
                timeUnit = Integer.parseInt(selected.replace("月", ""));
            }
        } catch (Throwable ignored) {

            return;
        }

        int id = view.getId();
        if (id == R.id.dpv_year) {
            mSelectedTime.set(Calendar.YEAR, timeUnit);
            if (!selected.contains("至今")) {
                linkageMonthUnit(true, LINKAGE_DELAY_DEFAULT);
            } else {//至今
                mMonthUnits.clear();
                mMonthUnits.add("");
                mDpvMonth.setDataList(mMonthUnits);
                mSelectedTime.set(Calendar.MONTH, 0);
                mDpvMonth.setSelected(0);
                mDpvMonth.startAnim();
            }
        } else if (id == R.id.dpv_month) {// 防止类似 2018/12/31 滚动到11月时因溢出变成 2018/12/01
            mSelectedTime.set(Calendar.MONTH, timeUnit - 1);
        }
    }

    private void initData() {
        mSelectedTime.setTimeInMillis(mBeginTime.getTimeInMillis());
        mBeginYear = mBeginTime.get(Calendar.YEAR);
        // Calendar.MONTH 值为 0-11
        mBeginMonth = mBeginTime.get(Calendar.MONTH) + 1;
        mEndYear = mEndTime.get(Calendar.YEAR);
        mEndMonth = mEndTime.get(Calendar.MONTH) + 1;
        boolean canSpanYear = mBeginYear != mEndYear;
        boolean canSpanMon = !canSpanYear && mBeginMonth != mEndMonth;

        if (canSpanYear) {
            initDateUnits(MAX_MONTH_UNIT);
        } else if (canSpanMon) {
            initDateUnits(mEndMonth);
        }
    }


    private void initDateUnits(int endMonth) {

        for (int i = mBeginYear; i <= mEndYear; i++) {
            mYearUnits.add(String.valueOf(i));
        }
        for (int i = mBeginMonth; i <= endMonth; i++) {
            mMonthUnits.add(mDecimalFormat.format(i));
        }


        mDpvYear.setDataList(mYearUnits);
        mDpvYear.setSelected(0);
        mDpvMonth.setDataList(mMonthUnits);
        mDpvMonth.setSelected(0);
        setCanScroll();
    }

    private void setCanScroll() {
        mDpvYear.setCanScroll(mYearUnits.size() > 1);
        mDpvMonth.setCanScroll(mMonthUnits.size() > 1);
    }

    /**
     * 联动“月”变化
     *
     * @param showAnim 是否展示滚动动画
     * @param delay    联动下一级延迟时间
     */
    private void linkageMonthUnit(final boolean showAnim, final long delay) {
        int minMonth;
        int maxMonth;
        int selectedYear = mSelectedTime.get(Calendar.YEAR);
        if (mBeginYear == mEndYear) {
            minMonth = mBeginMonth;
            maxMonth = mEndMonth;
        } else if (selectedYear == mBeginYear) {
            minMonth = mBeginMonth;
            maxMonth = MAX_MONTH_UNIT;
        } else if (selectedYear == mEndYear) {
            minMonth = 1;
            maxMonth = mEndMonth;
        } else {
            minMonth = 1;
            maxMonth = MAX_MONTH_UNIT;
        }

        // 重新初始化时间单元容器
        mMonthUnits.clear();
        for (int i = minMonth; i <= maxMonth; i++) {
            mMonthUnits.add(i + "月");
        }
        mDpvMonth.setDataList(mMonthUnits);
        // 确保联动时不会溢出或改变关联选中值
        int selectedMonth = getValueInRange(mSelectedTime.get(Calendar.MONTH) + 1, minMonth, maxMonth);
        mSelectedTime.set(Calendar.MONTH, selectedMonth - 1);
        mDpvMonth.setSelected(selectedMonth - minMonth);
        if (showAnim) {
            mDpvMonth.startAnim();
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
    public void show(String dateStr, boolean showFar) {
        if (!canShow() || TextUtils.isEmpty(dateStr)) return;
        // 弹窗时，考虑用户体验，不展示滚动动画
        if (setSelectedTime(dateStr, false, showFar)) {
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
    public boolean setSelectedTime(String dateStr, boolean showAnim, boolean showFar) {
        return canShow() && !TextUtils.isEmpty(dateStr)
                && setSelectedTime(DateFormatUtils.str2Long(dateStr, mCanShowPreciseTime), showAnim, showFar);
    }

    /**
     * 展示时间选择器
     *
     * @param timestamp 时间戳，毫秒级别
     */
    public void show(long timestamp, boolean showSoFar) {
        if (!canShow()) return;

        if (setSelectedTime(timestamp, false, showSoFar)) {
            mPickerDialog.show();
        }
    }

    /**
     * 设置日期选择器的选中时间
     *
     * @param timestamp 毫秒级时间戳
     * @param showAnim  是否展示动画
     * @param showFar   是否显示至今
     * @return 是否设置成功
     */
    public boolean setSelectedTime(long timestamp, boolean showAnim, boolean showFar) {
        if (!canShow()) return false;

        if (timestamp < mBeginTime.getTimeInMillis()) {
            timestamp = mBeginTime.getTimeInMillis();
        } else if (timestamp > mEndTime.getTimeInMillis()) {
            timestamp = mEndTime.getTimeInMillis();
        }
        mSelectedTime.setTimeInMillis(timestamp);

        mYearUnits.clear();
        if (showFar) {
            for (int i = mBeginYear; i <= mEndYear; i++) {
                mYearUnits.add(i + "年");
            }
            mYearUnits.add("至今");
        } else {
            for (int i = mBeginYear; i <= mEndYear; i++) {
                mYearUnits.add(i + "年");
            }
        }
        mDpvYear.setDataList(mYearUnits);
        mDpvYear.setSelected(mSelectedTime.get(Calendar.YEAR) - mBeginYear);
        linkageMonthUnit(showAnim, showAnim ? LINKAGE_DELAY_DEFAULT : 0);
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

        mDpvYear.setCanScrollLoop(canLoop);
        mDpvMonth.setCanScrollLoop(canLoop);
    }

    /**
     * 设置日期控件是否展示滚动动画
     */
    public void setCanShowAnim(boolean canShowAnim) {
        if (!canShow()) return;
        mDpvYear.setCanShowAnim(canShowAnim);
        mDpvMonth.setCanShowAnim(canShowAnim);
    }

    /**
     * 销毁弹窗
     */
    public void onDestroy() {
        if (mPickerDialog != null) {
            mPickerDialog.dismiss();
            mPickerDialog = null;
            mDpvYear.onDestroy();
            mDpvMonth.onDestroy();
        }
    }
}
