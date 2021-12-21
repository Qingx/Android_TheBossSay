package net.cd1369.tbs.android.data.entity;

import java.util.List;

import cn.wl.android.lib.core.Page;

public class PrintSubModel {

    private int articleNum;
    private String background;
    private String introduction;
    private String subjectName;
    private List<BossInfoEntity> bosses;
    private Page<PrintSubEntity> articleDetailsVOPage;

    public int getArticleNum() {
        return articleNum;
    }

    public String getBackground() {
        return background;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public List<BossInfoEntity> getBosses() {
        return bosses;
    }

    public Page<PrintSubEntity> getArticleDetailsVOPage() {
        return articleDetailsVOPage;
    }

    @Override
    public String toString() {
        return "PrintSubModel{" +
                "articleNum=" + articleNum +
                ", background='" + background + '\'' +
                ", introduction='" + introduction + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", bosses=" + bosses +
                ", articleDetailsVOPage=" + articleDetailsVOPage +
                '}';
    }
}
