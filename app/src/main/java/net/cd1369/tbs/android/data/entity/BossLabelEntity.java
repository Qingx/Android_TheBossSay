package net.cd1369.tbs.android.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Xiang on 2021/7/22 13:04
 *
 * @description
 * @email Cymbidium@outlook.com
 */
@Entity
public class BossLabelEntity implements Serializable {
    private static final long serialVersionUID = 456L;
    @Id
    private String id;
    @Property(nameInDb = "name")
    private String name;
    @Property(nameInDb = "keyValue")
    private String keyValue;
    @Property(nameInDb = "createTime")
    private Long createTime;
    @Property(nameInDb = "parentId")
    private String parentId;
    @Property(nameInDb = "sort")
    private int sort;
    @Property(nameInDb = "type")
    private int type;

    public static BossLabelEntity empty = new BossLabelEntity("-1", "", "", Long.getLong("0"), "", 0, 0);

    @Generated(hash = 1667490424)
    public BossLabelEntity(String id, String name, String keyValue, Long createTime,
                           String parentId, int sort, int type) {
        this.id = id;
        this.name = name;
        this.keyValue = keyValue;
        this.createTime = createTime;
        this.parentId = parentId;
        this.sort = sort;
        this.type = type;
    }

    @Generated(hash = 1489330764)
    public BossLabelEntity() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyValue() {
        return this.keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public Long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getParentId() {
        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getSort() {
        return this.sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BossLabelEntity that = (BossLabelEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BossLabelEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", keyValue='" + keyValue + '\'' +
                ", createTime=" + createTime +
                ", parentId='" + parentId + '\'' +
                ", sort=" + sort +
                ", type=" + type +
                '}';
    }
}
