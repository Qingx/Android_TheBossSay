package net.cd1369.tbs.android.ui.fragment

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.fragment_mine.*
import kotlinx.android.synthetic.main.layout_mine_ad.view.*
import kotlinx.android.synthetic.main.layout_mine_head.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.Const
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.MineItem
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.db.ArticleDaoManager
import net.cd1369.tbs.android.data.db.BossDaoManager
import net.cd1369.tbs.android.data.db.LabelDaoManager
import net.cd1369.tbs.android.data.model.BossSimpleModel
import net.cd1369.tbs.android.data.model.LabelModel
import net.cd1369.tbs.android.event.FollowBossEvent
import net.cd1369.tbs.android.event.JumpBossEvent
import net.cd1369.tbs.android.event.LoginEvent
import net.cd1369.tbs.android.event.RefreshUserEvent
import net.cd1369.tbs.android.ui.adapter.MineItemAdapter
import net.cd1369.tbs.android.ui.dialog.ShareDialog
import net.cd1369.tbs.android.ui.home.*
import net.cd1369.tbs.android.ui.test.TestActivity
import net.cd1369.tbs.android.util.Tools
import net.cd1369.tbs.android.util.Tools.logE
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
                    MineItem.Contact -> MineContactAuthorActivity.start(mActivity)
                    MineItem.Score -> testAdd()
                    MineItem.Clear -> testGet()
                }
            }
        }

        rv_content.layoutManager = LinearLayoutManager(mActivity)
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

    private fun testAdd() {
        val label = LabelModel(-1, "2313313")
        LabelDaoManager.getInstance(mActivity).insert(label)
//        val boss0 = BossSimpleModel(-1, "A", "A", "A", true, 1, listOf("1", "2"), listOf("2", "1"))
//        val boss1 = BossSimpleModel(-2, "A", "A", "A", true, 1, listOf("1", "2"), listOf("2", "1"))
//        val boss2 = BossSimpleModel(-11, "A", "A", "A", true, 1, listOf("1", "2"), listOf("2", "1"))
//        val list = listOf(boss0, boss1, boss2)
//        BossDaoManager.getInstance(mActivity).insertList(list)
    }

    private fun testGet() {
        val list = ArticleDaoManager.getInstance(mActivity).findAll()
//        BossDaoManager.getInstance(mActivity).deleteAll();
//        val list = BossDaoManager.getInstance(mActivity).findAll()
        list.logE(prefix = "daoManager")
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

        header?.text_follow_num?.text = entity.traceNum.toString()
        header?.text_history_num?.text = entity.readNum.toString()
        header?.text_favorite_num?.text = entity.collectNum.toString()

        mAdapter.onRefreshLogin()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: FollowBossEvent) {
        setUserInfo()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(event: LoginEvent) {
        setUserInfo()
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