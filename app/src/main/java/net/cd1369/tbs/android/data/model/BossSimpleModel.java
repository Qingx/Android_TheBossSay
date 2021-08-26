package net.cd1369.tbs.android.data.model;

import net.cd1369.tbs.android.util.StringConvert;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.List;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Xiang on 2021/8/26 10:08
 *
 * @description
 * @email Cymbidium@outlook.com
 */
@Entity
public class BossSimpleModel {
    @Id
    private Long id;
    @Property(nameInDb = "NAME")
    private String name; //boss名
    @Property(nameInDb = "HEAD")
    private String head; //boss头像
    @Property(nameInDb = "ROLE")
    private String role; //boss角色, 职务
    @Property(nameInDb = "TOP")
    private boolean top; //是否置顶
    @Property(nameInDb = "UPDATETIME")
    private Long updateTime; //上次更新时间
    @Convert(columnType = String.class, converter = StringConvert.class)
    private List<String> labels; //标签
    @Convert(columnType = String.class, converter = StringConvert.class)
    private List<String> photoUrl; //标签图片
    @Generated(hash = 405424545)
    public BossSimpleModel(Long id, String name, String head, String role,
            boolean top, Long updateTime, List<String> labels,
            List<String> photoUrl) {
        this.id = id;
        this.name = name;
        this.head = head;
        this.role = role;
        this.top = top;
        this.updateTime = updateTime;
        this.labels = labels;
        this.photoUrl = photoUrl;
    }
    @Generated(hash = 394938853)
    public BossSimpleModel() {
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
    public String getHead() {
        return this.head;
    }
    public void setHead(String head) {
        this.head = head;
    }
    public String getRole() {
        return this.role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public boolean getTop() {
        return this.top;
    }
    public void setTop(boolean top) {
        this.top = top;
    }
    public Long getUpdateTime() {
        return this.updateTime;
    }
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
    public List<String> getLabels() {
        return this.labels;
    }
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
    public List<String> getPhotoUrl() {
        return this.photoUrl;
    }
    public void setPhotoUrl(List<String> photoUrl) {
        this.photoUrl = photoUrl;
    }
}