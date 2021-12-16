package net.cd1369.tbs.android.ui.adapter

import android.annotation.SuppressLint
import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_folder.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.FolderEntity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.fullUrl

/**
 * Created by Xiang on 2021/10/26 13:01
 * @description
 * @email Cymbidium@outlook.com
 */
abstract class FolderAdapter :
    BaseQuickAdapter<FolderEntity, BaseViewHolder>(R.layout.item_folder) {
    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: FolderEntity) {
        GlideApp.display(item.cover.fullUrl(), helper.V.image_cover, R.drawable.ic_article_cover)
        helper.V.text_name.text = item.name
        helper.V.text_content.text = "${item.articleCount}个内容· ${item.bossCount}个Boss"

        helper.V.layout_content doClick {
            onItemClick(item)
        }

        helper.V.text_delete doClick {
            onDeleteClick(item)
        }
    }

    abstract fun onItemClick(item: FolderEntity?)

    abstract fun onDeleteClick(item: FolderEntity)
}