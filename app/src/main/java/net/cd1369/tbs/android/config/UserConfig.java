package net.cd1369.tbs.android.config;

import net.cd1369.tbs.android.data.entity.UserEntity;

import java.lang.ref.PhantomReference;

import cn.wl.android.lib.config.BaseConfig;
import cn.wl.android.lib.data.core.HttpConfig;

import static com.blankj.utilcode.util.SPStaticUtils.getBoolean;

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
    }

    public UserEntity getUserEntity() {
        UserEntity entity = getObject(KEY.keyUser, UserEntity.class);
        if (entity == null) {
            entity = UserEntity.Companion.getEmpty();
        }

        return entity;
    }

    @Override
    public void clear() {
        super.clear();

        HttpConfig.reset();
    }
}
