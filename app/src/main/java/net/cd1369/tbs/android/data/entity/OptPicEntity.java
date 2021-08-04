package net.cd1369.tbs.android.data.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 20:00 2021/8/2
 * @desc 运营图
 */
public class OptPicEntity implements Serializable {

    private String id;

    private String pictureLocation;

    private String bossId;

    private String name;

    private long date;

    private String role;

    private String info;

    private String head;

    private long createTime;

    private long updateTime;

    private int level;

    private long readCount;

    private int baseCollectNum;

    private boolean guide;

    public static final OptPicEntity EMPTY = new OptPicEntity();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBossId() {
        return bossId;
    }

    public void setBossId(String bossId) {
        this.bossId = bossId;
    }

    public String getPictureLocation() {
        return pictureLocation;
    }

    public void setPictureLocation(String pictureLocation) {
        this.pictureLocation = pictureLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getReadCount() {
        return readCount;
    }

    public void setReadCount(long readCount) {
        this.readCount = readCount;
    }

    public int getBaseCollectNum() {
        return baseCollectNum;
    }

    public void setBaseCollectNum(int baseCollectNum) {
        this.baseCollectNum = baseCollectNum;
    }

    public boolean isGuide() {
        return guide;
    }

    public void setGuide(boolean guide) {
        this.guide = guide;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptPicEntity that = (OptPicEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(bossId, that.bossId) &&
                Objects.equals(pictureLocation, that.pictureLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
