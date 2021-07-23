package net.cd1369.tbs.android.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import cn.wl.android.lib.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_web.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.util.doClick

class WebActivity : BaseActivity() {
    private lateinit var title: String
    private lateinit var url: String

    companion object {
        fun start(context: Context?, title: String, url: String) {
            val intent = Intent(context, WebActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("title", title)
                    putExtra("url", url)
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_web
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        title = intent.getStringExtra("title") as String
        url = intent.getStringExtra("url") as String
    }

    override fun initViewCreated(savedInstanceState: Bundle?) {
        text_title.text = title


        web_view.loadUrl(url)

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

        image_back doClick {
            onBackPressed()
        }
    }
}