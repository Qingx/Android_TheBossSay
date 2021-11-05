package net.cd1369.tbs.android.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.view.View
import cn.wl.android.lib.config.WLConfig
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.DirHelper
import cn.wl.android.lib.utils.GlideApp
import cn.wl.android.lib.utils.Times
import cn.wl.android.lib.utils.Toasts
import com.blankj.utilcode.util.ImageUtils
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_daily_poster.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.DailyEntity
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.doShareImgSession
import net.cd1369.tbs.android.util.doShareImgTimeline
import net.cd1369.tbs.android.util.fullUrl
import java.io.File

/**
 * Created by Xiang on 2021/10/21 16:48
 * @description 每日一言海报
 * @email Cymbidium@outlook.com
 */
class DailyPosterActivity : BaseActivity() {
    private var permission: Disposable? = null
    private lateinit var entity: DailyEntity

    companion object {
        fun start(context: Context?, dailyEntity: DailyEntity) {
            val intent = Intent(context, DailyPosterActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtras(Bundle().apply {
                        putSerializable("entity", dailyEntity)
                    })
                }
            context!!.startActivity(intent)
        }

        /**
         * 预申请动态权限
         */
        val mPer = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_daily_poster
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        entity = intent.getSerializableExtra("entity") as DailyEntity
    }

    @SuppressLint("SetTextI18n")
    override fun initViewCreated(savedInstanceState: Bundle?) {
        text_title.text = "生成海报"
        GlideApp.displayHead(entity.bossHead.fullUrl(), image_head)
        text_content.text = entity.content
        text_name.text = entity.bossName
        text_role.text = entity.bossRole

        image_back doClick {
            onBackPressed()
        }

        layout_session doClick {
            shareSession()
        }

        layout_timeline doClick {
            shareTimeline()
        }

        layout_save doClick {
            trySaveImg()
        }
    }

    private fun shareSession() {
        val view = findViewById<View>(R.id.layout_poster)
        val bitmap = ImageUtils.view2Bitmap(view)
        doShareImgSession(bitmap)
    }

    private fun shareTimeline() {
        val view = findViewById<View>(R.id.layout_poster)
        val bitmap = ImageUtils.view2Bitmap(view)
        doShareImgTimeline(bitmap)
    }

    private fun trySaveImg() {
        permission?.dispose()

        permission = rxPermission.request(*mPer).subscribe {
            if (it) {
                val view = findViewById<View>(R.id.layout_poster)
                val bitmap = ImageUtils.view2Bitmap(view)
                val path = DirHelper.getImageDir() + "daily${Times.current()}.jpg"

                ImageUtils.save(bitmap, File(path), Bitmap.CompressFormat.JPEG)
                MediaScannerConnection.scanFile(
                    WLConfig.getContext(),
                    arrayOf(path), null, null
                )
                Toasts.show("保存成功")
            } else {
                Toasts.show("获取权限失败")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        permission?.dispose()
    }
}