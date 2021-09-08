package net.cd1369.tbs.android.data.entity;

import java.util.Objects;

//@lombok.NoArgsConstructor
//@lombok.Data
public class PointEntity {

    private String id;
    private String articleDesc;
    private String articleId;
    private String articleTitle;
    private String bossHead;
    private String bossId;
    private String bossName;
    private long createTime;
    private long updateTime;
    private String articleContent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArticleDesc() {
        return articleDesc;
    }

    public void setArticleDesc(String articleDesc) {
        this.articleDesc = articleDesc;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getBossHead() {
        return bossHead;
    }

    public void setBossHead(String bossHead) {
        this.bossHead = bossHead;
    }

    public String getBossId() {
        return bossId;
    }

    public void setBossId(String bossId) {
        this.bossId = bossId;
    }

    public String getBossName() {
        return bossName;
    }

    public void setBossName(String bossName) {
        this.bossName = bossName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointEntity that = (PointEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PointEntity{" +
                "id='" + id + '\'' +
                ", articleDesc='" + articleDesc + '\'' +
                ", articleId='" + articleId + '\'' +
                ", articleTitle='" + articleTitle + '\'' +
                ", bossHead='" + bossHead + '\'' +
                ", bossId='" + bossId + '\'' +
                ", bossName='" + bossName + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", articleContent='" + articleContent + '\'' +
                '}';
    }

}
