package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.data.core.HttpConfig
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.Toasts
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tendcloud.tenddata.TCAgent
import com.tendcloud.tenddata.TDProfile
import kotlinx.android.synthetic.main.activity_mine_change_user.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.TbsApp
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.db.ArticleDaoManager
import net.cd1369.tbs.android.data.db.BossDaoManager
import net.cd1369.tbs.android.data.entity.UserEntity
import net.cd1369.tbs.android.event.LoginEvent
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.event.WechatLoginCodeEvent
import net.cd1369.tbs.android.ui.adapter.UserInfoAdapter
import net.cd1369.tbs.android.ui.dialog.ChangeNameDialog
import net.cd1369.tbs.android.ui.dialog.ConfirmPhoneDialog
import net.cd1369.tbs.android.util.Tools
import net.cd1369.tbs.android.util.doClick
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MineChangeUserActivity : BaseActivity() {
    private lateinit var mAdapter: UserInfoAdapter

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, MineChangeUserActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_mine_change_user
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        eventBus.register(this)

        mAdapter = object : UserInfoAdapter() {
            override fun onItemClick(item: Int) {
                when (item) {
                    0 -> Toasts.show("账号昵称：${UserConfig.get().userEntity.nickName}")
                    1 -> Tools.copyText(
                        mActivity,
                        "${UserConfig.get().userEntity.id}",
                        "账号ID：${UserConfig.get().userEntity.id}"
                    )
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

                                            UserConfig.get().updateUser {
                                                it.nickName = newName
                                            }
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
                        if (UserConfig.get().userEntity.type == "1" && UserConfig.get().userEntity?.phone.isNullOrEmpty()) {
                            UserChangePhoneActivity.start(mActivity, false)
                        } else {
                            ConfirmPhoneDialog.showDialog(supportFragmentManager).apply {
                                this.onConfirmClick = ConfirmPhoneDialog.OnConfirmClick { phone ->
                                    showLoadingAlert("发送验证码...")
                                    TbsApi.user().obtainSendCode(phone, 1).bindDefaultSub(doNext = {
                                        Toasts.show("验证码发送成功")

                                        this?.dismiss()
                                        UserConfirmPhoneActivity.start(mActivity, phone, it)
                                    }, doFail = {
                                        Toasts.show(it.msg)
                                    }, doDone = {
                                        hideLoadingAlert()
                                    })
                                }
                            }
                        }
                    }
                    3 -> {
                        if (UserConfig.get().userEntity.type == "1" && UserConfig.get().userEntity?.wxName.isNullOrEmpty()) {
                            tryJumpWechat()
                        } else {
                            Toasts.show("暂不支持换绑微信")
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
        mAdapter.setNewData(mutableListOf(0, 1, 2, 3))

        image_back doClick {
            onBackPressed()
        }

        text_logout doClick {
            DataConfig.get().tempId = Tools.createTempId()
            UserConfig.get().clear()

            HttpConfig.reset()
            UserConfig.get().userEntity = UserEntity.empty

            BossDaoManager.getInstance(mActivity).deleteAll()
            ArticleDaoManager.getInstance(mActivity).deleteAll()

            Toasts.show("退出成功")

            eventBus.post(LoginEvent())
            mActivity?.finish()
        }
    }

    /**
     * 尝试跳转微信授权
     */
    private fun tryJumpWechat() {
        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        TbsApp.getWeChatApi().sendReq(req)
    }

    /**
     * 绑定微信
     * @param code String
     */
    private fun tryBindWechat(code: String) {
        showLoadingAlert("正在尝试登录...")

        TbsApi.user().obtainBindWechat(code)
            .bindDefaultSub(doNext = {
                HttpConfig.saveToken(it.token)
                UserConfig.get().loginStatus = true
                val userInfo = it.userInfo
                UserConfig.get().userEntity = userInfo

                TCAgent.onLogin(userInfo.id, TDProfile.ProfileType.WEIXIN, userInfo.nickName)

                eventBus.post(LoginEvent())
                mActivity?.finish()
            }, doFail = {
                Toasts.show("登录失败，${it.msg}")
            }, doDone = {
                hideLoadingAlert()
            })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: RefreshUserEvent) {
        mAdapter.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: WechatLoginCodeEvent) {
        tryBindWechat(event.code)
    }
}