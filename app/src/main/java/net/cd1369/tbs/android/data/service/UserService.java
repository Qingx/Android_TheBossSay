package net.cd1369.tbs.android.data.service;

import net.cd1369.tbs.android.data.entity.DailyEntity;
import net.cd1369.tbs.android.data.entity.FavoriteEntity;
import net.cd1369.tbs.android.data.entity.FolderEntity;
import net.cd1369.tbs.android.data.entity.HisFavEntity;
import net.cd1369.tbs.android.data.entity.HistoryEntity;
import net.cd1369.tbs.android.data.entity.PortEntity;
import net.cd1369.tbs.android.data.entity.TokenEntity;
import net.cd1369.tbs.android.data.entity.VersionEntity;

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
     * 刷新用户信息
     *
     * @return
     */
    @GET("/api/account/tokenRefresh/{id}")
    Observable<WLData<TokenEntity>> obtainRefresh(@Path("id") String id);

    /**
     * 发送验证码
     *
     * @param body
     * @return
     */
    @POST("/api/account/send-code")
    Observable<WLData<String>> obtainSendCode(@Body RequestBody body);

    /**
     * 验证码登录
     *
     * @param body
     * @return
     */
    @POST("/api/account/sign-code")
    Observable<WLData<TokenEntity>> obtainSignPhone(@Body RequestBody body);

    /**
     * 微信授权登录
     *
     * @param body
     * @return
     */
    @POST("/api/account/wechat/sign")
    Observable<WLData<TokenEntity>> obtainSignWechat(@Body RequestBody body);

    /**
     * 微信绑定
     *
     * @param body
     * @return
     */
    @POST("/api/account/wechat/bound")
    Observable<WLData<TokenEntity>> obtainBindWechat(@Body RequestBody body);

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
     * 绑定手机号
     *
     * @param body
     * @return
     */
    @POST("/api/account/check-bound")
    Observable<WLData<Object>> obtainBindPhone(@Body RequestBody body);

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

    /**
     * 阅读文章
     *
     * @param id
     * @return
     */
    @GET("/api/article/read/{id}")
    Observable<WLData<Object>> obtainReadArticle(@Path("id") String id);

    /**
     * 检查版本
     *
     * @param version
     * @return
     */
    @GET("/api/version/check/android/{versionNumber}")
    Observable<WLData<VersionEntity>> obtainCheckUpdate(@Path("versionNumber") String version);

    /**
     * 检查版本
     *
     * @return
     */
    @GET("/web/port/find")
    Observable<WLData<PortEntity>> obtainPortStatus();

    /**
     * 获取每日一言
     *
     * @return
     */
    @GET("/api/speech/get-speech")
    Observable<WLData<DailyEntity>> obtainDaily();

    /**
     * 操作每日一言
     *
     * @param body
     * @return
     */
    @POST("/api/speech/options")
    Observable<WLData<Object>> obtainDailyOption(@Body RequestBody body);

    /**
     * 获取点赞记录列表 分页
     *
     * @param body
     * @return
     */
    @POST("/api/speech/point-history")
    Observable<WLPage<HisFavEntity>> obtainPointList(@Body RequestBody body);

    /**
     * 获取收藏夹列表
     *
     * @return
     */
    @GET("/api/collect/get-collet")
    Observable<WLList<FolderEntity>> obtainFolderList();

    /**
     * 获取收藏夹
     *
     * @param id
     * @return
     */
    @GET("/api/collect/get-obj/{groupId}")
    Observable<WLData<FolderEntity>> obtainGetFolder(@Path("groupId") String id);

    /**
     * 获取收藏夹内文章
     *
     * @param body
     * @return
     */
    @POST("/api/collect/list-collet")
    Observable<WLPage<HisFavEntity>> obtainFolderArticle(@Body RequestBody body);

    /**
     * 极光认证一键登录
     *
     * @param body
     * @return
     */
    @POST("/api/account/sign-token")
    Observable<WLData<TokenEntity>> obtainJverifyLogin(@Body RequestBody body);

    /**
     * 移除用户
     *
     * @param userId
     * @return
     */
    @GET("api/user/remove/{id}")
    Observable<WLData<Object>> logoff(@Path("id") String userId);

}
