/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: CalculateUtils
 * Author: 星河
 * Date: 2021/1/7 15:06
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.lzy.imagepicker.util;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @ClassName: CalculateUtils
 * @Description: 计算工具
 * @Author: 星河
 * @Date: 2021/1/7 15:06
 */
public class CalculateUtils {
    private static final long serialVersionUID = -3345205828566485102L;

    /**
     * 对double数据进行取精度.
     *
     * @param value        double数据.
     * @param scale        精度位数(保留的小数位数).
     * @param roundingMode 精度取值方式.
     * @return 精度计算后的数据.
     */
    public static double round(double value, int scale, int roundingMode) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, roundingMode);
        double d = bd.doubleValue();
        bd = null;
        return d;
    }

    /**
     * 对double数据进行取精度.
     *
     * @param value double数据.
     * @param scale 精度位数(保留的小数位数).
     * @return 精度计算后的数据.
     */
    public static double round(double value, int scale) {
        return round(value, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * double 相减
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double sub(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.subtract(bd2).doubleValue();
    }

    /**
     * double 相除
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double divide(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.divide(bd2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * double 乘法
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double mul(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.multiply(bd2).doubleValue();
    }

    /**
     * @param number
     * @return
     */
    public static String numberFormat(double number) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        return nf.format(number);
    }

    public static String formatPriceStyle(double price) {
        DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
        return paramZeroPrice(decimalFormat.format(price));
    }
    public static String paramZeroPrice(String price) {
        if (TextUtils.isEmpty(price)) {
            return price;
        }
        return price.replace(".00", "");
    }
}
