package net.cd1369.tbs.android.ui.dialog

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import cn.wl.android.lib.utils.Toasts
import kotlinx.android.synthetic.main.dialog_change_name.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Qing on 2021/7/5 4:44 下午
 * @description
 * @email Cymbidium@outlook.com
 */
class ConfirmPhoneDialog : DialogFragment() {
    companion object {
        fun showDialog(manager: FragmentManager): ConfirmPhoneDialog {
            val dialog = ConfirmPhoneDialog()
            dialog.show(manager, "confirmPhone")
            return dialog
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_confirm_phone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_confirm.isSelected = true

        text_confirm doClick {
            if (isInputAvailable()) {
                onConfirmClick?.onConfirm(edit_input.text.toString().trim())
            }
        }

        text_cancel doClick {
            dismiss()
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

    var onConfirmClick: OnConfirmClick? = null

    fun interface OnConfirmClick {
        fun onConfirm(content: String)
    }

    private fun isInputAvailable(): Boolean {
        if (edit_input?.text?.toString()?.trim().isNullOrEmpty()) {
            Toasts.show("请输入手机号")
            return false
        }

        if (edit_input.text.toString().trim().getOrNull(0).toString() != "1") {
            Toasts.show("请输入正确的手机号")
            return false
        }

        if (edit_input.text.toString().trim().length != 11) {
            Toasts.show("请输入正确的手机号")
            return false
        }

        return true
    }
}