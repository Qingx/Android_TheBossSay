package net.cd1369.tbs.android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.core.PageParam
import cn.wl.android.lib.ui.BaseListFragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.fragment_follow.*
import kotlinx.android.synthetic.main.fragment_square.*
import kotlinx.android.synthetic.main.fragment_square.layout_refresh
import kotlinx.android.synthetic.main.fragment_square.rv_content
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.data.entity.BossLabelEntity
import net.cd1369.tbs.android.data.model.FollowVal
import net.cd1369.tbs.android.ui.adapter.HomeTabAdapter
import net.cd1369.tbs.android.ui.adapter.SquareInfoAdapter
import net.cd1369.tbs.android.util.LabelManager

/**
 * Created by Qing on 2021/6/28 11:44 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class SquareFragment : BaseListFragment() {
    private var version: Long = 0L
    private lateinit var tabAdapter: HomeTabAdapter
    private lateinit var mAdapter: SquareInfoAdapter

    private var mSelectTab: String? = null
    private var needLoading = true

    companion object {

        private val mValueCache = hashMapOf<String, FollowVal>()

        fun createFragment(): SquareFragment {
            return SquareFragment()
        }
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : SquareInfoAdapter() {
            override fun onClick(item: ArticleEntity) {
                ArticleActivity.start(mActivity, item.id, item)
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
            override fun onSelect(labelId: String) {
                val followVal = FollowVal(PageParam.copy(pageParam), null, mAdapter.data)
                mValueCache[mSelectTab ?: ""] = followVal

                mSelectTab = labelId

                val value = mValueCache[labelId]

                if (value != null) {
                    val data = value.data
                    val param = value.param

                    mAdapter.setNewData(data)
                    pageParam?.set(param)

                    tryCompleteStatus(true)
                } else {
                    layout_refresh.autoRefresh()
                }
            }
        }

        rv_tab.adapter = tabAdapter
        rv_tab.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
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

        if (needLoading) showLoading()

        LabelManager.obtainLabels()
            .flatMap {
                if (LabelManager.needUpdate(version)) {
                    version = LabelManager.getVersion()

                    tabAdapter.setNewData(DataConfig.get().bossLabels)

                    it.add(0, BossLabelEntity.empty)
                    mSelectTab = it[0].id
                }

                TbsApi.boss().obtainAllArticle(pageParam, mSelectTab)
                    .onErrorReturn {
                        Page.empty()
                    }
            }.bindPageSubscribe(loadMore = loadMore, doNext = {
                if (loadMore) {
                    mAdapter.addData(it)
                } else{
                    mAdapter.setNewData(it)
                }
            }, doDone = {
                showContent()

                layout_refresh.finishRefresh()
            })
    }
}