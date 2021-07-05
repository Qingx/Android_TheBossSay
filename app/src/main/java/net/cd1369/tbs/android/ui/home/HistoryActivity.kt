package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseListActivity
import cn.wl.android.lib.utils.Toasts
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_history.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.ui.adapter.HistoryContentAdapter
import net.cd1369.tbs.android.util.doClick
import java.util.concurrent.TimeUnit

class HistoryActivity : BaseListActivity() {
    private lateinit var mAdapter: HistoryContentAdapter
    private var needLoading = true

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, HistoryActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_history
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData()
        }

        rv_content.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        rv_content.adapter = mAdapter

        image_back doClick {
            onBackPressed()
        }
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : HistoryContentAdapter() {
            override fun onDeleteClick(item: Int, doRemove: (item: Int) -> Unit) {
                doRemove.invoke(item)
            }

            override fun onItemClick(item: Int) {
                Toasts.show(item)
            }
        }.also {
            mAdapter = it
        }
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        if (!loadMore) {
            pageParam?.resetPage()
        }

        if (needLoading && !loadMore) showLoading()


        val testData = mutableListOf(0, 1, 2, 3, 4, 5, 6, 7)

        Observable.just(testData.toList())
            .observeOn(AndroidSchedulers.mainThread())
            .delay(2, TimeUnit.SECONDS)
            .bindListSubscribe {
                showContent()
                layout_refresh.finishRefresh()

                mAdapter.setNewData(it)
                needLoading = true
            }
    }
}