package net.cd1369.tbs.android.util

import cn.jpush.android.api.JPushInterface
import cn.wl.android.lib.config.WLConfig
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import net.cd1369.tbs.android.config.TbsApp
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.util.Tools.logE
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object JPushHelper {

    private var mAddTag: Disposable? = null
    private var mAddTags: Disposable? = null
    private var mAddAlias: Disposable? = null
    private val mVersion = AtomicInteger(0)
    private val mContext get() = WLConfig.getContext()

    /**
     * 尝试启动推送
     */
    fun tryStartPush() {
        JPushInterface.resumePush(TbsApp.mContext)

        val user = UserConfig.get().userEntity

        val userId = user.id
        if (user.type != "0") {
            tryAddAlias(userId)
        }
        tryAddTags(user.tags ?: mutableListOf())
    }

    /**
     * 尝试注册别名
     * @param userId String
     */
    fun tryAddAlias(userId: String) {
        mAddAlias?.dispose()

        JPushInterface.setAlias(
            mContext,
            userId
        ) { p0, p1, p2 ->
            "设置极光alias: $p0 ${p1 ?: "null"} $p2".logE()

            if (p0 == 6002) {
                mAddAlias = Observable.timer(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        tryAddAlias(userId)
                    }) {

                    }
            }
        }
    }

    /**
     * 尝试添加tag
     * @param tag String
     */
    fun tryAddTag(tag: String) {
        mAddTag?.dispose()

        JPushInterface.setTags(
            mContext,
            setOf(tag)
        ) { p0, p1, p2 ->
            "设置极光tag: $p0 ${p1 ?: "null"} $p2".logE()

            if (p0 == 6002) {
                mAddTag = Observable.timer(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        tryAddTag(tag)
                    }) {

                    }
            }
        }
    }

    /**
     * 尝试添加tag
     * @param tags MutableList<String>
     */
    fun tryAddTags(tags: MutableList<String>) {
        if (tags.isNullOrEmpty()) return

        mAddTags?.dispose()

//        val testTag = mutableListOf("0123456789")

        JPushInterface.setTags(
            mContext, tags.toSet()
        ) { p0, p1, p2 ->
            "设置极光tag: $p0 ${p1 ?: "null"} $p2".logE()

            if (p0 == 6002) {
                mAddTags = Observable.timer(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        tryAddTags(tags)
                    }) {

                    }
            }
        }
    }

    /**
     * 尝试删除tag
     * @param tag String
     */
    fun tryDelTag(tag: String) {
        try {
            JPushInterface.deleteTags(
                mContext,
                mVersion.getAndIncrement(),
                setOf(tag)
            )
        } catch (e: Exception) {
        }
    }


    /**
     * 尝试重置推送
     */
    fun tryClearTagAlias() {
//        JPushInterface.deleteAlias(mContext, mVersion.getAndIncrement())
        JPushInterface.cleanTags(mContext, mVersion.getAndIncrement())
    }
}