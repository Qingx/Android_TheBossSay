package cn.wl.android.lib.data.core;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.wl.android.lib.config.WLConfig;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by JustBlue on 2019-08-24.
 *
 * @email: bo.li@cdxzhi.com
 * @desc: okHttp辅助创建类
 */
public class OKHttpHelper {

    public static final long UPLOAD_TIMEOUT = 160_000L;  // 文件上传超时时间
    public static final long DEFAULT_TIMEOUT = 16_000L; // 默认连接超时时间

    /**
     * 创建默认的{@link okhttp3.OkHttpClient.Builder}构造器
     *
     * @return
     */
    private static OkHttpClient.Builder getDefaultBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            X509TrustManager trustAllManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

            };

            sc.init(null, new TrustManager[]{trustAllManager}, new SecureRandom());
            builder.sslSocketFactory(sc.getSocketFactory(), trustAllManager);

            builder.hostnameVerifier((hostname, session) -> true);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return builder
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .callTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(getLoggerInterceptor())
                .retryOnConnectionFailure(true); // 开启失败后重试
    }

    /**
     * 获取接口日志拦截器
     *
     * @return
     */
    private static Interceptor getLoggerInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

        if (WLConfig.isDebug()) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        return interceptor;
    }

    /**
     * 获取默认{@link OkHttpClient}
     * 注: 用于普通非上传资源文件的接口
     *
     * @return
     */
    public static OkHttpClient getDefault() {
        return getDefaultBuilder()
                .addInterceptor(new WLDataInterceptor())
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * 获取文件上传的{@link OkHttpClient}
     * 注: 用于上传、下载文件, 超时时间会更长
     *
     * @return
     */
    public static OkHttpClient getResource() {
        return getDefaultBuilder()
                .addInterceptor(new WLDataInterceptor())
                .connectTimeout(UPLOAD_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(UPLOAD_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(UPLOAD_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
    }

}
