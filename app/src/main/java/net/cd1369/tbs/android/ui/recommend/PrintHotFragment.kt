package net.cd1369.tbs.android.ui.recommend

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import cn.wl.android.lib.core.ErrorBean
import cn.wl.android.lib.ui.BaseListFragment
import com.blankj.utilcode.util.ConvertUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.internal.ContextUtils
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.PrintItemEntity
import net.cd1369.tbs.android.ui.recommend.adapter.PrintHotAdapter

class PrintHotFragment : BaseListFragment() , OnPrintConsumer {

    private var mSubjectId: String = ""
    private var mHotAdapter: PrintHotAdapter? = null

    companion object {
        fun newIns(id: String): PrintHotFragment {
            val fragment = PrintHotFragment()
            val bundle = Bundle().also {
                it.putString("id", id)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutResource(): Any = R.layout.fragment_print_hot

    override fun createAdapter(): BaseQuickAdapter<*, *>? =
        PrintHotAdapter().also {
            mHotAdapter = it
        }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        mSubjectId = arguments?.getString("id") ?: ""
        isLazyLoaded = true
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        setHolderBackgroundColor(R.color.white)
        val size = ConvertUtils.dp2px(8f)
        rvContent?.updatePadding(top = size, bottom = size)
        rvContent?.clipToPadding = false
        rvContent?.clipChildren = false
    }

    override fun loadData() {
        super.loadData()

        mActivity?.let {
            if (it is PrintActivity) {
                it.retryLoadData()
            }
        }
    }

    override fun doNext(data: List<PrintItemEntity>) {
        mHotAdapter?.setNewData(data)
        mHotAdapter?.loadMoreEnd(true)
    }

}