package net.cd1369.tbs.android.data.repository;


import net.cd1369.tbs.android.BuildConfig;
import net.cd1369.tbs.android.data.entity.ArticleEntity;
import net.cd1369.tbs.android.data.entity.BannerEntity;
import net.cd1369.tbs.android.data.entity.BossInfoEntity;
import net.cd1369.tbs.android.data.entity.OptPicEntity;
import net.cd1369.tbs.android.data.model.ArticleSimpleModel;
import net.cd1369.tbs.android.data.model.BossSimpleModel;
import net.cd1369.tbs.android.data.model.LabelModel;
import net.cd1369.tbs.android.data.service.BossService;

import java.util.ArrayList;
import java.util.List;

import cn.wl.android.lib.config.BaseConfig;
import cn.wl.android.lib.core.Page;
import cn.wl.android.lib.core.PageParam;
import cn.wl.android.lib.data.repository.BaseRepository;
import io.reactivex.Observable;
import okhttp3.RequestBody;

/**
 * Created by Xiang on 2021/6/22 15:21
 *
 * @description
 * @email Cymbidium@outlook.com
 */
public class BossRepository extends BaseRepository<BossService> {
    public BossRepository() {
    }

    /**
     * 引导页追踪多个boss
     *
     * @param ids
     * @return
     */
    public Observable<Boolean> obtainGuideFollow(List<String> ids) {
        RequestBody body = bodyFromCreator(jo -> {
            jo.put("sourceIds", ids);
            jo.put("type", 1);
            jo.put("target", true);
            jo.put("forced", true);
        });

        return getService().obtainGuideFollow(body).compose(combine())
                .compose(success());
    }

