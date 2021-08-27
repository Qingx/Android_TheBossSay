package net.cd1369.tbs.android.data.model;

import net.cd1369.tbs.android.data.entity.BossLabelEntity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Xiang on 2021/8/25 15:11
 *
 * @description
 * @email Cymbidium@outlook.com
 */
@Entity
public class LabelModel {
    @Id
    private Long id;
    @Property(nameInDb = "NAME")
    private String name;

    @Generated(hash = 1441721565)
    public LabelModel(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Generated(hash = 826118447)
    public LabelModel() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static LabelModel empty = new LabelModel(-1L, "全部");

}
