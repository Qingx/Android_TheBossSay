package net.cd1369.tbs.android.data.entity;

import java.util.List;

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 14:45 2021/12/17
 * @desc
 */
public class PrintItemEntity {

    private String cover;
    private Integer bossNum;
    private String background;
    private Integer articleNum;
    private String introduction;
    private String smallSubjectId;
    private String subjectName;
    private List<ArticlePrintEntity> articleVOs;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Integer getBossNum() {
        return bossNum;
    }

    public void setBossNum(Integer bossNum) {
        this.bossNum = bossNum;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public Integer getArticleNum() {
        return articleNum;
    }

    public void setArticleNum(Integer articleNum) {
        this.articleNum = articleNum;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getSmallSubjectId() {
        return smallSubjectId;
    }

    public void setSmallSubjectId(String smallSubjectId) {
        this.smallSubjectId = smallSubjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public List<ArticlePrintEntity> getArticleVOs() {
        return articleVOs;
    }

    public void setArticleVOs(List<ArticlePrintEntity> articleVOs) {
        this.articleVOs = articleVOs;
    }

    @Override
    public String toString() {
        return "PrintItemEntity{" +
                "cover='" + cover + '\'' +
                ", bossNum=" + bossNum +
                ", background='" + background + '\'' +
                ", articleNum=" + articleNum +
                ", introduction='" + introduction + '\'' +
                ", smallSubjectId='" + smallSubjectId + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", articleVOs=" + articleVOs +
                '}';
    }

}
