package net.cd1369.tbs.android.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.ErrorBean
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.ui.BaseListFragment
import cn.wl.android.lib.utils.Toasts
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.fragment_follow.*
import kotlinx.android.synthetic.main.header_follow.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.data.entity.BossLabelEntity
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.ui.adapter.FollowCardAdapter
import net.cd1369.tbs.android.ui.adapter.FollowInfoAdapter
import net.cd1369.tbs.android.ui.adapter.HomeTabAdapter
import net.cd1369.tbs.android.ui.adapter.SquareInfoAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Qing on 2021/6/28 11:44 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class FollowFragment : BaseListFragment() {
    private var headerView: View? = null
    private lateinit var tabAdapter: HomeTabAdapter
    private lateinit var cardAdapter: FollowCardAdapter
    private lateinit var mAdapter: FollowInfoAdapter
    private lateinit var mBossCards: MutableList<BossInfoEntity>

    private var mSelectTab: String? = null
    private var needLoading = true

    companion object {
        fun createFragment(): FollowFragment {
            return FollowFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_follow
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        eventBus.register(this)

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData(false)
        }

        tabAdapter = object : HomeTabAdapter() {
            override fun onSelect(select: String) {
                mSelectTab = select
                layout_refresh.autoRefresh()
            }
        }

        cardAdapter = object : FollowCardAdapter() {
            override fun onClick(item: BossInfoEntity) {
                BossHomeActivity.start(mActivity, entity = item)
            }
        }

        headerView = LayoutInflater.from(mActivity).inflate(R.layout.header_follow, null)
        headerView!!.rv_tab.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
        headerView!!.rv_tab.adapter = tabAdapter

        headerView!!.rv_card.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
        headerView!!.rv_card.adapter = cardAdapter

        mAdapter.addHeaderView(headerView)

        rv_content.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        val emptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_boss_card_view, null)
        cardAdapter.emptyView = emptyView
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : FollowInfoAdapter() {
            override fun onClick(item: ArticleEntity) {
                Toasts.show(item.id)
            }
        }.also {
            mAdapter = it
        }
    }

    @SuppressLint("SetTextI18n")
    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        if (!loadMore) {
            pageParam?.resetPage()

            if (needLoading) showLoading()

            if (DataConfig.get().bossLabels.isNullOrEmpty()) {
                TbsApi.boss().obtainBossLabels()
                    .flatMap {
                        it.add(0, BossLabelEntity.empty)
                        mSelectTab = it[0].id
                        DataConfig.get().bossLabels = it

                        TbsApi.boss().obtainFollowBossList(mSelectTab, true)
                            .onErrorReturn {
                                mutableListOf()
                            }
                    }.flatMap {
                        mBossCards = it

                        TbsApi.boss().obtainFollowArticle(pageParam)
                            .onErrorReturn {
                                Page.empty()
                            }
                    }.bindPageSubscribe(loadMore = loadMore, doNext = {
                        tabAdapter.setNewData(DataConfig.get().bossLabels)
                        cardAdapter.setNewData(mBossCards)

                        headerView!!.text_num.text = "共${(pageParam?.total ?: 0)}篇"
                        mAdapter.setNewData(it)
                    }, doDone = {
                        showContent()

                        layout_refresh.finishRefresh()
                    })
            } else {
                TbsApi.boss().obtainFollowBossList(mSelectTab, true)
                    .flatMap {
                        mBossCards = it

                        TbsApi.boss().obtainFollowArticle(pageParam)
                    }.bindPageSubscribe(loadMore = loadMore, doNext = {
                        tabAdapter.setNewData(DataConfig.get().bossLabels)
                        cardAdapter.setNewData(mBossCards)

                        headerView!!.text_num.text = "共${(pageParam?.total ?: 0)}篇"
                        mAdapter.setNewData(it)
                    }, doDone = {
                        showContent()

                        layout_refresh.finishRefresh()
                    })
            }
        } else {
            TbsApi.boss().obtainFollowArticle(pageParam)
                .bindPageSubscribe(loadMore = loadMore, doNext = {
                    mAdapter.addData(it)
                }, doDone = {
                    layout_refresh.finishLoadMore()
                })
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: RefreshUserEvent) {
        loadData(false)
    }
}