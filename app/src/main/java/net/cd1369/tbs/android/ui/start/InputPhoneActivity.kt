package net.cd1369.tbs.android.ui.start

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.SpanUtils
import cn.wl.android.lib.utils.Toasts
import com.blankj.utilcode.util.ColorUtils
import kotlinx.android.synthetic.main.activity_input_phone.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.util.Tools.hideInputMethod
import net.cd1369.tbs.android.util.Tools.isShouldHideInput
import net.cd1369.tbs.android.util.doClick
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class InputPhoneActivity : BaseActivity() {
    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, InputPhoneActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_input_phone
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

        text_permission.text = SpanUtils.getBuilder("我已阅读并同意")
            .setForegroundColor(ColorUtils.getColor(R.color.colorTextGray))
            .append("《服务条款》")
            .setForegroundColor(ColorUtils.getColor(R.color.colorAccent))
            .append("和")
            .setForegroundColor(ColorUtils.getColor(R.color.colorTextGray))
            .append("《隐私政策》")
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
    }

    private fun trySendCode() {
        showLoadingAlert("正在发送验证码...")

        TbsApi.user().obtainSendCode(edit_input.text.toString().trim(), 0)
            .bindDefaultSub(doNext = {
                Toasts.show("验证码发送成功")

                InputCodeActivity.start(mActivity, edit_input.text.toString().trim(),it)
            }, doFail = {
                Toasts.show("验证码发送失败，${it.msg}")
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
    fun eventBus(event: RefreshUserEvent) {
        mActivity?.finish()
    }
}