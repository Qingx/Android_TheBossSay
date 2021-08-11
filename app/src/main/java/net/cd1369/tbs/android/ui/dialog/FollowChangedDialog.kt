package net.cd1369.tbs.android.ui.dialog

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_follow_changed.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.Tools

/**
 * Created by Xiang on 2021/7/15 11:15
 * @description
 * @email Cymbidium@outlook.com
 */
class FollowChangedDialog : DialogFragment() {
    private var mIsCancel = false

    companion object {
        fun showDialog(
            fragmentManager: FragmentManager,
            isCancel: Boolean,
            tag: String,
        ): FollowChangedDialog {
            return FollowChangedDialog().apply {
                mIsCancel = isCancel
                show(fragmentManager, tag)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_follow_changed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mIsCancel) {
            text_title.text = "取消成功"
            text_notice.text = "已成功取消追踪，将不会实时推送"
        } else {
            text_title.text = "追踪成功"
            text_notice.text = "后续可在老板-我的追踪里查看"
        }

        Tools.countDown(1, this) {
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