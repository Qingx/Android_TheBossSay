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
import kotlinx.android.synthetic.main.fragment_speech_tack_content.*
import kotlinx.android.synthetic.main.header_boss_content.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.data.entity.BannerEntity
import net.cd1369.tbs.android.ui.adapter.BannerViewAdapter
import net.cd1369.tbs.android.ui.adapter.SquareInfoAdapter
import net.cd1369.tbs.android.ui.home.ArticleActivity
import net.cd1369.tbs.android.util.Tools

/**
 * Created by Xiang on 2021/8/11 12:06
 * @description
 * @email Cymbidium@outlook.com
 */
class SpeechSquareContentFragment : BaseListFragment() {
    private var mLabel: Long = -1L
    private var needLoading = true

    private lateinit var mAdapter: SquareInfoAdapter

    private var mBanners = mutableListOf<BannerEntity>()
    private lateinit var headerView: View

    companion object {
        fun createFragment(label: Long): SpeechSquareContentFragment {
            return SpeechSquareContentFragment().apply {
                arguments = Bundle().apply {
                    putLong("label", label)
                }
            }
        }
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        mLabel = arguments?.getLong("label") as Long
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : SquareInfoAdapter() {
            override fun onClick(item: ArticleEntity) {
                if (!item.isRead) {
                    Tools.addTodayRead()
                }

                ArticleActivity.start(mActivity, item.id)
            }
        }.also {
            mAdapter = it
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_speech_tack_content
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData(false)
        }

        rv_content.adapter = mAdapter
        rv_content.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        headerView = LayoutInflater.from(mActivity).inflate(R.layout.header_boss_content, null)
        headerView.layout_banner.addBannerLifecycleObserver(this)
        headerView.layout_banner.setLoopTime(4000)

        mAdapter.addHeaderView(headerView)

        val emptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_follow_article, null)
        mAdapter.emptyView = emptyView
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
                        headerView.layout_banner.adapter = BannerViewAdapter(mActivity, mBanners)
                        headerView.layout_banner.start()
                    }

                    mAdapter.setNewData(it)
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
                        mAdapter.addData(it)
                    },
                    doDone = {
                        layout_refresh.finishLoadMore()
                    }
                )
        }
    }
}