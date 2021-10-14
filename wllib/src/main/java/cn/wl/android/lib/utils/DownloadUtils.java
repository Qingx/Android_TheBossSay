package cn.wl.android.lib.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.wl.android.lib.config.WLConfig;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by JustBlue on 2019-11-18.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
public class DownloadUtils {
    private final SPUtils mSPName;

    //下载器
    private long downloadId;
    private Context mContext;
    private DownloadManager downloadManager;

    private boolean isCompleted = false;
    private Disposable mTimeDis;

    //下载的地址
    private String currentDownloadPath;
    private String currentDownloadName;

    public boolean isCompleted() {
        return isCompleted;
    }

    public DownloadUtils(Context context) {
        this.mContext = context;
        this.mSPName = SPUtils.getInstance("version_ob");

        //注册广播接收者，监听下载状态
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);

        mContext.registerReceiver(receiver, filter);
    }

    public String getCurrentDownloadPath(String name) {
        String savePath = mSPName.getString("V" + name, "");
        return savePath;
    }

    /**
     * 开始下载apk
     *
     * @param path
     * @param name
     */
    public void start(String path, String name) {
        try {
            String savePath = mSPName.getString(name, "");

            // 检测是否下载成功
            if (!TextUtils.isEmpty(savePath)) {
                if (FileUtils.isFileExists(savePath)) {
                    installAPK(savePath);
                    EventBus.getDefault().post(new DownloadStatusEvent(true));
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mSPName.put(name, "");
        }

        try {
            currentDownloadName = name;

            downloadAPK(path, name);
        } catch (Exception e) {
            e.printStackTrace();
            Toasts.show("下载失败");

            DownloadStatusEvent event = new DownloadStatusEvent(false);
            EventBus.getDefault().post(event);
        }
    }

    //下载apk
    public void downloadAPK(String u, String name) {
        String url = u;
        if (!u.startsWith("http")) {
            url = "http://" + u;
        }

        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        //移动网络情况下是否允许漫游
        request.setAllowedOverRoaming(false);
        request.setTitle(AppUtils.getAppName() + " " + name);
        request.setDescription("新版本正在下载...");
        request.setVisibleInDownloadsUi(false);

        //设置下载的路径
        File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), name + ".apk");
        request.setDestinationUri(Uri.fromFile(file));
        currentDownloadPath = file.getAbsolutePath();
        //获取DownloadManager
        if (downloadManager == null)
            downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
        if (downloadManager != null) {
            downloadId = downloadManager.enqueue(request);
        }

        resetDownloadCallback();
    }

    /**
     * 重置计时器, 间隔获取下载进度
     */
    private void resetDownloadCallback() {
        Disposable disposable = this.mTimeDis;

        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

        this.mTimeDis = Observable.interval(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((d) -> {
                    try {
                        checkProgress();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, (t) -> {

                });
    }

    /**
     * 清空计时器
     */
    private void clearDownloadCallback() {
        Disposable disposable = this.mTimeDis;

        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

        this.mTimeDis = null;
    }

    /**
     * 检查下载进度
     */
    private void checkProgress() {
        DownloadManager manager = this.downloadManager;

        if (manager == null) {
            return;
        }

        // 通过ID向下载管理查询下载情况，返回一个cursor
        DownloadManager.Query query = new DownloadManager
                .Query().setFilterById(downloadId);
        Cursor c = manager.query(query);
        if (c != null) {
            if (!c.moveToFirst()) {
                if (!c.isClosed()) {
                    c.close();
                }
                return;
            }
            int current = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            int total = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

            EventBus.getDefault().post(new DownloadEvent(current, total));

            if (!c.isClosed()) {
                c.close();
            }
        }
    }

    //广播监听下载的各个状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                checkStatus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    // 检查下载状态
    private void checkStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        //通过下载的id查找
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);

        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //下载暂停
                case DownloadManager.STATUS_PAUSED:
                    break;
                //下载延迟
                case DownloadManager.STATUS_PENDING:
                    break;
                //正在下载
                case DownloadManager.STATUS_RUNNING:
                    break;
                //下载完成
                case DownloadManager.STATUS_SUCCESSFUL:
                    isCompleted = true;
//                    Toasts.show("下载成功");
                    clearDownloadCallback();

                    //下载完成安装APK
                    installAPK(currentDownloadPath);
                    mSPName.put(currentDownloadName, currentDownloadPath);

                    EventBus.getDefault().post(new DownloadStatusEvent(true));
                    break;
                //下载失败
                case DownloadManager.STATUS_FAILED:
                    isCompleted = true;
//                    Toasts.show("下载失败");

                    clearDownloadCallback();
                    EventBus.getDefault().post(new DownloadStatusEvent(false));
                    break;
            }
        }

        cursor.close();
    }

    // 安装App
    public static void installAPK(String apkPathStr) {
        Context applicationContext = Utils.getApp().getApplicationContext();
        setPermission(apkPathStr);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Android 7.0以上要使用FileProvider
        if (Build.VERSION.SDK_INT >= 24) {
            File file = (new File(apkPathStr));
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(applicationContext, WLConfig.getAuthCode(), file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri,
                    "application/vnd.android.package-archive");
        } else {
            Uri uri = Uri.fromFile(new File(apkPathStr));
            intent.setDataAndType(uri,
                    "application/vnd.android.package-archive");
        }
        applicationContext.startActivity(intent);
    }

    // 修改文件权限
    private static void setPermission(String absolutePath) {
        String command = "chmod " + "777" + " " + absolutePath;
        Runtime runtime = Runtime.getRuntime();

        try {
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
