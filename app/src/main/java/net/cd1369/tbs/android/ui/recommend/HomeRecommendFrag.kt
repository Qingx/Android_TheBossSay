package net.cd1369.tbs.android.ui.recommend

import android.os.Bundle
import android.view.View
import cn.wl.android.lib.ui.BaseListFragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.fragment_home_recommend.*
import kotlinx.android.synthetic.main.fragment_home_recommend.layout_refresh
import kotlinx.android.synthetic.main.fragment_speech_tack_content.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.RecommendEntity
import net.cd1369.tbs.android.ui.recommend.adapter.RecommendRootAdapter

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 14:51 2021/12/15
 * @desc 首页推荐列表
 */
class HomeRecommendFrag : BaseListFragment() {

    private lateinit var mRootAdapter: RecommendRootAdapter

    companion object {

        fun newIns(): HomeRecommendFrag {
            val fragment = HomeRecommendFrag()
            val bundle = Bundle().also {

            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutResource(): Any =
        R.layout.fragment_home_recommend

    override fun createAdapter(): BaseQuickAdapter<*, *>? = RecommendRootAdapter().also {
        mRootAdapter = it
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            loadData(false)
        }
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        timerDelay(1000) {
            mRootAdapter.setNewData(MutableList(4) {
                RecommendEntity()
            })

            showContent()
            tryCompleteStatus(false)
        }
    }

    override fun tryFinishRefresh() {
//        super.tryFinishRefresh()
        layout_refresh?.finishRefresh()
    }

}