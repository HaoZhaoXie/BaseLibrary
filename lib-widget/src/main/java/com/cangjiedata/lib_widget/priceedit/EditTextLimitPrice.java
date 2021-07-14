/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: EditTextLimitPrice
 * Author: 星河
 * Date: 2020/7/22 19:16
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.cangjiedata.lib_widget.priceedit;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * @ClassName: EditTextLimitPrice
 * @Description: 限制小数点后多1位小数价格输入框
 * @Author: 星河
 * @Date: 2020/7/22 19:16
 */

public class EditTextLimitPrice extends AppCompatEditText {
    public EditTextLimitPrice(Context context) {
        super(context);
    }

    public EditTextLimitPrice(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextLimitPrice(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (selStart == selEnd) {
            if (getText() == null) {//判空，防止出现空指针
                setSelection(0);
            } else {
                setSelection(getText().length()); // 保证光标始终在最后面
            }
        }
    }

}