package net.cd1369.tbs.android.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.utils.GlideApp
import com.youth.banner.adapter.BannerAdapter
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.BannerEntity
import net.cd1369.tbs.android.ui.home.ArticleActivity
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Xiang on 2021/8/28 13:43
 * @description
 * @email Cymbidium@outlook.com
 */
class BannerTitleAdapter(val context: Context, list: List<BannerEntity>) :
    BannerAdapter<BannerEntity, BannerTitleAdapter.ImageTitleHolder>(list) {

    class ImageTitleHolder(@NonNull view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.image_banner_cover)
        var titleView: TextView = view.findViewById(R.id.text_banner_title)
    }

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): ImageTitleHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.banner_view, parent, false)
        return ImageTitleHolder(view)
    }

    override fun onBindView(
        holder: ImageTitleHolder?,
        data: BannerEntity?,
        position: Int,
        size: Int
    ) {
        GlideApp.display(data!!.pictureLocation, holder!!.imageView)
        holder.titleView.paint.isFakeBoldText = true
        holder.titleView.text = data.content

        holder.imageView doClick {
            ArticleActivity.start(context, data.resourceId)
        }
    }
}
