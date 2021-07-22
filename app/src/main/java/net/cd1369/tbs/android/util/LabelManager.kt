package net.cd1369.tbs.android.util

import android.util.Log
import cn.wl.android.lib.utils.Times
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.database.BossLabelDaoManager
import net.cd1369.tbs.android.data.entity.BossLabelEntity
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 16:10 2021/7/22
 * @desc 标签本地存储
 */
object LabelManager {

    private val dataVersion = AtomicLong(0)
    private val actualLabels = linkedSetOf<BossLabelEntity>()
    private val obtainRemote = TbsApi.boss().obtainBossLabels()
        .retry(2)
        .doOnNext {
            tryUpdateLabels(it, false)
        }
        .replay(1)
        .refCount(16, TimeUnit.MILLISECONDS)

    /**
     * 尝试刷新label是
     * @param it List<BossLabelEntity>
     */
    private fun tryUpdateLabels(it: List<BossLabelEntity>?, isLocal: Boolean) {
        if (it.isNullOrEmpty()) return

        var change = false

        // 如标签数量不相同, 表示标签列表已更新
        if (actualLabels.size != it.size) {
            change = true
            dataVersion.set(Times.current())

            actualLabels.clear()
            actualLabels.addAll(it)
        } else {
            ll@ for (entity in it) {
                // 标签数量相同, 但有当前列表未存在的标签, 表示标签列表已更新
                if (!actualLabels.contains(entity)) {
                    change = true
                    dataVersion.set(Times.current())

                    actualLabels.clear()
                    actualLabels.addAll(it)
                    break@ll
                }
            }
        }

        if (change && !isLocal) {
            Log.e("OkHttp", "标签已更新...")
            // 同步数据库
            BossLabelDaoManager.getInstance().insertList(it)
        } else {
            Log.e("OkHttp", "标签未更新...")
        }
    }

    private fun obtainLocals(): MutableList<BossLabelEntity> {
        if (this.actualLabels.isEmpty()) {
            // 空列表时, 刷新数据
            val bossLabels = DataConfig.get().bossLabels
            tryUpdateLabels(bossLabels, true)
            tryRefreshLabels()
        }

        return this.actualLabels.toMutableList()
    }

    /**
     * 尝试刷新本地labels
     */
    private fun tryRefreshLabels() {
        obtainRemote
            .subscribe({

            }) {

            }
    }

    /**
     * 获取
     * @return Observable<List<BossLabelEntity>>
     */
    fun obtainLabels(): Observable<MutableList<BossLabelEntity>> =
        Observable.defer {
            return@defer Observable.just(obtainLocals())
        }.flatMap {
            return@flatMap if (it.isNullOrEmpty()) {
                obtainRemote.onErrorReturn { mutableListOf() }
            } else Observable.just(it)
        }.observeOn(AndroidSchedulers.mainThread())

    /**
     * 获取标签更新版本
     * @return Long
     */
    fun getVersion() = dataVersion.get()

    /**
     * 检查当前labels数据是否更新
     * @param version Long
     * @return Boolean
     */
    fun needUpdate(version: Long): Boolean = version != dataVersion.get()

}