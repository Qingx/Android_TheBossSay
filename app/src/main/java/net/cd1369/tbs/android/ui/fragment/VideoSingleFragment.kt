package net.cd1369.tbs.android.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import cn.wl.android.lib.ui.BaseFragment
import com.bytedance.sdk.dp.*
import kotlinx.android.synthetic.main.fragment_video_single.*
import net.cd1369.tbs.android.R

/**
 * @Email 15025496981@163.com
 * @User JustBlue 李波
 * @time 14:42 2021/12/10
 * @desc 单个视频节目
 */
class VideoSingleFragment : BaseFragment() {

    private var mElement: IDPElement? = null

    companion object {
        fun newIns(): VideoSingleFragment {
            val fragment = VideoSingleFragment()
            val bundle = Bundle().also {

            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutResource(): Any =
        R.layout.fragment_video_single

    override fun initViewCreated(view: View?, savedInstanceState: Bundle?) {
//        val params = DPWidgetVideoCardParams.obtain()
////            .hidePlay(false) //隐藏播放按钮
//            .hideTitle(false) //隐藏标题
////            .hideBottomInfo(false) //隐藏底部信息
//            .listener(object :IDPVideoCardListener(){})
        val params = DPWidgetUniversalParams.obtain()
            .listener(object : IDPNewsListener(){})
            .adListener(object : IDPAdListener() {})

        DPSdk.factory().loadUniversalVideo(params, object : IDPWidgetFactory.Callback {
            override fun onError(code: Int, msg: String) {

            }

            override fun onSuccess(data: IDPElement) {
                if (data == null) {
                    return
                }
                mElement = data
                val view = data.view
                fl_root.removeAllViews()
                val viewGroup = view.parent as? ViewGroup
                viewGroup?.removeAllViews()
                fl_root.addView(view)
                mElement?.reportShow()
            }
        })
    }

}