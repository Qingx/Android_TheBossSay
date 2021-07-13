package net.cd1369.tbs.android.ui.start

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
import kotlinx.android.synthetic.main.activity_change_phone.*
import kotlinx.android.synthetic.main.activity_change_phone.edit_input
import kotlinx.android.synthetic.main.activity_change_phone.text_confirm
import kotlinx.android.synthetic.main.activity_input_phone.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.util.Tools
import net.cd1369.tbs.android.util.doClick

class ChangePhoneActivity() : BaseActivity(), VerificationCodeView.OnCodeFinishListener {
    private var phoneNumber: String? = null

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, ChangePhoneActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_change_phone
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
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
        showLoadingAlert("正在更新...")

        TbsApi.user().obtainChangePhone(phoneNumber, content)
            .bindDefaultSub(doNext = {
                val entity = UserConfig.get().userEntity
                entity.phone = phoneNumber!!

                UserConfig.get().userEntity = entity

                Toasts.show("更新成功")
                eventBus.post(RefreshUserEvent())
                mActivity?.finish()
            }, doFail = {
                Toasts.show("修改失败,${it.msg}")
            }, doDone = {
                hideLoadingAlert()
            })
    }

    private fun trySendCode() {
        if (isInputAvailable(true)) {
            showLoadingAlert("发送验证码...")
            TbsApi.user().obtainSendCode(edit_input.text.toString().trim(), 2)
                .bindDefaultSub(doNext = {
                    phoneNumber = edit_input.text.toString().trim()

                    Toasts.show("发送成功")
                    code_view.requestFocus()

                    layout_code.isVisible = true
                    countDown()
                }, doFail = {
                    Toasts.show("发送失败,${it.msg}")
                }, doDone = {
                    hideLoadingAlert()
                })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun countDown() {
        countdown(30) {
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