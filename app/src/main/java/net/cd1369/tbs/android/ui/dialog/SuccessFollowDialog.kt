package net.cd1369.tbs.android.ui.dialog

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.transition.TransitionManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_follow_success.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.doClick
import java.util.concurrent.TimeUnit

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

    private var mTimer: Disposable? = null
    private var showStep2 = false

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

    override fun dismiss() {
        if (showStep2) {
            super.dismiss()
        } else {
            showStep2 = true

            group_1.isVisible = false
            group_2.isVisible = true

            text_time.text = "${2}S"
            mTimer = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .map { (1 - it).toInt() }
                .takeWhile { it >= 0 }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it <= 0) {
                        dismiss()
                    } else {
                        text_time.text = "${it}S"
                    }
                }) {

                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mTimer?.dispose()
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