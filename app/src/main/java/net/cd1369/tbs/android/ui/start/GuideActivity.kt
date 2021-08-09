package net.cd1369.tbs.android.ui.start

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wl.android.lib.ui.BaseActivity
import cn.wl.android.lib.utils.Times
import cn.wl.android.lib.utils.Toasts
import kotlinx.android.synthetic.main.activity_guide.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApi
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.entity.BossInfoEntity
import net.cd1369.tbs.android.ui.adapter.GuideInfoAdapter
import net.cd1369.tbs.android.ui.home.ArticleActivity
import net.cd1369.tbs.android.ui.home.HomeActivity
import net.cd1369.tbs.android.util.Tools.logE
import net.cd1369.tbs.android.util.doClick

class GuideActivity : BaseActivity() {

    private lateinit var mLabels: ArrayList<BossInfoEntity>

    companion object {
        fun start(context: Context?, list: ArrayList<BossInfoEntity>) {
            val intent = Intent(context, GuideActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtras(Bundle().apply {
                        putSerializable("labels", list)
                    })
                }
            context!!.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.activity_guide
    }

    override fun beforeCreateView(savedInstanceState: Bundle?) {
        super.beforeCreateView(savedInstanceState)

        mLabels = intent.getSerializableExtra("labels") as ArrayList<BossInfoEntity>
    }

    @SuppressLint("SetTextI18n")
    override fun initViewCreated(savedInstanceState: Bundle?) {
        countdown(5) {
            text_time.text = "${it}s"

            if (it <= 0) {
                text_time.text = "跳过"
            }
        }

        val adapter = object : GuideInfoAdapter() {
            override fun onAddFollow(data: MutableList<String>) {
                showLoadingAlert("搜寻并追踪...")

                var tempId = WelActivity.tempId

                TbsApi.boss().obtainGuideFollow(data)
                    .bindDefaultSub(doNext = {
                        Toasts.show("追踪成功")
                        hideLoadingAlert()

                        UserConfig.get().userEntity.collectNum = data.size

                        DataConfig.get().firstUse = false
                        DataConfig.get().updateTime = Times.current()

                        if (tempId.isNullOrEmpty()) {
                            HomeActivity.start(mActivity)
                            mActivity?.finish()
                        } else {
                            var intentHome = Intent(mActivity, HomeActivity::class.java)
                            intentHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                            var intentArticle = Intent(mActivity, ArticleActivity::class.java)
                            intentArticle.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            intentArticle.putExtra("articleId", tempId)

                            mActivity.startActivities(arrayOf(intentHome, intentArticle))
                        }
                    }, doFail = {
                        hideLoadingAlert()
                        it.msg.logE()
                        Toasts.show(it.msg)
                    })
            }
        }

        rv_content.adapter = adapter
        rv_content.layoutManager =
            object : GridLayoutManager(mActivity, 2, RecyclerView.HORIZONTAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

        adapter.setNewData(mLabels)

        layout_add doClick {
            adapter.addFollow()
        }

        text_clear doClick {
            adapter.clearAll()
        }

        text_time doClick {
            var tempId = WelActivity.tempId

            if (text_time.text == "跳过") {
                DataConfig.get().firstUse = false

                if (tempId.isNullOrEmpty()) {
                    HomeActivity.start(mActivity)
                    mActivity?.finish()
                } else {
                    var intentHome = Intent(mActivity, HomeActivity::class.java)
                    intentHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                    var intentArticle = Intent(mActivity, ArticleActivity::class.java)
                    intentArticle.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intentArticle.putExtra("articleId", tempId)

                    mActivity.startActivities(arrayOf(intentHome, intentArticle))
                }
            }
        }
    }
}