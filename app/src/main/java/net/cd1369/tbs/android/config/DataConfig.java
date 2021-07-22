package net.cd1369.tbs.android.config;

import net.cd1369.tbs.android.data.database.BossLabelDaoManager;
import net.cd1369.tbs.android.data.entity.BossLabelEntity;

import java.util.List;

import cn.wl.android.lib.config.BaseConfig;

/**
 * Created by Xiang on 2021/06/22 15:27
 *
 * @description
 * @email Cymbidium@outlook.com
 */
public class DataConfig extends BaseConfig {
    public DataConfig() {
        super("data");
    }

    public static DataConfig dataConfig;

    public static DataConfig get() {
        if (dataConfig == null) {
            dataConfig = new DataConfig();
        }
        return dataConfig;
    }

    interface KEY {
        String keyFistUse = "KEY_FIRST_USE";
        String keyTempId = "KEY_TEMP_ID";
        String keyUpdateTime = "KEY_UPDATE_TIME";
    }

    private boolean firstUse; //是否第一次使用app
    private String tempId; //设备id
    private List<BossLabelEntity> bossLabels;
    private Long updateTime;

    public void setFirstUse(boolean firstUse) {
        putBoolean(KEY.keyFistUse, firstUse);
    }

    public boolean getFirstUse() {
        return getBoolean(KEY.keyFistUse, true);
    }

    public void setTempId(String tempId) {
        putString(KEY.keyTempId, tempId);
    }

    public String getTempId() {
        return getString(KEY.keyTempId, "");
    }

    public List<BossLabelEntity> getBossLabels() {
        return BossLabelDaoManager.getInstance().getAllLabel();
    }

    public void setUpdateTime(Long time) {
        putLong(KEY.keyUpdateTime, time);
    }

    public Long getUpdateTime() {
        return getLong(KEY.keyUpdateTime, 1);
    }
}
