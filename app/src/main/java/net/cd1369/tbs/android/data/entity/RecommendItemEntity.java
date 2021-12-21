package net.cd1369.tbs.android.data.entity;

import java.util.List;
import java.util.Objects;

public class RecommendItemEntity {

    private String id;
    private String background;
    private String bossName;
    private String cover;
    private Integer createTime;
    private String head;
    private String introduction;
    private String parentId;
    private String subjectName;
    private Integer type;
    private Integer updateTime;
    private ArticlePrintEntity article;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getBossName() {
        return bossName;
    }

    public void setBossName(String bossName) {
        this.bossName = bossName;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Integer updateTime) {
        this.updateTime = updateTime;
    }

    public ArticlePrintEntity getArticle() {
        return article;
    }

    public void setArticle(ArticlePrintEntity article) {
        this.article = article;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecommendItemEntity that = (RecommendItemEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RecommendItemEntity{" +
                "id='" + id + '\'' +
                ", background='" + background + '\'' +
                ", bossName='" + bossName + '\'' +
                ", cover='" + cover + '\'' +
                ", createTime=" + createTime +
                ", head='" + head + '\'' +
                ", introduction='" + introduction + '\'' +
                ", parentId='" + parentId + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", type=" + type +
                ", updateTime=" + updateTime +
                ", article=" + article +
                '}';
    }

}
