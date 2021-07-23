package net.cd1369.tbs.android.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.core.PageParam
import cn.wl.android.lib.ui.BaseListFragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.fragment_follow.*
import kotlinx.android.synthetic.main.header_follow.*
import kotlinx.android.synthetic.main.header_follow.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.data.entity.BossLabelEntity
import net.cd1369.tbs.android.data.model.FollowVal
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.ui.adapter.FollowCardAdapter
import net.cd1369.tbs.android.ui.adapter.FollowInfoAdapter
import net.cd1369.tbs.android.ui.adapter.HomeTabAdapter
import net.cd1369.tbs.android.util.LabelManager
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Qing on 2021/6/28 11:44 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class FollowFragment : BaseListFragment() {

    private var version: Long = 0L
    private var mRootHeight: Int = 0
    private var headerView: View? = null

    private lateinit var tabAdapter: HomeTabAdapter
    private lateinit var cardAdapter: FollowCardAdapter
    private lateinit var mAdapter: FollowInfoAdapter
    private lateinit var mBossCards: MutableList<BossInfoEntity>

    private var mEmptyView: View? = null

    private var mSelectTab: String? = null
    private var needLoading = true

    companion object {
        private val mValueCache = hashMapOf<String, FollowVal>()

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
            override fun onSelect(labelId: String) {
                val followVal = FollowVal(PageParam.copy(pageParam), cardAdapter.data, mAdapter.data)
                mValueCache[mSelectTab ?: ""] = followVal

                mSelectTab = labelId
                val value = mValueCache[labelId]

                if (value != null) {
                    val boss = value.boss
                    val data = value.data
                    val param = value.param

                    cardAdapter.setNewData(boss)
                    mAdapter.setNewData(data)
                    pageParam?.set(param)

                    tryCompleteStatus(true)
                } else {
                    layout_refresh.autoRefresh()
                }
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

        layout_refresh.post {
            mRootHeight = layout_refresh.height
        }
    }

    private fun showContentEmpty(show: Boolean) {
        if (mEmptyView == null) {
            mEmptyView = vs_follow_empty.inflate()

            var sumHeight = headerView?.rv_tab?.height ?: 0
            sumHeight += headerView?.rv_card?.height ?: 0
            sumHeight += headerView?.layout_title?.height ?: 0

            val h = mRootHeight - sumHeight
            mEmptyView!!.updateLayoutParams {
                this.height = h
            }
        }

        mEmptyView?.isVisible = show
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : FollowInfoAdapter() {
            override fun onClick(item: ArticleEntity) {
                ArticleActivity.start(mActivity, item.id, item)
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

            LabelManager.obtainLabels()
                .flatMap {
                    if (LabelManager.needUpdate(version)) {
                        version = LabelManager.getVersion()

                        tabAdapter.setNewData(DataConfig.get().bossLabels)

                        it.add(0, BossLabelEntity.empty)
                        mSelectTab = it[0].id
                    }

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
                    cardAdapter.setNewData(mBossCards)

                    headerView!!.text_num.text = "共${(pageParam?.total ?: 0)}篇"
                    mAdapter.setNewData(it.shuffled())
                }, doDone = {
                    showContent()

                    layout_refresh.finishRefresh()

                    showContentEmpty(mAdapter.data.isNullOrEmpty())
                })
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