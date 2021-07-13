package net.cd1369.tbs.android.data.repository;


import net.cd1369.tbs.android.data.entity.ArticleEntity;
import net.cd1369.tbs.android.data.entity.BossInfoEntity;
import net.cd1369.tbs.android.data.entity.BossLabelEntity;
import net.cd1369.tbs.android.data.service.BossService;

import java.util.ArrayList;
import java.util.List;

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
    public Observable<List<BossLabelEntity>> obtainBossLabels() {
        return getService().obtainBossLabels()
                .compose(combine())
                .compose(rebase());
    }

    /**
     * 获取已追踪的boss列表 已追踪且有更新的boss列表
     *
     * @param labels
     * @param mustUpdate
     * @return
     */
    public Observable<List<BossInfoEntity>> obtainFollowBossList(String labels, boolean mustUpdate) {
        RequestBody body = bodyFromCreator(jo -> {
            jo.put("mustUpdate", mustUpdate);

            if (!labels.equals("-1")) {
                List<String> list = new ArrayList<>();
                list.add(labels);
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
    public Observable<Page<ArticleEntity>> obtainFollowArticle(PageParam pageParam) {
        RequestBody body = bodyFromCreator(pageParam, jo -> {

        });

        return getService().obtainFollowArticle(body)
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
}
