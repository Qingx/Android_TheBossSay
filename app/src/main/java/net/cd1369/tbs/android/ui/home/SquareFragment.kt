package net.cd1369.tbs.android.ui.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseListFragment
import cn.wl.android.lib.utils.Toasts
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_square.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.TestMultiEntity
import net.cd1369.tbs.android.ui.adapter.HomeTabAdapter
import net.cd1369.tbs.android.ui.adapter.SquareInfoAdapter
import java.util.concurrent.TimeUnit

/**
 * Created by Qing on 2021/6/28 11:44 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class SquareFragment : BaseListFragment() {
    private lateinit var tabAdapter: HomeTabAdapter
    private lateinit var mAdapter: SquareInfoAdapter
    private var needLoading = true

    companion object {
        fun createFragment(): SquareFragment {
            return SquareFragment()
        }
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : SquareInfoAdapter() {
            override fun onClick(item: TestMultiEntity) {
                Toasts.show("${item.content}")
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
            override fun onSelect(select: Int) {
                showLoading()
                loadData(false)
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

        if (!loadMore && needLoading) {
            showLoading()
        }

        val testData = mutableListOf(0, 1, 2, 3, 4, 5, 6, 7)
        val multiData = testData.map {
            TestMultiEntity(it)
        }

        Observable.just(multiData)
            .observeOn(AndroidSchedulers.mainThread())
            .delay(2, TimeUnit.SECONDS)
            .bindListSubscribe {
                showContent()
                layout_refresh.finishRefresh()

                tabAdapter.mSelectId = testData[0]

                tabAdapter.setNewData(testData)
                mAdapter.setNewData(it)
                needLoading = true
            }
    }
}