package net.cd1369.tbs.android.data.entity;

import java.util.List;

public class PrintSubEntity {

    private int label;
    private String head;
    private String bossName;
    private ArticlePrintEntity article;

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public Integer getLabel() {
        return label;
    }

    public void setLabel(Integer label) {
        this.label = label;
    }

    public String getBossName() {
        return bossName;
    }

    public void setBossName(String bossName) {
        this.bossName = bossName;
    }

    public ArticlePrintEntity getArticle() {
        return article;
    }

    public void setArticle(ArticlePrintEntity article) {
        this.article = article;
    }

}
