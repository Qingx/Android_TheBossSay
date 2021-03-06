package net.cd1369.tbs.android.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseFragment
import cn.wl.android.lib.utils.Toasts
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.footer_count.view.*
import kotlinx.android.synthetic.main.fragment_home_boss.*
import kotlinx.android.synthetic.main.item_boss_info.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.PageItem
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.cache.CacheConfig
import net.cd1369.tbs.android.data.model.BossSimpleModel
import net.cd1369.tbs.android.data.model.LabelModel
import net.cd1369.tbs.android.event.*
import net.cd1369.tbs.android.ui.adapter.BossTackAdapter
import net.cd1369.tbs.android.ui.adapter.HomeTabAdapter
import net.cd1369.tbs.android.ui.dialog.FollowAskCancelDialog
import net.cd1369.tbs.android.ui.dialog.FollowChangedDialog
import net.cd1369.tbs.android.ui.home.BossHomeActivity
import net.cd1369.tbs.android.ui.home.HomeBossAllActivity
import net.cd1369.tbs.android.ui.home.SearchBossActivity
import net.cd1369.tbs.android.util.JPushHelper
import net.cd1369.tbs.android.util.OnChangeCallback
import net.cd1369.tbs.android.util.doClick
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit
import kotlin.math.max

/**
 * Created by Xiang on 2021/8/11 12:24
 * @description
 * @email Cymbidium@outlook.com
 */
class HomeBossContentFragment : BaseFragment() {
    private lateinit var tabAdapter: HomeTabAdapter
    private lateinit var mLabels: MutableList<LabelModel>
    private lateinit var mBossList: MutableList<BossSimpleModel>

    private lateinit var mAdapter: BossTackAdapter
    private var currentLabel = "-1"

    private var footerView: View? = null

    private var firstInit = true

    companion object {
        fun createFragment(): HomeBossContentFragment {
            return HomeBossContentFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_home_boss
    }

    private val mCall = object : OnChangeCallback() {
        @SuppressLint("SetTextI18n")
        override fun onDataChange() {
            val view = footerView ?: return
            view.tv_end_name.text = "?????????${mAdapter.data?.size ?: 0}???boss"
        }
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        mLabels = CacheConfig.getAllLabel()
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        eventBus.register(this)

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            loadData()
        }

        text_title.paint.isFakeBoldText = true

        rv_tab.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false) {
                override fun canScrollVertically() = false
            }

        tabAdapter = object : HomeTabAdapter() {
            override fun onSelect(select: String) {
                currentLabel = select

                mBossList = CacheConfig.getBossByLabel(currentLabel)
                mAdapter.setNewData(mBossList)
            }
        }

        rv_tab.adapter = tabAdapter
        tabAdapter.setNewData(mLabels)

        mAdapter = object : BossTackAdapter() {
            override fun onDoTop(item: BossSimpleModel, v: View, index: Int) {
                tryChangeTopic(item, v, index)
            }

            override fun onCancelFollow(item: BossSimpleModel) {
                FollowAskCancelDialog.showDialog(requireFragmentManager(), "askCancel")
                    .apply {
                        onConfirmClick = FollowAskCancelDialog.OnConfirmClick {
                            doCancelFollow(item, this)
                        }
                    }
            }

            override fun onClick(item: BossSimpleModel) {
                BossHomeActivity.start(mActivity, item.id.toString())
            }
        }
        mAdapter.registerAdapterDataObserver(mCall)

        rv_content.adapter = mAdapter
        rv_content.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        val emptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_boss_view, null)
        mAdapter.emptyView = emptyView

        footerView = LayoutInflater.from(mActivity).inflate(R.layout.footer_count, null)
        mAdapter.addFooterView(footerView)

        image_search doClick {
            SearchBossActivity.start(mActivity)
        }

        button_float doClick {
            HomeBossAllActivity.start(mActivity)
        }
    }

    private fun tryChangeTopic(item: BossSimpleModel, v: View, index: Int) {
        val topic: Boolean = !item.isTop
        showLoadingAlert("????????????...")

        TbsApi.boss().obtainTopicBoss(item.id.toString(), topic)
            .delay(600, TimeUnit.MILLISECONDS)
            .bindToastSub("") {
                v.isSelected = topic
                item.isTop = topic
                v.text_top.text = "????????????".takeIf { item.isTop } ?: "??????"

                CacheConfig.updateBoss(item)

                val layoutManager = rv_content.layoutManager as LinearLayoutManager
                val fp = layoutManager.findFirstVisibleItemPosition()
                val lp = layoutManager.findLastVisibleItemPosition()

                val taIndex = mAdapter.notifyTopic(item)

                try {
                    if (topic) {
                        if (taIndex in fp..lp) {
                            rv_content.scrollToPosition(fp)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

    private fun doCancelFollow(item: BossSimpleModel, dialog: FollowAskCancelDialog?) {
        showLoadingAlert("????????????...")

        TbsApi.boss().obtainCancelFollowBoss(item.id)
            .bindDefaultSub(
                doNext = {
                    dialog?.dismiss()

                    FollowChangedDialog.showDialog(requireFragmentManager(), true, "followChange")

                    UserConfig.get().updateUser {
                        it.traceNum = max((it.traceNum ?: 0) - 1, 0)
                    }

                    val index = mAdapter.data.indexOfFirst {
                        it.id == item.id
                    }

                    if (index != -1) {
                        mAdapter.remove(index)
                    }

                    CacheConfig.deleteBoss(item.id)
                    JPushHelper.tryDelTag(item.id.toString())

                    eventBus.post(BossTackEvent(item.id.toString(), false, item.labels, true))
                },
                doFail = {
                    Toasts.show("???????????????${it.msg}")
                },
                doLast = {
                    hideLoadingAlert()
                },
            )
    }

    override fun loadData() {
        super.loadData()

        if (firstInit) {
            firstInit = false
            mBossList = CacheConfig.getBossByLabel(currentLabel)
            mAdapter.setNewData(mBossList)
        } else {
            TbsApi.boss().obtainFollowBossList("-1", false)
                .onErrorReturn { mutableListOf() }
                .bindDefaultSub {
                    CacheConfig.insertBossList(it)

                    mBossList = if (currentLabel == "-1") {
                        it
                    } else {
                        it.filter {
                            it.labels.contains(currentLabel)
                        }.toMutableList()
                    }
                    mBossList.sort()

                    mAdapter.setNewData(mBossList)

                    layout_refresh.finishRefresh()
                }
        }
    }

    override fun onDestroyView() {
        mAdapter.unregisterAdapterDataObserver(mCall)

        super.onDestroyView()
    }

    private fun loginData() {
        TbsApi.boss().obtainFollowBossList("-1", false)
            .onErrorReturn { mutableListOf() }
            .bindDefaultSub {
                CacheConfig.insertBossList(it)

                mBossList = it
                if (currentLabel != "-1") {
                    mBossList = it.filter {
                        it.labels.contains(currentLabel)
                    }.toMutableList()
                }
                mBossList.sort()
                mAdapter.setNewData(mBossList)
            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: BossTackEvent) {
        if (!event.fromBossContent) {
            mBossList = CacheConfig.getBossByLabel(currentLabel)
            mAdapter.setNewData(mBossList)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: BossBatchTackEvent) {
        mBossList = CacheConfig.getBossByLabel(currentLabel)
        mAdapter.setNewData(mBossList)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: LoginEvent) {
        loginData()
    }
}