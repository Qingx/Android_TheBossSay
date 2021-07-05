package net.cd1369.tbs.android.config;

import cn.wl.android.lib.config.BaseConfig;
import cn.wl.android.lib.data.core.HttpConfig;

import static com.blankj.utilcode.util.SPStaticUtils.getBoolean;

public class UserConfig extends BaseConfig {
    public UserConfig() {
        super("data");
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
    }

    private boolean loginStatus; //是否登录

    public void setLoginStatus(boolean loginStatus) {
        putBoolean(KEY.keyIsLogin, loginStatus);
    }

    public boolean getLoginStatus() {
        return getBoolean(KEY.keyIsLogin, false);
    }

    @Override
    public void clear() {
        super.clear();

        HttpConfig.reset();
    }
}
