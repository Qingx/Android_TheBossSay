package net.cd1369.tbs.android.ui.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseFragment
import cn.wl.android.lib.utils.Toasts
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_boss.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.ui.adapter.BossInfoAdapter
import net.cd1369.tbs.android.ui.adapter.HomeTabAdapter
import net.cd1369.tbs.android.util.doClick
import java.util.concurrent.TimeUnit

/**
 * Created by Qing on 2021/6/28 11:44 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class BossFragment : BaseFragment() {
    private lateinit var tabAdapter: HomeTabAdapter
    private lateinit var mAdapter: BossInfoAdapter

    companion object {
        fun createFragment(): BossFragment {
            return BossFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_boss
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        tabAdapter = object : HomeTabAdapter() {
            override fun onSelect(select: Int) {
                showLoading()
                loadData()
            }
        }

        rv_tab.adapter = tabAdapter
        rv_tab.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

        mAdapter = object : BossInfoAdapter() {
            override fun onClick(item: Int) {
                Toasts.show("$item")
            }
        }
        rv_content.adapter = mAdapter
        rv_content.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        button_float doClick {
            Toasts.show("floatButton")
        }
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

                tabAdapter.mSelectId = testData[0]

                tabAdapter.setNewData(testData)
                mAdapter.setNewData(it)
            }
    }
}