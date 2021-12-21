package net.cd1369.tbs.android.data.entity;

import java.util.List;
import java.util.Objects;

public class RecommendEntity {

    private String id;
    private Integer type;
    private String cover;
    private String background;
    private String subjectName;
    private List<RecommendItemEntity> subjectRecommend;

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<RecommendItemEntity> getSubjectRecommend() {
        return subjectRecommend;
    }

    public void setSubjectRecommend(List<RecommendItemEntity> subjectRecommend) {
        this.subjectRecommend = subjectRecommend;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecommendEntity that = (RecommendEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RecommendEntity{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", cover='" + cover + '\'' +
                ", background='" + background + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", subjectRecommend=" + subjectRecommend +
                '}';
    }

}
