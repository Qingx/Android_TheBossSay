package net.cd1369.tbs.android.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseFragment
import cn.wl.android.lib.utils.SpanUtils
import cn.wl.android.lib.utils.Toasts
import com.blankj.utilcode.util.ColorUtils
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.fragment_home_boss_all_search.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.db.BossDaoManager
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.event.BossTackEvent
import net.cd1369.tbs.android.event.SearchEvent
import net.cd1369.tbs.android.ui.adapter.BossAllItemAdapter
import net.cd1369.tbs.android.ui.dialog.FollowAskCancelDialog
import net.cd1369.tbs.android.ui.dialog.FollowAskPushDialog
import net.cd1369.tbs.android.ui.dialog.FollowChangedDialog
import net.cd1369.tbs.android.ui.test.TestActivity
import net.cd1369.tbs.android.util.JPushHelper
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.max

/**
 * Created by Qing on 2021/6/30 3:50 下午
 * @description
 * @email Cymbidium@outlook.com
 */
class HomeBossAllSearchFragment : BaseFragment() {
    private lateinit var mAdapter: BossAllItemAdapter
    private var needLoading = true
    private var searchText = ""

    companion object {
        fun createFragment(): HomeBossAllSearchFragment {
            return HomeBossAllSearchFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_home_boss_all_search
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        eventBus.register(this)

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData()
        }

        mAdapter = object : BossAllItemAdapter() {
            override fun onItemClick(item: BossInfoEntity) {
                TestActivity.start(mActivity)
            }

            override fun onClickFollow(item: BossInfoEntity) {
                if (item.isCollect) {
                    FollowAskCancelDialog.showDialog(requireFragmentManager(), "askCancel")
                        .apply {
                            onConfirmClick = FollowAskCancelDialog.OnConfirmClick {
                                cancelFollow(item, this)
                            }
                        }
                } else followBoss(item)
            }
        }.also {
            it.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    super.onChanged()

                    layout_empty.isVisible = it.data.isNullOrEmpty()
                }
            })
        }
        rv_content.adapter = mAdapter
        rv_content.layoutManager =
            object : GridLayoutManager(mActivity, 4, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        val emptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_search, null)
        mAdapter.emptyView = emptyView
    }


    /**
     * 取消追踪boss
     */
    private fun cancelFollow(item: BossInfoEntity, dialog: FollowAskCancelDialog?) {
        showLoadingAlert("尝试取消...")

        TbsApi.boss().obtainCancelFollowBoss(item.id)
            .bindDefaultSub(doNext = {
                dialog?.dismiss()

                FollowChangedDialog.showDialog(requireFragmentManager(), true, "followChange")

                mAdapter.doFollowChange(item.id, false)

                UserConfig.get().updateUser {
                    it.traceNum = max((it.traceNum ?: 0) - 1, 0)
                }

                BossDaoManager.getInstance(mActivity).delete(item.id.toLong())
                eventBus.post(BossTackEvent(item.id, false, item.labels))

                JPushHelper.tryDelTag(item.id)
            }, doFail = {
                Toasts.show("取消失败，${it.msg}")
            }, doLast = {
                hideLoadingAlert()
            })
    }

    /**
     * 追踪boss
     */
    private fun followBoss(item: BossInfoEntity) {
        showLoadingAlert("尝试追踪...")

        TbsApi.boss().obtainFollowBoss(item.id)
            .bindDefaultSub(doNext = {
                FollowAskPushDialog.showDialog(requireFragmentManager(), "askPush")
                    .apply {
                        onConfirmClick = FollowAskPushDialog.OnConfirmClick {
                            JPushHelper.tryAddTag(item.id)

                            this.dismiss()

                            FollowChangedDialog.showDialog(
                                requireFragmentManager(),
                                false,
                                "followChange"
                            )
                        }
                        onCancelClick = FollowAskPushDialog.OnCancelClick {
                            this.dismiss()

                            FollowChangedDialog.showDialog(
                                requireFragmentManager(),
                                false,
                                "followChange"
                            )
                        }
                    }

                mAdapter.doFollowChange(item.id, true)

                UserConfig.get().updateUser {
                    it.traceNum = max((it.traceNum ?: 0) + 1, 0)
                }

                BossDaoManager.getInstance(mActivity).insert(item.toSimple())
                eventBus.post(BossTackEvent(item.id, true, item.labels))

            }, doFail = {
                Toasts.show("追踪失败，${it.msg}")
            }, doLast = {
                hideLoadingAlert()
            })
    }

    override fun loadData() {
        super.loadData()

        showLoading()

        TbsApi.boss().obtainAllBossSearchList(searchText)
            .onErrorReturn {
                mutableListOf()
            }.bindDefaultSub(doNext = {
                text_num.text = SpanUtils.getBuilder("共找到")
                    .setForegroundColor(ColorUtils.getColor(R.color.colorTextDark))
                    .append(" ${it.size} ")
                    .setForegroundColor(ColorUtils.getColor(R.color.colorAccent))
                    .append("条相关")
                    .setForegroundColor(ColorUtils.getColor(R.color.colorTextDark))
                    .create()
                mAdapter.setNewData(it)
                layout_refresh.finishRefresh(true)
            }, doFail = {
                layout_refresh.finishRefresh(false)
            }, doLast = {
                needLoading = true
                showContent()
            })
    }

    fun eventSearch(content: String) {
        searchText = content
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: SearchEvent) {
        searchText = event.content

        loadData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: BossTackEvent) {
        mAdapter.doFollowChange(event.id, event.isFollow)
    }
}
