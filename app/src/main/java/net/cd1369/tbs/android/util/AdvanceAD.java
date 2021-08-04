package net.cd1369.tbs.android.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.advance.AdvanceBanner;
import com.advance.AdvanceBannerListener;
import com.advance.AdvanceBaseAdspot;
import com.advance.AdvanceConfig;
import com.advance.AdvanceInterstitial;
import com.advance.AdvanceInterstitialListener;
import com.advance.AdvanceNativeExpress;
import com.advance.AdvanceNativeExpressAdItem;
import com.advance.AdvanceNativeExpressListener;
import com.advance.AdvanceSDK;
import com.advance.model.AdvanceError;
import com.advance.supplier.baidu.AdvanceBDManager;
import com.advance.supplier.csj.CsjNativeExpressAdItem;
import com.advance.utils.LogUtil;
import com.advance.utils.ScreenUtil;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.mercury.sdk.core.config.MercuryAD;

import net.cd1369.tbs.android.BuildConfig;
import net.cd1369.tbs.android.config.Const;
import net.cd1369.tbs.android.data.entity.ArticleEntity;

import java.util.ArrayList;
import java.util.List;

import cn.wl.android.lib.utils.Lists;

/**
 * Advance SDK广告加载逻辑统一处理类
 */
public class AdvanceAD {
    AdvanceBaseAdspot baseAD;
    Activity mActivity;

    String sdkId;

    /**
     * 初始化广告处理类
     *
     * @param activity 页面上下文
     */
    public AdvanceAD(Activity activity) {
        mActivity = activity;
    }


    /**
     * 初始化advance sdk
     *
     * @param context 上下文内容，一般是传入application的context
     */
    public static void initAD(Context context) {
        //必要配置：初始化聚合SDK，三个参数依次为context上下文，appId媒体id，isDebug调试模式开关
        AdvanceSDK.initSDK(context, Const.AD_ID, BuildConfig.DEBUG);
        //推荐配置：允许Mercury预缓存素材
        MercuryAD.needPreLoadMaterial(true);
    }

    /**
     * 开屏跳转回调
     */
    public interface SplashCallBack {
        void jumpMain();
    }

