package net.cd1369.tbs.android.ui.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.ui.BaseListFragment
import cn.wl.android.lib.utils.Toasts
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.fragment_square.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.data.entity.BossLabelEntity
import net.cd1369.tbs.android.ui.adapter.FollowInfoAdapter
import net.cd1369.tbs.android.ui.adapter.HomeTabAdapter
import net.cd1369.tbs.android.ui.adapter.SquareInfoAdapter

/**
 * Created by Qing on 2021/6/28 11:44 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class SquareFragment : BaseListFragment() {
    private lateinit var tabAdapter: HomeTabAdapter
    private lateinit var mAdapter: SquareInfoAdapter

    private var mSelectTab: String? = null
    private var needLoading = true

    companion object {
        fun createFragment(): SquareFragment {
            return SquareFragment()
        }
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : SquareInfoAdapter() {
            override fun onClick(item: ArticleEntity) {
                Toasts.show(item.id)
            }
        }.also {
            mAdapter = it
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_square
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData(false)
        }

        tabAdapter = object : HomeTabAdapter() {
            override fun onSelect(select: String) {
                layout_refresh.autoRefresh()
            }
        }

        rv_tab.adapter = tabAdapter
        rv_tab.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

        rv_content.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        if (!loadMore) {
            pageParam?.resetPage()

            if (needLoading) showLoading()

            if (DataConfig.get().getBossLabels().isNullOrEmpty()) {
                TbsApi.boss().obtainBossLabels()
                    .flatMap {
                        it.add(0, BossLabelEntity.empty)
                        mSelectTab = it[0].id
                        DataConfig.get().bossLabels = it

                        TbsApi.boss().obtainAllArticle(pageParam, mSelectTab)
                            .onErrorReturn {
                                Page.empty()
                            }
                    }.bindPageSubscribe(loadMore = loadMore, doNext = {
                        tabAdapter.setNewData(DataConfig.get().bossLabels)

                        mAdapter.setNewData(it)
                    }, doDone = {
                        showContent()

                        layout_refresh.finishRefresh()
                    })
            } else {
                TbsApi.boss().obtainAllArticle(pageParam, mSelectTab)
                    .onErrorReturn {
                        Page.empty()
                    }.bindPageSubscribe(loadMore = loadMore, doNext = {
                        tabAdapter.setNewData(DataConfig.get().bossLabels)

                        mAdapter.setNewData(it)
                    }, doDone = {
                        showContent()

                        layout_refresh.finishRefresh()
                    })
            }
        } else {
            TbsApi.boss().obtainAllArticle(pageParam, mSelectTab)
                .onErrorReturn {
                    Page.empty()
                }.bindPageSubscribe(loadMore = loadMore, doNext = {
                    mAdapter.addData(it)
                }, doDone = {
                    layout_refresh.finishLoadMore()
                })
        }
    }
}