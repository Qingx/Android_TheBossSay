package net.cd1369.tbs.android.data.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import net.cd1369.tbs.android.util.StringConvert;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.List;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Xiang on 2021/8/26 10:25
 *
 * @description
 * @email Cymbidium@outlook.com
 */
@Entity
public class ArticleSimpleModel implements MultiItemEntity {
    @Id
    private Long id; //id
    @Property(nameInDb = "TITLE")
    private String title; //标题
    @Property(nameInDb = "DESCONTENT")
    private String descContent; //摘要
    @Property(nameInDb = "ISCOLLECT")
    private boolean isCollect; //是否收藏
    @Property(nameInDb = "ISREAD")
    private boolean isRead; //是否已读
    @Property(nameInDb = "READCOUNT")
    private int readCount; //阅读数
    @Property(nameInDb = "COLLECT")
    private int collect; //收藏数
    @Property(nameInDb = "RELEASETIME")
    private Long releaseTime; //发布时间
    @Property(nameInDb = "ARTICLETIME")
    private Long articleTime; //文章时间
    @Convert(columnType = String.class, converter = StringConvert.class)
    private List<String> files; //图片列表
    @Property(nameInDb = "BOSSID")
    private String bossId; //BOSSNAME
    @Property(nameInDb = "NAME")
    private String bossName; //boss名
    @Property(nameInDb = "BOSSHEAD")
    private String bossHead; //boss头像
    @Property(nameInDb = "BOSSROLE")
    private String bossRole; //boss角色, 职务
    @Property(nameInDb = "RECOMMENDTYPE")
    private String recommendType; //0:最近更新 1:为你推荐

    @Generated(hash = 487171374)
    public ArticleSimpleModel(Long id, String title, String descContent, boolean isCollect, boolean isRead, int readCount,
                              int collect, Long releaseTime, Long articleTime, List<String> files, String bossId, String bossName,
                              String bossHead, String bossRole, String recommendType) {
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
    }

    @Generated(hash = 601260873)
    public ArticleSimpleModel() {
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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
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
}