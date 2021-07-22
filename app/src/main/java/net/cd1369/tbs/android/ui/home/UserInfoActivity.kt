package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.data.core.HttpConfig
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.Toasts
import kotlinx.android.synthetic.main.activity_user_info.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.entity.UserEntity
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.ui.adapter.UserInfoAdapter
import net.cd1369.tbs.android.ui.dialog.ChangeNameDialog
import net.cd1369.tbs.android.ui.dialog.ConfirmPhoneDialog
import net.cd1369.tbs.android.ui.start.ConfirmPhoneActivity
import net.cd1369.tbs.android.util.Tools
import net.cd1369.tbs.android.util.doClick
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class UserInfoActivity : BaseActivity() {
    private lateinit var mAdapter: UserInfoAdapter

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, UserInfoActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_user_info
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        eventBus.register(this)

        mAdapter = object : UserInfoAdapter() {
            override fun onItemClick(item: Int) {
                when (item) {
                    0 -> Toasts.show("账号昵称：${UserConfig.get().userEntity.nickName}")
                    1 -> Toasts.show("账号ID：${UserConfig.get().userEntity.id}")
                }
            }

            override fun onChangeClick(item: Int) {
                when (item) {
                    0 -> {
                        ChangeNameDialog.showDialog(supportFragmentManager).apply {
                            this.onConfirmClick = ChangeNameDialog.OnConfirmClick { newName ->
                                val entity = UserConfig.get().userEntity

                                if (entity.nickName != newName) {
                                    showLoadingAlert("正在更新...")

                                    TbsApi.user().obtainChangeName(newName)
                                        .bindDefaultSub(doNext = {
                                            entity.nickName = newName
                                            UserConfig.get().userEntity = entity

                                            mAdapter.notifyDataSetChanged()
                                            eventBus.post(RefreshUserEvent())

                                            this.dismiss()

                                            Toasts.show("更新成功")
                                        }, doFail = {
                                            Toasts.show("更新失败，${it.msg}")
                                        }, doDone = {
                                            hideLoadingAlert()
                                        })
                                } else Toasts.show("昵称重复，修改失败")


                            }
                        }
                    }
                    2 -> {
                        ConfirmPhoneDialog.showDialog(supportFragmentManager).apply {
                            this.onConfirmClick = ConfirmPhoneDialog.OnConfirmClick { phone ->
                                showLoadingAlert("发送验证码...")
                                TbsApi.user().obtainSendCode(phone, 1).bindDefaultSub(doNext = {
                                    Toasts.show("验证码发送成功")

                                    this?.dismiss()
                                    ConfirmPhoneActivity.start(mActivity, phone, it)
                                }, doFail = {
                                    Toasts.show("发送失败，${it.msg}")
                                }, doDone = {
                                    hideLoadingAlert()
                                })
                            }
                        }
                    }
                }
            }
        }

        rv_content.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }

                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

        rv_content.adapter = mAdapter
        mAdapter.setNewData(mutableListOf(0, 1, 2))

        image_back doClick {
            onBackPressed()
        }

        text_logout doClick {
            DataConfig.get().tempId = ""
            UserConfig.get().clear()

            HttpConfig.reset()
            // TODO: 添加数据
            UserConfig.get().userEntity = UserEntity.empty

            Toasts.show("退出成功")
            eventBus.post(RefreshUserEvent())
            mActivity?.finish()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: RefreshUserEvent) {
        mAdapter.notifyDataSetChanged()
    }
}