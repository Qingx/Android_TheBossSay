package net.cd1369.tbs.android.ui.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.utils.GlideApp
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.util.BannerUtils
import net.cd1369.tbs.android.data.entity.BannerEntity
import net.cd1369.tbs.android.ui.home.WebArticleActivity
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Xiang on 2021/8/18 15:01
 * @description
 * @email Cymbidium@outlook.com
 */
@Deprecated("新增BannerTitleAdapter")
class BannerViewAdapter(val context: Context, list: List<BannerEntity>) :
    BannerAdapter<BannerEntity?, BannerViewAdapter.BannerViewHolder?>(list) {

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder? {
        val imageView = ImageView(parent.context)

        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

        BannerUtils.setBannerRound(imageView, 16f)
        return BannerViewHolder(imageView)
    }

    class BannerViewHolder(@NonNull view: ImageView) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view
    }

    override fun onBindView(
        holder: BannerViewHolder?,
        data: BannerEntity?,
        position: Int,
        size: Int
    ) {
        GlideApp.display(data!!.pictureLocation, holder!!.imageView)

        holder.imageView doClick {
            WebArticleActivity.start(context, data.resourceId)
        }
    }
}