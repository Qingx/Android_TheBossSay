package net.cd1369.tbs.android.data.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import net.cd1369.tbs.android.util.Tools;
import net.cd1369.tbs.android.util.ToolsKt;

import java.util.List;

/**
 * Created by Xiang on 2021/8/26 10:25
 *
 * @description
 * @email Cymbidium@outlook.com
 */
public class ArticleSimpleModel implements MultiItemEntity {
    private String id; //id
    private String title; //标题
    private String descContent; //摘要
    private boolean isCollect; //是否收藏
    private boolean isRead; //是否已读
    private int readCount; //阅读数
    private int collect; //收藏数
    private Long releaseTime; //发布时间
    private Long articleTime; //文章时间
    private List<String> files; //图片列表
    private String bossId; //BOSSNAME
    private String bossName; //boss名
    private String bossHead; //boss头像
    private String bossRole; //boss角色, 职务
    private String recommendType; //0:最近更新 1:为你推荐
    private String filterType; //1:言论 2：咨询

    public ArticleSimpleModel(String id, String title, String descContent, boolean isCollect, boolean isRead, int readCount,
                              int collect, Long releaseTime, Long articleTime, List<String> files, String bossId, String bossName,
                              String bossHead, String bossRole, String recommendType, String filterType) {
        this.id = id;
        this.title = title;
        this.descContent = descContent;
        this.isCollect = isCollect;
        this.isRead = isRead;
        this.readCount = readCount;
        this.collect = collect;
        this.releaseTime = releaseTime;
        this.articleTime = articleTime;
        this.files = files;
        this.bossId = bossId;
        this.bossName = bossName;
        this.bossHead = bossHead;
        this.bossRole = bossRole;
        this.recommendType = recommendType;
        this.filterType = filterType;
    }

    public ArticleSimpleModel() {
    }

    public long getValidTime() {
        Long time = this.releaseTime;
        if (time == null)
            time = this.articleTime;
        if (time == null)
            time = 0L;

        return time;
    }

    public boolean isLatestUpdate() {
        boolean oneDayTime = ToolsKt.inThreeDayTime(getValidTime());
        return !isRead && oneDayTime;
    }

    public boolean isMsg() {
        return "1".equals(filterType);
    }

    public boolean isTalk() {
        return "2".equals(filterType);
    }

    public Long getShowTime() {
        if (this.articleTime == null) {
            return this.releaseTime;
        } else {
            return this.articleTime;
        }
    }

    @Override
    public int getItemType() {
        if (this.files == null || this.files.size() <= 0) {
            return 0;
        } else if (this.files.size() <= 1) {
            return 1;
        } else {
            return 2;
        }
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescContent() {
        return this.descContent;
    }

    public void setDescContent(String descContent) {
        this.descContent = descContent;
    }

    public boolean getIsCollect() {
        return this.isCollect;
    }

    public void setIsCollect(boolean isCollect) {
        this.isCollect = isCollect;
    }

    public boolean getIsRead() {
        return this.isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public int getReadCount() {
        return this.readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public int getCollect() {
        return this.collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public Long getReleaseTime() {
        return this.releaseTime;
    }

    public void setReleaseTime(Long releaseTime) {
        this.releaseTime = releaseTime;
    }

    public Long getArticleTime() {
        return this.articleTime;
    }

    public void setArticleTime(Long articleTime) {
        this.articleTime = articleTime;
    }

    public List<String> getFiles() {
        return this.files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public String getBossId() {
        return this.bossId;
    }

    public void setBossId(String bossId) {
        this.bossId = bossId;
    }

    public String getBossName() {
        return this.bossName;
    }

    public void setBossName(String bossName) {
        this.bossName = bossName;
    }

    public String getBossHead() {
        return this.bossHead;
    }

    public void setBossHead(String bossHead) {
        this.bossHead = bossHead;
    }

    public String getBossRole() {
        return this.bossRole;
    }

    public void setBossRole(String bossRole) {
        this.bossRole = bossRole;
    }

    public String getRecommendType() {
        return this.recommendType;
    }

    public void setRecommendType(String recommendType) {
        this.recommendType = recommendType;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }
}