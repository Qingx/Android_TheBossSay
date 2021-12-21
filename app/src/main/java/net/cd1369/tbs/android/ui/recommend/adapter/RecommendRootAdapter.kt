package net.cd1369.tbs.android.ui.recommend.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import cn.wl.android.lib.utils.GlideApp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_recommend_root.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.RecommendEntity
import net.cd1369.tbs.android.data.entity.RecommendItemEntity
import net.cd1369.tbs.android.util.V
import net.cd1369.tbs.android.util.fullUrl

class RecommendRootAdapter() :
    BaseQuickAdapter<RecommendEntity, RootVH>(R.layout.item_recommend_root) {

    override fun createBaseViewHolder(parent: ViewGroup?, layoutResId: Int): RootVH {
        return RootVH(mLayoutInflater.inflate(layoutResId, parent, false))
    }

    override fun convert(helper: RootVH, item: RecommendEntity) {
        GlideApp.display(
            item.cover.fullUrl(),
            helper.V.iv_rec_img
        )
        helper.V.tv_rec_title.text = item.subjectName
        helper.mAdapter.setNewData(item.subjectRecommend)
    }

}

class RootVH(v: View) : BaseViewHolder(v) {

    var mAdapter: RecommendItemAdapter

    init {
        v.rv_items.layoutManager = LinearLayoutManager(v.context)
        v.rv_items.adapter = RecommendItemAdapter().also {
            mAdapter = it
        }
    }

}