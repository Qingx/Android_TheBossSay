package cn.wl.android.lib.config;

import android.content.Context;

import com.blankj.utilcode.util.Utils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by JustBlue on 2019-08-24.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
public final class WLConfig {

    private static boolean isDebug; // 是否为开发环境
    private static Context mContext;// application context;

    private static UrlProvider mUrlProvider = new UrlProvider() {
        @Override
        public String baseUrl() {
            return "";
        }

        @Override
        public String downUrl() {
            return "";
        }

        @Override
        public String fileUrl() {
            return "";
        }
    };

    private static AtomicBoolean isInit = new AtomicBoolean();

    /**
     * 初始化配置参数
     *
     * @param context
     * @param isDebug
     * @param baseUrl
     */
    public static void init(Context context, boolean isDebug, String baseUrl) {
        if (isInit.compareAndSet(false, true)) {
            WLConfig.isDebug = isDebug;
            WLConfig.mContext = context;

            Utils.init(context);
        }
    }

    /**
     * 初始化配置参数
     *
     * @param context
     * @param isDebug
     */
    public static void init(Context context, boolean isDebug) {
        if (isInit.compareAndSet(false, true)) {
            WLConfig.isDebug = isDebug;
            WLConfig.mContext = context;
        }
    }

    public static void initHttp(UrlProvider provider) {
        WLConfig.mUrlProvider = provider;
    }

    /**
     * 是否是开发模式
     *
     * @return
     */
    public static boolean isDebug() {
        return WLConfig.isDebug;
    }

    /**
     * 获取全局Context
     *
     * @return
     */
    public static Context getContext() {
        return WLConfig.mContext;
    }

    /**
     * 获取{@link androidx.core.content.FileProvider} 授权码
     *
     * @return
     */
    public static String getAuthCode() {
        return mContext.getPackageName() + ".FileProvider";
    }

    public static String getBaseUrl() {
        return mUrlProvider.baseUrl();
    }

    public static String getDownUrl() {
        return mUrlProvider.downUrl();
    }

    public static String getFileUrl() {
        return mUrlProvider.fileUrl();
    }

    public interface UrlProvider {

        String baseUrl();

        String downUrl();

        String fileUrl();

    }

}
