package net.cd1369.tbs.android.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.core.view.isVisible
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.Toasts
import com.jyn.vcview.VerificationCodeView
import com.tendcloud.tenddata.TCAgent
import com.tendcloud.tenddata.TDProfile
import kotlinx.android.synthetic.main.activity_user_change_phone.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.cache.CacheConfig
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.util.JPushHelper
import net.cd1369.tbs.android.util.Tools
import net.cd1369.tbs.android.util.doClick

class UserChangePhoneActivity : BaseActivity(), VerificationCodeView.OnCodeFinishListener {
    private var isChange = true
    private var phoneNumber: String? = null
    private var rnd: String? = null

    companion object {
        fun start(context: Context?, isChange: Boolean = true) {
            val intent = Intent(context, UserChangePhoneActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("isChange", isChange)
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_user_change_phone
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        isChange = intent.getBooleanExtra("isChange", true)
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        text_page_name.text = if (isChange) "修改手机号" else "绑定手机号"

        layout_code.isVisible = false

        edit_input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                text_confirm.isSelected = isInputAvailable(false)
            }
        })

        text_confirm doClick {
            if (!layout_code.isVisible) {
                trySendCode()
            } else if (text_time.text.toString() == "重新发送") {
                trySendCode()
            } else {
                Toasts.show("操作频繁，请稍后再试")
            }
        }

        code_view.onCodeFinishListener = this

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
        if (isChange) {
            tryChangePhone(content)
        } else {
            tryBindPhone(content)
        }
    }

    //尝试绑定手机号
    private fun tryBindPhone(content: String?) {
        showLoadingAlert("正在绑定...")

        TbsApi.user().obtainBindPhone(phoneNumber, content, rnd)
            .bindDefaultSub(doNext = {
                val entity = UserConfig.get().userEntity
                entity.phone = phoneNumber!!
                UserConfig.get().userEntity = entity

                Toasts.show("绑定成功")
                eventBus.post(RefreshUserEvent())
                mActivity?.finish()
            }, doFail = {
                Toasts.show("绑定失败,${it.msg}")
            }, doDone = {
                hideLoadingAlert()
            })
    }

    //尝试修改手机号
    private fun tryChangePhone(content: String?) {
        showLoadingAlert("正在修改...")

        TbsApi.user().obtainChangePhone(phoneNumber, content, rnd)
            .bindDefaultSub(doNext = {
                val entity = UserConfig.get().userEntity
                entity.phone = phoneNumber!!
                UserConfig.get().userEntity = entity

                Toasts.show("修改成功")
                eventBus.post(RefreshUserEvent())
                mActivity?.finish()
            }, doFail = {
                Toasts.show("修改失败,${it.msg}")
            }, doDone = {
                hideLoadingAlert()
            })
    }

    private fun trySendCode() {
        val type = if (isChange) 2 else 3
        if (isInputAvailable(true)) {
            showLoadingAlert("发送验证码...")
            TbsApi.user().obtainSendCode(edit_input.text.toString().trim(), type)
                .bindDefaultSub(doNext = {
                    phoneNumber = edit_input.text.toString().trim()
                    rnd = it

                    Toasts.show("发送成功")
                    code_view.requestFocus()

                    layout_code.isVisible = true
                    countDown()
                }, doFail = {
                    Toasts.show(it.msg)
                }, doDone = {
                    hideLoadingAlert()
                })
        }
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (Tools.isShouldHideInput(view, ev)) {
                if (Tools.hideInputMethod(this, view)) {
                    return true
                }
            }
        }
        return super.dispatchTouchEvent(ev)
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
        return true
    }

}