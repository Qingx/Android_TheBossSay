package net.cd1369.tbs.android.config;

import net.cd1369.tbs.android.data.entity.UserEntity;
import net.cd1369.tbs.android.util.JPushHelper;

import cn.wl.android.lib.config.BaseConfig;
import cn.wl.android.lib.data.core.HttpConfig;
import cn.wl.android.lib.utils.Times;
import io.reactivex.functions.Consumer;

public class UserConfig extends BaseConfig {
    public UserConfig() {
        super("user");
    }

    public static UserConfig dataConfig;

    public static UserConfig get() {
        if (dataConfig == null) {
            dataConfig = new UserConfig();
        }
        return dataConfig;
    }

    interface KEY {
        String keyIsLogin = "KEY_IS_LOGIN";
        String keyUser = "KEY_USER";
        String kepLastRead = "KEY_LAST_READ";
        String keyDailyTime = "KEY_DAILY_TIME";
    }

    private boolean loginStatus; //是否登录
    private UserEntity userEntity;
    private Long lastReadTime;

    public void setLoginStatus(boolean loginStatus) {
        putBoolean(KEY.keyIsLogin, loginStatus);
    }

    public boolean getLoginStatus() {
        return getBoolean(KEY.keyIsLogin, false);
    }

    public void setLastReadTime(Long time) {
        putLong(KEY.kepLastRead, time);
    }

    public Long getLastReadTime() {
        return getLong(KEY.kepLastRead, Times.current());
    }

    public void setUserEntity(UserEntity entity) {
        putObject(KEY.keyUser, entity);
    }

    public UserEntity getUserEntity() {
        UserEntity entity = getObject(KEY.keyUser, UserEntity.class);

        if (entity == null) {
            entity = UserEntity.Companion.getEmpty();
        }

        return entity;
    }

    public void updateUser(Consumer<UserEntity> updater) {
        UserEntity userEntity = getUserEntity();

        try {
            if (!UserEntity.Companion.getEmpty().equals(userEntity)) {
                updater.accept(userEntity);
                setUserEntity(userEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDailyTime(Long dailyTime) {
        putLong(UserConfig.KEY.keyDailyTime, dailyTime);
    }

    public Long getDailyTime() {
        return getLong(UserConfig.KEY.keyDailyTime, -1L);
    }

    @Override
    public void clear() {
        super.clear();

        HttpConfig.reset();
        JPushHelper.INSTANCE.tryClearTagAlias();
    }
}
