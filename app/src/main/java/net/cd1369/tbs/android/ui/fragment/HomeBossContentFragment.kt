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
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.db.BossDaoManager
import net.cd1369.tbs.android.data.db.LabelDaoManager
import net.cd1369.tbs.android.data.model.BossSimpleModel
import net.cd1369.tbs.android.data.model.LabelModel
import net.cd1369.tbs.android.event.BossBatchTackEvent
import net.cd1369.tbs.android.event.BossTackEvent
import net.cd1369.tbs.android.event.LoginEvent
import net.cd1369.tbs.android.ui.adapter.BossTackAdapter
import net.cd1369.tbs.android.ui.adapter.HomeTabAdapter
import net.cd1369.tbs.android.ui.dialog.FollowAskCancelDialog
import net.cd1369.tbs.android.ui.dialog.FollowChangedDialog
import net.cd1369.tbs.android.ui.home.BossHomeActivity
import net.cd1369.tbs.android.ui.home.HomeBossAllActivity
import net.cd1369.tbs.android.ui.home.SearchBossActivity
import net.cd1369.tbs.android.ui.test.TestActivity
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
            view.tv_end_name.text = "已追踪${mAdapter.data?.size ?: 0}位boss"
        }
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        mLabels = LabelDaoManager.getInstance(mActivity).findAll()
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

                mBossList = BossDaoManager.getInstance(mActivity).findByLabel(currentLabel)
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
        val topic: Boolean = !item.top
        showLoadingAlert("正在保存...")

        TbsApi.boss().obtainTopicBoss(item.id.toString(), topic)
            .delay(600, TimeUnit.MILLISECONDS)
            .bindToastSub("") {
                v.isSelected = topic
                item.top = topic
                v.text_top.text = "取消置顶".takeIf { item.top } ?: "置顶"

                BossDaoManager.getInstance(mActivity).update(item)

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
        showLoadingAlert("尝试取消...")

        TbsApi.boss().obtainCancelFollowBoss(item.id.toString())
            .bindDefaultSub(
                doNext = {
                    dialog?.dismiss()

                    FollowChangedDialog.showDialog(requireFragmentManager(), true, "followChange")

                    UserConfig.get().updateUser {
                        it.traceNum = max((it.traceNum ?: 0) - 1, 0)
                    }

                    BossDaoManager.getInstance(mActivity).delete(item.id)

                    eventBus.post(
                        BossTackEvent(
                            id = item.id.toString(),
                            isFollow = false,
                            labels = item.labels,
                            fromBossContent = true
                        )
                    )
                    val index = mAdapter.data.indexOfFirst {
                        it.id == item.id
                    }

                    JPushHelper.tryDelTag(item.id.toString())

                    if (index != -1) {
                        mAdapter.remove(index)
                    }
                },
                doFail = {
                    Toasts.show("取消失败，${it.msg}")
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
            mBossList = BossDaoManager.getInstance(mActivity).findByLabel(currentLabel)
            mAdapter.setNewData(mBossList)
        } else {
            TbsApi.boss().obtainFollowBossList(-1, false)
                .onErrorReturn { mutableListOf() }
                .bindDefaultSub(
                    doNext = {
                        BossDaoManager.getInstance(mActivity).insertList(it)

                        mBossList = if (currentLabel == "-1") {
                            it
                        } else {
                            it.filter {
                                it.labels.contains(currentLabel)
                            }.toMutableList()
                        }

                        mAdapter.setNewData(mBossList)

                        layout_refresh.finishRefresh(true)
                    },
                    doFail = {
                        layout_refresh.finishRefresh(false)
                    }
                )
        }
    }

    override fun onDestroyView() {
        mAdapter.unregisterAdapterDataObserver(mCall)

        super.onDestroyView()
    }

    private fun loginData() {
        TbsApi.boss().obtainFollowBossList(-1L, false)
            .onErrorReturn { mutableListOf() }
            .bindDefaultSub {
                mBossList = it
                val list = if (currentLabel == "-1") {
                    BossDaoManager.getInstance(mActivity).insertList(it)
                    it.filter {
                        it.isLatest
                    }.toMutableList()
                } else {
                    it.filter {
                        it.labels.contains(currentLabel) && it.isLatest
                    }.toMutableList()
                }
                mAdapter.setNewData(list)
            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: BossTackEvent) {
        if (!event.fromBossContent) {
            mBossList = BossDaoManager.getInstance(mActivity).findByLabel(currentLabel)
            mAdapter.setNewData(mBossList)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: BossBatchTackEvent) {
        mBossList = BossDaoManager.getInstance(mActivity).findByLabel(currentLabel)
        mAdapter.setNewData(mBossList)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: LoginEvent) {
        loginData()
    }
}