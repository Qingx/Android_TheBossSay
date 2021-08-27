package net.cd1369.tbs.android.data.entity;

import android.text.TextUtils;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import cn.wl.android.lib.utils.GlideApp;
import cn.wl.android.lib.utils.Lists;
import cn.wl.android.lib.utils.ViewHelper;

/**
 * Created by Xiang on 2021/7/22 14:31
 *
 * @description
 * @email Cymbidium@outlook.com
 */
public class BossInfoEntity implements Serializable {
    private static final long serialVersionUID = 123L;
    private String id;
    private String name; //boss名
    private String head; //boss头像
    private String role; //boss角色, 职务
    private String info; //boss描述
    private long date; //生日
    private boolean isCollect; //是否追踪
    private boolean isPoint;//是否点赞
    private boolean deleted; //是否被删除
    private boolean guide; //是否被推荐
    private int point; //点赞数
    private int collect; //收藏数
    private int updateCount; //更新数量
    private int totalCount; //发布文章总数
    private int readCount; //阅读数
    private Long updateTime; //上次更新时间
    private Long createTime; //创建时间

    private List<String> photoUrl;
    private List<String> labels;
    private boolean top; //是否置顶
    private long relTime; //置顶时间

    private String bossType; //boss类型:新boss:newBoss,热门boss:hotBoss,没有标签:without

    public boolean checkLabels(String label) {
        if (Lists.isEmpty(labels)) return false;
        if (TextUtils.isEmpty(label)) return false;

        for (String s : labels) {
            if (label.equals(s)) {
                return true;
            }
        }

        return false;
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

    public boolean checkSort(BossInfoEntity boss) {
        if (id.equals(boss.id))
            return false;

        if (boss.top) {
            if (!top) return true;
        } else {
            if (top) return false;
        }
        return updateTime < boss.updateTime;
    }

    static BossInfoEntity empty = new BossInfoEntity();

    public BossInfoEntity(String id, String name, String head, String role, String info, long date,
                          boolean isCollect, boolean isPoint, boolean deleted, boolean guide, int point, int collect,
                          int updateCount, int totalCount, int readCount, Long updateTime, Long createTime, String bossType) {
        this.id = id;
        this.name = name;
        this.head = head;
        this.role = role;
        this.info = info;
        this.date = date;
        this.isCollect = isCollect;
        this.isPoint = isPoint;
        this.deleted = deleted;
        this.guide = guide;
        this.point = point;
        this.collect = collect;
        this.updateCount = updateCount;
        this.totalCount = totalCount;
        this.readCount = readCount;
        this.updateTime = updateTime;
        this.createTime = createTime;
        this.bossType = bossType;
    }

    public BossInfoEntity() {
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
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

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Long getDate() {
        return this.date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public boolean getIsCollect() {
        return this.isCollect;
    }

    public void setIsCollect(boolean isCollect) {
        this.isCollect = isCollect;
    }

    public boolean getIsPoint() {
        return this.isPoint;
    }

    public void setIsPoint(boolean isPoint) {
        this.isPoint = isPoint;
    }

    public boolean getDeleted() {
        return this.deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean getGuide() {
        return this.guide;
    }

    public void setGuide(boolean guide) {
        this.guide = guide;
    }

    public int getPoint() {
        return this.point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getCollect() {
        return this.collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public int getUpdateCount() {
        return this.updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getReadCount() {
        return this.readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public Long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setBossType(String bossType) {
        this.bossType = bossType;
    }

    public String getBossType() {
        if (TextUtils.isEmpty(this.bossType)) {
            return "without";
        }
        return this.bossType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BossInfoEntity that = (BossInfoEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean getTop() {
        return this.top;
    }
}
