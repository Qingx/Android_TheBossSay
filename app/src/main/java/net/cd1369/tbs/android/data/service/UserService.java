package net.cd1369.tbs.android.data.service;

import net.cd1369.tbs.android.data.entity.ArticleEntity;
import net.cd1369.tbs.android.data.entity.FavoriteEntity;
import net.cd1369.tbs.android.data.entity.HistoryEntity;
import net.cd1369.tbs.android.data.entity.TokenEntity;

import cn.wl.android.lib.core.WLData;
import cn.wl.android.lib.core.WLList;
import cn.wl.android.lib.core.WLPage;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

    /**
     * 获取用户收藏夹列表
     *
     * @return
     */
    @GET("/api/collect/list")
    Observable<WLList<FavoriteEntity>> obtainFavoriteList();

    /**
     * 创建收藏夹
     *
     * @param body
     * @return
     */
    @POST("/api/collect/commit")
    Observable<WLData<FavoriteEntity>> obtainCreateFavorite(@Body RequestBody body);

    /**
     * 删除收藏夹
     *
     * @param body
     * @return
     */
    @POST("/api/collect/delete")
    Observable<WLData<Object>> obtainRemoveFolder(@Body RequestBody body);

    /**
     * 操作文章
     *
     * @param body
     * @return
     */
    @POST("/api/article/options")
    Observable<WLData<Object>> obtainOptionArticle(@Body RequestBody body);


    /**
     * 阅读记录
     *
     * @param body
     * @return
     */
    @POST("/api/article/read-history")
    Observable<WLPage<HistoryEntity>> obtainHistory(@Body RequestBody body);

    /**
     * 删除阅读记录
     *
     * @param id
     * @return
     */
    @GET("/api/article/del-read/{id}")
    Observable<WLData<Object>> obtainRemoveHistory(@Path("id") String id);
}
