package net.cd1369.tbs.android.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.ui.BaseListFragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.fragment_speech_tack_content.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.ui.adapter.SquareInfoAdapter
import net.cd1369.tbs.android.ui.home.ArticleActivity

/**
 * Created by Xiang on 2021/8/11 12:06
 * @description
 * @email Cymbidium@outlook.com
 */
class SpeechSquareContentFragment : BaseListFragment() {
    private lateinit var mLabel: String
    private var needLoading = true

    private lateinit var mAdapter: SquareInfoAdapter

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
        return object : SquareInfoAdapter() {
            override fun onClick(item: ArticleEntity) {
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

        val emptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_follow_article, null)
        mAdapter.emptyView = emptyView
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        if (!loadMore) {
            pageParam?.resetPage()

            if (needLoading) showLoading()
        }

        TbsApi.boss().obtainAllArticle(pageParam, mLabel)
            .onErrorReturn { Page.empty() }
            .bindPageSubscribe(
                loadMore = loadMore,
                doNext = {
                    if (loadMore) {
                        mAdapter.addData(it)
                    } else {
                        mAdapter.setNewData(it)
                    }
                },
                doDone = {
                    needLoading = true
                    showContent()

                    if (loadMore) {
                        layout_refresh.finishLoadMore()
                    } else {
                        layout_refresh.finishRefresh()
                    }
                }
            )
    }
}