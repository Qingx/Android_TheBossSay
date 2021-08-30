package net.cd1369.tbs.android.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.ui.BaseListActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.activity_boss_article.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.ui.adapter.BossAllArticleAdapter
import net.cd1369.tbs.android.util.doClick

class BossArticleActivity : BaseListActivity() {
    private lateinit var mAdapter: BossAllArticleAdapter
    private var needLoading = true
    private lateinit var bossId: String

    companion object {
        fun start(context: Context?, bossId: String) {
            val intent = Intent(context, BossArticleActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("bossId", bossId)
                }
            context!!.startActivity(intent)
        }
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : BossAllArticleAdapter() {
            override fun onClick(item: ArticleEntity) {
                ArticleActivity.start(mActivity, item.id)
            }
        }.also {
            mAdapter = it
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_boss_article
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        bossId = intent.getStringExtra("bossId") as String
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData(false)
        }

        rv_content.adapter = mAdapter
        rv_content.layoutManager = object : LinearLayoutManager(mActivity) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }

        val emptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_follow_article, null)
        mAdapter.emptyView = emptyView

        image_back doClick {
            onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        if (!loadMore) {
            pageParam?.resetPage()
        }

        if (needLoading && !loadMore) {
            showLoading()
        }

        TbsApi.boss().obtainBossAllArticle(pageParam, bossId)
            .onErrorReturn { Page.empty() }
            .bindPageSubscribe(
                loadMore = loadMore,
                doNext = {
                    if (loadMore) {
                        mAdapter.addData(it)
                        layout_refresh.finishLoadMore(true)
                    } else {
                        text_title.text = "全部文章(${pageParam?.total ?: 0}篇)"
                        mAdapter.setNewData(it)
                        layout_refresh.finishRefresh(true)
                    }
                }, doFail = {
                    if (loadMore) {
                        layout_refresh.finishLoadMore(false)
                    } else {
                        layout_refresh.finishRefresh(false)
                    }
                }, doDone = {
                    showContent()
                })
    }
}