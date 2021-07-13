package net.cd1369.tbs.android.ui.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.cd1369.tbs.android.data.entity.ArticleEntity

/**
 * Created by Xiang on 2021/7/13 16:28
 * @description
 * @email Cymbidium@outlook.com
 */
class FollowArticleAdapter :
    BaseMultiItemQuickAdapter<ArticleEntity, BaseViewHolder>(mutableListOf()) {
    override fun convert(helper: BaseViewHolder?, item: ArticleEntity?) {

    }
}