package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import cn.wl.android.lib.config.WLConfig
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.Toasts
import kotlinx.android.synthetic.main.activity_article.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.Const
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.ui.dialog.CreateFolderDialog
import net.cd1369.tbs.android.ui.dialog.SelectFolderDialog
import net.cd1369.tbs.android.ui.dialog.ShareDialog
import net.cd1369.tbs.android.ui.start.InputPhoneActivity
import net.cd1369.tbs.android.util.*
import kotlin.math.max

class ArticleActivity : BaseActivity() {
    private var articleId: String? = null
    private var articleUrl: String? = null
    private var isCollect = false
    private var articleTitle: String? = null
    private var articleDes: String? = null
    private var articleCover: String = ""

    companion object {
        fun start(context: Context?, id: String) {
            val intent = Intent(context, ArticleActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtras(Bundle().apply {
                        putString("articleId", id)
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

        var userEntity = UserConfig.get().userEntity

        articleId = intent.getStringExtra("articleId") as String
        articleUrl = "${WLConfig.getBaseUrl()}#/article?" +
                "id=$articleId" +
                "&version=${userEntity?.version ?: "1000"}" +
                "&type=0"
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

        web_view.webViewClient = (object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, uri: String?): Boolean {
                view?.loadUrl(uri!!)
                return true
            }
        })

        tryReadArticle(articleId!!)

        image_collect doClick {
            if (UserConfig.get().loginStatus) {
                if (isCollect!!) {
                    cancelCollect()
                } else doCollect()
            } else {
                Toasts.show("请先登录！")
                InputPhoneActivity.start(mActivity)
            }
        }

        image_share doClick {
            onShare()
        }

        image_back doClick {
            onBackPressed()
        }
    }

    private fun onShare() {
        ShareDialog.showDialog(supportFragmentManager, "shareDialog")
            .apply {
                onSession = Runnable {
                    doShareSession(
                        resources,
                        cover = articleCover,
                        title = articleTitle!!,
                        des = articleDes!!
                    )
                }
                onTimeline = Runnable {
                    doShareTimeline(
                        resources, cover = articleCover,
                        title = articleTitle!!,
                    )
                }
                onCopyLink = Runnable {
                    Tools.copyText(mActivity, Const.SHARE_URL)
                }
            }
    }

    override fun loadData() {
        super.loadData()
        showContent()

        TbsApi.boss().obtainDetailArticle(articleId)
            .bindDefaultSub {
                isCollect = it.isCollect!!
                articleTitle = it?.title ?: ""
                articleDes = it?.descContent ?: ""
                if (!it.files.isNullOrEmpty()) {
                    articleCover = it.files[0].avatar()
                }

                image_collect.isSelected = isCollect
            }
    }

    private fun cancelCollect() {
        showLoadingAlert("尝试取消...")

        TbsApi.user().obtainCancelFavoriteArticle(articleId)
            .bindDefaultSub(doNext = {
                isCollect = false
                image_collect.isSelected = false

                UserConfig.get().updateUser {
                    it.traceNum = max((it.traceNum ?: 0) - 1, 0)
                }
                eventBus.post(RefreshUserEvent())

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

                                                    UserConfig.get().updateUser {
                                                        it.collectNum =
                                                            max((it.collectNum ?: 0) + 1, 0)
                                                    }
                                                    eventBus.post(RefreshUserEvent())

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
     * 尝试阅读记录文章
     * @param articleId String
     */
    private fun tryReadArticle(articleId: String) {
        TbsApi.user().obtainReadArticle(articleId)
            .bindDefaultSub(doNext = {
                UserConfig.get().updateUser {
                    it.readNum = max((it.readNum ?: 0) + 1, 0)
                }
                eventBus.post(RefreshUserEvent())
            }, doFail = {

            })
    }
}