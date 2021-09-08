package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import cn.wl.android.lib.config.WLConfig
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.GlideApp
import cn.wl.android.lib.utils.Toasts
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_article.*
import kotlinx.android.synthetic.main.activity_article.image_back
import kotlinx.android.synthetic.main.activity_article.image_head
import kotlinx.android.synthetic.main.activity_article.text_follow
import kotlinx.android.synthetic.main.activity_article.text_name
import kotlinx.android.synthetic.main.activity_boss_home.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.Const
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.config.elif
import net.cd1369.tbs.android.data.db.BossDaoManager
import net.cd1369.tbs.android.data.entity.ArticleEntity
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.event.ArticleCollectEvent
import net.cd1369.tbs.android.event.ArticleReadEvent
import net.cd1369.tbs.android.event.BossTackEvent
import net.cd1369.tbs.android.ui.dialog.*
import net.cd1369.tbs.android.ui.start.WelActivity
import net.cd1369.tbs.android.util.*
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import kotlin.math.max

class ArticleActivity : BaseActivity() {
    private var mPointDis: Disposable? = null
    private var articleId: String? = null
    private var articleUrl: String? = null
    private var isCollect = false
    private var articleTitle: String? = null
    private var articleDes: String? = null
    private var articleCover: String = ""

    private var bossId: String? = null
    private var bossEntity: BossInfoEntity? = null
    private var mArticleEntity: ArticleEntity? = null

    private var isTack = false
    private var mLabels: MutableList<String>? = null

    private var fromBoss: Boolean = false

    companion object {
        fun start(context: Context?, id: String, fromBoss: Boolean = false) {
            val intent = Intent(context, ArticleActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtras(Bundle().apply {
                        putString("articleId", id)
                        putBoolean("fromBoss", fromBoss)
                    })
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_article
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        val userEntity = UserConfig.get().userEntity

        articleId = intent.getStringExtra("articleId") as String
        articleUrl = "${WLConfig.getBaseUrl()}#/article?" +
                "id=$articleId" +
                "&version=${userEntity?.version ?: "1000"}"

        fromBoss = intent.getBooleanExtra("fromBoss", false)
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        web_view.loadUrl(articleUrl!!)

        val webSettings = web_view.settings

        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webSettings.domStorageEnabled = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.defaultTextEncodingName = "UTF-8";

        val androidCallback = AndroidCallback()
        androidCallback.formBoss = fromBoss
        androidCallback.mAct = WeakReference(this)

        web_view.addJavascriptInterface(androidCallback, "RecommendArticle")

        web_view.webViewClient = (object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, uri: String?): Boolean {
                view?.loadUrl(uri!!)
                return true
            }
        })

        layout_boss.isVisible = false

        tryReadArticle(articleId!!)

        layout_collect doClick {
            if (UserConfig.get().loginStatus) {
                if (isCollect) {
                    cancelCollect()
                } else doCollect()
            } else {
                Toasts.show("请先登录！")
                LoginPhoneWechatActivity.start(mActivity)
            }
        }

        layout_point doClick {
            val article = mArticleEntity
            if (article != null) {
                switchPointStatus(article)
            } else {
                Toasts.show("数据正在加载中...")
            }
        }

        layout_share doClick {
            onShare()
        }

        text_follow doClick {
            if (isTack) {
                FollowAskCancelDialog.showDialog(supportFragmentManager, "askCancel")
                    .apply {
                        onConfirmClick = FollowAskCancelDialog.OnConfirmClick {
                            cancelFollow(this)
                        }
                    }
            } else {
                followBoss()
            }
        }

        layout_info doClick {
            if (fromBoss) {
                onBackPressed()
            } else {
                BossHomeActivity.start(mActivity, bossId!!)
            }
        }

        image_back doClick {
            onBackPressed()
        }
    }

    /**
     * 切换点赞状态
     * @param article ArticleEntity
     */
    private fun switchPointStatus(article: ArticleEntity) {
        var target = !(article.isPoint ?: false)

//        showLoadingAlert(target.elif("正在点赞...", "正在取消点赞..."))

        article.isPoint = target
        showPointStatus(target)

        var alertMsg = target.elif("点赞成功", "取消成功")

        mPointDis?.dispose() // 快速操作取消上一次的操作
        mPointDis = TbsApi.boss().switchPointStatus(article.id, target)
            .bindToastSub(alertMsg, doFail = {
                Toasts.show(it.msg)

                article.isPoint = !target
                showPointStatus(!target)
            }) {
                UserConfig.get().updateUser {
                    it.pointNum = max((it.pointNum ?: 0) + 1, 0)
                }
                eventBus.post(BossTackEvent(bossId!!, true, bossEntity!!.labels))
            }
    }

