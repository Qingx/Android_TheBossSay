package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.SpanUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ColorUtils
import kotlinx.android.synthetic.main.activity_about_us.*
import net.cd1369.tbs.android.BuildConfig
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.Const
import net.cd1369.tbs.android.config.Const.SERVICE_URL
import net.cd1369.tbs.android.util.doClick

class MineAboutAppActivity : BaseActivity() {
    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, MineAboutAppActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_about_us
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        text_version.text = AppUtils.getAppVersionName()
        val company = if (BuildConfig.ENV != "MI") "©成都一三六九网络科技有限公司" else "©北京一三六九网络科技有限公司"

        val privacyUrl = if (BuildConfig.ENV != "MI") Const.PRIVACY_URL else Const.MI_PRIVACY_URL

        text_copyright.movementMethod = LinkMovementMethod.getInstance()

        text_copyright.text = SpanUtils.getBuilder("$company\n")
            .setForegroundColor(ColorUtils.getColor(R.color.colorTextDark))
            .append(" 服务条款 ")
            .setForegroundColor(ColorUtils.getColor(R.color.colorAccent))
            .setClickSpan(object : ClickableSpan() {
                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                    ds.bgColor = ColorUtils.getColor(R.color.colorPageBg)
                }

                override fun onClick(widget: View) {
                    WebDocActivity.start(
                        mActivity,
                        "服务条款",
                        SERVICE_URL
                    )
                }
            })
            .append("|")
            .setForegroundColor(ColorUtils.getColor(R.color.colorTextDark))
            .append(" 隐私政策 ")
            .setForegroundColor(ColorUtils.getColor(R.color.colorAccent))
            .setClickSpan(object : ClickableSpan() {
                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                    ds.bgColor = ColorUtils.getColor(R.color.colorPageBg)
                }

                override fun onClick(widget: View) {
                    WebDocActivity.start(
                        mActivity,
                        "隐私政策",
                        privacyUrl
                    )
                }
            })
            .create()

        image_back doClick {
            onBackPressed()
        }
    }
}