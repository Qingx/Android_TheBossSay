package net.cd1369.tbs.android.config;

import net.cd1369.tbs.android.data.entity.BossLabelEntity;
import net.cd1369.tbs.android.util.Tools;

import java.util.List;

import cn.wl.android.lib.config.BaseConfig;
import retrofit2.http.PUT;

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
    }

    private boolean firstUse; //是否第一次使用app
    private String tempId; //设备id
    private List<BossLabelEntity> bossLabels;

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

    public void setBossLabels(List<BossLabelEntity> labels) {
        this.bossLabels = labels;
    }

    public List<BossLabelEntity> getBossLabels() {
        return this.bossLabels;
    }
}
