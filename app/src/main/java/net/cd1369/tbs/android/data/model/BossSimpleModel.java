package net.cd1369.tbs.android.data.model;

import android.widget.ImageView;

import java.util.List;
import java.util.Objects;

import cn.wl.android.lib.utils.GlideApp;
import cn.wl.android.lib.utils.Lists;
import cn.wl.android.lib.utils.Times;
import cn.wl.android.lib.utils.ViewHelper;

/**
 * Created by Xiang on 2021/8/26 10:08
 *
 * @description
 * @email Cymbidium@outlook.com
 */
public class BossSimpleModel implements Comparable<BossSimpleModel> {
    private String id;
    private String name; //boss名
    private String head; //boss头像
    private String role; //boss角色, 职务
    private boolean top; //是否置顶
    private Long updateTime; //上次更新时间
    private List<String> labels; //标签
    private List<String> photoUrl; //标签图片

    public BossSimpleModel() {

    }

    public BossSimpleModel(String id, String name, String head, String role, boolean top, Long updateTime, List<String> labels, List<String> photoUrl) {
        this.id = id;
        this.name = name;
        this.head = head;
        this.role = role;
        this.top = top;
        this.updateTime = updateTime;
        this.labels = labels;
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<String> getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(List<String> photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BossSimpleModel model = (BossSimpleModel) o;
        return Objects.equals(id, model.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean checkSort(BossSimpleModel boss) {
        if (this.id.equals(boss.id))
            return false;

        if (boss.top) {
            if (!this.top) return true;
        } else {
            if (this.top) return false;
        }
        return this.updateTime < boss.updateTime;
    }

    public Long getSort() {
        if (this.top) {
            Long years = 20L * 365L * 24L * 60L * 60L * 1000L;
            return (this.updateTime + years);
        } else return this.updateTime;
    }

    public void showImage(ImageView iv1, ImageView iv2) {
        List<String> photoUrl = this.photoUrl;

        if (Lists.isEmpty(photoUrl)) {
            ViewHelper.setVisible(iv1, false);
            ViewHelper.setVisible(iv2, false);
        } else if (photoUrl.size() == 1) {
            ViewHelper.setVisible(iv1, true);
            ViewHelper.setVisible(iv2, false);

            GlideApp.display(photoUrl.get(0), iv1);
        } else if (photoUrl.size() > 1) {
            ViewHelper.setVisible(iv1, true);
            ViewHelper.setVisible(iv2, true);

            GlideApp.display(photoUrl.get(0), iv1);
            GlideApp.display(photoUrl.get(1), iv2);
        }
    }

    public boolean isLatest() {
        long diff = Times.current() - this.updateTime;
        long days = diff / (1000 * 60 * 60 * 24);
        return days <= 30L;
    }

    @Override
    public int compareTo(BossSimpleModel o) {
        return o.getSort().compareTo(this.getSort());
    }
}