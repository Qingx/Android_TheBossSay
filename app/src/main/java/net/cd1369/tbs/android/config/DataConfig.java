package net.cd1369.tbs.android.config;

import com.google.gson.reflect.TypeToken;

import net.cd1369.tbs.android.util.Tools;

import java.lang.reflect.Type;
import java.util.HashMap;

import cn.wl.android.lib.config.BaseConfig;
import cn.wl.android.lib.utils.DateFormat;
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
        String keyTackTotalNum = "keyTackTotalNum";
        String keyHasData = "keyHasData";
    }

    private boolean firstUse; //是否第一次使用app
    private String tempId; //设备id
    private Long updateTime;
    private String hotSearch;
    private Long bossTime;
    private int tackTotalNum;
    private boolean hasData;
    private boolean isRunning = false;

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

    public void setHotSearch(String hotSearch) {
        putString(KEY.keyHotSearch, hotSearch);
    }

    public String getHotSearch() {
        return getString(KEY.keyHotSearch, "-1");
    }

    public void setTackTotalNum(int num) {
        putInt(KEY.keyTackTotalNum, num);
    }

    public int getTackTotalNum() {
        return getInt(KEY.keyTackTotalNum, 0);
    }

    public void setHasData(boolean hasData) {
        putBoolean(KEY.keyHasData, hasData);
    }

    public boolean getHasData() {
        return getBoolean(KEY.keyHasData, true);
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

    public void setNoticeTime(long time) {
        putLong("notice_time", time);
    }

    /**
     * 检查通知时间, 不要短时间重复提示用户开启通知权限
     *
     * @param time
     * @return
     */
    public boolean checkNoticeTime(long time) {
        long noticeTime = getLong("notice_time", 0L);

        return Math.abs(noticeTime - time) > DateFormat.DAY_1;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
