package net.cd1369.tbs.android.ui.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseFragment
import cn.wl.android.lib.utils.Toasts
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.fragment_boss.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.data.entity.BossLabelEntity
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.ui.adapter.BossInfoAdapter
import net.cd1369.tbs.android.ui.adapter.HomeTabAdapter
import net.cd1369.tbs.android.util.doClick
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Qing on 2021/6/28 11:44 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class BossFragment : BaseFragment() {
    private lateinit var tabAdapter: HomeTabAdapter
    private lateinit var mAdapter: BossInfoAdapter

    private var mSelectTab: String? = null
    private var needLoading = true

    companion object {
        fun createFragment(): BossFragment {
            return BossFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_boss
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        eventBus.register(this)

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData()
        }

        tabAdapter = object : HomeTabAdapter() {
            override fun onSelect(select: String) {
                mSelectTab = select
                layout_refresh.autoRefresh()
            }
        }

        mAdapter = object : BossInfoAdapter() {
            override fun onClick(item: BossInfoEntity) {
                BossHomeActivity.start(mActivity, entity = item)
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

        button_float doClick {
            SearchActivity.start(mActivity)
        }

        image_search doClick {
            SearchBossActivity.start(mActivity)
        }
    }

    override fun loadData() {
        super.loadData()

        if (needLoading) showLoading()

        if (DataConfig.get().bossLabels.isNullOrEmpty()) {
            TbsApi.boss().obtainBossLabels()
                .flatMap {
                    it.add(0, BossLabelEntity.empty)
                    mSelectTab = it[0].id
                    DataConfig.get().bossLabels = it

                    TbsApi.boss().obtainFollowBossList(mSelectTab, false)
                }.bindDefaultSub(doNext = {
                    tabAdapter.setNewData(DataConfig.get().bossLabels)
                    mAdapter.setNewData(it)
                }, doLast = {
                    showContent()

                    layout_refresh.finishRefresh()
                })
        } else {
            TbsApi.boss().obtainFollowBossList(mSelectTab, false)
                .onErrorReturn { mutableListOf() }
                .bindDefaultSub(doNext = {
                    tabAdapter.setNewData(DataConfig.get().bossLabels)
                    mAdapter.setNewData(it)
                }, doLast = {
                    showContent()

                    layout_refresh.finishRefresh()
                })
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: RefreshUserEvent) {
        loadData()
    }
}