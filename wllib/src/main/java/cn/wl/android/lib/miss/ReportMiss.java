package cn.wl.android.lib.miss;

/**
 * Created by JustBlue on 2019-12-17.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
public class ReportMiss extends BaseMiss {

    public ReportMiss(Throwable e, String url) {
        super(e, getRealMsg(e, url));


    }


    private static String getRealMsg(Throwable e, String url) {
        String errorMsg = "error url: " + url + "";
        return e == null ? errorMsg : errorMsg + e.getMessage();
    }

}
