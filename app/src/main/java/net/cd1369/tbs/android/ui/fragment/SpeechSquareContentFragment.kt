package net.cd1369.tbs.android.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.ui.BaseListFragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.youth.banner.indicator.CircleIndicator
import kotlinx.android.synthetic.main.fragment_speech_tack_content.*
import kotlinx.android.synthetic.main.header_boss_content.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.PageItem
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.data.entity.BannerEntity
import net.cd1369.tbs.android.event.GlobalScrollEvent
import net.cd1369.tbs.android.event.PageScrollEvent
import net.cd1369.tbs.android.ui.adapter.ArticleSquareAdapter
import net.cd1369.tbs.android.ui.adapter.BannerTitleAdapter
import net.cd1369.tbs.android.ui.home.ArticleActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Xiang on 2021/8/11 12:06
 * @description
 * @email Cymbidium@outlook.com
 */
class SpeechSquareContentFragment : BaseListFragment() {
    private var mLabel: String = "-1"
    private var needLoading = true

    private lateinit var mAdapterArticle: ArticleSquareAdapter

    private var mBanners = mutableListOf<BannerEntity>()
    private lateinit var headerView: View

    companion object {
        fun createFragment(label: String): SpeechSquareContentFragment {
            return SpeechSquareContentFragment().apply {
                arguments = Bundle().apply {
                    putString("label", label)
                }
            }
        }
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        mLabel = arguments?.getString("label") as String
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : ArticleSquareAdapter() {
            override fun onClick(item: ArticleEntity) {
                ArticleActivity.start(mActivity, item.id)
            }
        }.also {
            mAdapterArticle = it
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
            needLoading = false
            loadData(false)
        }

        rv_content.adapter = mAdapterArticle
        rv_content.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        headerView = LayoutInflater.from(mActivity).inflate(R.layout.header_boss_content, null)
        headerView.layout_banner.addBannerLifecycleObserver(this)
        headerView.layout_banner.setLoopTime(4000)
        headerView.layout_banner.indicator = CircleIndicator(mActivity)

        mAdapterArticle.addHeaderView(headerView)

        val emptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_follow_article, null)
        mAdapterArticle.emptyView = emptyView
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        if (!loadMore) {
            pageParam?.resetPage()

            if (needLoading) showLoading()

            TbsApi.boss().obtainBanner().onErrorReturn {
                mutableListOf()
            }.flatMap {
                mBanners = it

                return@flatMap TbsApi.boss().obtainAllArticle(pageParam, mLabel.toString())
            }.onErrorReturn {
                Page.empty()
            }.bindPageSubscribe(
                loadMore = false,
                doNext = {
                    headerView.layout_banner.isVisible = !mBanners.isNullOrEmpty()
                    if (!mBanners.isNullOrEmpty()) {
                        headerView.layout_banner.adapter = BannerTitleAdapter(mActivity, mBanners)
                        headerView.layout_banner.start()
                    }

                    mAdapterArticle.setNewData(it)
                }, doDone = {
                    needLoading = true
                    showContent()
                    layout_refresh.finishRefresh()
                })
        } else {
            TbsApi.boss().obtainAllArticle(pageParam, mLabel.toString())
                .onErrorReturn { Page.empty() }
                .bindPageSubscribe(
                    loadMore = true,
                    doNext = {
                        mAdapterArticle.addData(it)
                    },
                    doDone = {
                        layout_refresh.finishLoadMore()
                    }
                )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: PageScrollEvent) {
        if (GlobalScrollEvent.talkPage == PageItem.Square.code &&
            GlobalScrollEvent.squareLabel == mLabel.toString()
        ) {
            rv_content.smoothScrollToPosition(0)
        }
    }
}