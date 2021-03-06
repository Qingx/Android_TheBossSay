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
import net.cd1369.tbs.android.data.cache.CacheConfig
import net.cd1369.tbs.android.data.entity.UserEntity
import net.cd1369.tbs.android.event.LoginEvent
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.event.WechatLoginCodeEvent
import net.cd1369.tbs.android.ui.adapter.UserInfoAdapter
import net.cd1369.tbs.android.ui.dialog.ChangeNameDialog
import net.cd1369.tbs.android.ui.dialog.ConfirmPhoneDialog
import net.cd1369.tbs.android.ui.dialog.FollowAskLogoffDialog
import net.cd1369.tbs.android.util.JPushHelper
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
                    0 -> Toasts.show("???????????????${UserConfig.get().userEntity.nickName}")
                    1 -> Tools.copyText(
                        mActivity,
                        "${UserConfig.get().userEntity.id}",
                        "??????ID???${UserConfig.get().userEntity.id}"
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
                                    showLoadingAlert("????????????...")

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

                                            Toasts.show("????????????")
                                        }, doFail = {
                                            Toasts.show("???????????????${it.msg}")
                                        }, doDone = {
                                            hideLoadingAlert()
                                        })
                                } else Toasts.show("???????????????????????????")
                            }
                        }
                    }
                    2 -> {
                        if (UserConfig.get().userEntity.type == "1" && UserConfig.get().userEntity?.phone.isNullOrEmpty()) {
                            UserChangePhoneActivity.start(mActivity, false)
                        } else {
                            ConfirmPhoneDialog.showDialog(supportFragmentManager).apply {
                                this.onConfirmClick = ConfirmPhoneDialog.OnConfirmClick { phone ->
                                    showLoadingAlert("???????????????...")
                                    TbsApi.user().obtainSendCode(phone, 1).bindDefaultSub(doNext = {
                                        Toasts.show("?????????????????????")

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
                            Toasts.show("????????????????????????")
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
            Toasts.show("????????????")

            logout()
        }

        text_logoff doClick {
            tryLogoff()
        }
    }

    /**
     * ??????????????????
     */
    private fun tryLogoff() {

        FollowAskLogoffDialog.showDialog(supportFragmentManager, "ffd")
            .also { it ->
                it.onConfirmClick = FollowAskLogoffDialog.OnConfirmClick {
                    showLoadingAlert("????????????")

                    TbsApi.user().logoff()
                        .bindDefaultSub {
                            if (it) {
                                Toasts.show("????????????")
                                logout()
                            } else {
                                Toasts.show("????????????")
                            }
                        }
                }
            }
    }

    private fun logout() {
        DataConfig.get().tempId = Tools.createTempId()
        UserConfig.get().clear()

        HttpConfig.reset()
        UserConfig.get().userEntity = UserEntity.empty

        CacheConfig.clearBoss()
        CacheConfig.clearArticle()
        JPushHelper.tryClearTagAlias()

        eventBus.post(LoginEvent())
        mActivity?.finish()
    }

    /**
     * ????????????????????????
     */
    private fun tryJumpWechat() {
        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        TbsApp.getWeChatApi().sendReq(req)
    }

    /**
     * ????????????
     * @param code String
     */
    private fun tryBindWechat(code: String) {
        showLoadingAlert("??????????????????...")

        TbsApi.user().obtainBindWechat(code)
            .bindDefaultSub(doNext = {
                HttpConfig.saveToken(it.token)
                UserConfig.get().loginStatus = true
                val userInfo = it.userInfo
                UserConfig.get().userEntity = userInfo

//                TCAgent.onRegister(userInfo.id, TDProfile.ProfileType.ANONYMOUS, userInfo.nickName)
                TCAgent.onLogin(userInfo.id, TDProfile.ProfileType.WEIXIN, userInfo.nickName)
                CacheConfig.clearBoss()
                CacheConfig.clearArticle()

                JPushHelper.tryClearTagAlias()
                JPushHelper.tryAddAlias(it.userInfo.id)
                JPushHelper.tryAddTags(it.userInfo.tags ?: mutableListOf())

                eventBus.post(LoginEvent())
                mActivity?.finish()
            }, doFail = {
                Toasts.show("???????????????${it.msg}")
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