package net.cd1369.tbs.android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.PageParam
import cn.wl.android.lib.ui.BaseFragment
import cn.wl.android.lib.utils.Toasts
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.activity_boss_home.*
import kotlinx.android.synthetic.main.fragment_boss.*
import kotlinx.android.synthetic.main.fragment_boss.layout_refresh
import kotlinx.android.synthetic.main.fragment_boss.rv_content
import kotlinx.android.synthetic.main.fragment_boss.rv_tab
import kotlinx.android.synthetic.main.fragment_square.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.database.BossLabelDaoManager
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.data.entity.BossLabelEntity
import net.cd1369.tbs.android.data.model.FollowVal
import net.cd1369.tbs.android.event.FollowBossEvent
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.ui.adapter.BossInfoAdapter
import net.cd1369.tbs.android.ui.adapter.HomeTabAdapter
import net.cd1369.tbs.android.util.LabelManager
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

    private var version = 0L
    private var mSelectTab: String? = null
    private var needLoading = true

    companion object {

        private val mValueCache = hashMapOf<String, FollowVal>()

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
            override fun onSelect(labelId: String) {
                val followVal = FollowVal(null, mAdapter.data, null)
                mValueCache[mSelectTab ?: ""] = followVal

                mSelectTab = labelId

                val value = mValueCache[labelId]

                if (value != null) {
                    val data = value.boss

                    mAdapter.setNewData(data)
                } else {
                    layout_refresh.autoRefresh()
                }
            }
        }

        mAdapter = object : BossInfoAdapter() {
            override fun onDoTop(item: BossInfoEntity) {
                val index = mData.indexOfFirst {
                    it.id == item.id
                }

                if (index != -1) {
                    mData.removeAt(index)
                    mData.add(0, item)
                    notifyDataSetChanged()
                }
            }

            override fun onCancelFollow(item: BossInfoEntity) {
                doCancelFollow(item.id)
            }

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

        val emptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_boss_view, null)
        mAdapter.emptyView = emptyView

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

        LabelManager.obtainLabels()
            .flatMap {
                if (LabelManager.needUpdate(version)) {
                    version = LabelManager.getVersion()

                    it.add(0, BossLabelEntity.empty)
                    tabAdapter.setNewData(it)

                    mSelectTab = it[0].id
                }

                TbsApi.boss().obtainFollowBossList(mSelectTab, false)
                    .onErrorReturn {
                        mutableListOf()
                    }
            }.bindDefaultSub(doNext = {
                mAdapter.setNewData(it)
            }, doLast = {
                showContent()

                layout_refresh.finishRefresh()
            })
    }

    private fun doCancelFollow(id: String) {
        showLoadingAlert("尝试取消...")

        TbsApi.boss().obtainCancelFollowBoss(id)
            .bindDefaultSub(doNext = {
                eventBus.post(RefreshUserEvent())
                eventBus.post(FollowBossEvent(id, false, needLoading = false))
                val index = mAdapter.data.indexOfFirst {
                    it.id == id
                }

                if (index != -1) {
                    mAdapter.remove(index)
                }
            }, doFail = {
                Toasts.show("取消失败，${it.msg}")
            }, doLast = {
                hideLoadingAlert()
            })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: FollowBossEvent) {
        if (event.needLoading) {
            loadData()
        }
    }
}