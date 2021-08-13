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
        String PushAlias = "push_alias";
        String kepLastRead = "KEY_LAST_READ";
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

        if (entity != null && !UserEntity.Companion.getEmpty().equals(entity)) {
            JPushHelper.INSTANCE.tryStartPush();
        }
    }

    public UserEntity getUserEntity() {
        UserEntity entity = getObject(KEY.keyUser, UserEntity.class);

        if (entity == null) {
            entity = UserEntity.Companion.getEmpty();
        }

        return entity;
    }

    public void setAlias(String alias) {
        putString(KEY.PushAlias, alias);
    }

    public String getAlias() {
        return getString(KEY.PushAlias, "");
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

    @Override
    public void clear() {
        super.clear();

        HttpConfig.reset();
        JPushHelper.INSTANCE.tryClearPush();
    }
}
