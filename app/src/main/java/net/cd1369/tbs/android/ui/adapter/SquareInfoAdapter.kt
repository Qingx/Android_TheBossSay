package net.cd1369.tbs.android.ui.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.GridLayoutManager
import cn.wl.android.lib.utils.DateFormat
import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_article_onlytext_nocontent.view.*
import kotlinx.android.synthetic.main.item_article_onlytext_nocontent.view.text_info
import kotlinx.android.synthetic.main.item_article_onlytext_nocontent.view.text_title
import kotlinx.android.synthetic.main.item_article_singleimg_nocontent.view.*
import kotlinx.android.synthetic.main.item_article_triimg_nocontent.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.ArticleEntity
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
        addItemType(2, R.layout.item_article_triimg_nocontent)
    }

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: ArticleEntity) {
        when (helper.itemViewType) {
            0 -> {
                helper.V.text_title.text = item.title
                GlideApp.display(item.bossVO.head.avatar(), helper.V.image_head)
                helper.V.text_name.text = item.bossVO.name
                helper.V.text_info.text = item.bossVO.role
                helper.V.text_hot.text = "${item.collect}k收藏·${item.point}w人围观"
                helper.V.text_time.text = DateFormat.date2yymmdd(item.createTime)
            }
            1 -> {
                helper.V.text_title.text = item.title
                GlideApp.display(item.bossVO.head.avatar(), helper.V.image_head)
                GlideApp.display(item.bossVO.head.avatar(), helper.V.image_res)
                helper.V.text_name.text = item.bossVO.name
                helper.V.text_info.text = item.bossVO.role
                helper.V.text_hot.text = "${item.collect}k收藏·${item.point}w人围观"
                helper.V.text_time.text = DateFormat.date2yymmdd(item.createTime)
            }
            2 -> {
                helper.V.text_title.text = item.title
                val adapter = GridImageAdapter()
                helper.V.rv_content.adapter = adapter
                adapter.setNewData(item.files)
                helper.V.rv_content.layoutManager = object : GridLayoutManager(mContext, 3) {
                    override fun canScrollHorizontally(): Boolean {
                        return false
                    }

                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
                helper.V.text_info.text = "广告·海南万科"
            }
        }

        helper.V doClick {
            onClick(item)
        }
    }

    abstract fun onClick(item: ArticleEntity)
}