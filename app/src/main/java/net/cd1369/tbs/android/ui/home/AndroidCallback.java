package net.cd1369.tbs.android.ui.home;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import java.lang.ref.WeakReference;

import cn.wl.android.lib.config.WLConfig;

public class AndroidCallback {

    public boolean formBoss;
    public WeakReference<Activity> mAct;

    @JavascriptInterface
    public void postMessage(String articleId) {
        ArticleActivity.Companion.start(WLConfig.getContext(), articleId, formBoss);

        Activity activity = mAct.get();
        if (activity != null) {
            activity.finish();
        }
    }

}
