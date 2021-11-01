package net.cd1369.tbs.android.ui.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentManager
import cn.wl.android.lib.ui.common.LoadingDialog
import cn.wl.android.lib.utils.GlideApp
import cn.wl.android.lib.utils.Toasts
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_daily.*
import kotlinx.android.synthetic.main.dialog_daily.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.entity.DailyEntity
import net.cd1369.tbs.android.event.ArticleCollectEvent
import net.cd1369.tbs.android.event.ArticlePointEvent
import net.cd1369.tbs.android.event.DailyPointCollectChangedEvent
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.fullUrl
import org.greenrobot.eventbus.EventBus
import kotlin.math.max

/**
 * Created by Xiang on 2021/10/15 11:07
 * @description
 * @email Cymbidium@outlook.com
 */
class DailyDialog : BottomSheetDialogFragment() {
    private lateinit var entity: DailyEntity
    private lateinit var context: Activity

    var doShare: Runnable? = null
    var doLogin: Runnable? = null
    private var mPoint: Disposable? = null
    private var mCollect: Disposable? = null
    private var mAlert: LoadingDialog? = null

    companion object {
        fun showDialog(
            fragmentManager: FragmentManager,
            tag: String,
            dailyEntity: DailyEntity,
            activity: Activity,
        ): DailyDialog {
            return DailyDialog().apply {
                show(fragmentManager, tag)
                entity = dailyEntity
                context = activity
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_daily, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlideApp.displayHead(entity.bossHead.fullUrl(), image_head)
        text_content.text = entity.content
        text_name.text = "— —  ${entity.bossName}"
        text_role.text = entity.bossRole

        image_point.isSelected = entity.isPoint
        image_collect.isSelected = entity.isCollect

        image_point doClick {
            doPoint(view)
        }

        image_collect doClick {
            onCollect(view)
        }

        image_share doClick {
            doShare?.run()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT //可以写入自己想要的高度
        }
        val view = view
        view!!.post {
            val parent = view.parent as View
            val params: CoordinatorLayout.LayoutParams =
                parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            val bottomSheetBehavior =
                behavior as BottomSheetBehavior<*>
            bottomSheetBehavior.peekHeight = view.measuredHeight
            parent.setBackgroundResource(R.drawable.ic_daily_bg)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mPoint?.dispose()
        mCollect?.dispose()
        mAlert?.dismiss()
        mAlert = null
    }

    private fun doPoint(view: View) {
        val target = !entity.isPoint
        mPoint?.dispose()

        mPoint = TbsApi.user().obtainDailyPoint(entity.id, target)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                entity.isPoint = target
                view.image_point.isSelected = target


                UserConfig.get().updateUser {
                    val num = if (target) {
                        (it.pointNum ?: 0) + 1
                    } else {
                        (it.pointNum ?: 0) - 1
                    }
                    it.pointNum = max(num, 0)
                }

                EventBus.getDefault().post(
                    ArticlePointEvent(
                        entity.id,
                        doPoint = target,
                        fromHistory = false
                    )
                )
            }, {
                Toasts.show("操作失败")
            })
    }

    private fun onCollect(view: View) {
        if (UserConfig.get().loginStatus) {
            if (entity.isCollect) {
                doCancelCollect(view)
            } else {
                showFolder(view)
            }
        } else {
            doLogin?.run()
        }
    }

    private fun doCancelCollect(view: View) {
        mCollect?.dispose()

        mCollect = TbsApi.user().obtainDailyNoCollect(entity.id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                entity.isCollect = false
                view.image_collect.isSelected = false

                UserConfig.get().updateUser { user ->
                    user.collectNum =
                        max((user.collectNum ?: 0) - 1, 0)
                }
                EventBus.getDefault()
                    .post(
                        ArticleCollectEvent(
                            fromCollect = false,
                            fromFolder = false,
                            articleId = entity.id,
                            doCollect = false,
                        )
                    )
                EventBus.getDefault().post(DailyPointCollectChangedEvent(entity.id, false))

            }, {
                Toasts.show("取消失败")
            })
    }

