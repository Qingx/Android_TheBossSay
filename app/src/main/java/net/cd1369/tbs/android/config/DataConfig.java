package net.cd1369.tbs.android.config;

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
    }

    private boolean firstUse; //是否第一次使用app

    public void setFirstUse(boolean firstUse) {
        putBoolean(KEY.keyFistUse, firstUse);
    }

    public boolean getFirstUse() {
        return getBoolean(KEY.keyFistUse, true);
    }
}
