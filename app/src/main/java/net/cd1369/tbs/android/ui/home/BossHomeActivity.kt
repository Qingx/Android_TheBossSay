package net.cd1369.tbs.android.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.ui.BaseListActivity
import cn.wl.android.lib.utils.GlideApp
import cn.wl.android.lib.utils.Toasts
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.activity_boss_home.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.Const
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.event.FollowBossEvent
import net.cd1369.tbs.android.event.SetBossTimeEvent
import net.cd1369.tbs.android.ui.adapter.BossArticleAdapter
import net.cd1369.tbs.android.ui.dialog.*
import net.cd1369.tbs.android.util.*
import net.cd1369.tbs.android.util.Tools.formatCount
import kotlin.math.max

class BossHomeActivity : BaseListActivity() {

    private var showDialog: FollowCancelDialog? = null
    private lateinit var mAdapter: BossArticleAdapter
    private var needLoading = true
    private lateinit var entity: BossInfoEntity

    companion object {
        fun start(context: Context?, entity: BossInfoEntity) {
            val intent = Intent(context, BossHomeActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtras(Bundle().apply {
                        putSerializable("entity", entity)
                    })
                }
            context!!.startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        DataConfig.get().setBossTime(entity.id)
        eventBus.post(SetBossTimeEvent(entity.id))
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_boss_home
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        entity = intent.getSerializableExtra("entity") as BossInfoEntity
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        setUserInfo()

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData(false)
        }

        rv_content.adapter = mAdapter
        rv_content.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        val emptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_follow_article, null)
        mAdapter.emptyView = emptyView

        text_follow doClick {
            if (entity.isCollect) {
                FollowAskCancelDialog.showDialog(supportFragmentManager, "askCancel")
                    .apply {
                        onConfirmClick = FollowAskCancelDialog.OnConfirmClick {
                            cancelFollow(this)
                        }
                    }
            } else followBoss()
        }

        image_back doClick {
            onBackPressed()
        }

        text_content doClick {
            BossInfoActivity.start(mActivity, entity)
        }

        image_share doClick {
            onShare()
        }

        image_setting doClick {
            BossSettingDialog.showDialog(supportFragmentManager, "bossSetting")
                .apply {
                    onConfirm = Runnable {
                        JPushHelper.tryAddTag(entity.id)
                        dialog?.dismiss()
                    }
                }
        }
    }

    private fun onShare() {
        ShareDialog.showDialog(supportFragmentManager, "shareDialog")
            .apply {
                onSession = Runnable {
                    doShareSession(resources)
                }
                onTimeline = Runnable {
                    doShareTimeline(resources)
                }
                onCopyLink = Runnable {
                    Tools.copyText(mActivity, Const.SHARE_URL)
                }
            }
    }

    /**
     * 取消追踪boss
     */
    private fun cancelFollow(dialog: FollowAskCancelDialog?) {
        showLoadingAlert("尝试取消...")

        TbsApi.boss().obtainCancelFollowBoss(entity.id)
            .bindDefaultSub(doNext = {
                dialog?.dismiss()

                FollowChangedDialog.showDialog(supportFragmentManager, true, "followChange")

                UserConfig.get().updateUser {
                    it.traceNum = max((it.traceNum ?: 0) - 1, 0)
                }
                eventBus.post(
                    FollowBossEvent(
                        id = entity.id,
                        isFollow = false,
                        needLoading = true,
                        labels = entity.labels
                    )
                )
                entity.isCollect = false

                text_follow.isSelected = false
                text_follow.text = if (entity.isCollect) "已追踪" else "追踪"

                JPushHelper.tryDelTag(entity.id)

                showDialog?.tryDismiss()
            }, doFail = {
                Toasts.show("取消失败，${it.msg}")
            }, doDone = {
                hideLoadingAlert()
            })
    }

    /**
     * 追踪boss
     */
    private fun followBoss() {
        showLoadingAlert("尝试追踪...")

        TbsApi.boss().obtainFollowBoss(entity.id)
            .bindDefaultSub(doNext = {
                FollowAskPushDialog.showDialog(supportFragmentManager, "askPush")
                    .apply {
                        onConfirmClick = FollowAskPushDialog.OnConfirmClick {
                            JPushHelper.tryAddTag(entity.id)

                            this.dismiss()

                            FollowChangedDialog.showDialog(
                                supportFragmentManager,
                                false,
                                "followChange"
                            )
                        }
                        onCancelClick = FollowAskPushDialog.OnCancelClick {
                            this.dismiss()

                            FollowChangedDialog.showDialog(
                                supportFragmentManager,
                                false,
                                "followChange"
                            )
                        }
                    }

                UserConfig.get().updateUser {
                    it.traceNum = max((it.traceNum ?: 0) + 1, 0)
                }

                eventBus.post(
                    FollowBossEvent(
                        entity.id, true,
                        needLoading = true,
                        labels = entity.labels
                    )
                )

                entity.isCollect = true
                text_follow.isSelected = true
                text_follow.text = if (entity.isCollect) "已追踪" else "追踪"
            }, doFail = {
                Toasts.show("追踪失败，${it.msg}")
            }, doDone = {
                hideLoadingAlert()
            })
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : BossArticleAdapter() {
            override fun onClick(item: ArticleEntity) {
                ArticleActivity.start(mActivity, item.id)
                item.isRead = true

                val index = data.indexOfFirst { it.id == item.id }

                if (index != -1) {
                    notifyItemChanged(index)
                }
            }
        }.also {
            mAdapter = it
        }
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        if (!loadMore) {
            pageParam?.resetPage()

            if (needLoading) showLoading()
        }

        TbsApi.boss().obtainBossArticleList(pageParam, entity.id)
            .onErrorReturn {
                Page.empty()
            }.bindPageSubscribe(loadMore = loadMore, doNext = {
                if (loadMore) mAdapter.addData(it)
                else mAdapter.setNewData(it)
            }, doDone = {
                showContent()

                layout_refresh.finishRefresh()
                layout_refresh.finishLoadMore()
            })
    }

    @SuppressLint("SetTextI18n")
    private fun setUserInfo() {
        GlideApp.displayHead(entity.head.fullUrl(), image_head)
        text_name.text = entity.name
        text_info.text = entity.role
        text_label.text =
            "${(entity.readCount ?: 0).formatCount()}阅读·${entity.totalCount}篇言论·${(entity.collect ?: 0).formatCount()}关注"
        text_follow.text = if (entity.isCollect) "已追踪" else "追踪"
        text_follow.isSelected = entity.isCollect
        text_content.text = "个人简介：${entity.info}"
        text_num.text = "共${entity.totalCount}篇"
    }
}