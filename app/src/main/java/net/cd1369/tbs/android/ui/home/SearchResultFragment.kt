package net.cd1369.tbs.android.ui.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseFragment
import cn.wl.android.lib.utils.SpanUtils
import cn.wl.android.lib.utils.Toasts
import com.blankj.utilcode.util.ColorUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_search_result.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.event.SearchEvent
import net.cd1369.tbs.android.ui.adapter.SearchInfoAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

/**
 * Created by Qing on 2021/6/30 3:50 下午
 * @description
 * @email Cymbidium@outlook.com
 */
class SearchResultFragment : BaseFragment() {
    private lateinit var mAdapter: SearchInfoAdapter

    companion object {
        fun createFragment(): SearchResultFragment {
            return SearchResultFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_search_result
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        eventBus.register(this)

        text_num.text = SpanUtils.getBuilder("共找到")
            .setForegroundColor(ColorUtils.getColor(R.color.colorTextDark))
            .append(" 8 ")
            .setForegroundColor(ColorUtils.getColor(R.color.colorAccent))
            .append("条相关")
            .setForegroundColor(ColorUtils.getColor(R.color.colorTextDark))
            .create()

        mAdapter = object : SearchInfoAdapter() {
            override fun onClickFollow(item: Int) {
                Toasts.show("$item")
            }
        }

        rv_content.adapter = mAdapter

        rv_content.layoutManager =
            object : GridLayoutManager(mActivity, 4, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: SearchEvent) {
        loadData()
    }

    override fun loadData() {
        super.loadData()

        showLoading()

        val testData = mutableListOf(0, 1, 2, 3, 4, 5, 6, 7)

        Observable.just(testData)
            .observeOn(AndroidSchedulers.mainThread())
            .delay(2, TimeUnit.SECONDS)
            .bindDefaultSub {
                showContent()

                mAdapter.setNewData(it)
            }
    }
}