package net.cd1369.tbs.android.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.ui.BaseListActivity
import cn.wl.android.lib.utils.Toasts
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.activity_mine_history_all.*
import kotlinx.android.synthetic.main.empty_follow_article.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.ui.adapter.HistoryContentAdapter
import net.cd1369.tbs.android.util.doClick
import kotlin.math.max

class MineHistoryTodayActivity : BaseListActivity() {
    private lateinit var mAdapter: HistoryContentAdapter
    private var needLoading = true
    private var number = UserConfig.get().userEntity.readNum ?: 0

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, MineHistoryTodayActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_mine_history_all
    }

    @SuppressLint("SetTextI18n")
    override fun initViewCreated(savedInstanceState: Bundle?) {
        text_title.text = "今日阅读($number)"

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

        val emptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_follow_article, null)
        emptyView.text_notice.text = "暂无历史记录"
        mAdapter.emptyView = emptyView

        image_back doClick {
            onBackPressed()
        }
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : HistoryContentAdapter(false) {
            override fun onContentClick(articleId: String) {
                ArticleActivity.start(mActivity, articleId)
            }

            override fun onContentDelete(historyId: String, doRemove: (id: String) -> Unit) {
                removeHistory(historyId, doRemove)
            }
        }.also {
            mAdapter = it
        }
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        if (!loadMore) {
            pageParam?.resetPage()

            if (needLoading) showLoading()
        }

        TbsApi.user().obtainHistory(pageParam, true)
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

    @SuppressLint("SetTextI18n")
    private fun removeHistory(id: String, doRemove: (id: String) -> Unit) {
        showLoadingAlert("尝试删除...")

        TbsApi.user().obtainRemoveHistory(id)
            .bindDefaultSub(doNext = {
                doRemove(id)

                number -= 1
                text_title.text = "阅读记录($number)"
                UserConfig.get().updateUser {
                    it.readNum = max((it.readNum ?: 0) - 1, 0)
                }

                eventBus.post(RefreshUserEvent())

                Toasts.show("删除成功")
            }, doDone = {
                hideLoadingAlert()
            }, doFail = {
                Toasts.show("删除失败，${it.msg}")
            })
    }
}