    private fun showFolder(view: View) {
        showLoadingAlert("尝试获取收藏夹")
        mCollect = TbsApi.user().obtainFolderList()
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { mutableListOf() }
            .subscribe {
                hideLoadingAlert()
                SelectFolderDialog.showDialog(fragmentManager!!, "selectFolder", it)
                    .apply select@{
                        this@select.onCreateClick = SelectFolderDialog.OnCreateClick {
                            doCollectToNewFolder(view, this@select)
                        }
                        this@select.onConfirmClick = SelectFolderDialog.OnConfirmClick { folderId ->
                            doCollectToSelectFolder(view, folderId, this@select)
                        }
                    }
            }
    }

    private fun doCollectToNewFolder(view: View, select: SelectFolderDialog) {
        CreateFolderDialog.showDialog(fragmentManager!!, "createFolder")
            .apply create@{
                this@create.onConfirmClick =
                    CreateFolderDialog.OnConfirmClick { name ->
                        TbsApi.user().obtainCreateFavorite(name)
                            .observeOn(AndroidSchedulers.mainThread())
                            .flatMap { folder ->
                                TbsApi.user()
                                    .obtainDailyCollect(
                                        entity.id,
                                        folder.id
                                    )
                            }
                            .subscribe({
                                entity.isCollect = true
                                view.image_collect.isSelected = true

                                UserConfig.get().updateUser { user ->
                                    user.collectNum =
                                        max(
                                            (user.collectNum ?: 0) + 1,
                                            0
                                        )
                                }
                                EventBus.getDefault()
                                    .post(
                                        ArticleCollectEvent(
                                            fromCollect = false,
                                            fromFolder = false,
                                            articleId = entity.id,
                                            doCollect = true,
                                        )
                                    )
                                EventBus.getDefault().post(
                                    DailyPointCollectChangedEvent(
                                        entity.id,
                                        true
                                    )
                                )

                                select.dismiss()
                                this@create.dismiss()

                            }, {
                                Toasts.show("收藏失败")
                            })
                    }
            }
    }

    @SuppressLint("CheckResult")
    private fun doCollectToSelectFolder(view: View, folderId: String, select: SelectFolderDialog) {
        TbsApi.user().obtainDailyCollect(entity.id, folderId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                entity.isCollect = true
                view.image_collect.isSelected = true

                UserConfig.get().updateUser { user ->
                    user.collectNum =
                        max((user.collectNum ?: 0) + 1, 0)
                }
                EventBus.getDefault()
                    .post(
                        ArticleCollectEvent(
                            fromCollect = false,
                            fromFolder = false,
                            articleId = entity.id,
                            doCollect = true,
                        )
                    )
                EventBus.getDefault().post(
                    DailyPointCollectChangedEvent(
                        entity.id,
                        true
                    )
                )
                select.dismiss()
            }, {
                Toasts.show("收藏失败")
            })
    }


    private fun showLoadingAlert(msg: String) {
        if (mAlert == null) {
            mAlert = LoadingDialog(context)
        }
        mAlert!!.setMsg(msg)
        mAlert!!.show()

        val window = mAlert!!.window
        val dm = DisplayMetrics()
        context?.windowManager?.defaultDisplay?.getMetrics(dm)
        val width = dm.widthPixels * 0.8
        val height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
        val params = window!!.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params
        window.setLayout(width.toInt(), height)
        window.attributes = params
        window.setLayout(width.toInt(), height)
        window.setBackgroundDrawableResource(cn.wl.android.lib.R.drawable.draw_dialog_loading)
        mAlert!!.setCanceledOnTouchOutside(false)
        mAlert!!.setCancelable(false)
    }

    private fun hideLoadingAlert() {
        if (mAlert != null) {
            mAlert?.dismiss()
            mAlert = null
        }
    }
}