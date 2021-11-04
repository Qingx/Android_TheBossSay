package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.ui.BaseListActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.activity_search_article.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.event.HotSearchEvent
import net.cd1369.tbs.android.ui.adapter.ArticleSearchAdapter
import net.cd1369.tbs.android.util.doClick
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SearchArticleActivity : BaseListActivity() {
    private lateinit var mAdapter: ArticleSearchAdapter
    private var needLoading = true
    private var searchText = ""

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, SearchArticleActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_search_article
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : ArticleSearchAdapter() {
            override fun onClick(item: ArticleEntity) {
                WebArticleActivity.start(mActivity, item.id)
            }
        }.also {
            mAdapter = it
        }
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        eventBus.register(this)

        edit_input.hint = if (DataConfig.get().hotSearch == "-1")
            "请输入内容" else DataConfig.get().hotSearch

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData(false)
        }


        edit_input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && !edit_input.text.toString()
                    .isNullOrEmpty()
            ) {
                searchText = edit_input.text.toString()
                layout_refresh.autoRefresh()
            }
            false
        }

        rv_content.adapter = mAdapter
        rv_content.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        val emptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_search, null)
        mAdapter.emptyView = emptyView

        image_cancel doClick {
            if (!edit_input.text.toString().isNullOrEmpty()) {
                edit_input.setText("")
                searchText = edit_input.text.toString()
                layout_refresh.autoRefresh()
            }
        }

        image_back doClick {
            onBackPressed()
        }

        text_search doClick {
            if (!edit_input.text.toString().isNullOrEmpty()) {
                searchText = edit_input.text.toString()
                layout_refresh.autoRefresh()
            }
        }
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        if (!loadMore) {
            pageParam?.resetPage()
            if (needLoading) showLoading()
        }

        TbsApi.boss().obtainSearchArticle(pageParam, searchText)
            .onErrorReturn {
                Page.empty()
            }.bindPageSubscribe(loadMore = loadMore, doNext = {
                if (loadMore) mAdapter.addData(it)
                else mAdapter.setNewData(it)
            }, doDone = {
                showContent()
                layout_refresh.finishRefresh()
                layout_refresh.finishLoadMore()
            })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: HotSearchEvent) {
        edit_input.hint = event.content
    }
}