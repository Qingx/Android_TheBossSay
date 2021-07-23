package net.cd1369.tbs.android.ui.dialog

import android.os.Bundle
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import cn.wl.android.lib.utils.ActStack
import cn.wl.android.lib.utils.SpanUtils
import com.blankj.utilcode.util.ColorUtils
import kotlinx.android.synthetic.main.dialog_service_privacy.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.ui.home.WebActivity
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Xiang on 2021/7/23 14:55
 * @description
 * @email Cymbidium@outlook.com
 */
class ServicePrivacyDialog : DialogFragment() {
     var doConfirm: Runnable? = null

    companion object{
        fun showDialog(fragmentManager: FragmentManager, tag: String): ServicePrivacyDialog {
            return ServicePrivacyDialog().apply {
                show(fragmentManager, tag)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_service_privacy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_content.movementMethod = LinkMovementMethod.getInstance()

        text_content.text =
            SpanUtils.getBuilder("""请你务必认真阅读、充分理解"服务条款"和"隐私政策"各条款，包括但不限于，为了向你提供数据、分享等服务所要获取的权限信息。你可以阅读""")
                .setForegroundColor(ColorUtils.getColor(R.color.colorTextDark))
                .append("《服务条款》")
                .setForegroundColor(ColorUtils.getColor(R.color.colorAccent))
                .setClickSpan(object : ClickableSpan() {
                    override fun updateDrawState(ds: TextPaint) {
                        ds.isUnderlineText = false
                        ds.bgColor = ColorUtils.getColor(R.color.colorWhite)
                    }

                    override fun onClick(widget: View) {
                        WebActivity.start(
                            context,
                            "服务条款",
                            "http://file.tianjiemedia.com/serviceProtocol.html"
                        )
                    }
                })
                .append("和")
                .setForegroundColor(ColorUtils.getColor(R.color.colorTextDark))
                .append("《隐私政策》")
                .setForegroundColor(ColorUtils.getColor(R.color.colorAccent))
                .setClickSpan(object : ClickableSpan() {
                    override fun updateDrawState(ds: TextPaint) {
                        ds.isUnderlineText = false
                        ds.bgColor = ColorUtils.getColor(R.color.colorWhite)
                    }

                    override fun onClick(widget: View) {
                        WebActivity.start(
                            context,
                            "隐私政策",
                            "http://file.tianjiemedia.com/privacyService.html"
                        )
                    }
                })
                .append("""了解详细信息。如您同意，请点击"同意"开始接受我们的服务。""")
                .setForegroundColor(ColorUtils.getColor(R.color.colorTextDark))
                .create()

        text_cancel doClick {
            dismiss()
            ActStack.get().exitApp()
        }

        text_confirm doClick {
            dismiss()
            doConfirm?.run()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            val dm = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(dm)
            val width = dm.widthPixels * 0.72
            val height: Int = ViewGroup.LayoutParams.WRAP_CONTENT

            val params = window!!.attributes
            params.gravity = Gravity.CENTER
            window.attributes = params
            window.setLayout(width.toInt(), height)
            window.setBackgroundDrawableResource(R.drawable.draw_white_12)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
        }
    }
}