package cn.wl.android.lib.utils;

/**
 * Created by JustBlue on 2019-12-11.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
public class DownloadEvent {

    private final long current;
    private final long total;

    public DownloadEvent(long current, long total) {
        this.total = total;
        this.current = current;
    }

    public long getCurrent() {
        return current;
    }

    public long getTotal() {
        return total;
    }

    public float getProgress() {
        return (float) (current * 1.0 / total);
    }

    public String getProgressDesc() {
        int i = (int) (current * 1.0 / total * 100);
        return i + "%";
    }

}
