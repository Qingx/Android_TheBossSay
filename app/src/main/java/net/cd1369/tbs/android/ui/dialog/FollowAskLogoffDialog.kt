package net.cd1369.tbs.android.ui.dialog

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_follow_ask_cancel.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Xiang on 2021/8/11 13:26
 * @description
 * @email Cymbidium@outlook.com
 */
class FollowAskLogoffDialog : DialogFragment() {
    companion object {
        fun showDialog(fragmentManager: FragmentManager, tag: String): FollowAskLogoffDialog {
            return FollowAskLogoffDialog().apply {
                show(fragmentManager, tag)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_follow_ask_cancel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_confirm.isSelected = true

        text_title.text = "确定注销账号吗?"
        text_notice.text = "注销后账号后, 账号保存的所有信息将遗失并不可找回, 确定需要注销吗?"

        text_cancel doClick {
            dismiss()
        }

        text_confirm doClick {
            dismiss()
            onConfirmClick?.onConfirm()
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
        fun onConfirm()
    }
}