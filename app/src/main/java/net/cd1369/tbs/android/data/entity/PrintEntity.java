package net.cd1369.tbs.android.data.entity;

import java.util.List;
import java.util.Objects;

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 14:43 2021/12/17
 * @desc
 */
public class PrintEntity {

    private String id;
    private String cover;
    private Integer type;
    private String background;
    private String subjectName;
    private String introduction;
    private List<PrintItemEntity> smallSubjectIntroduction;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getIntroduction() {
        return introduction;
    }

    public List<PrintItemEntity> getSmallSubjectIntroduction() {
        return smallSubjectIntroduction;
    }

    public void setSmallSubjectIntroduction(List<PrintItemEntity> smallSubjectIntroduction) {
        this.smallSubjectIntroduction = smallSubjectIntroduction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrintEntity that = (PrintEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PrintEntity{" +
                "id='" + id + '\'' +
                ", cover='" + cover + '\'' +
                ", type=" + type +
                ", background='" + background + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", smallSubjectIntroduction=" + smallSubjectIntroduction +
                '}';
    }
}
