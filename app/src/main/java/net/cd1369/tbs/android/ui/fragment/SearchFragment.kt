package net.cd1369.tbs.android.ui.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.core.PageParam
import cn.wl.android.lib.ui.BaseListFragment
import cn.wl.android.lib.utils.Toasts
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.fragment_search.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.data.entity.OptPicEntity
import net.cd1369.tbs.android.event.FollowBossEvent
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.ui.adapter.SearchInfoAdapter
import net.cd1369.tbs.android.ui.adapter.SearchTabAdapter
import net.cd1369.tbs.android.ui.dialog.*
import net.cd1369.tbs.android.ui.home.BossHomeActivity
import net.cd1369.tbs.android.ui.home.BossInfoActivity
import net.cd1369.tbs.android.util.JPushHelper
import net.cd1369.tbs.android.util.doClick
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.max


/**
 * Created by Qing on 2021/6/30 3:49 下午
 * @description
 * @email Cymbidium@outlook.com
 */
class SearchFragment : BaseListFragment() {

    private lateinit var tabAdapter: SearchTabAdapter
    private lateinit var mAdapter: SearchInfoAdapter

    private var mTempBossList: List<BossInfoEntity> = mutableListOf()

    private var mOptPic: OptPicEntity? = null
    private var version: Long = 0L
    private var mSelectTab = ""

