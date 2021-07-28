package net.cd1369.tbs.android.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_boss_setting.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Xiang on 2021/7/28 16:14
 * @description
 * @email Cymbidium@outlook.com
 */
class BossSettingDialog : DialogFragment() {
    var onConfirm: Runnable? = null

    companion object {
        fun showDialog(fragmentManager: FragmentManager, tag: String): BossSettingDialog {
            return BossSettingDialog().apply {
                show(fragmentManager, tag)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_boss_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_confirm doClick {
            onConfirm?.run()
        }

        text_cancel doClick {
            dialog?.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height: Int = ViewGroup.LayoutParams.WRAP_CONTENT

            val params = window!!.attributes
            params.gravity = Gravity.BOTTOM
            window.attributes = params
            window.setLayout(width, height)
            window.setBackgroundDrawableResource(R.drawable.draw_white_12_top)
            window.setWindowAnimations(R.style.dialogWindowAnim)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
        }
    }
}