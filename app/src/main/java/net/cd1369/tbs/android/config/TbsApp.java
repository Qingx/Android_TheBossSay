package net.cd1369.tbs.android.config;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import androidx.multidex.MultiDexApplication;

import com.advance.AdvanceSDK;
import com.blankj.utilcode.util.Utils;
import com.github.gzuliyujiang.oaid.DeviceID;
import com.mercury.sdk.core.config.MercuryAD;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tendcloud.tenddata.TCAgent;

import net.cd1369.tbs.android.BuildConfig;
import net.cd1369.tbs.android.R;
import net.cd1369.tbs.android.data.entity.TokenEntity;
import net.cd1369.tbs.android.util.Tools;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import cn.wl.android.lib.config.WLConfig;
import cn.wl.android.lib.data.core.HttpConfig;
import cn.wl.android.lib.data.repository.BaseApi;
import cn.wl.android.lib.ui.BaseCommonActivity;
import cn.wl.android.lib.ui.OnActivityCallback;
import cn.wl.android.lib.utils.GlideApp;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;

public class TbsApp extends MultiDexApplication {
    public static TbsApp mContext;

    public static TbsApp getContext() {
        return mContext;
    }

    /**
     * 微信sdk
     */
    private static String weChatId = "wx5fd9da0bd24efe83"; //微信appId
    private static IWXAPI iwxapi;

    private static volatile boolean hasInitThree = false;

    public static String getWeChatId() {
        return weChatId;
    }

    public static IWXAPI getWeChatApi() {
        return iwxapi;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

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

        GlideApp.defaultHeadRes = R.mipmap.ic_default_head;
        GlideApp.DRAW_FAILURE = R.mipmap.ic_default_img;
        GlideApp.DRAW_DEFAULT = R.mipmap.ic_default_img;

        BaseApi.mProvider = () -> RetryHolder.mTempRetry;

        BaseCommonActivity.mCall = new OnActivityCallback() {
            @Override
            public void onCreated(Activity activity) {
                TCAgent.onPageStart(activity, activity.getClass().getSimpleName());
            }

            @Override
            public void onDestroy(Activity activity) {
                TCAgent.onPageEnd(activity, activity.getClass().getSimpleName());
            }
        };

//        tryInitThree(mContext);
    }

    /**
     * 初始化第三方lib
     *
     * @param context
     */
    public static void tryInitThree(Context context) {
        if (DataConfig.get().isNeedService()) {
            return;
        }

        if (hasInitThree) return;
        hasInitThree = true;

        Utils.init(context);

        registerToWeChat();

        //必要配置：初始化聚合SDK，三个参数依次为context上下文，appId媒体id，isDebug调试模式开关
        AdvanceSDK.initSDK(context, Const.AD_ID, BuildConfig.DEBUG);
        //推荐配置：允许Mercury预缓存素材
        MercuryAD.needPreLoadMaterial(true);

        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        JPushInterface.init(mContext);

        TCAgent.LOG_ON = WLConfig.isDebug();
        // App ID: 在TalkingData创建应用后，进入数据报表页中，在“系统设置”-“编辑应用”页面里查看App ID。
        // 渠道 ID: 是渠道标识符，可通过不同渠道单独追踪数据。
        TCAgent.init(context);
        // 如果已经在AndroidManifest.xml配置了App ID和渠道ID，调用
        // TCAgent.init(this)即可；或与AndroidManifest.xml中的对应参数保持一致。
        TCAgent.setReportUncaughtExceptions(true);
    }

    /**
     * @param entity
     * @param tempId
     */
    public static void doUserRefresh(TokenEntity entity, String tempId) {
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

    /**
     * 注册至微信
     */
    private static void registerToWeChat() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        iwxapi = WXAPIFactory.createWXAPI(mContext, weChatId, true);

        // 将应用的appId注册到微信
        iwxapi.registerApp(weChatId);

        //建议动态监听微信启动广播进行注册到微信
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // 将该app注册到微信
                iwxapi.registerApp(weChatId);
            }
        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));
    }
}
