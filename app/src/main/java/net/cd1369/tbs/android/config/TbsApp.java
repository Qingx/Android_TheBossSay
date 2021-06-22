package net.cd1369.tbs.android.config;

import androidx.multidex.MultiDexApplication;

import com.blankj.utilcode.util.Utils;

import net.cd1369.tbs.android.BuildConfig;

import cn.wl.android.lib.config.WLConfig;

public class TbsApp extends MultiDexApplication {
    public static TbsApp mContext;

    public static TbsApp getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        Utils.init(this);
        WLConfig.init(mContext, BuildConfig.DEBUG);
        WLConfig.initHttp(new WLConfig.UrlProvider() {
            @Override
            public String baseUrl() {
                return BuildConfig.BASE_URL;
            }

            @Override
            public String downUrl() {
                return BuildConfig.DOWN_URL;
            }

            @Override
            public String fileUrl() {
                return BuildConfig.FILE_URL;
            }

        });
    }
}
