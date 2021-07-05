package net.cd1369.tbs.android.ui.start

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.Toasts
import com.jyn.vcview.VerificationCodeView
import kotlinx.android.synthetic.main.activity_input_code.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.event.LoginEvent
import net.cd1369.tbs.android.ui.home.HomeActivity
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.startShakeAnim

class InputCodeActivity : BaseActivity(), VerificationCodeView.OnCodeFinishListener {
    private lateinit var phoneNumber: String

    companion object {
        fun start(context: Context?, phoneNumber: String) {
            val intent = Intent(context, InputCodeActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("phoneNumber", phoneNumber)
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_input_code
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        phoneNumber = intent.getStringExtra("phoneNumber") as String
    }

    @SuppressLint("SetTextI18n")
    override fun initViewCreated(savedInstanceState: Bundle?) {
        text_time.text = "30s"

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
        showLoadingAlert("尝试登录...")

        timerDelay(3000) {
            hideLoadingAlert()
            eventBus.post(LoginEvent(true))
            mActivity?.finish()
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

    private fun trySendCode() {
        showLoadingAlert("正在发送验证码...")

        timerDelay(3000) {
            Toasts.show("验证码发送成功")
            hideLoadingAlert()
            countDown()
        }
    }

    private fun codeError() {
        code_view.isSelected = true
        startShakeAnim(code_view)
    }
}