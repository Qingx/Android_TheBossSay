package net.cd1369.tbs.android.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import cn.wl.android.lib.data.core.HttpConfig
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.Toasts
import com.jyn.vcview.VerificationCodeView
import com.tendcloud.tenddata.TCAgent
import com.tendcloud.tenddata.TDProfile
import kotlinx.android.synthetic.main.activity_login_input_code.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.cache.CacheConfig
import net.cd1369.tbs.android.event.LoginEvent
import net.cd1369.tbs.android.util.JPushHelper
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.startShakeAnim

class LoginInputCodeActivity : BaseActivity(), VerificationCodeView.OnCodeFinishListener {
    private lateinit var phoneNumber: String
    private lateinit var rnd: String

    companion object {
        fun start(context: Context?, phoneNumber: String, rnd: String) {
            val intent = Intent(context, LoginInputCodeActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("phoneNumber", phoneNumber)
                    putExtra("rnd", rnd)
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_login_input_code
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        phoneNumber = intent.getStringExtra("phoneNumber") as String
        rnd = intent.getStringExtra("rnd") as String
    }

    @SuppressLint("SetTextI18n")
    override fun initViewCreated(savedInstanceState: Bundle?) {
        text_time.text = "60s"

        code_view.requestFocus()
        code_view.onCodeFinishListener = this

        countDown()

        image_back doClick {
            onBackPressed()
        }

        text_time doClick {
            if (text_time.text.toString() == "重新发送") {
                trySendCode()
            } else {
                Toasts.show("操作频繁，请稍后再试")
            }
        }
    }

    override fun onTextChange(view: View?, content: String?) {
        code_view.isSelected = false
    }

    override fun onComplete(view: View?, content: String?) {
        tryLogin(content!!)
    }

    private fun tryLogin(code: String) {
        JPushHelper.tryClearTagAlias()

        showLoadingAlert("尝试登录...")

        TbsApi.user().obtainSignPhone(phoneNumber, code, rnd)
            .bindDefaultSub(doNext = {
                HttpConfig.saveToken(it.token)

                UserConfig.get().loginStatus = true
                val userInfo = it.userInfo
                UserConfig.get().userEntity = userInfo

//                TCAgent.onRegister(userInfo.id, TDProfile.ProfileType.ANONYMOUS, userInfo.nickName)
                TCAgent.onLogin(userInfo.id, TDProfile.ProfileType.ANONYMOUS, userInfo.nickName)
                CacheConfig.clearBoss()
                CacheConfig.clearArticle()

                JPushHelper.tryAddTags(it.userInfo.tags ?: mutableListOf())
                JPushHelper.tryAddAlias(it.userInfo.id)

                eventBus.post(LoginEvent())
                mActivity?.finish()
            }, doFail = {
                codeError()
            }, doDone = {
                hideLoadingAlert()
            })
    }

    @SuppressLint("SetTextI18n")
    private fun countDown() {
        countdown(60) {
            text_time.text = "${it}s"

            if (it <= 0) {
                text_time.text = "重新发送"
            }
        }
    }

    private fun trySendCode() {
        showLoadingAlert("正在发送验证码...")

        TbsApi.user().obtainSendCode(phoneNumber, 0)
            .bindDefaultSub(doNext = {
                rnd = it
                Toasts.show("验证码发送成功")

                countDown()
            }, doFail = {
                Toasts.show(it.msg)
            }, doDone = {
                hideLoadingAlert()
            })
    }

    private fun codeError() {
        code_view.isSelected = true
        startShakeAnim(code_view)
    }
}