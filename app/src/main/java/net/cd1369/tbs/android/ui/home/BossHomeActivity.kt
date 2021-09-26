package net.cd1369.tbs.android.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.GlideApp
import cn.wl.android.lib.utils.Toasts
import com.blankj.utilcode.util.ConvertUtils
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_boss_home.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.Const
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.db.BossDaoManager
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.event.BossTackEvent
import net.cd1369.tbs.android.event.SetBossTimeEvent
import net.cd1369.tbs.android.ui.dialog.*
import net.cd1369.tbs.android.ui.fragment.BossHomeFragment
import net.cd1369.tbs.android.util.*
import net.cd1369.tbs.android.util.Tools.formatCount
import kotlin.math.absoluteValue
import kotlin.math.max

class BossHomeActivity : BaseActivity() {
    private lateinit var bossEntity: BossInfoEntity
    private lateinit var bossId: String

    private var mLayoutH: Int = 0
    private var mBgDraw: Drawable? = null

    companion object {
        fun start(context: Context?, bossId: String) {
            val intent = Intent(context, BossHomeActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("bossId", bossId)
                }
            context!!.startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        DataConfig.get().setBossTime(bossEntity.id)
        eventBus.post(SetBossTimeEvent(bossEntity.id))
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_boss_home
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        bossId = intent.getStringExtra("bossId") as String
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        app_root.post {
            mBgDraw = view_mask.background
            mBgDraw?.alpha = 0

            mLayoutH = app_root.height - ConvertUtils.dp2px(36F)
        }

        app_root.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val fl = (mLayoutH - verticalOffset.absoluteValue).toFloat() / mLayoutH
            mBgDraw?.alpha = ((1F - fl) * 255).toInt()
        })

        tab_1.isSelected = true

        view_pager.offscreenPageLimit = 2
        view_pager.isUserInputEnabled = true

        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tab_1.isSelected = position == 0
                tab_2.isSelected = position == 1
//                tab_3.isSelected = position == 2
            }
        })

        text_follow doClick {
            if (bossEntity.isCollect) {
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
            BossInfoActivity.start(mActivity, bossEntity)
        }

        image_share doClick {
            onShare()
        }

        image_setting doClick {
            BossSettingDialog.showDialog(supportFragmentManager, "bossSetting")
                .apply {
                    onConfirm = Runnable {
                        JPushHelper.tryAddTag(bossEntity.id)
                        Toasts.show("开启成功")
                        dialog?.dismiss()
                    }
                }
        }

        tab_1 doClick {
            tab_1.isSelected = true
            tab_2.isSelected = false
//            tab_3.isSelected = false

            view_pager.currentItem = 0
        }

        tab_2 doClick {
            tab_1.isSelected = false
            tab_2.isSelected = true
//            tab_3.isSelected = false

            view_pager.currentItem = 1
        }

//        tab_3 doClick {
//            tab_1.isSelected = false
//            tab_2.isSelected = false
//            tab_3.isSelected = true
//
//            view_pager.currentItem = 2
//        }
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

        TbsApi.boss().obtainCancelFollowBoss(bossEntity.id)
            .bindDefaultSub(doNext = {
                dialog?.dismiss()

                FollowChangedDialog.showDialog(supportFragmentManager, true, "followChange")

                bossEntity.isCollect = false

                text_follow.isSelected = false
                text_follow.text = if (bossEntity.isCollect) "已追踪" else "追踪"

                UserConfig.get().updateUser {
                    it.traceNum = max((it.traceNum ?: 0) - 1, 0)
                }
                BossDaoManager.getInstance(mActivity).delete(bossId.toLong())
                eventBus.post(BossTackEvent(bossId, false, bossEntity.labels))

                JPushHelper.tryDelTag(bossEntity.id)

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

        TbsApi.boss().obtainFollowBoss(bossEntity.id)
            .bindDefaultSub(doNext = {
                FollowAskPushDialog.showDialog(supportFragmentManager, "askPush")
                    .apply {
                        onConfirmClick = FollowAskPushDialog.OnConfirmClick {
                            JPushHelper.tryAddTag(bossEntity.id)

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

                bossEntity.isCollect = true
                text_follow.isSelected = true
                text_follow.text = if (bossEntity.isCollect) "已追踪" else "追踪"

                UserConfig.get().updateUser {
                    it.traceNum = max((it.traceNum ?: 0) + 1, 0)
                }
                BossDaoManager.getInstance(mActivity).insert(bossEntity.toSimple())
                eventBus.post(BossTackEvent(bossId, true, bossEntity.labels))

            }, doFail = {
                Toasts.show("追踪失败，${it.msg}")
            }, doDone = {
                hideLoadingAlert()
            })
    }

    override fun loadData() {
        super.loadData()
        showLoading()

        TbsApi.boss().obtainBossDetail(bossId)
            .bindDefaultSub {
                bossEntity = it
                setBossInfo()
                showContent()

                view_pager.adapter = object : FragmentStateAdapter(mActivity) {
                    override fun getItemCount(): Int {
                        return 2
                    }

                    override fun createFragment(position: Int): Fragment {
                        return BossHomeFragment.createFragment(
                            bossId,
                            (position + 1).toString(),
                            it.totalCount
                        )
                    }
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun setBossInfo() {
        GlideApp.displayHead(bossEntity.head.fullUrl(), image_head)
        text_name.text = bossEntity.name
        text_info.text = bossEntity.role
        text_label.text =
            "${(bossEntity.readCount ?: 0).formatCount()}阅读·${bossEntity.totalCount}篇言论·${(bossEntity.collect ?: 0).formatCount()}关注"
        text_follow.text = if (bossEntity.isCollect) "已追踪" else "追踪"
        text_follow.isSelected = bossEntity.isCollect
        text_content.text = "个人简介：${bossEntity.info}"
    }
}