package net.cd1369.tbs.android.ui.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseFragment
import cn.wl.android.lib.ui.BaseListFragment
import cn.wl.android.lib.utils.Toasts
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_follow.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.layout_refresh
import kotlinx.android.synthetic.main.fragment_search.rv_content
import kotlinx.android.synthetic.main.fragment_search.rv_tab
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.ui.adapter.SearchInfoAdapter
import net.cd1369.tbs.android.ui.adapter.SearchTabAdapter
import java.util.concurrent.TimeUnit

/**
 * Created by Qing on 2021/6/30 3:49 下午
 * @description
 * @email Cymbidium@outlook.com
 */
class SearchFragment : BaseListFragment() {
    private lateinit var tabAdapter: SearchTabAdapter
    private lateinit var mAdapter: SearchInfoAdapter
    private var needLoading = true

    companion object {
        fun createFragment(): SearchFragment {
            return SearchFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_search
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData(false)
        }

        tabAdapter = object : SearchTabAdapter() {
            override fun onClick(item: Int) {
                layout_refresh.autoRefresh()
            }
        }

        rv_tab.adapter = tabAdapter
        rv_tab.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        rv_content.adapter = mAdapter
        rv_content.layoutManager =
            object : GridLayoutManager(mActivity, 3, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : SearchInfoAdapter() {
            override fun onClickFollow(item: Int) {
                Toasts.show("$item")
            }
        }.also {
            mAdapter = it
        }
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        if (!loadMore && needLoading) showLoading()

        val testData = mutableListOf(0, 1, 2, 3, 4, 5, 6, 7,8)

        Observable.just(testData.toList())
            .observeOn(AndroidSchedulers.mainThread())
            .delay(2, TimeUnit.SECONDS)
            .bindListSubscribe {
                showContent()
                layout_refresh.finishRefresh()

                tabAdapter.setNewData(it)
                mAdapter.setNewData(it)
//                mAdapter.loadMoreEnd(true)
                needLoading = true
            }
    }

}