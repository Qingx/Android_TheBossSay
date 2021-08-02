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
    private String bossId;
    private String pictureLocation;

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
