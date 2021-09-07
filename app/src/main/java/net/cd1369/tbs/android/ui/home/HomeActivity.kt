package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cn.wl.android.lib.config.WLConfig
import cn.wl.android.lib.ui.BaseActivity
import com.blankj.utilcode.util.AppUtils
import com.github.gzuliyujiang.oaid.DeviceID
import kotlinx.android.synthetic.main.activity_home.*
import net.cd1369.tbs.android.BuildConfig
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.PageItem
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.PortEntity
import net.cd1369.tbs.android.event.GlobalScrollEvent
import net.cd1369.tbs.android.event.JumpBossEvent
import net.cd1369.tbs.android.event.LoginEvent
import net.cd1369.tbs.android.event.PageScrollEvent
import net.cd1369.tbs.android.ui.dialog.CheckUpdateDialog
import net.cd1369.tbs.android.ui.fragment.HomeBossContentFragment
import net.cd1369.tbs.android.ui.fragment.HomeMineFragment
import net.cd1369.tbs.android.ui.fragment.HomeSpeechFragment
import net.cd1369.tbs.android.ui.fragment.HomeToolFragment
import net.cd1369.tbs.android.util.DownloadHelper
import net.cd1369.tbs.android.util.JPushHelper
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.fullDownloadUrl
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 10:22 2021/8/9
 * @desc
 */
class HomeActivity : BaseActivity() {
    private val fragments = mutableListOf<Fragment>()
    private var isPortStatus = false

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, HomeActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_home
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        fragments.add(HomeSpeechFragment.createFragment())
        fragments.add(HomeBossContentFragment.createFragment())
        fragments.add(HomeMineFragment.createFragment())

        // 在 Application#onCreate 里调用预取。注意：如果不需要调用`getClientId()`及`getOAID()`，请不要调用这个方法
        // 在 Application#onCreate 里调用预取。注意：如果不需要调用`getClientId()`及`getOAID()`，请不要调用这个方法
        DeviceID.register(this.application)
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        eventBus.register(this)

        checkUpdate()
        tryRegisterJPush()

        layout_tools.isVisible = isPortStatus && BuildConfig.ENV == "YYB"

        view_pager.adapter = object : FragmentStateAdapter(mActivity) {
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }

        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val index = if (BuildConfig.ENV == "YYB" || isPortStatus) 3 else 2

                layout_talk.isSelected = position == 0
                text_talk.text = if (position == 0) "回顶部" else "言论"
                layout_boss.isSelected = position == 1
                text_boss.text = if (position == 1) "回顶部" else "老板"
                layout_mine.isSelected = position == index
                layout_tools.isSelected = position == 2

                if (layout_talk.isSelected) {
                    GlobalScrollEvent.homePage = PageItem.Talk.code
                }

                if (layout_boss.isSelected) {
                    GlobalScrollEvent.homePage = PageItem.Boss.code
                }
            }
        })

        view_pager.currentItem = 0
        view_pager.isUserInputEnabled = false

        layout_talk doClick {
            if (view_pager.currentItem == 0) {
                eventBus.post(PageScrollEvent())
            } else {
                view_pager.setCurrentItem(0, false)
            }
        }

        layout_boss doClick {
            if (view_pager.currentItem == 1) {
                eventBus.post(PageScrollEvent())
            } else {
                view_pager.setCurrentItem(1, false)
            }
        }

        layout_mine doClick {
            val index = if (BuildConfig.ENV == "YYB" || isPortStatus) 3 else 2
            view_pager.setCurrentItem(index, false)
        }

        layout_tools doClick {
            view_pager.setCurrentItem(2, false)
        }

        checkNoticeEnable()
    }

    /**
     * 检查是否有通知权限
     */
    private fun checkNoticeEnable() {

    }

    /**
     * 开启极光推送
     */
    private fun tryRegisterJPush() {
        JPushHelper.tryStartPush()
    }

    /**
     * 检查更新
     */
    private fun checkUpdate() {
        TbsApi.user().obtainCheckUpdate(AppUtils.getAppVersionName())
            .bindDefaultSub(
                doNext = {
                    CheckUpdateDialog.showDialog(
                        supportFragmentManager,
                        "checkUpdate",
                        !it.forcedUpdating
                    )
                        .apply {
                            onCancelClick = CheckUpdateDialog.OnCancelClick {
                                dismiss()
                            }

                            onConfirmClick = CheckUpdateDialog.OnConfirmClick {
                                DownloadHelper.requestDownload(
                                    it.fileUrl.fullDownloadUrl(),
                                    "v${it.versions}"
                                )

                                if (!it.forcedUpdating) {
                                    dismiss()
                                }
                            }
                        }
                },
                doFail = {
                }
            )
    }

    override fun loadData() {
        TbsApi.user().obtainPortStatus()
            .onErrorReturn { PortEntity() }
            .bindDefaultSub({

            }) {
                if (it.groundingStatus && BuildConfig.ENV == "YYB") {
                    isPortStatus = true
                    layout_tools.isVisible = true
                    fragments.add(2, HomeToolFragment.createFragment())
                    view_pager.adapter?.notifyDataSetChanged()
                }

                timerDelay(300) {
                    view_pager.offscreenPageLimit = fragments.size
                }
            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: JumpBossEvent) {
        view_pager.setCurrentItem(1, true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: LoginEvent) {
        view_pager.setCurrentItem(0, true)
    }
}