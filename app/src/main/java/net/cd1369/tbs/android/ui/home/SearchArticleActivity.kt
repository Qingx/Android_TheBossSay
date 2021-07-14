package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.ui.BaseListActivity
import cn.wl.android.lib.utils.Toasts
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.activity_search_boss.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.ui.adapter.FollowInfoAdapter
import net.cd1369.tbs.android.util.doClick

class SearchArticleActivity : BaseListActivity() {
    private lateinit var mAdapter: FollowInfoAdapter
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
        return R.layout.activity_search_boss
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

    override fun initViewCreated(savedInstanceState: Bundle?) {
        edit_input.hint = "大家都在搜莉莉娅"

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
}