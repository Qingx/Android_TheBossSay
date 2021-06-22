package cn.wl.android.lib.data.core;

import android.text.TextUtils;

import java.util.concurrent.atomic.AtomicReference;

import cn.wl.android.lib.config.DataConfig;

/**
 * Created by JustBlue on 2019-08-26.
 *
 * @email: bo.li@cdxzhi.com
 * @desc: 网络配置参数
 */
public final class HttpConfig {

    public static final String EMPTY_TOKEN = "EMPTY_TOKEN";
    public static final String EMPTY_SIGN = "EMPTY_SIGN";
    public static final String REPORT_ID = "REPORT_ID";

    private volatile static String mTokenRef;
    private static final AtomicReference<String> mSignRef = new AtomicReference<>(EMPTY_SIGN);

    private static final AtomicReference<String> mReportId = new AtomicReference<>(REPORT_ID);

    /**
     * 保存文件上传的sign
     *
     * @param sign
     */
    public static void saveSign(String sign) {
        mSignRef.set(sign);
    }

    /**
     * 获取文件上传的sign
     *
     * @return
     */
    public static String getSign() {
        return mSignRef.get();
    }

    /**
     * 重置信息
     */
    public static void reset() {
        saveSign(EMPTY_SIGN);
        saveToken(EMPTY_TOKEN);
    }

    /**
     * 检查sign是否为空
     *
     * @return
     */
    public static boolean checkSign() {
        for (; ; ) {
            String sign = mSignRef.get();

            boolean hasSign = !TextUtils.isEmpty(sign)
                    && !EMPTY_SIGN.equals(sign);

            if (sign == mSignRef.get()) {
                return hasSign;
            }
        }
    }

    /**
     * 保存token
     *
     * @param token
     */
    public static void saveToken(String token) {
        synchronized (HttpConfig.class) {
            DataConfig.saveToken(token);
            mTokenRef = token;
        }
    }

    /**
     * 获取token
     *
     * @return
     */
    public static String getToken() {
        if (mTokenRef == null) {
            synchronized (HttpConfig.class) {
                if (mTokenRef == null) {
                    mTokenRef = DataConfig.getToken();
                }
            }
        }

        return mTokenRef;
    }

    /**
     * 保存报告列表id
     *
     * @param id
     */
    public static void saveReportId(String id) {
        mReportId.set(id);
    }

    /**
     * 获取报告列表id
     *
     * @return
     */
    public static String getReportId() {
        return mReportId.get();
    }


}
