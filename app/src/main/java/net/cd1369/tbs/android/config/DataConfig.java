package net.cd1369.tbs.android.config;

import net.cd1369.tbs.android.data.entity.BossLabelEntity;
import net.cd1369.tbs.android.util.Tools;

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
        String keyHotSearch = "KEY_HOT_SEARCH";
        String keyNeedService = "keyNeedService";
    }

    private boolean firstUse; //是否第一次使用app
    private String tempId; //设备id
    private Long updateTime;
    private String hotSearch;
    private List<BossLabelEntity> bossLabels;

    /**
     * 判断是否需要显示服务
     *
     * @param isNeed
     */
    public void setNeedService(boolean isNeed) {
        putBoolean(KEY.keyNeedService, isNeed);
    }

    public boolean isNeedService() {
        return getBoolean(KEY.keyNeedService, true);
    }

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
        return getString(KEY.keyTempId, Tools.INSTANCE.createTempId());
    }

    public List<BossLabelEntity> getBossLabels() {
        return this.bossLabels;
    }

    public void setBossLabels(List<BossLabelEntity> list) {
        this.bossLabels = list;
    }

    public void setUpdateTime(Long time) {
        putLong(KEY.keyUpdateTime, time);
    }

    public Long getUpdateTime() {
        return getLong(KEY.keyUpdateTime, 1);
    }

    public void setHotSearch(String hotSearch) {
        putString(KEY.keyHotSearch, hotSearch);
    }

    public String getHotSearch() {
        return getString(KEY.keyHotSearch, "-1");
    }

}
