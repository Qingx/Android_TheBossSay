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
import kotlinx.android.synthetic.main.fragment_home_boss_content.*
import kotlinx.android.synthetic.main.fragment_home_boss_content.layout_refresh
import kotlinx.android.synthetic.main.fragment_home_boss_content.rv_content
import kotlinx.android.synthetic.main.item_boss_info.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.data.entity.BossLabelEntity
import net.cd1369.tbs.android.event.FollowBossEvent
import net.cd1369.tbs.android.event.LoginEvent
import net.cd1369.tbs.android.ui.adapter.BossInfoAdapter
import net.cd1369.tbs.android.ui.dialog.FollowAskCancelDialog
import net.cd1369.tbs.android.ui.dialog.FollowChangedDialog
import net.cd1369.tbs.android.ui.home.BossHomeActivity
import net.cd1369.tbs.android.ui.home.HomeBossAllActivity
import net.cd1369.tbs.android.util.JPushHelper
import net.cd1369.tbs.android.util.OnChangeCallback
import net.cd1369.tbs.android.util.doClick
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit
import kotlin.math.max

/**
 * Created by Xiang on 2021/8/11 12:32
 * @description
 * @email Cymbidium@outlook.com
 */
class HomeBossContentFragment : BaseFragment() {
    private lateinit var mLabel: String
    private var needLoading = true

    private var footerView: View? = null

    private lateinit var mAdapter: BossInfoAdapter

    companion object {
        fun createFragment(label: String): HomeBossContentFragment {
            return HomeBossContentFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString("label", label)
                    }
                }
        }
    }

    private val mCall = object : OnChangeCallback() {
        @SuppressLint("SetTextI18n")
        override fun onDataChange() {
            val view = footerView ?: return
            view.tv_end_name.text = "已追踪${mAdapter.data?.size ?: 0}位boss"
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_home_boss_content
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        mLabel = arguments?.getString("label") as String
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        eventBus.register(this)

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData()
        }

        mAdapter = object : BossInfoAdapter() {
            override fun onDoTop(item: BossInfoEntity, v: View, index: Int) {
                tryChangeTopic(item, v, index)
            }

            override fun onCancelFollow(item: BossInfoEntity) {
                FollowAskCancelDialog.showDialog(requireFragmentManager(), "askCancel")
                    .apply {
                        onConfirmClick = FollowAskCancelDialog.OnConfirmClick {
                            doCancelFollow(item, this)
                        }
                    }
            }

            override fun onClick(item: BossInfoEntity) {
                BossHomeActivity.start(mActivity, entity = item)
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

        button_float doClick {
            HomeBossAllActivity.start(mActivity)
        }
    }

    override fun loadData() {
        super.loadData()

        if (needLoading) {
            showLoading()
        }

        TbsApi.boss().obtainFollowBossList(mLabel, false)
            .onErrorReturn { mutableListOf() }
            .bindDefaultSub(doNext = {
                mAdapter.setNewData(it)
            }, doLast = {
                showContent()

                layout_refresh.finishRefresh()
            })
    }

    private fun tryChangeTopic(item: BossInfoEntity, v: View, index: Int) {
        val topic: Boolean = !item.isTop
        showLoadingAlert("正在保存...")


        TbsApi.boss().topicBoss(item.id, topic)
            .delay(600, TimeUnit.MILLISECONDS)
            .bindToastSub("") {
                v.isSelected = topic
                item.top = topic
                v.text_top.text = "取消置顶".takeIf { item.top } ?: "置顶"

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

    private fun doCancelFollow(item: BossInfoEntity, dialog: FollowAskCancelDialog?) {
        showLoadingAlert("尝试取消...")

        TbsApi.boss().obtainCancelFollowBoss(item.id)
            .bindDefaultSub(
                doNext = {
                    dialog?.dismiss()

                    FollowChangedDialog.showDialog(requireFragmentManager(), true, "followChange")

                    UserConfig.get().updateUser {
                        it.traceNum = max((it.traceNum ?: 0) - 1, 0)
                    }

                    eventBus.post(
                        FollowBossEvent(
                            id = item.id,
                            isFollow = false,
                            needLoading = false,
                            labels = item.labels
                        )
                    )
                    val index = mAdapter.data.indexOfFirst {
                        it.id == item.id
                    }

                    JPushHelper.tryDelTag(item.id)

                    if (index != -1) {
                        mAdapter.remove(index)
                    }
                },
                doFail = {
                    Toasts.show("取消失败，${it.msg}")
                },
                doLast = {
                    needLoading = true
                    hideLoadingAlert()
                },
            )
    }

    override fun onDestroyView() {
        mAdapter.unregisterAdapterDataObserver(mCall)

        super.onDestroyView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: FollowBossEvent) {
        if (event.needLoading) {
            if (mLabel == BossLabelEntity.empty.id || event.labels?.contains(mLabel) == true) {
                layout_refresh.autoRefresh()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: LoginEvent) {
        loadData()
    }
}