package net.cd1369.tbs.android.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import java.lang.ref.WeakReference;

import cn.wl.android.lib.config.WLConfig;

public class JumpCallback {

    public WeakReference<Activity> mAct;

    @JavascriptInterface
    public void postMessage(String url) {
        Activity activity = mAct.get();

        if (activity != null) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse("http://www.cnblogs.com");
            intent.setData(content_url);
            activity.startActivity(intent);
        }
    }

}
