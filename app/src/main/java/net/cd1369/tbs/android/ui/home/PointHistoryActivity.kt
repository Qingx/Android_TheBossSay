package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.ErrorBean
import cn.wl.android.lib.ui.BaseListActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.activity_mine_history_all.*
import kotlinx.android.synthetic.main.empty_follow_article.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.event.ArticleReadEvent
import net.cd1369.tbs.android.ui.adapter.PointHistoryAdapter
import net.cd1369.tbs.android.util.doClick
import kotlin.math.max

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 14:21 2021/9/8
 * @desc 点赞历史界面
 */
class PointHistoryActivity : BaseListActivity() {

    private lateinit var mAdapter: PointHistoryAdapter
    private var needLoading = true

    companion object {
        fun start(context: Context) {
            val intent = Intent(
                context,
                PointHistoryActivity::class.java
            )
            context.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any = R.layout.activity_point_history

    override fun createAdapter(): BaseQuickAdapter<*, *>? = object : PointHistoryAdapter() {
        override fun onContentClick(articleId: String) {
            ArticleActivity.start(mActivity, articleId)
        }

        override fun onContentDelete(articleId: String, doRemove: (id: String) -> Unit) {
            cancelPointStatus(articleId, doRemove)
        }
    }.also {
        mAdapter = it
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        text_title.text = "点赞记录"

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

        val emptyView = LayoutInflater.from(mActivity)
            .inflate(R.layout.empty_follow_article, null)
        emptyView.text_notice.text = "暂无历史记录"
        mAdapter.emptyView = emptyView

        image_back doClick {
            onBackPressed()
        }
    }

    override fun showEmptyData(bean: ErrorBean) {
//        super.showEmptyData(bean)
    }

    /**
     * 切换点赞状态
     * @param article ArticleEntity
     */
    private fun cancelPointStatus(articleId: String, doRemove: (id: String) -> Unit) {
        showLoadingAlert("正在取消点赞...")

        TbsApi.boss().switchPointStatus(articleId, false)
            .bindToastSub("取消成功") {
                doRemove.invoke(articleId)

                UserConfig.get().updateUser {
                    it.pointNum = max((it.pointNum ?: 0) - 1, 0)
                }
                eventBus.post(ArticleReadEvent(articleId))
            }
    }

    override fun tryFinishRefresh() {
        super.tryFinishRefresh()
        layout_refresh.finishRefresh()
        layout_refresh.finishLoadMore()
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        TbsApi.boss().obtainPoints(pageParam)
            .bindPageSubscribe(loadMore) {
                if (loadMore) {
                    mAdapter.addData(it)
                } else {
                    mAdapter.setNewData(it)
                }
            }
    }

}