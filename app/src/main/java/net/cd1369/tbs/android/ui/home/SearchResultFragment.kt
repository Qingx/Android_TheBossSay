package net.cd1369.tbs.android.ui.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.ui.BaseListFragment
import cn.wl.android.lib.utils.SpanUtils
import cn.wl.android.lib.utils.Toasts
import com.blankj.utilcode.util.ColorUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_search_result.*
import kotlinx.android.synthetic.main.fragment_search_result.layout_refresh
import kotlinx.android.synthetic.main.fragment_search_result.rv_content
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.event.FollowBossEvent
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.event.SearchEvent
import net.cd1369.tbs.android.ui.adapter.SearchInfoAdapter
import net.cd1369.tbs.android.ui.dialog.CancelFollowDialog
import net.cd1369.tbs.android.ui.dialog.SuccessFollowDialog
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Qing on 2021/6/30 3:50 下午
 * @description
 * @email Cymbidium@outlook.com
 */
class SearchResultFragment : BaseListFragment() {
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
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : SearchInfoAdapter() {
            override fun onItemClick(item: BossInfoEntity) {
                BossHomeActivity.start(mActivity, item)
            }

            override fun onClickFollow(item: BossInfoEntity) {
                if (item.isCollect) {
                    cancelFollow(item.id)
                } else followBoss(item.id)
            }
        }.also {
            mAdapter = it
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
                eventBus.post(RefreshUserEvent())
                layout_refresh.autoRefresh()

                CancelFollowDialog.showDialog(requireFragmentManager(), "cancelFollowBoss")
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
                eventBus.post(RefreshUserEvent())
                layout_refresh.autoRefresh()

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

    fun eventSearch(content: String) {
        searchText = content
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: SearchEvent) {
        searchText = event.content

        loadData(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: RefreshUserEvent) {
        loadData(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: FollowBossEvent) {
        loadData(false)
    }
}
