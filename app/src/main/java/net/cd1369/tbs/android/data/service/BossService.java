package net.cd1369.tbs.android.data.service;

import net.cd1369.tbs.android.data.entity.ArticleEntity;
import net.cd1369.tbs.android.data.entity.BossInfoEntity;
import net.cd1369.tbs.android.data.entity.BossLabelEntity;

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
public interface BossService {
    /**
     * 引导页追踪多个boss
     *
     * @param body
     * @return
     */
    @POST("/api/boss/options")
    Observable<WLData<Object>> obtainGuideFollow(@Body RequestBody body);

    /**
     * 获取boss标签列表
     *
     * @return
     */
    @GET("/api/boss/labels")
    Observable<WLList<BossLabelEntity>> obtainBossLabels();

    /**
     * 获取已追踪的boss列表 已追踪且有更新的boss列表
     *
     * @param body
     * @return
     */
    @POST("/api/boss/collected")
    Observable<WLList<BossInfoEntity>> obtainFollowBossList(@Body RequestBody body);

    /**
     * 获取追踪 最近更新文章
     *
     * @param body
     * @return
     */
    @POST("/api/article/recommend")
    Observable<WLPage<ArticleEntity>> obtainFollowArticle(@Body RequestBody body);

    /**
     * 广场 所有文章
     *
     * @param body
     * @return
     */
    @POST("/api/article/list")
    Observable<WLPage<ArticleEntity>> obtainAllArticle(@Body RequestBody body);

    /**
     * 获取所有boss列表 分页
     *
     * @param body
     * @return
     */
    @POST("/api/boss/list")
    Observable<WLPage<BossInfoEntity>> obtainAllBossList(@Body RequestBody body);

    /**
     * 获取boss详情
     *
     * @param id
     * @return
     */
    @GET("/api/boss/details/{id}")
    Observable<WLData<BossInfoEntity>> obtainBossDetail(@Path("id") String id);

    /**
     * 获取文章详情
     *
     * @param id
     * @return
     */
    @GET("/api/article/details/{id}")
    Observable<WLData<ArticleEntity>> obtainDetailArticle(@Path("id") String id);

    /**
     * 获取全部boss
     *
     * @param time
     * @return
     */
    @GET("/api/boss/all-list/{time}")
    Observable<WLList<BossInfoEntity>> obtainAllBoss(@Path("time") Long time);
}
