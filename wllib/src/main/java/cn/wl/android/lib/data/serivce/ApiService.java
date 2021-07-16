package cn.wl.android.lib.data.serivce;

import cn.wl.android.lib.core.WLData;
import cn.wl.android.lib.data.core.TokenModel;
import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ApiService {

    @GET("/api/account/refresh")
    Observable<WLData<TokenModel>> obtainRefresh();

}
