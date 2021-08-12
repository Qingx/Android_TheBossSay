package net.cd1369.tbs.android.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.ui.BaseListFragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.empty_boss_card_view.view.*
import kotlinx.android.synthetic.main.fragment_speech_tack_content.layout_refresh
import kotlinx.android.synthetic.main.fragment_speech_tack_content.rv_content
import kotlinx.android.synthetic.main.header_speech_content.*
import kotlinx.android.synthetic.main.header_speech_content.view.layout_title
import kotlinx.android.synthetic.main.header_speech_content.view.rv_card
import kotlinx.android.synthetic.main.header_speech_content.view.text_num
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.data.entity.BossLabelEntity
import net.cd1369.tbs.android.event.FollowBossEvent
import net.cd1369.tbs.android.event.LoginEvent
import net.cd1369.tbs.android.ui.adapter.FollowCardAdapter
import net.cd1369.tbs.android.ui.adapter.FollowInfoAdapter
import net.cd1369.tbs.android.ui.home.ArticleActivity
import net.cd1369.tbs.android.ui.home.BossHomeActivity
import net.cd1369.tbs.android.ui.home.HomeBossAllActivity
import net.cd1369.tbs.android.util.doClick
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Xiang on 2021/8/11 10:05
 * @description
 * @email Cymbidium@outlook.com
 */
class SpeechTackContentFragment : BaseListFragment() {
    private lateinit var mLabel: String
    private var cardEmptyView: View? = null
    private var mRootHeight: Int = 0
    private var headerView: View? = null
    private var mEmptyView: View? = null

    private lateinit var cardAdapter: FollowCardAdapter
    private lateinit var mAdapter: FollowInfoAdapter
    private lateinit var mBossList: MutableList<BossInfoEntity>

    companion object {
        fun createFragment(label: String): SpeechTackContentFragment {
            return SpeechTackContentFragment().apply {
                arguments = Bundle().apply {
                    putString("label", label)
                }
            }
        }
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        mLabel = arguments?.getString("label") as String
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : FollowInfoAdapter() {
            override fun onClick(item: ArticleEntity) {
                ArticleActivity.start(mActivity, item.id)
            }
        }.also {
            mAdapter = it
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_speech_tack_content
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        eventBus.register(this)

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            loadData(false)
        }

        cardAdapter = object : FollowCardAdapter() {
            override fun onClick(item: BossInfoEntity) {
                BossHomeActivity.start(mActivity, entity = item)
            }
        }

        headerView = LayoutInflater.from(mActivity).inflate(R.layout.header_speech_content, null)
        headerView!!.rv_card.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
        headerView!!.rv_card.adapter = cardAdapter
        mAdapter.addHeaderView(headerView)

        rv_content.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        cardEmptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_boss_card_view, null)
        cardAdapter.emptyView = cardEmptyView

        cardEmptyView!!.text_add doClick {
            HomeBossAllActivity.start(mActivity)
        }

        layout_refresh.post {
            mRootHeight = layout_refresh.height
        }
    }

    private fun showContentEmpty(show: Boolean) {
        if (mEmptyView == null) {
            mEmptyView = vs_follow_empty.inflate()

            var sumHeight = headerView?.rv_card?.height ?: 0
            sumHeight += headerView?.layout_title?.height ?: 0

            val h = mRootHeight - sumHeight
            mEmptyView!!.updateLayoutParams {
                this.height = h
            }
        }

        mEmptyView?.isVisible = show
    }

    @SuppressLint("SetTextI18n")
    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        if (!loadMore) {
            pageParam?.resetPage()

            TbsApi.boss().obtainFollowBossList(mLabel, true)
                .onErrorReturn { mutableListOf() }
                .flatMap {
                    mBossList = it

                    TbsApi.boss().obtainFollowArticle(mLabel, pageParam)
                }.onErrorReturn { Page.empty() }
                .bindPageSubscribe(
                    loadMore = false,
                    doNext = {
                        cardAdapter.setNewData(mBossList)

                        headerView!!.text_num.text = "共${(pageParam?.total ?: 0)}篇"
                        mAdapter.setNewData(it)

                    },
                    doDone = {
                        showContent()

                        val userEntity = UserConfig.get().userEntity
                        val traceNum = userEntity.traceNum ?: 0

                        if (traceNum > 0) {
                            cardEmptyView?.text_notice?.text = "追踪的老板暂无言论更新"
                        } else {
                            cardEmptyView?.text_notice?.text = "当前还没有追踪的老板"
                        }

                        layout_refresh.finishRefresh()

                        showContentEmpty(mAdapter.data.isNullOrEmpty())
                    }
                )
        } else {
            TbsApi.boss().obtainFollowArticle(mLabel, pageParam)
                .bindPageSubscribe(
                    loadMore = true,
                    doNext = {
                        mAdapter.addData(it)
                    },
                    doDone = {
                        layout_refresh.finishLoadMore()
                    })
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: FollowBossEvent) {
        if (mLabel == BossLabelEntity.empty.id || event.labels?.contains(mLabel) == true) {
            layout_refresh.autoRefresh()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: LoginEvent) {
        loadData(false)
    }
}