    /**
     * 加载并展示banner广告
     *
     * @param adContainer banner广告的承载布局
     */
    public void loadBanner(final ViewGroup adContainer) {
        AdvanceBanner advanceBanner = new AdvanceBanner(mActivity, adContainer, Const.BANNER_ID);
        baseAD = advanceBanner;
        //设置穿山甲布局尺寸，宽度全屏，高度自适应
        advanceBanner.setCsjExpressViewAcceptedSize(ScreenUtil.px2dip(mActivity, ScreenUtil.getScreenWidth(mActivity)), 0);
        //推荐：核心事件监听回调
        advanceBanner.setAdListener(new AdvanceBannerListener() {
            @Override
            public void onDislike() {
                logAndToast(mActivity, "广告关闭");

                adContainer.removeAllViews();
            }

            @Override
            public void onAdShow() {
                logAndToast(mActivity, "广告展现");
            }

            @Override
            public void onAdFailed(AdvanceError advanceError) {
                logAndToast(mActivity, "广告加载失败 code=" + advanceError.code + " msg=" + advanceError.msg);
            }

            @Override
            public void onSdkSelected(String id) {
                logAndToast(mActivity, "策略选中SDK id = " + id);
            }

            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");
            }


            @Override
            public void onAdLoaded() {
                logAndToast(mActivity, "广告加载成功");
            }

        });
        advanceBanner.loadStrategy();
    }


    /**
     * 加载并展示插屏广告。
     * 也可以选择性先提前加载，然后在合适的时机再调用展示方法
     */
    public void loadInterstitial() {
        //初始化
        final AdvanceInterstitial advanceInterstitial = new AdvanceInterstitial(mActivity, Const.BANNER_ID);
        baseAD = advanceInterstitial;
        //注意：穿山甲是否为"新插屏广告"，默认为true
//        advanceInterstitial.setCsjNew(false);
        //推荐：核心事件监听回调
        advanceInterstitial.setAdListener(new AdvanceInterstitialListener() {

            @Override
            public void onAdReady() {
                logAndToast(mActivity, "广告就绪");

                // 大多数情况下可以直接展示
                // 如果有业务需求，可以提前加载广告，保存广告对象，需要时再调用show
                if (advanceInterstitial != null) {
                    advanceInterstitial.show();
                }
            }

            @Override
            public void onAdClose() {
                logAndToast(mActivity, "广告关闭");
            }


            @Override
            public void onAdShow() {
                logAndToast(mActivity, "广告展示");
            }

            @Override
            public void onAdFailed(AdvanceError advanceError) {
                logAndToast(mActivity, "广告加载失败 code=" + advanceError.code + " msg=" + advanceError.msg);
            }

            @Override
            public void onSdkSelected(String id) {
                logAndToast(mActivity, "onSdkSelected = " + id);
            }

            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");
            }
        });
        advanceInterstitial.loadStrategy();

    }

    boolean isGdtExpress2 = false;
    AdvanceNativeExpressAdItem advanceNativeExpressAdItem;
    boolean hasNativeShow = false;
    boolean isNativeLoading = false;

    /**
     * 加载并展示原生模板信息流广告
     *
     * @param adContainer 广告的承载布局
     */
    public void loadNativeExpress(final ViewGroup adContainer) {
        if (hasNativeShow) {
            LogUtil.d("loadNativeExpress hasNativeShow");
            return;
        }
        if (advanceNativeExpressAdItem != null) {
            if (adContainer.getChildCount() > 0 && adContainer.getChildAt(0) == advanceNativeExpressAdItem.getExpressAdView()) {
                return;
            }
        }
        if (isNativeLoading) {
            LogUtil.d("loadNativeExpress isNativeLoading");
            return;
        }
        isNativeLoading = true;

        if (adContainer.getChildCount() > 0) {
            adContainer.removeAllViews();
        }

        AdvanceBDManager.getInstance().nativeExpressContainer = adContainer;

        //初始化
        final AdvanceNativeExpress advanceNativeExpress = new AdvanceNativeExpress(mActivity, Const.FLOW_ID);
        baseAD = advanceNativeExpress;
        //推荐：核心事件监听回调
        advanceNativeExpress.setAdListener(new AdvanceNativeExpressListener() {
            @Override
            public void onAdLoaded(List<AdvanceNativeExpressAdItem> list) {

                if (null == list || list.isEmpty()) {
                    Log.d("DEMO", "NO AD RESULT");
                } else {
                    advanceNativeExpressAdItem = list.get(0);
                    if (advanceNativeExpressAdItem == null) {
                        Log.d("DEMO", "NO AD RESULT");
                        return;
                    }
                    logAndToast(mActivity, "广告加载成功");

                    //穿山甲需要设置dislike逻辑，要在选中回调里移除广告
                    if (AdvanceConfig.SDK_ID_CSJ.equals(advanceNativeExpressAdItem.getSdkId())) {
                        CsjNativeExpressAdItem csjNativeExpressAdItem = (CsjNativeExpressAdItem) advanceNativeExpressAdItem;
                        csjNativeExpressAdItem.setDislikeCallback(mActivity, new TTAdDislike.DislikeInteractionCallback() {
                            @Override
                            public void onShow() {

                            }

                            @Override
                            public void onSelected(int i, String s, boolean enforce) {
                                if (adContainer != null)
                                    adContainer.removeAllViews();
                            }

                            @Override
                            public void onCancel() {

                            }

//                    @Override
//                    public void onRefuse() {
//
//                    }
                        });
                    }

                    //从实际执行结果中获取是否是广点通模板2.0类型广告
                    isGdtExpress2 = AdvanceConfig.SDK_ID_GDT.equals(advanceNativeExpressAdItem.getSdkId()) && advanceNativeExpress.isGdtExpress2();

                    //广点通模板2.0不可以在这里可以直接添加视图，否则无法展示，应该在onAdRenderSuccess中添加视图
                    if (!isGdtExpress2) {
                        adContainer.removeAllViews();
                        adContainer.setVisibility(View.VISIBLE);
                        adContainer.addView(advanceNativeExpressAdItem.getExpressAdView());
                    }
                    //render以后才会进行广告渲染， 广告可见才会产生曝光，否则将无法产生收益。
                    advanceNativeExpressAdItem.render();
                }
            }

            @Override
            public void onAdRenderSuccess(View view) {
                logAndToast(mActivity, "广告渲染成功");

                //需要对回调进行主线程切换，防止回调在非主线程导致崩溃
                boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();
                if (isMainThread) {
                    renderGdt2(adContainer);
                } else {
                    //如果是非主线程，需要强制切换到主线程来进行初始化
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.AdvanceLog("force to main thread run render");
                            renderGdt2(adContainer);
                        }
                    });
                }
            }


            @Override
            public void onAdClose(View view) {
                //移除布局
                adContainer.removeAllViews();
                logAndToast(mActivity, "广告关闭");
            }

            @Override
            public void onAdShow(View view) {
                hasNativeShow = true;
                isNativeLoading = false;
                logAndToast(mActivity, "广告展示");
            }

            @Override
            public void onAdFailed(AdvanceError advanceError) {
                isNativeLoading = false;
                logAndToast(mActivity, "广告加载失败 code=" + advanceError.code + " msg=" + advanceError.msg);
            }

            @Override
            public void onSdkSelected(String id) {
                logAndToast(mActivity, "onSdkSelected = " + id);
            }

            @Override
            public void onAdRenderFailed(View view) {
                logAndToast(mActivity, "广告渲染失败");
            }

            @Override
            public void onAdClicked(View view) {
                logAndToast(mActivity, "广告点击");
            }

        });
        advanceNativeExpress.loadStrategy();
    }

    private void renderGdt2(ViewGroup adContainer) {
        Log.d("NativeExpressActivity", "renderGdt2  adContainer = " + adContainer);

        if (adContainer == null) {
            return;
        }
        //广点通模板2.0 需要在RenderSuccess以后再加载视图
        if (advanceNativeExpressAdItem != null && advanceNativeExpressAdItem.getSdkId().equals(AdvanceConfig.SDK_ID_GDT) && isGdtExpress2) {
            adContainer.removeAllViews();
            adContainer.setVisibility(View.VISIBLE);

            // 广告可见才会产生曝光，否则将无法产生收益。
            adContainer.addView(advanceNativeExpressAdItem.getExpressAdView());
        }
    }

    /**
     * 销毁广告
     */
    public void destroy() {
        if (baseAD != null) {
            baseAD.destroy();
            baseAD = null;
        }
    }

    /**
     * 统一处理打印日志，并且toast提示。
     *
     * @param context 上下文
     * @param msg     需要显示的内容
     */
    public void logAndToast(Context context, String msg) {
        Log.d("[DemoUtil][logAndToast]", msg);
        //如果不想弹出toast可以在此注释掉下面代码
//        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    private static final int AD_COUNT = 7;

    public static List<ArticleEntity> insertAd(List<ArticleEntity> adapterList, List<ArticleEntity> resultList, boolean loadMore) {
        if (Lists.isEmpty(resultList)) return new ArrayList<>(0);

        if (!loadMore || adapterList == null) {
            adapterList = new ArrayList<>(0);
        }

        int count = 0;
        ll:
        for (int i = adapterList.size() - 1; i >= 0; i--) {
            ArticleEntity entity = adapterList.get(i);
            if (entity.isAD() || count >= AD_COUNT) {
                break ll;
            }
            count++;
        }

        List<ArticleEntity> temp = new ArrayList<>();

        for (ArticleEntity articleEntity : resultList) {
            if (++count >= AD_COUNT) {
                count = 0;
                temp.add(ArticleEntity.Companion.getTempAD());
            }

            temp.add(articleEntity);
        }

        return temp;
    }

}