    override fun onDestroy() {
        super.onDestroy()

        WelActivity.tempId = ""
    }

    private fun onShare() {
        ShareDialog.showDialog(supportFragmentManager, "shareDialog")
            .apply {
                onSession = Runnable {
                    doShareSession(
                        resources,
                        cover = articleCover,
                        title = articleTitle!!,
                        des = articleDes!!,
                        url = pathQuery(Const.SHARE_URL) {
                            it["id"] = articleId ?: ""
                            it["type"] = "1"
                        }
                    )
                }
                onTimeline = Runnable {
                    doShareTimeline(
                        resources, cover = articleCover,
                        title = articleTitle!!,
                        url = pathQuery(Const.SHARE_URL) {
                            it["id"] = articleId ?: ""
                            it["type"] = "1"
                        }
                    )
                }
                onCopyLink = Runnable {
                    Tools.copyText(mActivity, Const.SHARE_URL)
                }
            }
    }

    /**
     * 修改点赞显示状态
     * @param isPoint Boolean
     */
    private fun showPointStatus(isPoint: Boolean) {
        image_point.isSelected = isPoint
        text_point.text = isPoint
            .elif("已点赞", "点赞")
    }

    override fun loadData() {
        super.loadData()
        showContent()

        TbsApi.boss().obtainDetailArticle(articleId)
            .bindDefaultSub {
                mArticleEntity = it
                isCollect = it.isCollect!!
                articleTitle = it?.title ?: ""
                articleDes = it?.descContent ?: ""
                if (!it.files.isNullOrEmpty()) {
                    articleCover = it.files[0].fullUrl()
                }

                image_collect.isSelected = isCollect
                text_collect.isSelected = isCollect
                text_collect.text = if (isCollect) "已收藏" else "收藏"

                layout_boss.isVisible = true

                showPointStatus(it.isPoint ?: false)

                bossEntity = it.bossVO
                isTack = it.bossVO.isCollect
                bossId = it.bossId
                mLabels = it.bossVO.labels

                GlideApp.displayHead(it.bossVO.head, image_head)
                text_name.text = it.bossVO.name
                text_role.text = it.bossVO.role
                text_follow.text = if (it.bossVO.isCollect) "已追踪" else "追踪"
                text_follow.isSelected = it.bossVO.isCollect
            }
    }

    private fun cancelCollect() {
        showLoadingAlert("尝试取消...")

        TbsApi.user().obtainCancelFavoriteArticle(articleId)
            .bindDefaultSub(doNext = {
                isCollect = false
                this@ArticleActivity.image_collect.isSelected = false
                this@ArticleActivity.text_collect.isSelected =
                    false
                this@ArticleActivity.text_collect.text = "收藏"

                UserConfig.get().updateUser {
                    it.collectNum = max((it.collectNum ?: 0) - 1, 0)
                }
                eventBus.post(ArticleCollectEvent())

                Toasts.show("取消成功")
            }, doFail = {
                Toasts.show("取消失败，${it.msg}")
            }, doDone = {
                hideLoadingAlert()
            })
    }

