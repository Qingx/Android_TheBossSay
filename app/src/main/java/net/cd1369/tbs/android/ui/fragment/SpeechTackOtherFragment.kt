package net.cd1369.tbs.android.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.ui.BaseListFragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.empty_boss_card_view.view.*
import kotlinx.android.synthetic.main.fragment_speech_tack_content.*
import kotlinx.android.synthetic.main.header_speech_content.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.PageItem
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.cache.CacheConfig
import net.cd1369.tbs.android.data.model.ArticleSimpleModel
import net.cd1369.tbs.android.data.model.BossSimpleModel
import net.cd1369.tbs.android.event.*
import net.cd1369.tbs.android.ui.adapter.ArticleTackAdapter
import net.cd1369.tbs.android.ui.adapter.FollowCardAdapter
import net.cd1369.tbs.android.ui.home.ArticleActivity
import net.cd1369.tbs.android.ui.home.BossHomeActivity
import net.cd1369.tbs.android.ui.home.HomeBossAllActivity
import net.cd1369.tbs.android.util.Tools
import net.cd1369.tbs.android.util.doClick
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Xiang on 2021/8/11 10:05
 * @description
 * @email Cymbidium@outlook.com
 */
class SpeechTackOtherFragment : BaseListFragment() {
    private var mLabel: String = "-1"
    private var cardEmptyView: View? = null
    private var mRootHeight: Int = 0
    private var headerView: View? = null
    private var mEmptyView: View? = null

    private lateinit var cardAdapter: FollowCardAdapter
    private lateinit var mAdapter: ArticleTackAdapter
    private lateinit var mBossList: MutableList<BossSimpleModel>

    private var firstInit = true

    companion object {
        fun createFragment(label: String): SpeechTackOtherFragment {
            return SpeechTackOtherFragment().apply {
                arguments = Bundle().apply {
                    putString("label", label)
                }
            }
        }
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        mLabel = arguments?.getString("label", "-1") as String
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : ArticleTackAdapter() {
            override fun onClick(item: ArticleSimpleModel) {
                ArticleActivity.start(mActivity, item.id.toString())
            }
        }.also {
            mAdapter = it
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_speech_tack_content
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        eventBus.register(this)

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            loadData(false)
        }

        cardAdapter = object : FollowCardAdapter() {
            override fun onClick(item: BossSimpleModel) {
                BossHomeActivity.start(mActivity, item.id.toString())
            }
        }

        headerView = LayoutInflater.from(mActivity).inflate(R.layout.header_speech_content, null)
        headerView!!.text_title.paint.isFakeBoldText = true
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

        cardEmptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_boss_card_view, null)
        cardAdapter.emptyView = cardEmptyView

        cardEmptyView!!.text_add doClick {
            HomeBossAllActivity.start(mActivity)
        }

