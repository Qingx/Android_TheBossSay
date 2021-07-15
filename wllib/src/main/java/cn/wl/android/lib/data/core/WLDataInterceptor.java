package cn.wl.android.lib.data.core;

import android.util.Log;

import com.blankj.utilcode.util.AppUtils;

import java.io.IOException;

import cn.wl.android.lib.config.WLConfig;
import cn.wl.android.lib.miss.ReportMiss;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by JustBlue on 2019-08-24.
 *
 * @email: bo.li@cdxzhi.com
 * @desc: 默认数据拦截器
 */
public class WLDataInterceptor implements Interceptor {

    public static final String MOVE_URL = "myhuaweicloud.com";

    @Override
    public Response intercept(Chain chain) throws IOException {
        return proceed(chain);
    }

    private Response proceed(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Request.Builder requestBuilder = oldRequest.newBuilder();


        String url = oldRequest.url().toString();

        if (!url.contains(MOVE_URL)) {
            String sign = HttpConfig.getSign();
            String token = HttpConfig.getToken();

            if (WLConfig.isDebug()) {
                Log.e("OkHttp", " sign -> " + sign);
                Log.e("OkHttp", " Authorization ->" + token);
            }

            String versionName = AppUtils.getAppVersionName();

            requestBuilder
                    .addHeader("Authorization", token)
                    .addHeader("version", versionName)
                    .addHeader("sign", sign);
        }

        Request request = requestBuilder.build();

        return chain.proceed(request);
    }

}