    private fun doCollect() {
        showLoadingAlert("获取收藏夹...")

        TbsApi.user().obtainFavoriteList()
            .onErrorReturn {
                mutableListOf()
            }.bindDefaultSub(doNext = {
                hideLoadingAlert()

                SelectFolderDialog.showDialog(supportFragmentManager, "selectFolder", it)
                    .apply select@{
                        this.onCreateClick = SelectFolderDialog.OnCreateClick {
                            CreateFolderDialog.showDialog(supportFragmentManager, "createFolder")
                                .apply create@{
                                    this.onConfirmClick =
                                        CreateFolderDialog.OnConfirmClick { name ->
                                            showLoadingAlert("尝试收藏...")

                                            TbsApi.user().obtainCreateFavorite(name)
                                                .flatMap { folder ->

                                                    TbsApi.user()
                                                        .obtainFavoriteArticle(folder.id, articleId)
                                                }.bindDefaultSub(doNext = {
                                                    isCollect = true
                                                    this@ArticleActivity.image_collect.isSelected =
                                                        true
                                                    this@ArticleActivity.text_collect.isSelected =
                                                        true
                                                    this@ArticleActivity.text_collect.text = "已收藏"

                                                    UserConfig.get().updateUser {
                                                        it.collectNum =
                                                            max((it.collectNum ?: 0) + 1, 0)
                                                    }
                                                    eventBus.post(ArticleCollectEvent())

                                                    Toasts.show("收藏成功")
                                                    this@create.dismiss()
                                                    this@select.dismiss()
                                                }, doFail = { error ->
                                                    Toasts.show("收藏失败，${error.msg}")
                                                }, doDone = {
                                                    hideLoadingAlert()
                                                })
                                        }
                                }
                        }
                        this.onConfirmClick = SelectFolderDialog.OnConfirmClick { folderId ->
                            showLoadingAlert("尝试收藏...")

                            TbsApi.user().obtainFavoriteArticle(folderId, articleId)
                                .bindDefaultSub(doNext = {
                                    isCollect = true
                                    this@ArticleActivity.image_collect.isSelected = true
                                    this@ArticleActivity.text_collect.isSelected =
                                        true
                                    this@ArticleActivity.text_collect.text = "已收藏"

                                    UserConfig.get().updateUser {
                                        it.collectNum =
                                            max((it.collectNum ?: 0) + 1, 0)
                                    }
                                    eventBus.post(ArticleCollectEvent())

                                    Toasts.show("收藏成功")

                                    this.dismiss()
                                }, doFail = { error ->
                                    Toasts.show("收藏失败，${error.msg}")
                                }, doDone = {
                                    hideLoadingAlert()
                                })
                        }
                    }
            })
    }

    /**
     * 取消追踪boss
     */
    private fun cancelFollow(dialog: FollowAskCancelDialog?) {
        showLoadingAlert("尝试取消...")

        TbsApi.boss().obtainCancelFollowBoss(bossId)
            .bindDefaultSub(doNext = {
                dialog?.dismiss()

                FollowChangedDialog.showDialog(supportFragmentManager, true, "followChange")

                isTack = false
                text_follow.isSelected = false
                text_follow.text = "追踪"

                UserConfig.get().updateUser {
                    it.traceNum = max((it.traceNum ?: 0) - 1, 0)
                }
                eventBus.post(BossTackEvent(bossId!!, false, bossEntity!!.labels))
                BossDaoManager.getInstance(mActivity).delete(bossId!!.toLong())

                JPushHelper.tryDelTag(bossId!!)
            }, doFail = {
                Toasts.show("取消失败，${it.msg}")
            }, doDone = {
                hideLoadingAlert()
            })
    }

    /**
     * 追踪boss
     */
    private fun followBoss() {
        showLoadingAlert("尝试追踪...")

        TbsApi.boss().obtainFollowBoss(bossId)
            .bindDefaultSub(doNext = {
                FollowAskPushDialog.showDialog(supportFragmentManager, "askPush")
                    .apply {
                        onConfirmClick = FollowAskPushDialog.OnConfirmClick {
                            JPushHelper.tryAddTag(bossId!!)

                            this.dismiss()

                            FollowChangedDialog.showDialog(
                                supportFragmentManager,
                                false,
                                "followChange"
                            )
                        }
                        onCancelClick = FollowAskPushDialog.OnCancelClick {
                            this.dismiss()

                            FollowChangedDialog.showDialog(
                                supportFragmentManager,
                                false,
                                "followChange"
                            )
                        }
                    }
                isTack = true
                text_follow.isSelected = true
                text_follow.text = "已追踪"

                UserConfig.get().updateUser {
                    it.traceNum = max((it.traceNum ?: 0) + 1, 0)
                }
                eventBus.post(BossTackEvent(bossId!!, true, bossEntity!!.labels))
                BossDaoManager.getInstance(mActivity).insert(bossEntity!!.toSimple())

            }, doFail = {
                Toasts.show("追踪失败，${it.msg}")
            }, doDone = {
                hideLoadingAlert()
            })
    }

    /**
     * 尝试阅读记录文章
     * @param articleId String
     */
    private fun tryReadArticle(articleId: String) {
        TbsApi.user().obtainReadArticle(articleId)
            .bindDefaultSub(doNext = {
                Tools.addTodayRead()
                EventBus.getDefault().post(ArticleReadEvent(articleId))
            }, doFail = {

            })
    }

}