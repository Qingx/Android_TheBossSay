package net.cd1369.tbs.android.data.model;

import net.cd1369.tbs.android.data.entity.ArticleEntity;
import net.cd1369.tbs.android.data.entity.BossInfoEntity;

import java.util.ArrayList;
import java.util.List;

import cn.wl.android.lib.core.PageParam;

public class FollowVal {

    private final PageParam param;
    private final List<ArticleEntity> data;
    private final List<BossInfoEntity> boss;

    public FollowVal(PageParam param, List<BossInfoEntity> boss, List<ArticleEntity> data) {
        this.param = param;
        this.data = data == null ? new ArrayList<>(0) : data;
        this.boss = boss == null ? new ArrayList<>(0) : boss;
    }

    public PageParam getParam() {
        return param;
    }

    public List<ArticleEntity> getData() {
        return data;
    }

    public List<BossInfoEntity> getBoss() {
        return boss;
    }

}
