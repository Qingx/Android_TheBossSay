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
import net.cd1369.tbs.android.data.cache.CacheConfig
import net.cd1369.tbs.android.event.LoginEvent
import net.cd1369.tbs.android.event.WechatLoginCodeEvent
import net.cd1369.tbs.android.util.JPushHelper
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

        text_permission.text = SpanUtils.getBuilder("?????????????????????")
            .setForegroundColor(ColorUtils.getColor(R.color.colorTextGray))
            .append("??????????????????")
            .setClickSpan(object : ClickableSpan() {
                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                    ds.bgColor = ColorUtils.getColor(R.color.colorPageBg)
                }

                override fun onClick(widget: View) {
                    WebDocActivity.start(
                        mActivity,
                        "????????????",
                        SERVICE_URL
                    )
                }
            })
            .setForegroundColor(ColorUtils.getColor(R.color.colorAccent))
            .append("???")
            .setForegroundColor(ColorUtils.getColor(R.color.colorTextGray))
            .append("??????????????????")
            .setClickSpan(object : ClickableSpan() {
                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                    ds.bgColor = ColorUtils.getColor(R.color.colorPageBg)
                }

                override fun onClick(widget: View) {
                    WebDocActivity.start(
                        mActivity,
                        "????????????",
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
                Toasts.show("?????????????????????????????????????????????????????????")
            } else {
                tryJumpWechat()
            }
        }
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
     * ????????????????????????
     * @param code String
     */
    private fun trySignWechat(code: String) {
        JPushHelper.tryClearTagAlias()

        showLoadingAlert("??????????????????...")

        TbsApi.user().obtainSignWechat(code)
            .bindDefaultSub(doNext = {
                HttpConfig.saveToken(it.token)
                UserConfig.get().loginStatus = true
                val userInfo = it.userInfo
                UserConfig.get().userEntity = userInfo

//                TCAgent.onRegister(userInfo.id, TDProfile.ProfileType.ANONYMOUS, userInfo.nickName)
                TCAgent.onLogin(userInfo.id, TDProfile.ProfileType.WEIXIN, userInfo.nickName)
                CacheConfig.clearBoss()
                CacheConfig.clearArticle()

                JPushHelper.tryAddTags(it.userInfo.tags ?: mutableListOf())
                JPushHelper.tryAddAlias(it.userInfo.id)

                eventBus.post(LoginEvent())
                mActivity?.finish()
            }, doFail = {
                Toasts.show("???????????????${it.msg}")
            }, doDone = {
                hideLoadingAlert()
            })
    }

    private fun trySendCode() {
        showLoadingAlert("?????????????????????...")

        TbsApi.user().obtainSendCode(edit_input.text.toString().trim(), 0)
            .bindDefaultSub(doNext = {
                Toasts.show("?????????????????????")

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
                Toasts.show("??????????????????")
            }
            return false
        }

        if (input[0].toString() != "1") {
            if (needToast) {
                Toasts.show("???????????????????????????")
            }
            return false
        }

        if (input.toString().length != 11) {
            if (needToast) {
                Toasts.show("???????????????????????????")
            }
            return false
        }

        if (!check_permission.isChecked) {
            if (needToast) {
                Toasts.show("?????????????????????????????????????????????????????????")
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