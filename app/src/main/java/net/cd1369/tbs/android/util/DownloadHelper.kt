package net.cd1369.tbs.android.util

import cn.wl.android.lib.utils.DownloadUtils
import cn.wl.android.lib.utils.Toasts
import net.cd1369.tbs.android.config.TbsApp

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 10:51 2021/8/11
 * @desc
 */
object DownloadHelper {

    val mManager by lazy {
        DownloadUtils(TbsApp.mContext)
    }

    /**
     * 请求下载apk
     * @param path String
     * @param name String
     */
    fun requestDownload(path: String, name: String) {
        try {
            mManager.start(path, name)
        } catch (e: Exception) {
            Toasts.show("下载失败")
            e.printStackTrace()
        }
    }

    /**
     * 获取apk保存路径
     * @param name String
     * @return String
     */
    fun getApkPath(name: String): String {
        return mManager.getCurrentDownloadPath(name)
    }

}