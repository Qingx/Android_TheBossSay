package cn.wl.android.lib.ui;

import android.app.Activity;

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 17:17 2021/8/6
 * @desc
 */
public interface OnActivityCallback {

    void onCreated(Activity activity);

    void onDestroy(Activity activity);

}
