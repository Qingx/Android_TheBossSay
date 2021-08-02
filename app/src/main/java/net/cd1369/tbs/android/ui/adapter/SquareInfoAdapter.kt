package net.cd1369.tbs.android.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import cn.wl.android.lib.utils.DateFormat
import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_ad_layout.view.*
import kotlinx.android.synthetic.main.item_article_onlytext_nocontent.view.image_head
import kotlinx.android.synthetic.main.item_article_onlytext_nocontent.view.text_hot
import kotlinx.android.synthetic.main.item_article_onlytext_nocontent.view.text_info
import kotlinx.android.synthetic.main.item_article_onlytext_nocontent.view.text_name
import kotlinx.android.synthetic.main.item_article_onlytext_nocontent.view.text_time
import kotlinx.android.synthetic.main.item_article_onlytext_nocontent.view.text_title
import kotlinx.android.synthetic.main.item_article_singleimg_nocontent.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.util.AdvanceAD
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.avatar
import net.cd1369.tbs.android.util.doClick


/**
 * Created by Qing on 2021/6/30 10:36 上午
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class SquareInfoAdapter :
    BaseMultiItemQuickAdapter<ArticleEntity, BaseViewHolder>(mutableListOf()) {
    init {
        addItemType(0, R.layout.item_article_onlytext_nocontent)
        addItemType(1, R.layout.item_article_singleimg_nocontent)
        addItemType(ArticleEntity.AD_TYPE, R.layout.item_ad_layout)
    }

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder {
        if (viewType == ArticleEntity.AD_TYPE) {
            var inflate = mLayoutInflater.inflate(R.layout.item_ad_layout, parent, false)
            var adVH = AdVH(inflate)
            adVH.ad = AdvanceAD(mContext as Activity?)
            return adVH
        }
        return super.onCreateDefViewHolder(parent, viewType)
    }

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: ArticleEntity) {
        when (helper.itemViewType) {
            0 -> {
                helper.V.text_title.text = item.title
                GlideApp.displayHead(item.bossVO.head.avatar(), helper.V.image_head)
                helper.V.text_name.text = item.bossVO.name
                helper.V.text_info.text = item.bossVO.role
                helper.V.text_hot.text = "${item.collect}k收藏·${item.point}w人围观"
                helper.V.text_time.text = DateFormat.date2yymmdd(item.createTime)
            }
            1 -> {
                helper.V.text_title.text = item.title
                GlideApp.displayHead(item.bossVO.head.avatar(), helper.getView(R.id.image_head))
                GlideApp.display(item.files?.get(0) ?: "", helper.V.image_res)
                helper.V.text_name.text = item.bossVO.name
                helper.V.text_info.text = item.bossVO.role
                helper.V.text_hot.text =
                    "${item.collect}k收藏·${item.point}w人围观"
                helper.V.text_time.text = DateFormat.date2yymmdd(item.createTime)
            }
            ArticleEntity.AD_TYPE -> {
                if (helper is AdVH) {
                    //核心步骤：如果是广告布局，执行广告加载
                    helper.ad?.loadNativeExpress(helper.itemView.fl_ad)
                }
            }
        }

        helper.V doClick {
            onClick(item)
        }
    }

    abstract fun onClick(item: ArticleEntity)
}

class AdVH(iv: View) : BaseViewHolder(iv) {
    var ad: AdvanceAD? = null
}