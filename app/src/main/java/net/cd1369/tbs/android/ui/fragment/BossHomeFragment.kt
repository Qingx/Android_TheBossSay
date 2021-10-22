package net.cd1369.tbs.android.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.ui.BaseListFragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.fragment_boss_article.*
import kotlinx.android.synthetic.main.header_boss_home.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.model.ArticleSimpleModel
import net.cd1369.tbs.android.event.ArticleReadEvent
import net.cd1369.tbs.android.ui.adapter.BossArticleAdapter
import net.cd1369.tbs.android.ui.home.ArticleActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Xiang on 2021/9/7 12:00
 * @description
 * @email Cymbidium@outlook.com
 */
class BossHomeFragment : BaseListFragment() {
    private lateinit var bossId: String
    private lateinit var type: String
    private var totalNum: Int = 0

    private lateinit var mAdapter: BossArticleAdapter
    private var headerView: View? = null

    private var needLoading = true

    companion object {
        fun createFragment(bossId: String, type: String): BossHomeFragment {
            return BossHomeFragment().apply {
                arguments = Bundle().apply {
                    putString("bossId", bossId)
                    putString("type", type)
                }
            }
        }
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)
        bossId = arguments!!.getString("bossId") as String
        type = arguments!!.getString("type") as String
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : BossArticleAdapter() {
            override fun onClick(item: ArticleSimpleModel) {
                ArticleActivity.start(mActivity, item.id.toString(), true)
            }
        }.also {
            mAdapter = it
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_boss_article
    }

    @SuppressLint("SetTextI18n")
    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        eventBus.register(this)

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

        headerView = LayoutInflater.from(mActivity).inflate(R.layout.header_boss_home, null)
        mAdapter.addHeaderView(headerView)

        val emptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_boss_article, null)
        mAdapter.emptyView = emptyView
        headerView!!.text_notice.text = if (type == "1") {
            "收录BOSS自己的言论语录及演讲采访等"
        } else {
            "收录BOSS相关的评论分析及实时新闻等"
        }
        headerView!!.text_num.text = "共${totalNum}篇"
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        if (!loadMore) {
            pageParam?.resetPage()
        }

        if (!loadMore && needLoading) {
            showLoading()
        }

        TbsApi.boss().obtainBossArticle(pageParam, bossId, type)
            .onErrorReturn { Page.empty() }
            .bindPageSubscribe(
                loadMore = loadMore,
                doNext = {
                    if (loadMore) {
                        mAdapter.addData(it)
                        layout_refresh.finishLoadMore()
                    } else {
                        mAdapter.setNewData(it)
                        totalNum = pageParam?.total ?: 0
                        headerView!!.text_num.text = "共${totalNum}篇"
                        layout_refresh.finishRefresh()
                    }
                },
                doDone = {
                    needLoading = true
                    showContent()
                }
            )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: ArticleReadEvent) {
        val index = mAdapter.data.indexOfFirst {
            it.id.toString() == event.articleId
        }

        if (index != -1) {
            mAdapter.data[index].isRead = true

            val layoutManager = rv_content.layoutManager as LinearLayoutManager

            val firstIndex = layoutManager.findFirstVisibleItemPosition()
            val lastIndex = layoutManager.findLastVisibleItemPosition()

            if (index in firstIndex + 1..lastIndex) {
                mAdapter.notifyItemChanged(index)
            }
        }
    }
}