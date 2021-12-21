package net.cd1369.tbs.android.ui.recommend.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_print_root.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.PrintItemEntity
import net.cd1369.tbs.android.util.V

class PrintRootAdapter : BaseQuickAdapter<PrintItemEntity, BaseViewHolder>(R.layout.item_print_root){

    override fun convert(helper: BaseViewHolder, item: PrintItemEntity) {
        val rvItems = helper.getView<RecyclerView>(R.id.rv_items)
        rvItems.layoutManager = LinearLayoutManager(rvItems.context)
        val adapter = rvItems.adapter as? PrintItemAdapter ?: PrintItemAdapter().also {
            rvItems.adapter = it
        }

        adapter.setNewData(item.articleVOs)

        helper.V.tv_root_name.text = item.subjectName
        helper.V.tv_root_desc.text = item.introduction
    }

}