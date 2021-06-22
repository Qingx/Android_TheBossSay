package cn.wl.android.lib.utils;

import java.text.DecimalFormat;

/**
 * Created by JustBlue on 2019-11-16.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
public final class FormatUtil {

    /**
     * 格式化数据
     *
     * @param num
     * @return
     */
    public static String num0_xx(double num) {
        try {
            return new DecimalFormat("0.##").format(num);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 格式化数据
     *
     * @param num
     * @return
     */
    public static String num0_00(double num) {
        try {
            return new DecimalFormat("0.00").format(num);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 格式数字, 大于1000加K
     *
     * @param num
     * @return
     */
    public static String formatNum(double num) {
        if (num >= 10_000) {
            return num0_xx(num / 10000) + "W";
        } else if (num >= 1000) {
            return num0_xx(num / 1000) + "K";
        } else {
            return num0_xx(num);
        }
    }

}
