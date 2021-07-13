package net.cd1369.tbs.android.data.service;

import net.cd1369.tbs.android.data.entity.TokenEntity;

import cn.wl.android.lib.core.WLData;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Xiang on 2021/4/25 15:20
 *
 * @description
 * @email Cymbidium@outlook.com
 */
public interface UserService {

    /**
     * 游客登录
     *
     * @param body
     * @return
     */
    @POST("/api/account/sign-device")
    Observable<WLData<TokenEntity>> obtainTempLogin(@Body RequestBody body);

    /**
     * 刷新用户信息
     *
     * @return
     */
    @GET("/api/account/refresh")
    Observable<WLData<TokenEntity>> obtainRefresh();

    /**
     * 发送验证码
     *
     * @param body
     * @return
     */
    @POST("/api/account/send-code")
    Observable<WLData<Object>> obtainSendCode(@Body RequestBody body);

    /**
     * 验证码登录
     *
     * @param body
     * @return
     */
    @POST("/api/account/sign-code")
    Observable<WLData<TokenEntity>> obtainSignPhone(@Body RequestBody body);

    /**
     * 修改账号昵称
     *
     * @param body
     * @return
     */
    @POST("/api/account/update-user")
    Observable<WLData<Object>> obtainChangeName(@Body RequestBody body);

    /**
     * 验证当前手机号
     *
     * @param body
     * @return
     */
    @POST("/api/account/check-current")
    Observable<WLData<Object>> obtainConfirmPhone(@Body RequestBody body);

    /**
     * 修改手机号
     *
     * @param body
     * @return
     */
    @POST("/api/account/check-change")
    Observable<WLData<Object>> obtainChangePhone(@Body RequestBody body);
}
