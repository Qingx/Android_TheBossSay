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
import cn.wl.android.lib.ui.BaseFragment
import cn.wl.android.lib.utils.Toasts
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.fragment_home_boss_all_add.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.db.BossDaoManager
import net.cd1369.tbs.android.data.db.LabelDaoManager
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.data.entity.OptPicEntity
import net.cd1369.tbs.android.data.model.BossSimpleModel
import net.cd1369.tbs.android.event.BossBatchTackEvent
import net.cd1369.tbs.android.event.BossTackEvent
import net.cd1369.tbs.android.ui.adapter.BossAllItemAdapter
import net.cd1369.tbs.android.ui.adapter.BossAllTabAdapter
import net.cd1369.tbs.android.ui.dialog.FollowAskCancelDialog
import net.cd1369.tbs.android.ui.dialog.FollowAskPushDialog
import net.cd1369.tbs.android.ui.dialog.FollowChangedDialog
import net.cd1369.tbs.android.ui.home.BossHomeActivity
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
class HomeBossAllAddFragment : BaseFragment() {

    private lateinit var tabAdapter: BossAllTabAdapter
    private lateinit var mAdapter: BossAllItemAdapter

    private var mTempBossList: MutableList<BossInfoEntity> = mutableListOf()

    private var mOptPic: OptPicEntity? = null
    private var mSelectTab = "-1"

    private var needLoading = true

    companion object {
        fun createFragment(): HomeBossAllAddFragment {
            return HomeBossAllAddFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_home_boss_all_add
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        eventBus.register(this)

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData()
        }

        tabAdapter = object : BossAllTabAdapter() {
            override fun onClick(item: String) {
                mSelectTab = item
                mAdapter.setNewData(filterBossList())
            }
        }

        rv_tab.adapter = tabAdapter
        rv_tab.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        val labelList = LabelDaoManager.getInstance(mActivity).findAll()
        tabAdapter.setNewData(labelList)

        mAdapter = object : BossAllItemAdapter() {
            override fun onItemClick(item: BossInfoEntity) {
                BossHomeActivity.start(mActivity, item.id)
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
        }
        rv_content.adapter = mAdapter
        rv_content.layoutManager =
            object : GridLayoutManager(mActivity, 3, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        iv_img doClick {
            val optPic = mOptPic

            if (optPic != null) {
                BossHomeActivity.start(mActivity, optPic.entity.id)
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

                val bossList: MutableList<BossSimpleModel> = mTempBossList.filter {
                    idSet.toMutableList().contains(it.id)
                }.toMutableList().map {
                    it.toSimple()
                }.toMutableList()

                BossDaoManager.getInstance(mActivity).insertList(bossList)
                eventBus.post(BossBatchTackEvent())

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
                BossDaoManager.getInstance(mActivity).delete(item.id.toLong())
                eventBus.post(BossTackEvent(item.id, false, item.labels))

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
                BossDaoManager.getInstance(mActivity).insert(item.toSimple())
                eventBus.post(BossTackEvent(item.id, true, item.labels))

                tryUpdateItem(item.id, true)

            }, doFail = {
                Toasts.show("追踪失败，${it.msg}")
            }, doLast = {
                hideLoadingAlert()
            })
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

    override fun loadData() {
        super.loadData()

        if (needLoading) {
            showLoading()
        }

        TbsApi.boss().obtainOptPic().flatMap {
            mOptPic = it

            TbsApi.boss().obtainAllBossList()
        }.bindDefaultSub(doNext = {
            loadImage(mOptPic!!.pictureLocation)

            mTempBossList = it

            mAdapter.setNewData(filterBossList())
            layout_refresh.finishRefresh(true)
        }, doFail = {
            layout_refresh.finishRefresh(false)
        }, doLast = {
            needLoading = true
            showContent()
        })
    }

    /**
     * 过滤数据
     * @return List<BossInfoEntity>
     */
    private fun filterBossList(): MutableList<BossInfoEntity> {
        return if (mSelectTab == "-1") {
            mTempBossList
        } else {
            mTempBossList.filter {
                it.labels.contains(mSelectTab)
            }.toMutableList()
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: BossTackEvent) {
        mAdapter.doFollowChange(event.id, event.isFollow)
    }
}