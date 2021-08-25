package net.cd1369.tbs.android.ui.test

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.palette.graphics.Palette
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.GlideApp
import com.blankj.utilcode.util.ImageUtils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_test.*
import net.cd1369.tbs.android.R

class TestActivity : BaseActivity() {
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

        collapse_view.contentScrim = resources.getDrawable(R.drawable.ic_boss_top_bg)
    }
}