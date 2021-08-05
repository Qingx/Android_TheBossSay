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

    public static final OptPicEntity EMPTY = new OptPicEntity();

    private String id;

    private String pictureLocation;

    private BossInfoEntity entity;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPictureLocation() {
        return pictureLocation;
    }

    public void setPictureLocation(String pictureLocation) {
        this.pictureLocation = pictureLocation;
    }

    public BossInfoEntity getEntity() {
        return entity;
    }

    public void setEntity(BossInfoEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptPicEntity that = (OptPicEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OptPicEntity{" +
                "id='" + id + '\'' +
                ", pictureLocation='" + pictureLocation + '\'' +
                ", entity=" + entity +
                '}';
    }

}
