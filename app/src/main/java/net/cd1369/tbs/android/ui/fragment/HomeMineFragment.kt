package net.cd1369.tbs.android.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.wl.android.lib.ui.BaseFragment
import cn.wl.android.lib.utils.Toasts
import com.advance.AdvanceBanner
import com.advance.AdvanceBannerListener
import com.advance.model.AdvanceError
import com.blankj.utilcode.util.AppUtils
import kotlinx.android.synthetic.main.fragment_mine.*
import kotlinx.android.synthetic.main.layout_mine_ad.view.*
import kotlinx.android.synthetic.main.layout_mine_head.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.Const
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.MineItem
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.event.*
import net.cd1369.tbs.android.ui.adapter.MineItemAdapter
import net.cd1369.tbs.android.ui.dialog.ShareDialog
import net.cd1369.tbs.android.ui.home.*
import net.cd1369.tbs.android.util.Tools
import net.cd1369.tbs.android.util.doClick
import net.cd1369.tbs.android.util.doShareSession
import net.cd1369.tbs.android.util.doShareTimeline
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Qing on 2021/6/28 11:44 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class HomeMineFragment : BaseFragment(), AdvanceBannerListener {
    private var header: View? = null
    private var footer: View? = null
    private lateinit var mAdapter: MineItemAdapter
    private var advanceBanner: AdvanceBanner? = null

    companion object {
        fun createFragment(): HomeMineFragment {
            return HomeMineFragment()
        }
    }

    override fun getLayoutResource(): Any {
        return R.layout.fragment_mine
    }

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        eventBus.register(this)

        mAdapter = object : MineItemAdapter() {
            override fun onItemClick(item: MineItem) {
                when (item) {
                    MineItem.Favorite -> onClickFavorite()
                    MineItem.History -> onClickHistory()
                    MineItem.Share -> onShare()
                    MineItem.About -> MineAboutAppActivity.start(mActivity)
                    MineItem.Contact -> {
                        MineContactAuthorActivity.start(mActivity)
                    }
                    MineItem.Score -> {
                        tryScoreApp()
//                        doTest()
                    }
                }
            }
        }

        rv_content.layoutManager = object : LinearLayoutManager(mActivity) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }

            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rv_content.adapter = mAdapter

        addHeaderView(mAdapter)
        addFooterView(mAdapter)

        image_setting doClick {
            onClickInfo()
        }

        image_share doClick {
            onShare()
        }

        timerDelay(40) {
            val items = MineItem.values().toMutableList()
            mAdapter.setNewData(items)
            mAdapter.onRefreshLogin()
        }

        setUserInfo()
    }

    /**
     * 添加底部布局
     * @param adapter MineItemAdapter
     */
    private fun addFooterView(adapter: MineItemAdapter) {
        footer = LayoutInflater.from(mActivity).inflate(
            R.layout.layout_mine_ad, null, false
        )
        adapter.addFooterView(footer)

        //rl是banner的父布局，用来展示广告
        //rl是banner的父布局，用来展示广告
        advanceBanner = AdvanceBanner(mActivity, footer!!.ll_ad, Const.BANNER_ID)
        //推荐：核心事件监听回调
        //推荐：核心事件监听回调
        advanceBanner?.setAdListener(this)
        //必须：请求策略并请求和展示广告
        //必须：请求策略并请求和展示广告
        advanceBanner?.loadStrategy()
    }

    /**
     * 添加头部布局
     * @param adapter MineItemAdapter
     */
    private fun addHeaderView(adapter: MineItemAdapter) {
        header = LayoutInflater.from(mActivity).inflate(
            R.layout.layout_mine_head, null, false
        )
        adapter.addHeaderView(header)

        header!!.layout_history doClick {
            MineHistoryTodayActivity.start(mActivity)
        }

        header!!.layout_favorite doClick {
            onClickFavorite()
        }

        header!!.layout_follow doClick {
            eventBus.post(JumpBossEvent())
        }

        header!!.image_info doClick {
            onClickInfo()
        }

        header?.layout_point doClick {
            onClickPoint()
        }
    }

    private fun onShare() {
        ShareDialog.showDialog(requireFragmentManager(), "shareDialog")
            .apply {
                onSession = Runnable {
                    doShareSession(resources)
                }
                onTimeline = Runnable {
                    doShareTimeline(resources)
                }
                onCopyLink = Runnable {
                    Tools.copyText(mActivity, Const.SHARE_URL)
                }
            }
    }

    //点击用户信息
    private fun onClickInfo() {
        if (UserConfig.get().loginStatus) {
            MineChangeUserActivity.start(mActivity)
        } else {
            Toasts.show("请先登录！")
            LoginPhoneWechatActivity.start(mActivity)
        }
    }

    //点击我的收藏
    private fun onClickFavorite() {
        if (UserConfig.get().loginStatus) {
            MineCollectArticleActivity.start(mActivity)
        } else {
            Toasts.show("请先登录！")
            LoginPhoneWechatActivity.start(mActivity)
        }
    }

    //点击历史记录
    private fun onClickHistory() {
        if (UserConfig.get().loginStatus) {
            MineHistoryAllActivity.start(mActivity)
        } else {
            Toasts.show("请先登录！")
            LoginPhoneWechatActivity.start(mActivity)
        }
    }

    //点击历史记录
    private fun onClickPoint() {
        if (UserConfig.get().loginStatus) {
            MinePointActivity.start(mActivity)
        } else {
            Toasts.show("请先登录！")
            LoginPhoneWechatActivity.start(mActivity)
        }
    }

    //尝试跳转应用商店评分
    private fun tryScoreApp() {
        val uri: Uri = Uri.parse("market://details?id=" + AppUtils.getAppPackageName())
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(mActivity.packageManager) != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            Toasts.show("未检测到应用商店")
        }
    }

    private fun doTest() {
//        DailyDialog.showDialog(requireFragmentManager(),"testDialog")
    }

    /**
     * 更新用户信息
     */
    @SuppressLint("SetTextI18n")
    private fun setUserInfo() {
        val loginStatus = UserConfig.get().loginStatus
        val entity = UserConfig.get().userEntity

        if (loginStatus) {
            header?.text_name?.text = entity.nickName
            header?.text_id?.text = "ID：${entity.id}"
        } else {
            val tempId = DataConfig.get().tempId

            header?.text_name?.text = "请先登录！"
            header?.text_id?.text = "游客：${tempId.substring(0, 12)}..."
        }

        header?.text_point_num?.text = entity.pointNum.toString()
        header?.text_follow_num?.text = entity.traceNum.toString()
        header?.text_history_num?.text = entity.readNum.toString()
        header?.text_favorite_num?.text = entity.collectNum.toString()

        mAdapter.onRefreshLogin()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: LoginEvent) {
        setUserInfo()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: BossTackEvent) {
        val entity = UserConfig.get().userEntity
        header?.text_follow_num?.text = entity.traceNum.toString()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: BossBatchTackEvent) {
        val entity = UserConfig.get().userEntity
        header?.text_follow_num?.text = entity.traceNum.toString()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: ArticleReadEvent) {
        val entity = UserConfig.get().userEntity
        header?.text_history_num?.text = entity.readNum.toString()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: ArticleCollectEvent) {
        val entity = UserConfig.get().userEntity
        header?.text_favorite_num?.text = entity.collectNum.toString()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: ArticlePointEvent) {
        val entity = UserConfig.get().userEntity
        header?.text_point_num?.text = entity.pointNum.toString()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: RefreshUserEvent) {
        setUserInfo()
    }

    override fun onAdFailed(p0: AdvanceError?) {
        Log.e("OkHttp", "add...onAdFailed")
    }

    override fun onSdkSelected(p0: String?) {
    }

    override fun onAdShow() {
    }

    override fun onAdClicked() {
    }

    override fun onDislike() {
    }

    override fun onAdLoaded() {
    }

}