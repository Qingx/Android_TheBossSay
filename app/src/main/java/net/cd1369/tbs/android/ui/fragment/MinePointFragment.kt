package net.cd1369.tbs.android.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.data.core.HttpConfig
import cn.wl.android.lib.ui.BaseListFragment
import cn.wl.android.lib.utils.Toasts
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.tendcloud.tenddata.TCAgent
import com.tendcloud.tenddata.TDProfile
import kotlinx.android.synthetic.main.empty_follow_article.view.*
import kotlinx.android.synthetic.main.fragment_speech_tack_content.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.Const
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.cache.CacheConfig
import net.cd1369.tbs.android.data.entity.DailyEntity
import net.cd1369.tbs.android.data.entity.HisFavEntity
import net.cd1369.tbs.android.event.ArticlePointEvent
import net.cd1369.tbs.android.event.DailyPointCollectChangedEvent
import net.cd1369.tbs.android.event.LoginEvent
import net.cd1369.tbs.android.ui.adapter.MinePointAdapter
import net.cd1369.tbs.android.ui.dialog.DailyDialog
import net.cd1369.tbs.android.ui.dialog.ShareDialog
import net.cd1369.tbs.android.ui.home.WebArticleActivity
import net.cd1369.tbs.android.ui.home.DailyPosterActivity
import net.cd1369.tbs.android.util.JPushHelper
import net.cd1369.tbs.android.util.Tools
import net.cd1369.tbs.android.util.doShareSession
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.max

/**
 * Created by Xiang on 2021/10/21 17:02
 * @description
 * @email Cymbidium@outlook.com
 */
class MinePointFragment : BaseListFragment() {
    private lateinit var type: String
    private lateinit var mAdapter: MinePointAdapter
    private var needLoading = true

    companion object {
        fun createFragment(type: String): MinePointFragment {
            return MinePointFragment().apply {
                arguments = Bundle().apply {
                    putString("type", type)
                }
            }
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_speech_tack_content
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        type = arguments?.getString("type") as String
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : MinePointAdapter() {
            override fun onContentClick(entity: HisFavEntity) {
                if (type == "1") {
                    WebArticleActivity.start(mActivity, entity.articleId)
                } else {
                    DailyDialog.showDialog(
                        requireFragmentManager(),
                        "dailyDialog",
                        entity.toDaily(),
                        mActivity
                    )
                        .apply {
                            doShare = Runnable {
                                ShareDialog.showDialog(
                                    requireFragmentManager(),
                                    "shareDialog",
                                    true
                                )
                                    .apply {
                                        onSession = Runnable {
                                            doShareWechat(entity.toDaily())
                                        }
                                        onTimeline = Runnable {
                                            doShareTimeline(entity.toDaily())
                                        }
                                        onCopyLink = Runnable {
                                            Tools.copyText(mActivity, Const.SHARE_URL)
                                        }
                                        onPoster = Runnable {
                                            DailyPosterActivity.start(mActivity, entity.toDaily())
                                            this.dismiss()
                                        }
                                    }
                            }
                            doLogin = Runnable {
                                JPushHelper.jumpLogin(mActivity) { token ->
                                    TbsApi.user().obtainJverifyLogin(token)
                                        .bindDefaultSub(doNext = {
                                            HttpConfig.saveToken(it.token)
                                            UserConfig.get().loginStatus = true
                                            val userInfo = it.userInfo
                                            UserConfig.get().userEntity = userInfo

                                            TCAgent.onLogin(
                                                userInfo.id,
                                                TDProfile.ProfileType.WEIXIN,
                                                userInfo.nickName
                                            )
                                            CacheConfig.clearBoss()
                                            CacheConfig.clearArticle()

                                            JPushHelper.tryAddTags(
                                                it.userInfo.tags ?: mutableListOf()
                                            )
                                            JPushHelper.tryAddAlias(it.userInfo.id)

                                            eventBus.post(LoginEvent())
                                        }, doFail = {
                                            Toasts.show("登录失败，${it.msg}")
                                        }, doLast = {
                                            hideLoadingAlert()
                                        })
                                }
                            }
                        }
                }
            }

            override fun onContentDelete(entity: HisFavEntity, doRemove: (id: String) -> Unit) {
                if (type == "1") {
                    removeArticle(entity, doRemove)
                } else {
                    removeDaily(entity, doRemove)
                }
            }
        }.also {
            mAdapter = it
        }
    }

    private fun doShareWechat(entity: DailyEntity) {
        doShareSession(
            resources,
            entity.bossHead,
            title = "分享一段${entity.bossName}的语录，深有感触",
            des = entity.content
        )
    }

    private fun doShareTimeline(entity: DailyEntity) {
        net.cd1369.tbs.android.util.doShareTimeline(
            resources,
            entity.bossHead,
            title = "分享一段${entity.bossName}的语录，深有感触",
            des = entity.content
        )
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        eventBus.register(this)

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData(false)
        }

        rv_content.adapter = mAdapter
        rv_content.layoutManager =
            object : LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        val emptyView = LayoutInflater.from(mActivity)
            .inflate(R.layout.empty_follow_article, null)
        emptyView.text_notice.text = "暂无历史记录"
        mAdapter.emptyView = emptyView
    }

    private fun removeArticle(entity: HisFavEntity, doRemove: (id: String) -> Unit) {
        showLoadingAlert("正在取消...")

        TbsApi.boss().switchPointStatus(entity.articleId, false)
            .bindToastSub("取消成功") {
                doRemove.invoke(entity.articleId)

                UserConfig.get().updateUser {
                    it.pointNum = max((it.pointNum ?: 0) - 1, 0)
                }
                eventBus.post(
                    ArticlePointEvent(
                        entity.articleId,
                        doPoint = false,
                        fromHistory = true
                    )
                )
            }
    }

    private fun removeDaily(entity: HisFavEntity, doRemove: (id: String) -> Unit) {
        showLoadingAlert("正在取消...")

        TbsApi.user().obtainDailyPoint(entity.articleId, false)
            .bindToastSub("取消成功") {
                doRemove.invoke(entity.articleId)

                UserConfig.get().updateUser {
                    it.pointNum = max((it.pointNum ?: 0) - 1, 0)
                }
                eventBus.post(
                    ArticlePointEvent(
                        entity.articleId,
                        doPoint = false,
                        fromHistory = true
                    )
                )
            }
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        if (!loadMore && needLoading) {
            showLoading()
        }

        TbsApi.user().obtainPointList(pageParam, type)
            .onErrorReturn { Page.empty() }
            .bindPageSubscribe(loadMore, null, {
                showContent()

                if (loadMore) {
                    layout_refresh.finishLoadMore()
                } else {
                    layout_refresh.finishRefresh()
                }
            }, {
                if (loadMore) {
                    mAdapter.addData(it)
                } else {
                    mAdapter.setNewData(it)
                }
            })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: ArticlePointEvent) {
        if (!event.fromHistory) {
            if (event.doPoint) {
                layout_refresh.autoRefresh()
            } else {
                val index = mAdapter.data.indexOfFirst {
                    it.articleId == event.id
                }

                if (index != -1) {
                    mAdapter.remove(index)
                    mAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: DailyPointCollectChangedEvent) {
        val index = mAdapter.data.indexOfFirst {
            it.articleId == event.id
        }

        if (index != -1) {
            mAdapter.data[index].isCollect = event.target
        }
    }
}