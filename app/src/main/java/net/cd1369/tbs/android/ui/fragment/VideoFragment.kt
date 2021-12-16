package net.cd1369.tbs.android.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import cn.wl.android.lib.ui.BaseFragment
import com.bytedance.sdk.dp.*
import net.cd1369.tbs.android.R

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 17:35 2021/12/8
 * @desc
 */
class VideoFragment : BaseFragment() {

    private var mIDPWidget: IDPWidget? = null

    companion object {
        fun newIns(): VideoFragment {
            val fragment = VideoFragment()
            val bundle = Bundle().also {

            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutResource(): Any =
        R.layout.fragment_video

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
        initDraw()


        childFragmentManager.beginTransaction()
            .replace(R.id.fl_root, mIDPWidget!!.fragment, "DPWidgetNewsParams")
            .commitAllowingStateLoss()
    }


    private fun initDraw() {
        val params = DPWidgetNewsParams.obtain()
            .channelCategory("subv_video_finace")
            .adListener(object : IDPAdListener() {})
            .listener(object : IDPNewsListener() {})

        mIDPWidget = DPSdk.factory().createNewsOneTab(params)

//        mIDPWidget = DPSdk.factory().createDraw(DPWidgetDrawParams.obtain()
//            .adOffset(49) //单位 dp
//            .hideClose(true, null)
//            .listener(object : IDPDrawListener() {
//                override fun onDPRefreshFinish() {
//                    log("onDPRefreshFinish")
//                }
//
//                override fun onDPPageChange(position: Int) {
//                    log("onDPPageChange: $position")
//                }
//
//                override fun onDPVideoPlay(map: Map<String, Any>) {
//                    log("onDPVideoPlay")
//                }
//
//                override fun onDPVideoCompletion(map: Map<String, Any>) {
//                    log("onDPVideoCompletion: ")
//                }
//
//                override fun onDPVideoOver(map: Map<String, Any>) {
//                    log("onDPVideoOver")
//                }
//
//                override fun onDPClose() {
//                    log("onDPClose")
//                }
//
//                override fun onDPRequestStart(@Nullable map: Map<String, Any>?) {
//                    log("onDPRequestStart")
//                }
//
//                override fun onDPRequestSuccess(list: List<Map<String, Any>>) {
//                    log("onDPRequestSuccess")
//                }
//
//                override fun onDPRequestFail(
//                    code: Int,
//                    msg: String,
//                    @Nullable map: Map<String, Any>?
//                ) {
//                    log("onDPRequestFail")
//                }
//
//                override fun onDPClickAuthorName(map: Map<String, Any>) {
//                    log("onDPClickAuthorName")
//                }
//
//                override fun onDPClickAvatar(map: Map<String, Any>) {
//                    log("onDPClickAvatar")
//                }
//
//                override fun onDPClickComment(map: Map<String, Any>) {
//                    log("onDPClickComment")
//                }
//
//                override fun onDPClickLike(isLike: Boolean, map: Map<String, Any>) {
//                    log("onDPClickLike")
//                }
//
//                override fun onDPVideoPause(map: Map<String, Any>) {
//                    log("onDPVideoPause")
//                }
//
//                override fun onDPVideoContinue(map: Map<String, Any>) {
//                    log("onDPVideoContinue")
//                }
//            })
//        )


//        val params = DPWidgetNewsParams.obtain()
//            .channelCategory("video")
//            .allowDetailShowLock(false)
//            .adListener(object : IDPAdListener() {})
//            .listener(object : IDPNewsListener() {})
//        val createNewsTabs = DPSdk.factory()
//            .createNewsTabs(params)
    }

    private fun log(s: String) {

    }

}