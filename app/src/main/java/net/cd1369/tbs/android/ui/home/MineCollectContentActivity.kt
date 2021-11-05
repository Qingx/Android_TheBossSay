package net.cd1369.tbs.android.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import cn.wl.android.lib.core.Page
import cn.wl.android.lib.data.core.HttpConfig
import cn.wl.android.lib.ui.BaseListActivity
import cn.wl.android.lib.utils.GlideApp
import cn.wl.android.lib.utils.Toasts
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.tendcloud.tenddata.TCAgent
import com.tendcloud.tenddata.TDProfile
import kotlinx.android.synthetic.main.activity_mine_collect_content.*
import kotlinx.android.synthetic.main.empty_follow_article.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.Const
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.cache.CacheConfig
import net.cd1369.tbs.android.data.entity.DailyEntity
import net.cd1369.tbs.android.data.entity.FolderEntity
import net.cd1369.tbs.android.data.entity.HisFavEntity
import net.cd1369.tbs.android.event.ArticleCollectEvent
import net.cd1369.tbs.android.event.ArticlePointEvent
import net.cd1369.tbs.android.event.LoginEvent
import net.cd1369.tbs.android.ui.adapter.FolderContentAdapter
import net.cd1369.tbs.android.ui.dialog.DailyDialog
import net.cd1369.tbs.android.ui.dialog.ShareDialog
import net.cd1369.tbs.android.util.*
import net.cd1369.tbs.android.util.doShareSession
import net.cd1369.tbs.android.util.fullUrl
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.max

class MineCollectContentActivity : BaseListActivity() {
    private var needLoading: Boolean = true
    private lateinit var mAdapter: FolderContentAdapter
    private lateinit var folderEntity: FolderEntity

