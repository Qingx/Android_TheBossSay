package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import cn.wl.android.lib.data.core.HttpConfig
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.SpanUtils
import cn.wl.android.lib.utils.Toasts
import com.blankj.utilcode.util.ColorUtils
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tendcloud.tenddata.TCAgent
import com.tendcloud.tenddata.TDProfile
import kotlinx.android.synthetic.main.activity_login_phone_wechat.*
import net.cd1369.tbs.android.BuildConfig
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.Const
import net.cd1369.tbs.android.config.Const.SERVICE_URL
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.TbsApp
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.db.ArticleDaoManager
import net.cd1369.tbs.android.data.db.BossDaoManager
import net.cd1369.tbs.android.event.LoginEvent
import net.cd1369.tbs.android.event.WechatLoginCodeEvent
import net.cd1369.tbs.android.util.Tools.hideInputMethod
import net.cd1369.tbs.android.util.Tools.isShouldHideInput
import net.cd1369.tbs.android.util.doClick
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LoginPhoneWechatActivity : BaseActivity() {
    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, LoginPhoneWechatActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_login_phone_wechat
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        eventBus.register(this)

        check_permission.isChecked = false

        edit_input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                text_confirm.isSelected = isInputAvailable(false)
            }
        })

        val privacyUrl = if (BuildConfig.ENV != "MI") Const.PRIVACY_URL else Const.MI_PRIVACY_URL

        text_permission.movementMethod = LinkMovementMethod.getInstance()

        text_permission.text = SpanUtils.getBuilder("我已阅读并同意")
            .setForegroundColor(ColorUtils.getColor(R.color.colorTextGray))
            .append("《服务条款》")
            .setClickSpan(object : ClickableSpan() {
                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                    ds.bgColor = ColorUtils.getColor(R.color.colorPageBg)
                }

                override fun onClick(widget: View) {
                    WebActivity.start(
                        mActivity,
                        "服务条款",
                        SERVICE_URL
                    )
                }
            })
            .setForegroundColor(ColorUtils.getColor(R.color.colorAccent))
            .append("和")
            .setForegroundColor(ColorUtils.getColor(R.color.colorTextGray))
            .append("《隐私政策》")
            .setClickSpan(object : ClickableSpan() {
                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                    ds.bgColor = ColorUtils.getColor(R.color.colorPageBg)
                }

                override fun onClick(widget: View) {
                    WebActivity.start(
                        mActivity,
                        "隐私政策",
                        privacyUrl
                    )
                }
            })
            .setForegroundColor(ColorUtils.getColor(R.color.colorAccent))
            .create()

        view_check doClick {
            check_permission.isChecked = !check_permission.isChecked

            text_confirm.isSelected = isInputAvailable(false)
        }

        text_confirm doClick {
            if (isInputAvailable(true)) {
                trySendCode()
            }
        }

        image_back doClick {
            onBackPressed()
        }

        layout_wechat doClick {
            if (!check_permission.isChecked) {
                Toasts.show("请阅读并同意《服务条款》和《隐私政策》")
            } else {
                tryJumpWechat()
            }
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
     * 尝试微信授权登录
     * @param code String
     */
    private fun trySignWechat(code: String) {
        showLoadingAlert("正在尝试登录...")

        TbsApi.user().obtainSignWechat(code)
            .bindDefaultSub(doNext = {
                HttpConfig.saveToken(it.token)
                UserConfig.get().loginStatus = true
                val userInfo = it.userInfo
                UserConfig.get().userEntity = userInfo

                TCAgent.onLogin(userInfo.id, TDProfile.ProfileType.WEIXIN, userInfo.nickName)
                BossDaoManager.getInstance(mActivity).deleteAll()
                ArticleDaoManager.getInstance(mActivity).deleteAll()

                eventBus.post(LoginEvent())
                mActivity?.finish()
            }, doFail = {
                Toasts.show("登录失败，${it.msg}")
            }, doDone = {
                hideLoadingAlert()
            })
    }

    private fun trySendCode() {
        showLoadingAlert("正在发送验证码...")

        TbsApi.user().obtainSendCode(edit_input.text.toString().trim(), 0)
            .bindDefaultSub(doNext = {
                Toasts.show("验证码发送成功")

                LoginInputCodeActivity.start(mActivity, edit_input.text.toString().trim(), it)
            }, doFail = {
                Toasts.show(it.msg)
            }, doDone = {
                hideLoadingAlert()
            })
    }

    fun isInputAvailable(needToast: Boolean): Boolean {
        val input = edit_input.text?.toString()?.trim()
        if (input.isNullOrEmpty()) {
            if (needToast) {
                Toasts.show("请输入手机号")
            }
            return false
        }

        if (input[0].toString() != "1") {
            if (needToast) {
                Toasts.show("请输入正确的手机号")
            }
            return false
        }

        if (input.toString().length != 11) {
            if (needToast) {
                Toasts.show("请输入正确的手机号")
            }
            return false
        }

        if (!check_permission.isChecked) {
            if (needToast) {
                Toasts.show("请阅读并同意《服务条款》和《隐私政策》")
            }
            return false
        }

        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (isShouldHideInput(view, ev)) {
                if (hideInputMethod(this, view)) {
                    return true
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: LoginEvent) {
        mActivity?.finish()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: WechatLoginCodeEvent) {
        trySignWechat(event.code)
    }
}