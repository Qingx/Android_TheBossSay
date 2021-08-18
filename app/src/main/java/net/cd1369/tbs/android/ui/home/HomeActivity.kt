package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cn.wl.android.lib.ui.BaseActivity
import com.blankj.utilcode.util.AppUtils
import com.github.gzuliyujiang.oaid.DeviceID
import kotlinx.android.synthetic.main.activity_home.*
import net.cd1369.tbs.android.BuildConfig
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.event.JumpBossEvent
import net.cd1369.tbs.android.event.LoginEvent
import net.cd1369.tbs.android.ui.dialog.CheckUpdateDialog
import net.cd1369.tbs.android.ui.fragment.HomeBossFragment
import net.cd1369.tbs.android.ui.fragment.HomeMineFragment
import net.cd1369.tbs.android.ui.fragment.HomeSpeechFragment
import net.cd1369.tbs.android.ui.fragment.HomeToolFragment
import net.cd1369.tbs.android.util.*
import net.cd1369.tbs.android.util.Tools.logE
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 10:22 2021/8/9
 * @desc
 */
class HomeActivity : BaseActivity() {
    val fragments = mutableListOf<Fragment>()

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
        fragments.add(HomeBossFragment.createFragment())
        if (BuildConfig.ENV == "YYB") {
            fragments.add(HomeToolFragment.createFragment())
        }
        fragments.add(HomeMineFragment.createFragment())

        // 在 Application#onCreate 里调用预取。注意：如果不需要调用`getClientId()`及`getOAID()`，请不要调用这个方法
        // 在 Application#onCreate 里调用预取。注意：如果不需要调用`getClientId()`及`getOAID()`，请不要调用这个方法
        DeviceID.register(this.application)
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        eventBus.register(this)

        checkUpdate()
        tryRegisterJPush()

        layout_tools.isVisible = BuildConfig.ENV == "YYB"

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
                val index = if (BuildConfig.ENV == "YYB") 3 else 2

                layout_talk.isSelected = position == 0
                layout_boss.isSelected = position == 1
                layout_mine.isSelected = position == index
                layout_tools.isSelected = position == 2
            }
        })

        view_pager.currentItem = 0
        view_pager.isUserInputEnabled = false

        layout_talk doClick {
            view_pager.setCurrentItem(0, false)
        }

        layout_boss doClick {
            view_pager.setCurrentItem(1, false)
        }

        layout_mine doClick {
            val index = if (BuildConfig.ENV == "YYB") 3 else 2
            view_pager.setCurrentItem(index, false)
        }

        layout_tools doClick {
            view_pager.setCurrentItem(2, false)
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: JumpBossEvent) {
        view_pager.setCurrentItem(1, true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: LoginEvent) {
        view_pager.setCurrentItem(0, true)
    }
}