        layout_refresh.post {
            mRootHeight = layout_refresh.height
        }
    }

    private fun showContentEmpty(show: Boolean) {
        if (mEmptyView == null) {
            mEmptyView = headerView?.vs_follow_empty?.inflate()

            var sumHeight = headerView?.rv_card?.height ?: 0
            sumHeight += headerView?.layout_title?.height ?: 0

            val h = mRootHeight - sumHeight
            mEmptyView!!.updateLayoutParams {
                this.height = h
            }
        }

        mEmptyView?.isVisible = show
    }

    /**
     * 标签-其他 进入时处理数据
     */
    @SuppressLint("SetTextI18n")
    private fun initOtherData() {
        firstInit = false
        pageParam?.nextPage(1)

        showLoading()

        mBossList = CacheConfig.getBossWithLastByLabel(mLabel)
        cardAdapter.setNewData(mBossList)

        TbsApi.boss().obtainTackArticle(mLabel, pageParam)
            .onErrorReturn { Page.empty() }
            .bindPageSubscribe(false)
            {
                headerView!!.text_num.text = "共${pageParam!!.total}篇"
                mAdapter.setNewData(it)

                val traceNum = UserConfig.get().userEntity.traceNum ?: 0

                if (traceNum > 0) {
                    cardEmptyView?.text_notice?.text = "追踪的老板暂无言论更新"
                } else {
                    cardEmptyView?.text_notice?.text = "当前还没有追踪的老板"
                }

                headerView?.text_title?.text =
                    if (mAdapter?.data?.getOrNull(0)?.recommendType ?: "0" == "0") {
                        "最近更新"
                    } else {
                        "为你推荐"
                    }

                showContentEmpty(mAdapter.data.isNullOrEmpty())

                showContent()
            }
    }

    /**
     * 下拉刷新时处理数据
     */
    @SuppressLint("SetTextI18n")
    private fun refreshData() {
        pageParam?.resetPage()

        TbsApi.boss().obtainFollowBossList("-1", false)
            .onErrorReturn { mutableListOf() }
            .flatMap {
                it.sort()
                CacheConfig.insertBossList(it)

                mBossList = it.filter { model ->
                    model.labels.contains(mLabel) && model.isLatest && Tools.showRedDots(
                        model.id,
                        model.updateTime
                    )
                }.toMutableList()

                TbsApi.boss().obtainTackArticle(mLabel, pageParam)
            }.onErrorReturn { Page.empty() }
            .bindPageSubscribe(false) {
                cardAdapter.setNewData(mBossList)
                mAdapter.setNewData(it)

                headerView!!.text_num.text = "共${pageParam!!.total}篇"

                val traceNum = UserConfig.get().userEntity.traceNum ?: 0

                if (traceNum > 0) {
                    cardEmptyView?.text_notice?.text = "追踪的老板暂无言论更新"
                } else {
                    cardEmptyView?.text_notice?.text = "当前还没有追踪的老板"
                }

                headerView?.text_title?.text =
                    if (mAdapter?.data?.getOrNull(0)?.recommendType ?: "0" == "0") {
                        "最近更新"
                    } else {
                        "为你推荐"
                    }

                showContentEmpty(mAdapter.data.isNullOrEmpty())

                showContent()
                layout_refresh.finishRefresh()
            }
    }

    /**
     * 加载更多数据
     */
    private fun loadMoreData() {
        TbsApi.boss().obtainTackArticle(mLabel, pageParam)
            .onErrorReturn { Page.empty() }
            .bindPageSubscribe(true) {
                mAdapter.addData(it)

                layout_refresh.finishLoadMore()
            }
    }

    @SuppressLint("SetTextI18n")
    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        if (!loadMore) {
            pageParam?.resetPage()
        }

        when {
            firstInit -> initOtherData()
            !firstInit && !loadMore -> refreshData()
            !firstInit && loadMore -> loadMoreData()
        }
    }

    /**
     * eventBus 关注(批量关注)/取消关注时处理数据
     */
    @SuppressLint("SetTextI18n")
    private fun eventData() {
        mBossList = CacheConfig.getBossWithLastByLabel(mLabel)

        pageParam?.resetPage()
        TbsApi.boss().obtainTackArticle(mLabel, pageParam)
            .onErrorReturn { Page.empty() }
            .bindPageSubscribe(false) {
                cardAdapter.setNewData(mBossList)
                mAdapter.setNewData(it)

                headerView!!.text_num.text = "共${pageParam!!.total}篇"

                val traceNum = UserConfig.get().userEntity.traceNum ?: 0

                if (traceNum > 0) {
                    cardEmptyView?.text_notice?.text = "追踪的老板暂无言论更新"
                } else {
                    cardEmptyView?.text_notice?.text = "当前还没有追踪的老板"
                }

                headerView?.text_title?.text =
                    if (mAdapter?.data?.getOrNull(0)?.recommendType ?: "0" == "0") {
                        "最近更新"
                    } else {
                        "为你推荐"
                    }

                showContentEmpty(mAdapter.data.isNullOrEmpty())
            }
    }

    /**
     * eventBus 登录(退出登录)时处理数据
     */
    @SuppressLint("SetTextI18n")
    private fun loginData() {
        pageParam?.resetPage()

        TbsApi.boss().obtainFollowBossList("-1", false)
            .onErrorReturn { mutableListOf() }
            .flatMap {
                CacheConfig.insertBossList(it)

                mBossList = it.filter { model ->
                    model.labels.contains(mLabel) && model.isLatest && Tools.showRedDots(
                        model.id,
                        model.updateTime
                    )
                }.toMutableList()

                TbsApi.boss().obtainTackArticle(mLabel, pageParam)
            }
            .onErrorReturn { Page.empty() }
            .bindPageSubscribe(false) {
                cardAdapter.setNewData(mBossList)
                mAdapter.setNewData(it)

                headerView!!.text_num.text = "共${pageParam!!.total}篇"

                val traceNum = UserConfig.get().userEntity.traceNum ?: 0

                if (traceNum > 0) {
                    cardEmptyView?.text_notice?.text = "追踪的老板暂无言论更新"
                } else {
                    cardEmptyView?.text_notice?.text = "当前还没有追踪的老板"
                }

                headerView?.text_title?.text =
                    if (mAdapter?.data?.getOrNull(0)?.recommendType ?: "0" == "0") {
                        "最近更新"
                    } else {
                        "为你推荐"
                    }

                showContentEmpty(mAdapter.data.isNullOrEmpty())
            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: BossTackEvent) {
        if (mLabel == "-1" || event.labels.contains(mLabel.toString())) {
            eventData()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: BossBatchTackEvent) {
        eventData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: LoginEvent) {
        loginData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: SetBossTimeEvent) {
        val index = mBossList.indexOfFirst {
            it.id.toString() == event.id
        }
        if (index != -1) {
            cardAdapter.remove(index)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: JpushArticleEvent) {
        val index = mBossList.indexOfFirst {
            it.id.toString() == event.bossId
        }

        if (index != -1) {
            mBossList[index].updateTime = event.time

            cardAdapter.data[index].updateTime = event.time
            cardAdapter.notifyItemChanged(index)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: PageScrollEvent) {
        if (GlobalScrollEvent.talkPage == PageItem.Tack.code &&
            GlobalScrollEvent.tackLabel == mLabel
        ) {
            rv_content.smoothScrollToPosition(0)
        }
    }
}