package net.cd1369.tbs.android.ui.recommend

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.LinearLayoutManager
import cn.wl.android.lib.ui.BaseListFragment
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.fragment_home_recommend.*
import kotlinx.android.synthetic.main.fragment_home_recommend.layout_refresh
import kotlinx.android.synthetic.main.fragment_speech_tack_content.*
import kotlinx.android.synthetic.main.layout_recommend_header.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.data.entity.RecommendEntity
import net.cd1369.tbs.android.ui.recommend.adapter.RecommendCardAdapter
import net.cd1369.tbs.android.ui.recommend.adapter.RecommendRootAdapter

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 14:51 2021/12/15
 * @desc 首页推荐列表
 */
class HomeRecommendFrag : BaseListFragment() {

    private var mHeaderView: View? = null
    private lateinit var mCardAdapter: RecommendCardAdapter
    private lateinit var mRootAdapter: RecommendRootAdapter

    companion object {

        private val WH_RATIO = 110.0 / 140

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

        val screenWidth = ScreenUtils.getScreenWidth()
        val itemW = (screenWidth - 4 * ConvertUtils.dp2px(16F)) / 3
        val itemH = (itemW / WH_RATIO).toInt()

        addHeader(itemW, itemH)
    }

    private fun addHeader(itemW: Int, itemH: Int) {
        val headerView = layoutInflater.inflate(
            R.layout.layout_recommend_header, null
        )

        val layoutParams = headerView.rv_card.layoutParams
        layoutParams.height = itemH
        headerView.rv_card.layoutParams = layoutParams

        headerView.rv_card.layoutManager = LinearLayoutManager(
            mActivity, LinearLayoutManager.HORIZONTAL, false
        )
        headerView.rv_card.adapter = RecommendCardAdapter().also {
            mCardAdapter = it
        }

        mRootAdapter.addHeaderView(headerView)

        mHeaderView = headerView
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        TbsApi.boss().obtainHomeRecommend()
            .bindListSubscribe {
                showContent()

                mCardAdapter.setNewData(it)
                mRootAdapter.setNewData(it)
            }
    }

    override fun tryFinishRefresh() {
        layout_refresh?.finishRefresh()
    }

    override fun onDestroyView() {
        mHeaderView = null
        super.onDestroyView()
    }

    fun tryDispatchTouchEvent(ev: MotionEvent) {

    }

}