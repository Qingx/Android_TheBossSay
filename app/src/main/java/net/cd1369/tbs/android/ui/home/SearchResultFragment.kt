package net.cd1369.tbs.android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.ErrorBean
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.ui.BaseListFragment
import cn.wl.android.lib.utils.SpanUtils
import cn.wl.android.lib.utils.Toasts
import com.blankj.utilcode.util.ColorUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_search_result.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.event.FollowBossEvent
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.event.SearchEvent
import net.cd1369.tbs.android.ui.adapter.SearchInfoAdapter
import net.cd1369.tbs.android.ui.dialog.CancelFollowDialog
import net.cd1369.tbs.android.ui.dialog.FollowCancelDialog
import net.cd1369.tbs.android.ui.dialog.SuccessFollowDialog
import net.cd1369.tbs.android.util.JPushHelper
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.max

/**
 * Created by Qing on 2021/6/30 3:50 下午
 * @description
 * @email Cymbidium@outlook.com
 */
class SearchResultFragment : BaseListFragment() {
    private var showDialog: FollowCancelDialog? = null
    private lateinit var mAdapter: SearchInfoAdapter

    private var searchText = ""

    companion object {
        fun createFragment(): SearchResultFragment {
            return SearchResultFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_search_result
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        eventBus.register(this)

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

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : SearchInfoAdapter() {
            override fun onItemClick(item: BossInfoEntity) {
                BossHomeActivity.start(mActivity, item)
            }

            override fun onClickFollow(item: BossInfoEntity) {
                if (item.isCollect) {
                    showDialog = FollowCancelDialog.showDialog(childFragmentManager, "cancel")
                    showDialog?.onConfirmClick = FollowCancelDialog.OnConfirmClick {
                        cancelFollow(item.id)
                    }
                } else followBoss(item.id)
            }
        }.also {
            mAdapter = it

            it.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    super.onChanged()

                    layout_empty.isVisible = it.data.isNullOrEmpty()
                }
            })
        }
    }

    /**
     * 取消追踪boss
     * @param id String
     */
    private fun cancelFollow(id: String) {
        showLoadingAlert("尝试取消...")

        TbsApi.boss().obtainCancelFollowBoss(id)
            .bindDefaultSub(doNext = {
                mAdapter.doFollowChange(id, false)

                UserConfig.get().updateUser {
                    it.traceNum = max((it.traceNum ?: 0) - 1, 0)
                }

                eventBus.post(RefreshUserEvent())
                eventBus.post(FollowBossEvent(id, isFollow = false))

                JPushHelper.tryDelTag(id)

//                CancelFollowDialog.showDialog(requireFragmentManager(), "cancelFollowBoss")
                showDialog?.tryDismiss()
            }, doFail = {
                Toasts.show("取消失败，${it.msg}")
            }, doLast = {
                hideLoadingAlert()
            })
    }

    /**
     * 追踪boss
     * @param id String
     */
    private fun followBoss(id: String) {
        showLoadingAlert("尝试追踪...")

        TbsApi.boss().obtainFollowBoss(id)
            .bindDefaultSub(doNext = {
                mAdapter.doFollowChange(id, true)

                UserConfig.get().updateUser {
                    it.traceNum = max((it.traceNum ?: 0) + 1, 0)
                }

                eventBus.post(RefreshUserEvent())
                eventBus.post(FollowBossEvent(id, isFollow = true))

                JPushHelper.tryAddTag(id)

                SuccessFollowDialog.showDialog(requireFragmentManager(), "successFollowBoss")
                    .apply {
                        onConfirmClick = SuccessFollowDialog.OnConfirmClick {
                            Toasts.show("开启推送")
                            this.dismiss()
                        }
                    }
            }, doFail = {
                Toasts.show("追踪失败，${it.msg}")
            }, doLast = {
                hideLoadingAlert()
            })
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        if (!loadMore) {
            pageParam?.resetPage()

            showLoading()

            TbsApi.boss().obtainSearchBossList(pageParam, searchText)
                .onErrorReturn {
                    Page.empty()
                }.bindPageSubscribe(loadMore = loadMore, doNext = {
                    text_num.text = SpanUtils.getBuilder("共找到")
                        .setForegroundColor(ColorUtils.getColor(R.color.colorTextDark))
                        .append(" ${pageParam?.total ?: 0} ")
                        .setForegroundColor(ColorUtils.getColor(R.color.colorAccent))
                        .append("条相关")
                        .setForegroundColor(ColorUtils.getColor(R.color.colorTextDark))
                        .create()

                    mAdapter.setNewData(it)
                }, doDone = {
                    showContent()
                })
        } else {
            TbsApi.boss().obtainSearchBossList(pageParam, searchText)
                .onErrorReturn {
                    Page.empty()
                }.bindPageSubscribe(loadMore = loadMore, doNext = {
                    mAdapter.addData(it)
                }, doDone = {
                    layout_refresh.finishLoadMore()
                })
        }
    }

    override fun onInterceptDataMiss(error: ErrorBean): Boolean {
        return super.onInterceptDataMiss(error)
    }

    fun eventSearch(content: String) {
        searchText = content
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: SearchEvent) {
        searchText = event.content

        loadData(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: FollowBossEvent) {
        mAdapter.doFollowChange(event.id!!, event.isFollow)
    }
}
