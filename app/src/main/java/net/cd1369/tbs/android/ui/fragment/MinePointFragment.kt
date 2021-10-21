package net.cd1369.tbs.android.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseFragment
import cn.wl.android.lib.ui.BaseListFragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.activity_mine_history_all.*
import kotlinx.android.synthetic.main.empty_follow_article.view.*
import kotlinx.android.synthetic.main.fragment_speech_tack_content.*
import kotlinx.android.synthetic.main.fragment_speech_tack_content.layout_refresh
import kotlinx.android.synthetic.main.fragment_speech_tack_content.rv_content
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.HisFavEntity
import net.cd1369.tbs.android.event.ArticlePointEvent
import net.cd1369.tbs.android.ui.adapter.MinePointAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Xiang on 2021/10/21 17:02
 * @description
 * @email Cymbidium@outlook.com
 */
class MinePointFragment : BaseListFragment() {
    private lateinit var type: String
    private lateinit var mAdapter: MinePointAdapter
    private var needLoading = true

    companion object {
        fun createFragment(type: String): MinePointFragment {
            return MinePointFragment().apply {
                arguments = Bundle().apply {
                    putString("type", type)
                }
            }
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_speech_tack_content
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        type = arguments?.getString("type") as String
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : MinePointAdapter() {
            override fun onContentClick(entity: HisFavEntity) {

            }

            override fun onContentDelete(entity: HisFavEntity, doRemove: (id: String) -> Unit) {
            }
        }.also {
            mAdapter = it
        }
    }

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

        val emptyView = LayoutInflater.from(mActivity)
            .inflate(R.layout.empty_follow_article, null)
        emptyView.text_notice.text = "暂无历史记录"
        mAdapter.emptyView = emptyView
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        if (!loadMore && needLoading) {
            showLoading()
        }

        TbsApi.user().obtainPointList(pageParam, type)
            .bindPageSubscribe(loadMore) {
                if (loadMore) {
                    mAdapter.addData(it)
                } else {
                    mAdapter.setNewData(it)
                }
            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: ArticlePointEvent) {
        if (!event.fromHistory && type == "1") {
            if (event.doPoint) {
                layout_refresh.autoRefresh()
            } else {
                val index = mAdapter.data.indexOfFirst {
                    it.articleId == event.id
                }

                if (index != -1) {
                    mAdapter.remove(index)
                    mAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}