    companion object {
        fun createFragment(): SearchFragment {
            return SearchFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_search
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        eventBus.register(this)

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            loadData(false)
        }

        tabAdapter = object : SearchTabAdapter() {
            override fun onClick(item: String) {
                mSelectTab = item

                var filterBossList = filterBossList(mTempBossList)
                mAdapter.setNewData(filterBossList)
                mAdapter.loadMoreEnd(getLoadGone())
            }
        }

        rv_tab.adapter = tabAdapter
        rv_tab.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        rv_content.layoutManager =
            object : GridLayoutManager(mActivity, 3, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        iv_img doClick {
            var optPic = mOptPic

            if (optPic != null) {
                BossInfoActivity.start(mActivity, optPic.entity)
            }
        }

        tv_action_open doClick {
            if (!it.isSelected) {
                switchActionShow(it)
            }
        }

        tv_action_goto doClick {
            tryFollowAllSelect()

//            switchActionShow(tv_action_open)
        }

        tv_action_cancel doClick {
            switchActionShow(tv_action_open)
        }
    }

    /**
     * 尝试关注所有选中的boss
     */
    private fun tryFollowAllSelect() {
        val idSet = mAdapter.mIdSet

        if (idSet.isNullOrEmpty()) {
            Toasts.show("请选择关注的boss")
            return
        }
        showLoadingAlert("正在追踪...")

        TbsApi.boss().obtainGuideFollow(idSet.toList())
            .doOnNext {
                changeBossCollectStatus(mTempBossList, idSet)
                changeBossCollectStatus(mAdapter.data, idSet)
            }
            .bindDefaultSub {
                switchActionShow(tv_action_open)
                mAdapter.notifyDataSetChanged()

                UserConfig.get().updateUser {
                    it.traceNum = max((it.traceNum ?: 0) + idSet.size, 0)
                }
                eventBus.post(RefreshUserEvent())
                eventBus.post(FollowBossEvent(needLoading = true))

                JPushHelper.tryAddAllTag(idSet)
            }
    }

    private fun changeBossCollectStatus(list: List<BossInfoEntity>, idSet: HashSet<String>) {
        if (list.isEmpty()) return
        if (idSet.isEmpty()) return

        for (entity in list) {
            if (entity.id in idSet) {
                entity.isCollect = true
            }
        }
    }

    private fun switchActionShow(openView: View) {
        val target = !openView.isSelected
        openView.isSelected = target

        TransitionManager.beginDelayedTransition(ll_action)

        ll_action_root.isVisible = target

        mAdapter.isEdit = target
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : SearchInfoAdapter() {
            override fun onItemClick(item: BossInfoEntity) {
                BossHomeActivity.start(mActivity, item)
            }

            override fun onClickFollow(item: BossInfoEntity) {
                if (item.isCollect) {
                    FollowAskCancelDialog.showDialog(requireFragmentManager(), "askCancel")
                        .apply {
                            onConfirmClick = FollowAskCancelDialog.OnConfirmClick {
                                cancelFollow(item, this)
                            }
                        }
                } else followBoss(item)
            }
        }.also {
            mAdapter = it
        }
    }

    /**
     * 取消追踪boss
     */
    private fun cancelFollow(item: BossInfoEntity, dialog: FollowAskCancelDialog?) {
        showLoadingAlert("尝试取消...")

        TbsApi.boss().obtainCancelFollowBoss(item.id)
            .bindDefaultSub(doNext = {
                dialog?.dismiss()

                FollowChangedDialog.showDialog(requireFragmentManager(), true, "followChange")

                mAdapter.doFollowChange(item.id, false)

                UserConfig.get().updateUser {
                    it.traceNum = max((it.traceNum ?: 0) - 1, 0)
                }

                eventBus.post(
                    FollowBossEvent(
                        id = item.id,
                        isFollow = false,
                        needLoading = true,
                        labels = item.labels
                    )
                )

                JPushHelper.tryDelTag(item.id)
                tryUpdateItem(item.id, false)

            }, doFail = {
                Toasts.show("取消失败，${it.msg}")
            }, doLast = {
                hideLoadingAlert()
            })
    }

    /**
     * 追踪boss
     */
    private fun followBoss(item: BossInfoEntity) {
        showLoadingAlert("尝试追踪...")

        TbsApi.boss().obtainFollowBoss(item.id)
            .bindDefaultSub(doNext = {
                FollowAskPushDialog.showDialog(requireFragmentManager(), "askPush")
                    .apply {
                        onConfirmClick = FollowAskPushDialog.OnConfirmClick {
                            JPushHelper.tryAddTag(item.id)

                            this.dismiss()

                            FollowChangedDialog.showDialog(
                                requireFragmentManager(),
                                false,
                                "followChange"
                            )
                        }
                        onCancelClick = FollowAskPushDialog.OnCancelClick {
                            this.dismiss()

                            FollowChangedDialog.showDialog(
                                requireFragmentManager(),
                                false,
                                "followChange"
                            )
                        }
                    }

                UserConfig.get().updateUser {
                    it.traceNum = max((it.traceNum ?: 0) + 1, 0)
                }

                mAdapter.doFollowChange(item.id, true)
                tryUpdateItem(item.id, true)

                eventBus.post(
                    FollowBossEvent(
                        id = item.id,
                        isFollow = true,
                        needLoading = true,
                        labels = item.labels
                    )
                )
            }, doFail = {
                Toasts.show("追踪失败，${it.msg}")
            }, doLast = {
                hideLoadingAlert()
            })
    }

    private fun loadOptPic() {
        val optPic = mOptPic

        if (optPic != null) {
            return
        }

        TbsApi.boss().obtainOptPic()
            .bindDefaultSub {
                mOptPic = it

                loadImage(it.pictureLocation)
            }
    }

    private fun loadImage(location: String?) {
        Glide.with(mActivity)
            .asBitmap()
            .load(location ?: R.mipmap.test_banner)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val width = resource.width
                    val height = resource.height

                    val rootW = ll_ad.width
                    val rootH = rootW * (height.toFloat() / width)
                    iv_img.updateLayoutParams<LinearLayoutCompat.LayoutParams> {
                        this.height = rootH.toInt()
                    }

                    iv_img.setImageBitmap(resource)
                }
            })
    }

    override fun createPageParam(): PageParam {
        return super.createPageParam().apply {
            this.size = Int.MAX_VALUE
        }
    }

    override fun loadData(loadMore: Boolean) {
//        super.loadData(loadMore)
        val pageParam = pageParam
        if (!loadMore) {
            pageParam?.resetPage()

            if (mTempBossList.isNullOrEmpty()) {
                showLoading()
            }
        }

        loadOptPic()

        tabAdapter.setNewData(DataConfig.get().bossLabels)

        mSelectTab = DataConfig.get().bossLabels[0].id

        TbsApi.boss().obtainAllBossList(pageParam, "-1")
            .onErrorReturn { Page.empty() }
            .bindPageSubscribe(loadMore = loadMore, doNext = {
                mTempBossList = it

                val filterBossList = filterBossList(it)
                mAdapter.setNewData(filterBossList)
            }, doDone = {
                showContent()

                layout_refresh.finishRefresh()
            })
    }

    /**
     * 过滤数据
     * @param list List<BossInfoEntity>
     * @return List<BossInfoEntity>
     */
    private fun filterBossList(list: List<BossInfoEntity>): List<BossInfoEntity> {
        if (list.isEmpty()) return list

        var selectTab = mSelectTab

        if (selectTab.isNullOrEmpty()) {
            selectTab = "-1"
        }

        return list.toMutableList().filter {
            return@filter "-1" == selectTab || it.checkLabels(selectTab)
        }
    }

    /**
     * 尝试刷新缓存数据
     * @param id String
     * @param target Boolean
     */
    private fun tryUpdateItem(id: String, target: Boolean) {
        if (id.isNullOrEmpty()) return

        val list = mTempBossList

        if (!list.isNullOrEmpty()) {
            list.firstOrNull {
                id == it.id
            }?.let {
                it.isCollect = target
            }
        }
    }

    override fun getLoadGone(): Boolean = true

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: FollowBossEvent) {
        mAdapter.doFollowChange(event.id!!, event.isFollow)
    }

    override fun onLoadMoreRequested() {
        timerDelay(100) {
            tryCompleteStatus(false)
        }
    }

}