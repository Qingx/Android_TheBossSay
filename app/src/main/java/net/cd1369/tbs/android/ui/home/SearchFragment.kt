package net.cd1369.tbs.android.ui.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.ui.BaseListFragment
import cn.wl.android.lib.utils.Toasts
import com.advance.AdvanceBanner
import com.advance.AdvanceBannerListener
import com.advance.model.AdvanceError
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.fragment_search.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.Const
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.data.entity.BossLabelEntity
import net.cd1369.tbs.android.event.FollowBossEvent
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.ui.adapter.SearchInfoAdapter
import net.cd1369.tbs.android.ui.adapter.SearchTabAdapter
import net.cd1369.tbs.android.ui.dialog.CancelFollowDialog
import net.cd1369.tbs.android.ui.dialog.SuccessFollowDialog
import net.cd1369.tbs.android.util.LabelManager
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by Qing on 2021/6/30 3:49 下午
 * @description
 * @email Cymbidium@outlook.com
 */
class SearchFragment : BaseListFragment(), AdvanceBannerListener {
    private var version: Long = 0L
    private lateinit var tabAdapter: SearchTabAdapter
    private lateinit var mAdapter: SearchInfoAdapter
    private var needLoading = true
    private var mSelectTab = ""
    private var advanceBanner: AdvanceBanner? = null

    companion object {
        fun createFragment(): SearchFragment {
            return SearchFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_search
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        eventBus.register(this)

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData(false)
        }

        tabAdapter = object : SearchTabAdapter() {
            override fun onClick(item: String) {
                mSelectTab = item
                layout_refresh.autoRefresh()
            }
        }

        rv_tab.adapter = tabAdapter
        rv_tab.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        rv_content.adapter = mAdapter
        rv_content.layoutManager =
            object : GridLayoutManager(mActivity, 3, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        //rl是banner的父布局，用来展示广告
        //rl是banner的父布局，用来展示广告
        advanceBanner = AdvanceBanner(mActivity, ll_ad, Const.BANNER_ID)
        //推荐：核心事件监听回调
        //推荐：核心事件监听回调
        advanceBanner?.setAdListener(this)
        //必须：请求策略并请求和展示广告
        //必须：请求策略并请求和展示广告
        advanceBanner?.loadStrategy()
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

            if (needLoading) showLoading()

            LabelManager.obtainLabels()
                .flatMap {
                    if (LabelManager.needUpdate(version)) {
                        version = LabelManager.getVersion()

                        it.add(0, BossLabelEntity.empty)
                        tabAdapter.setNewData(it)

                        mSelectTab = it[0].id
                    }

                    TbsApi.boss().obtainAllBossList(pageParam, mSelectTab)
                        .onErrorReturn {
                            Page.empty()
                        }
                }.bindPageSubscribe(loadMore = loadMore, doNext = {
                    mAdapter.setNewData(it)
                }, doDone = {
                    showContent()

                    layout_refresh.finishRefresh()
                })
        } else {
            TbsApi.boss().obtainAllBossList(pageParam, mSelectTab)
                .onErrorReturn {
                    Page.empty()
                }.bindPageSubscribe(loadMore = loadMore, doNext = {
                    mAdapter.addData(it)
                }, doDone = {
                    layout_refresh.finishLoadMore()
                })
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        advanceBanner?.destroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: RefreshUserEvent) {
        layout_refresh.autoRefresh()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: FollowBossEvent) {
        layout_refresh.autoRefresh()
    }

    override fun onAdFailed(p0: AdvanceError?) {

    }

    override fun onSdkSelected(p0: String?) {

    }

    override fun onAdShow() {

    }

    override fun onAdClicked() {

    }

    override fun onDislike() {

    }

    override fun onAdLoaded() {

    }
}