    /**
     * 获取boss标签列表
     *
     * @return
     */
    public Observable<List<LabelModel>> obtainBossLabels() {
        return getService().obtainBossLabels()
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 获取已追踪的boss列表 已追踪且有更新的boss列表
     *
     * @param label
     * @param mustUpdate
     * @return
     */
    public Observable<List<BossSimpleModel>> obtainFollowBossList(Long label, boolean mustUpdate) {
        RequestBody body = bodyFromCreator(jo -> {
            jo.put("mustUpdate", mustUpdate);

            if (!label.equals(-1L)) {
                List<Long> list = new ArrayList<>();
                list.add(label);
                jo.put("labels", list);
            }
        });

        return getService().obtainFollowBossList(body)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 获取追踪 最近更新文章
     *
     * @param pageParam
     * @return
     */
    @Deprecated
    public Observable<Page<ArticleEntity>> obtainFollowArticle(String label, PageParam pageParam) {
        RequestBody body = bodyFromCreator(pageParam, jo -> {
            if (!label.equals("-1")) {
                List<String> list = new ArrayList<>();
                list.add(label);
                jo.put("labels", list);
            }
        });

        return getService().obtainFollowArticle(body)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 获取追踪 最近更新文章
     *
     * @param pageParam
     * @return
     */
    public Observable<Page<ArticleSimpleModel>> obtainTackArticle(Long label, PageParam pageParam) {
        RequestBody body = bodyFromCreator(pageParam, jo -> {
            if (!label.toString().equals("-1")) {
                List<String> list = new ArrayList<>();
                list.add(label.toString());
                jo.put("labels", list);
            }
        });

        return getService().obtainTackArticle(body)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 广场 所有文章
     *
     * @param pageParam
     * @param label
     * @return
     */
    public Observable<Page<ArticleEntity>> obtainAllArticle(PageParam pageParam, String label) {
        RequestBody body = bodyFromCreator(pageParam, jo -> {
            if (!label.equals("-1")) {
                List<String> list = new ArrayList<>();
                list.add(label);
                jo.put("labels", list);
            }
        });

        return getService().obtainAllArticle(body)
                .compose(combine())
                .compose(rebase());
    }


    /**
     * 获取全部boss列表
     *
     * @return
     */
    public Observable<List<BossInfoEntity>> obtainAllBossList() {
        return getService().obtainAllBossList()
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 获取全部boss列表
     *
     * @return
     */
    public Observable<List<BossInfoEntity>> obtainAllBossSearchList(String name) {
        String url = BuildConfig.BASE_URL + "api/boss/noPageList/?name=" + name;
        return getService().obtainAllBossSearchList(url)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 搜索boss
     *
     * @param pageParam
     * @param searchText
     * @return
     */
    public Observable<Page<BossInfoEntity>> obtainSearchBossList(PageParam pageParam, String searchText) {
        RequestBody body = bodyFromCreator(pageParam, jo -> {
            if (searchText != null && !searchText.equals("")) {
                jo.put("name", searchText);
            }
        });

        return getService().obtainSearchBossList(body)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 搜索文章
     *
     * @param pageParam
     * @param searchText
     * @return
     */
    public Observable<Page<ArticleEntity>> obtainSearchArticle(PageParam pageParam, String searchText) {
        RequestBody body = bodyFromCreator(pageParam, jo -> {
            if (searchText != null && !searchText.equals("")) {
                jo.put("name", searchText);
            }
        });

        return getService().obtainAllArticle(body)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 获取boss的文章列表
     *
     * @param pageParam
     * @param bossId
     * @return
     */
    public Observable<Page<ArticleEntity>> obtainBossArticleList(PageParam pageParam, String bossId) {
        RequestBody body = bodyFromCreator(pageParam, jo -> {
            jo.put("bossId", bossId);
        });

        return getService().obtainAllArticle(body)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 获取boss详情
     *
     * @param id
     * @return
     */
    public Observable<BossInfoEntity> obtainBossDetail(String id) {
        return getService().obtainBossDetail(id)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 追踪boss
     *
     * @param id
     * @return
     */
    public Observable<Boolean> obtainFollowBoss(String id) {
        RequestBody body = bodyFromCreator(jo -> {
            jo.put("sourceId", id);
            jo.put("type", 1);
            jo.put("target", true);
        });

        return getService().obtainGuideFollow(body)
                .compose(combine())
                .compose(success());
    }

    /**
     * 取消追踪boss
     *
     * @param id
     * @return
     */
    public Observable<Boolean> obtainCancelFollowBoss(String id) {
        RequestBody body = bodyFromCreator(jo -> {
            jo.put("sourceId", id);
            jo.put("type", 1);
            jo.put("target", false);
        });

        return getService().obtainGuideFollow(body)
                .compose(combine())
                .compose(success());
    }

    /**
     * 获取文章详情
     *
     * @param id
     * @return
     */
    public Observable<ArticleEntity> obtainDetailArticle(String id) {
        return getService().obtainDetailArticle(id)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 获取全部boss
     *
     * @return
     */
    public Observable<List<BossSimpleModel>> obtainGuideBoss() {
        return getService().obtainGuideBoss()
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 获取全部boss
     *
     * @param time
     * @return
     */
    public Observable<List<BossInfoEntity>> obtainAllBoss(Long time) {
        return getService().obtainAllBoss(time)
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 获取运营图
     *
     * @return
     */
    public Observable<OptPicEntity> obtainOptPic() {
        return getService().obtainOptList()
                .compose(combine())
                .compose(rebase())
                .onErrorReturn(t -> OptPicEntity.EMPTY);
    }

    /**
     * 置顶boss
     *
     * @param bossId
     * @return
     */
    public Observable<Boolean> obtainTopicBoss(String bossId, boolean target) {
        RequestBody requestBody = bodyFromCreator(map -> {
            map.put("bossId", bossId);
            map.put("top", target);
        });
        return getService().obtainTopicBoss(requestBody)
                .compose(combine())
                .compose(success());
    }

    /**
     * 获取banner
     *
     * @return
     */
    public Observable<List<BannerEntity>> obtainBanner() {
        return getService().obtainBanner()
                .compose(combine())
                .compose(rebase());
    }
}
