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

class RecommendRootAdapter() :
    BaseQuickAdapter<RecommendEntity, RootVH>(R.layout.item_recommend_root) {

    override fun createBaseViewHolder(parent: ViewGroup?, layoutResId: Int): RootVH {
        return RootVH(mLayoutInflater.inflate(layoutResId, parent, false))
    }

    override fun convert(helper: RootVH, item: RecommendEntity) {
        GlideApp.display(
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Ffile02.16sucai.com%2Fd%2Ffile%2F2014%2F0829%2F372edfeb74c3119b666237bd4af92be5.jpg&refer=http%3A%2F%2Ffile02.16sucai.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1642163406&t=1a2873b7a292a9f9076a14aaf5c7bd64",
            helper.V.iv_rec_img
        )
        helper.V.tv_rec_title.text = "标题${helper.layoutPosition}"
        helper.mAdapter.setNewData(MutableList(5) {
            RecommendItemEntity()
        })
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