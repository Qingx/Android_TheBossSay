package cn.wl.android.lib.data.repository;

import java.util.concurrent.atomic.AtomicInteger;

import cn.wl.android.lib.data.core.HttpConfig;
import cn.wl.android.lib.miss.TempLoginMiss;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

public final class BaseApi {

    public static RetryProvider mProvider;

    public static <T> ObservableTransformer<T, T> retryTemp() {
        return upstream -> upstream.retryWhen(throwableObservable -> {
            AtomicInteger retryCount = new AtomicInteger(0);

            return throwableObservable.flatMap(throwable -> {
                if (retryCount.incrementAndGet() <= 2 && throwable instanceof TempLoginMiss) {
                    return mProvider.retryToken()
                            .doOnNext(token -> {
                                HttpConfig.saveToken(token);
                            });
                }

                return Observable.error(throwable);
            });
        });
    }

    public interface RetryProvider {

        Observable<String> retryToken();
    }

}
