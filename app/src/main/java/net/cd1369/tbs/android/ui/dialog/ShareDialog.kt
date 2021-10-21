package net.cd1369.tbs.android.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_share.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Xiang on 2021/7/28 14:12
 * @description
 * @email Cymbidium@outlook.com
 */
class ShareDialog : DialogFragment() {
    var onSession: Runnable? = null
    var onTimeline: Runnable? = null
    var onCopyLink: Runnable? = null
    var onPoster: Runnable? = null

    private var isPoster = false

    companion object {
        fun showDialog(
            fragmentManager: FragmentManager,
            tag: String,
            poster: Boolean = false
        ): ShareDialog {
            return ShareDialog().apply {
                show(fragmentManager, tag)
                isPoster = poster
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layout_poster.isInvisible = !isPoster

        layout_session doClick {
            onSession?.run()
        }

        layout_timeline doClick {
            onTimeline?.run()
        }

        layout_link doClick {
            onCopyLink?.run()
        }

        text_cancel doClick {
            dialog?.dismiss()
        }

        layout_poster doClick {
            onPoster?.run()
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