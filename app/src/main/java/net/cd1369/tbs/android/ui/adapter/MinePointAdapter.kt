package net.cd1369.tbs.android.ui.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.HisFavEntity

/**
 * Created by Xiang on 2021/10/21 18:14
 * @description
 * @email Cymbidium@outlook.com
 */
class MinePointAdapter : BaseMultiItemQuickAdapter<HisFavEntity, BaseViewHolder>(mutableListOf()) {
    init {
        addItemType(0, R.layout.item_article_onlytext_withcontent)
        addItemType(1, R.layout.item_article_singleimg_withcontent)
    }

    override fun convert(helper: BaseViewHolder, item: HisFavEntity) {

    }
}