    companion object {
        fun start(context: Context?, entity: FolderEntity) {
            val intent = Intent(context, MineCollectContentActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtras(Bundle().apply {
                        putSerializable("entity", entity)
                    })
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_mine_collect_content
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)
        folderEntity = intent.getSerializableExtra("entity") as FolderEntity
    }

    @SuppressLint("SetTextI18n")
    override fun initViewCreated(savedInstanceState: Bundle?) {
        eventBus.register(this)

        GlideApp.display(folderEntity.cover.fullUrl(), image_cover, R.drawable.ic_article_cover)
        text_name.text = folderEntity.name
        text_content.text = "${folderEntity.articleCount}个内容· ${folderEntity.bossCount}个Boss"

        layout_refresh.setRefreshHeader(ClassicsHeader(mActivity))
        layout_refresh.setHeaderHeight(60f)

        layout_refresh.setOnRefreshListener {
            needLoading = false
            loadData(false)
        }

        rv_content.layoutManager = object : LinearLayoutManager(mActivity) {
            override fun canScrollHorizontally(): Boolean = false
        }
        rv_content.adapter = mAdapter

        val emptyView = LayoutInflater.from(mActivity)
            .inflate(R.layout.empty_follow_article, null)
        emptyView.text_notice.text = "暂无收藏记录"
        mAdapter.emptyView = emptyView

        image_back doClick {
            onBackPressed()
        }
    }

    override fun createAdapter(): BaseQuickAdapter<*, *>? {
        return object : FolderContentAdapter() {
            override fun onArticleClick(item: HisFavEntity) {
                tryClickArticle(item)
            }

            override fun onDailyClick(item: HisFavEntity) {
                tryClickDaily(item)
            }

            override fun onArticleDelete(item: HisFavEntity) {
                tryDeleteArticle(item)
            }

            override fun onDailyDelete(item: HisFavEntity) {
                tryDeleteDaily(item)
            }
        }.also {
            mAdapter = it
        }
    }

    override fun loadData(loadMore: Boolean) {
        super.loadData(loadMore)

        val searchId: String

        if (!loadMore) {
            pageParam?.resetPage()
            searchId = ""

            if (needLoading) {
                showLoading()
            }
        } else {
            searchId = if (mAdapter.data.isNullOrEmpty()) {
                ""
            } else {
                mAdapter.data[mAdapter.data.size - 1].articleId
            }
        }

        TbsApi.user().obtainFolderArticle(pageParam, folderEntity.id, searchId)
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

    private fun tryClickArticle(item: HisFavEntity) {
        ArticleActivity.start(mActivity, item.articleId, false)
    }

    private fun tryClickDaily(item: HisFavEntity) {
        DailyDialog.showDialog(supportFragmentManager, "dailyDialg", item.toDaily(), mActivity)
            .apply {
                doShare = Runnable {
                    ShareDialog.showDialog(supportFragmentManager, "shareDialog", true)
                        .apply {
                            onSession = Runnable {
                                doShareWechat(item.toDaily())
                            }
                            onTimeline = Runnable {
                                doShareTimeline(item.toDaily())
                            }
                            onCopyLink = Runnable {
                                Tools.copyText(mActivity, Const.SHARE_URL)
                            }
                            onPoster = Runnable {
                                DailyPosterActivity.start(mActivity, item.toDaily())
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

                                JPushHelper.tryAddTags(it.userInfo.tags ?: mutableListOf())
                                JPushHelper.tryAddAlias(it.userInfo.id)

                                eventBus.post(LoginEvent())
                            }, doFail = {
                                Toasts.show("登录失败，${it.msg}")
                            }, doDone = {
                                hideLoadingAlert()
                            })
                    }
                }
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

    private fun tryDeleteArticle(item: HisFavEntity) {
        showLoadingAlert("尝试取消收藏...")

        TbsApi.user().obtainCancelFavoriteArticle(item.articleId)
            .bindDefaultSub(
                doNext = {
                    val index = mAdapter.data.indexOfFirst {
                        it.articleId == item.articleId
                    }
                    if (index != -1) {
                        mAdapter.remove(index)
                        refreshFolder()
                    }

                    UserConfig.get().updateUser {
                        it.collectNum = max((it.collectNum ?: 0) - 1, 0)
                    }
                    EventBus.getDefault()
                        .post(
                            ArticleCollectEvent(
                                fromCollect = false,
                                fromFolder = true,
                                articleId = item.articleId,
                                doCollect = false
                            )
                        )

                    Toasts.show("取消成功")
                },
                doFail = {
                    Toasts.show("取消失败")
                },
                doDone = {
                    hideLoadingAlert()
                },
            )
    }

    private fun tryDeleteDaily(item: HisFavEntity) {
        showLoadingAlert("尝试取消收藏...")

        TbsApi.user().obtainDailyNoCollect(item.articleId)
            .bindDefaultSub(
                doNext = {
                    val index = mAdapter.data.indexOfFirst {
                        it.articleId == item.articleId
                    }

                    if (index != -1) {
                        mAdapter.remove(index)
                        refreshFolder()
                    }

                    UserConfig.get().updateUser {
                        it.collectNum = max((it.collectNum ?: 0) - 1, 0)
                    }

                    EventBus.getDefault()
                        .post(
                            ArticleCollectEvent(
                                fromCollect = false,
                                fromFolder = true,
                                articleId = item.articleId,
                                doCollect = false
                            )
                        )

                    Toasts.show("取消成功")
                },
                doFail = {
                    Toasts.show("取消失败")
                },
                doDone = {
                    hideLoadingAlert()
                },
            )
    }

    @SuppressLint("SetTextI18n")
    private fun refreshFolder() {
        TbsApi.user().obtainGetFolder(folderEntity.id)
            .onErrorReturn { folderEntity }
            .bindDefaultSub {
                folderEntity = it
                text_content.text =
                    "${folderEntity.articleCount}个内容· ${folderEntity.bossCount}个Boss"
            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: ArticleCollectEvent) {
        if (!event.fromFolder) {
            refreshFolder()

            if (event.doCollect) {
                layout_refresh.autoRefresh()
            } else {
                val index = mAdapter.data.indexOfFirst {
                    it.articleId == event.articleId
                }
                if (index != -1) {
                    mAdapter.remove(index)
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: ArticlePointEvent) {
        val index = mAdapter.data.indexOfFirst {
            it.articleId == event.id
        }
        if (index != -1) {
            mAdapter.data[index].isPoint = event.doPoint
        }
    }
}