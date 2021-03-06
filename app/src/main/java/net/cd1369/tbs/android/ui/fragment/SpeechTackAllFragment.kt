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
class SpeechTackAllFragment : BaseListFragment() {
    private var cardEmptyView: View? = null
    private var mRootHeight: Int = 0
    private var headerView: View? = null
    private var mEmptyView: View? = null

    private lateinit var cardAdapter: FollowCardAdapter
    private lateinit var mAdapter: ArticleTackAdapter
    private lateinit var mBossList: MutableList<BossSimpleModel>

    private var firstInit = true

    companion object {
        fun createFragment(): SpeechTackAllFragment {
            return SpeechTackAllFragment()
        }
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)
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
     * ??????-?????? ?????????????????????
     */
    @SuppressLint("SetTextI18n")
    private fun initData() {
        firstInit = false
        pageParam?.nextPage(1)

        mBossList = CacheConfig.getBossWithLastByLabel("-1")
        cardAdapter.setNewData(mBossList)

        val page = CacheConfig.getAllArticle()
        mAdapter.setNewData(page.records)

        val traceNum = UserConfig.get().userEntity.traceNum ?: 0

        if (traceNum > 0) {
            cardEmptyView?.text_notice?.text = "?????????????????????????????????"
        } else {
            cardEmptyView?.text_notice?.text = "??????????????????????????????"
        }

        headerView?.text_title?.text =
            if (page.records?.getOrNull(0)?.recommendType ?: "0" == "0") {
                "????????????"
            } else {
                "????????????"
            }
        headerView!!.text_num.text = "???${page.total}???"

        showContentEmpty(page.records.isNullOrEmpty())

        showContent()
    }

    /**
     * ???????????????????????????
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
                    model.isLatest && Tools.showRedDots(model.id, model.updateTime)
                }.toMutableList()

                TbsApi.boss().obtainTackArticle("-1", pageParam)
            }
            .onErrorReturn { Page.empty() }
            .bindPageSubscribe(false) {
                val page = Page.empty<ArticleSimpleModel>()
                page.total = pageParam?.total ?: 0
                page.pages = pageParam?.totalPage ?: 0
                page.current = 1
                page.records = it
                CacheConfig.insertArticle(page)

                headerView!!.text_num.text = "???${pageParam!!.total}???"
                cardAdapter.setNewData(mBossList)
                mAdapter.setNewData(it)

                val traceNum = UserConfig.get().userEntity.traceNum ?: 0

                if (traceNum > 0) {
                    cardEmptyView?.text_notice?.text = "?????????????????????????????????"
                } else {
                    cardEmptyView?.text_notice?.text = "??????????????????????????????"
                }

                headerView?.text_title?.text =
                    if (mAdapter?.data?.getOrNull(0)?.recommendType ?: "0" == "0") {
                        "????????????"
                    } else {
                        "????????????"
                    }

                showContentEmpty(mAdapter.data.isNullOrEmpty())

                showContent()
                layout_refresh.finishRefresh()
            }
    }

    /**
     * ??????????????????
     */
    private fun loadMoreData() {
        TbsApi.boss().obtainTackArticle("-1", pageParam)
            .onErrorReturn { Page.empty() }
            .bindPageSubscribe(loadMore = true) {
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
            firstInit -> initData()
            !firstInit && !loadMore -> refreshData()
            !firstInit && loadMore -> loadMoreData()
        }
    }

    /**
     * eventBus ??????(????????????)/???????????????????????????
     */
    @SuppressLint("SetTextI18n")
    private fun eventData() {
        pageParam?.resetPage()

        mBossList = CacheConfig.getBossWithLastByLabel("-1")

        TbsApi.boss().obtainTackArticle("-1", pageParam)
            .onErrorReturn {
                Page.empty()
            }
            .bindPageSubscribe(loadMore = false) {
                val page = Page.empty<ArticleSimpleModel>()
                page.total = pageParam?.total ?: 0
                page.pages = pageParam?.totalPage ?: 0
                page.current = 1
                page.records = it
                CacheConfig.insertArticle(page)

                headerView!!.text_num.text = "???${pageParam!!.total}???"

                cardAdapter.setNewData(mBossList)
                mAdapter.setNewData(it)

                val traceNum = UserConfig.get().userEntity.traceNum ?: 0

                if (traceNum > 0) {
                    cardEmptyView?.text_notice?.text = "?????????????????????????????????"
                } else {
                    cardEmptyView?.text_notice?.text = "??????????????????????????????"
                }

                headerView?.text_title?.text =
                    if (mAdapter?.data?.getOrNull(0)?.recommendType ?: "0" == "0") {
                        "????????????"
                    } else {
                        "????????????"
                    }

                showContentEmpty(mAdapter.data.isNullOrEmpty())
            }
    }

    /**
     * eventBus ??????(????????????)???????????????
     */
    @SuppressLint("SetTextI18n")
    private fun loginData() {
        pageParam?.resetPage()

        TbsApi.boss().obtainFollowBossList("-1", false)
            .onErrorReturn { mutableListOf() }
            .flatMap {
                CacheConfig.insertBossList(it)
                mBossList = it.filter { model ->
                    model.isLatest && Tools.showRedDots(model.id, model.updateTime)
                }.toMutableList()

                TbsApi.boss().obtainTackArticle("-1", pageParam)
            }
            .onErrorReturn { Page.empty() }
            .bindPageSubscribe(loadMore = false) {
                val page = Page.empty<ArticleSimpleModel>()
                page.total = pageParam?.total ?: 0
                page.pages = pageParam?.totalPage ?: 0
                page.current = 1
                page.records = it
                CacheConfig.insertArticle(page)

                headerView!!.text_num.text = "???${pageParam!!.total}???"

                cardAdapter.setNewData(mBossList)
                mAdapter.setNewData(it)

                val traceNum = UserConfig.get().userEntity.traceNum ?: 0

                if (traceNum > 0) {
                    cardEmptyView?.text_notice?.text = "?????????????????????????????????"
                } else {
                    cardEmptyView?.text_notice?.text = "??????????????????????????????"
                }

                headerView?.text_title?.text =
                    if (mAdapter?.data?.getOrNull(0)?.recommendType ?: "0" == "0") {
                        "????????????"
                    } else {
                        "????????????"
                    }

                showContentEmpty(mAdapter.data.isNullOrEmpty())
                showContent()
            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: BossTackEvent) {
        eventData()
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
            GlobalScrollEvent.tackLabel == "-1"
        ) {
            rv_content.smoothScrollToPosition(0)
        }
    }
}