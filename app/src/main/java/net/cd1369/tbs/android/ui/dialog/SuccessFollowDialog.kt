package net.cd1369.tbs.android.ui.dialog

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_follow_success.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Xiang on 2021/7/15 11:04
 * @description
 * @email Cymbidium@outlook.com
 */
class SuccessFollowDialog : DialogFragment() {
    companion object {
        fun showDialog(fragmentManager: FragmentManager, tag: String): SuccessFollowDialog {
            return SuccessFollowDialog().apply {
                show(fragmentManager, tag)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_follow_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_confirm.isSelected = true

        text_confirm doClick {
            onConfirmClick?.onConfirm()
        }

        text_cancel doClick {
            dismiss()
        }
    }

    var onConfirmClick: OnConfirmClick? = null

    fun interface OnConfirmClick {
        fun onConfirm()
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