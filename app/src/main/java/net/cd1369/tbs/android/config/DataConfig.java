package net.cd1369.tbs.android.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.cd1369.tbs.android.data.entity.BossLabelEntity;
import net.cd1369.tbs.android.util.Tools;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import cn.wl.android.lib.config.BaseConfig;
import cn.wl.android.lib.utils.Gsons;
import cn.wl.android.lib.utils.Times;

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
        String keyHotSearch = "KEY_HOT_SEARCH";
        String keyNeedService = "keyNeedService";
        String keyBossTime = "keyBossTime";
    }

    private boolean firstUse; //是否第一次使用app
    private String tempId; //设备id
    private Long updateTime;
    private String hotSearch;
    private List<BossLabelEntity> bossLabels;
    private Long bossTime;

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

    public void setHotSearch(String hotSearch) {
        putString(KEY.keyHotSearch, hotSearch);
    }

    public String getHotSearch() {
        return getString(KEY.keyHotSearch, "-1");
    }

    public void setBossTime(String bossId) {
        Long nowTime = Times.current();

        HashMap<String, Long> map = new HashMap<>();

        String str = getString(KEY.keyBossTime, "empty");
        if (!str.equals("empty")) {
            Type type = new TypeToken<HashMap<String, Long>>() {
            }.getType();

            map = Gsons.getGson().fromJson(str, type);
        }

        map.put(bossId, nowTime);

        String newStr = Gsons.getGson().toJson(map);

        putString(KEY.keyBossTime, newStr);
    }

    public Long getBossTime(String bossId) {
        Long time = -1L;

        String str = getString(KEY.keyBossTime, "empty");

        if (!str.equals("empty")) {
            Type type = new TypeToken<HashMap<String, Long>>() {
            }.getType();

            HashMap<String, Long> map = Gsons.getGson().fromJson(str, type);

            if (map.containsKey(bossId)) {
                time = map.get(bossId);
            }
        }

        return time;
    }
}
