package net.cd1369.tbs.android.util

import cn.jpush.android.api.JPushInterface
import cn.wl.android.lib.config.WLConfig
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.entity.UserEntity
import net.cd1369.tbs.android.util.Tools.logE
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object JPushHelper {

    private var mRetryTimer: Disposable? = null
    private val mVersion = AtomicInteger(0)
    private val mContext get() = WLConfig.getContext()

    /**
     * 尝试听着推送
     */
    fun tryClearPush() {
        if (!JPushInterface.isPushStopped(mContext)) {
            JPushInterface.stopPush(mContext)
        }

        JPushInterface.cleanTags(mContext, mVersion.getAndIncrement())

        "停止推送, 清空Tag".logE()
    }

    /**
     * 尝试启动推送
     */
    fun tryStartPush() {
        var user = UserConfig.get().userEntity

        if (UserEntity.empty == user) return

        if (JPushInterface.isPushStopped(mContext)) {
            JPushInterface.resumePush(mContext)
        }

        val userId = user.id
        val alias = UserConfig.get().alias

        if (alias != userId) {
            tryRegisterAlias(userId)
        } else {
            "已存在推送别名:$alias".logE()
        }

        var tags = user.tags
        tryRegisterTags(tags)
    }

    /**
     * 尝试注册tag
     * @param tags List<String>?
     */
    private fun tryRegisterTags(tags: List<String>?) {
        if (tags.isNullOrEmpty()) return

        JPushInterface.setTags(
            mContext,
            mVersion.getAndIncrement(),
            tags.toSet()
        )
    }

    /**
     * 尝试注册别名
     * @param userId String
     * @param tryCount Int
     */
    private fun tryRegisterAlias(userId: String, tryCount: Int = 6) {
        mRetryTimer?.dispose()
        if (tryCount < 0) return

        JPushInterface.setAlias(
            mContext,
            userId
        ) { i, _, _ ->
            ("code: $i, id: $userId").logE(prefix = "极光推送注册:")

            if (i == 0) {
                UserConfig.dataConfig.alias = userId
            } else if (i == 6002) {
                mRetryTimer = Observable.timer(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        tryRegisterAlias(userId, tryCount - 1)
                    }) {

                    }
            }
        }
    }

    /**
     * 尝试添加tag
     * @param tags Array<out String>
     */
    fun tryAddTag(tag: String) {
        if (tag.isNullOrEmpty()) return

        "添加推送Tag:$tag".logE()

        JPushInterface.addTags(
            mContext,
            mVersion.getAndIncrement(),
            setOf(tag)
        )
    }

    /**
     * 尝试添加tag
     * @param tags Array<out String>
     */
    fun tryAddAllTag(tag: Collection<String>) {
        if (tag.isNullOrEmpty()) return

        "添加推送Tag:$tag".logE()

        JPushInterface.addTags(
            mContext,
            mVersion.getAndIncrement(),
            tag.toSet()
        )
    }

    /**
     * 尝试添加tag
     * @param tags Array<out String>
     */
    fun tryDelTag(tag: String) {
        if (tag.isNullOrEmpty()) return

        "移除推送Tag:$tag".logE()

        try {
            JPushInterface.deleteTags(
                mContext,
                mVersion.getAndIncrement(),
                setOf(tag)
            )
        } catch (e: Exception) {
        }
    }

}