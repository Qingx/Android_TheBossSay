package cn.wl.android.lib.utils;

/**
 * Created by JustBlue on 2019-12-11.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
public class DownloadStatusEvent {

    private final boolean success;

    public DownloadStatusEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
