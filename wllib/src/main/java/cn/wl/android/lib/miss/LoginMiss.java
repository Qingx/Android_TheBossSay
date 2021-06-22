package cn.wl.android.lib.miss;

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 10:41 2020-03-12
 * @desc 登录失效自定义异常
 */
public final class LoginMiss extends BaseMiss {

    public static final int LOGIN_MISS_CODE = -6;

    public LoginMiss() {
        super(LOGIN_MISS_CODE, "登录失效, 请重新登录!");
    }

}
