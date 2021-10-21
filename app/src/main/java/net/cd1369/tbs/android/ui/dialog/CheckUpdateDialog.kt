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
import cn.wl.android.lib.utils.DownloadEvent
import cn.wl.android.lib.utils.DownloadStatusEvent
import cn.wl.android.lib.utils.Toasts
import com.blankj.utilcode.util.AppUtils
import kotlinx.android.synthetic.main.dialog_version_update.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.doClick
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Xiang on 2021/8/11 14:07
 * @description
 * @email Cymbidium@outlook.com
 */
class CheckUpdateDialog : DialogFragment() {
    private var mCanClose = false
    private var mVersion = ""

    companion object {
        fun showDialog(
            fragmentManager: FragmentManager,
            tag: String,
            canClose: Boolean,
            versionName: String
        ): CheckUpdateDialog {
            return CheckUpdateDialog().apply {
                mCanClose = canClose
                mVersion = versionName
                show(fragmentManager, tag)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_version_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        EventBus.getDefault().register(this)

        text_confirm.isSelected = true

        text_cancel.isVisible = mCanClose

        text_cancel doClick {
            onCancelClick?.onCancel()
        }

        text_confirm doClick {

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
    var onCancelClick: OnCancelClick? = null

    fun interface OnConfirmClick {
        fun onConfirm()
    }

    fun interface OnCancelClick {
        fun onCancel()
    }

    /**
     * 下载状态
     */
    fun downStartStatus() {
        text_progress.text = "正在下载${AppUtils.getAppName()} V${mVersion}..."
        text_progress_num.text ="已更新0%"


        group_update?.isVisible = true
        group_action?.isVisible = false
    }

    /**
     *
     * @param event DownloadStatusEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun down(event: DownloadStatusEvent) {
        group_update?.isVisible = false
        group_action?.isVisible = true

        if (!event.isSuccess) {
            Toasts.show("下载失败, 请稍后再试")
        }
    }

    /**
     *
     * @param event DownloadStatusEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun down(event: DownloadEvent) {
        text_progress_num.text ="已更新${event.progressDesc}"
    }

}