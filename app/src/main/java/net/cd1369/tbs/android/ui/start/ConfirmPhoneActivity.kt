package net.cd1369.tbs.android.ui.start

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.Toasts
import com.jyn.vcview.VerificationCodeView
import kotlinx.android.synthetic.main.activity_confirm_phone.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.util.doClick
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ConfirmPhoneActivity : BaseActivity(), VerificationCodeView.OnCodeFinishListener {
    private lateinit var phoneNumber: String
    private lateinit var rnd: String

    companion object {
        fun start(context: Context?, phone: String, rnd: String) {
            val intent = Intent(context, ConfirmPhoneActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("phone", phone)
                    putExtra("rnd", rnd)
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_confirm_phone
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        phoneNumber = intent.getStringExtra("phone") as String
        rnd = intent.getStringExtra("rnd") as String
    }

    @SuppressLint("SetTextI18n")
    override fun initViewCreated(savedInstanceState: Bundle?) {
        eventBus.register(this)

        text_title.text = "我们已经向尾号${phoneNumber.substring(7, 11)}发送了一条验证码"
        text_info.text = "如果${phoneNumber.substring(7, 11)}是您当前账号登录的手机号\n请填写短信验证码以继续"

        code_view.requestFocus()
        code_view.onCodeFinishListener = this

        image_back doClick {
            onBackPressed()
        }
    }

    override fun onTextChange(view: View?, content: String?) {
        code_view.isSelected = false
    }

    override fun onComplete(view: View?, content: String?) {
        showLoadingAlert("正在验证...")

        TbsApi.user().obtainConfirmPhone(phoneNumber, content, rnd).bindDefaultSub(doDone = {
            Toasts.show("验证成功")
            ChangePhoneActivity.start(mActivity)
            mActivity?.finish()
        }, doFail = {
            Toasts.show("验证失败，${it.msg}")
        }, doNext = {
            hideLoadingAlert()
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: RefreshUserEvent) {
        mActivity?.finish()
    }
}