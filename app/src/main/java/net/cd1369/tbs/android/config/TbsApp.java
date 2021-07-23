package net.cd1369.tbs.android.config;

import android.text.TextUtils;

import androidx.multidex.MultiDexApplication;

import com.advance.AdvanceSDK;
import com.blankj.utilcode.util.Utils;
import com.mercury.sdk.core.config.MercuryAD;

import net.cd1369.tbs.android.BuildConfig;
import net.cd1369.tbs.android.data.entity.TokenEntity;
import net.cd1369.tbs.android.util.Tools;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import cn.wl.android.lib.config.WLConfig;
import cn.wl.android.lib.data.core.HttpConfig;
import cn.wl.android.lib.data.repository.BaseApi;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;

public class TbsApp extends MultiDexApplication {
    public static TbsApp mContext;

    public static TbsApp getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        BaseApi.mProvider = () -> RetryHolder.mTempRetry;

        Utils.init(this);

        //必要配置：初始化聚合SDK，三个参数依次为context上下文，appId媒体id，isDebug调试模式开关
        AdvanceSDK.initSDK(this,"100171",BuildConfig.DEBUG);
        //推荐配置：允许Mercury预缓存素材
        MercuryAD.needPreLoadMaterial(true);

        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        JPushInterface.init(mContext);

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

    /**
     *
     * @param entity
     * @param tempId
     */
    public static void doUserRefresh(TokenEntity entity, String tempId) {
        DataConfig.get().setTempId(tempId);
        HttpConfig.saveToken(entity.getToken());
        UserConfig.get().setUserEntity(entity.getUserInfo());
    }

    public static class RetryHolder {

        public static final Observable<String> mTempRetry = Observable
                .defer(() -> {
                    String tempId = DataConfig.get().getTempId();

                    if (TextUtils.isEmpty(tempId)) {
                        tempId = Tools.INSTANCE.createTempId();
                    }

                    String finalTempId = tempId;
                    return TbsApi.INSTANCE.user().obtainTempLogin(tempId)
                            .doOnNext(t -> doUserRefresh(t, finalTempId))
                            .map(t -> t.getToken());
                })
                .retry(3)
                .replay(1)
                .refCount(16, TimeUnit.SECONDS);

    }
}
