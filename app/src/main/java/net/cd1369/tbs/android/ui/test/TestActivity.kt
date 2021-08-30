package net.cd1369.tbs.android.ui.test

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import cn.wl.android.lib.ui.BaseActivity
import com.blankj.utilcode.util.ConvertUtils
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_test.*
import net.cd1369.tbs.android.R
import kotlin.math.absoluteValue

class TestActivity : BaseActivity() {

    private var mLayoutH: Int = 0
    private var mBgDraw: Drawable? = null

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, TestActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_test
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
//        val bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_boss_top_bg)
//        val color: Int =
//            Palette.from(bitmap).generate()
//                .getLightMutedColor(resources.getColor(R.color.colorAccent))

//        collapse_view.contentScrim = resources.getDrawable(R.drawable.ic_boss_top_bg)

        app_root.post {
            mBgDraw = view_mask.background
            mBgDraw?.alpha = 0

            mLayoutH = app_root.height + ConvertUtils.dp2px(24F)
        }

        app_root.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            var fl = (mLayoutH - verticalOffset.absoluteValue).toFloat() / mLayoutH
            mBgDraw?.alpha = ((1F - fl) * 255).toInt()
        })
    }

}