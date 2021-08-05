package net.cd1369.tbs.android.config;

import net.cd1369.tbs.android.data.entity.UserEntity;
import net.cd1369.tbs.android.util.JPushHelper;

import cn.wl.android.lib.config.BaseConfig;
import cn.wl.android.lib.data.core.HttpConfig;

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
    }

    private boolean loginStatus; //是否登录
    private UserEntity userEntity;

    public void setLoginStatus(boolean loginStatus) {
        putBoolean(KEY.keyIsLogin, loginStatus);
    }

    public boolean getLoginStatus() {
        return getBoolean(KEY.keyIsLogin, false);
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

    @Override
    public void clear() {
        super.clear();

        HttpConfig.reset();
        JPushHelper.INSTANCE.tryClearPush();
    }
}
