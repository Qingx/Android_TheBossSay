package net.cd1369.tbs.android.wxapi

import android.app.Activity
import android.os.Bundle
import android.view.Window
import cn.wl.android.lib.utils.Toasts
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelbiz.WXOpenBusinessView
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import net.cd1369.tbs.android.config.TbsApp
import net.cd1369.tbs.android.event.WechatLoginCodeEvent
import net.cd1369.tbs.android.util.Tools.logE
import org.greenrobot.eventbus.EventBus


class WXEntryActivity : Activity(), IWXAPIEventHandler {
    companion object {
        private const val TAG = "okhttp"

        private const val TYPE_LOGIN = 1 //登录
        private const val TYPE_SHARE = 2 //分享
    }

    private lateinit var weChatApi: IWXAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        weChatApi = TbsApp.getWeChatApi()
        weChatApi.handleIntent(intent, this)
    }

    override fun onReq(p0: BaseReq?) {

    }

    override fun onResp(baseResp: BaseResp) {
        baseResp.type.logE(prefix = "weChat.resp.type")
        baseResp.errCode.logE(prefix = "weChat.resp.errCode")

        val type = baseResp.type //类型：分享还是登录

        if (type == ConstantsAPI.COMMAND_OPEN_BUSINESS_VIEW) {
            val miniResp = baseResp as WXOpenBusinessView.Resp
            miniResp.logE(prefix = "小程序支付fen")
        } else {
            when (baseResp.errCode) {
                BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                    //用户拒绝授权
                    Toasts.show("拒绝授权微信登录")
                }
                BaseResp.ErrCode.ERR_USER_CANCEL -> {
                    //用户取消授权
                    val message = when (type) {
                        TYPE_LOGIN -> "取消了微信登录"
                        TYPE_SHARE -> "取消了微信分享"
                        else -> "未知类型type"
                    }
                    Toasts.show(message)
                }
                BaseResp.ErrCode.ERR_OK -> {
                    //用户同意
                    when (type) {
                        TYPE_LOGIN -> {
                            //用户换取access_token的code，仅在ErrCode为0时有效
                            val code = (baseResp as (SendAuth.Resp)).code
                            EventBus.getDefault().post(WechatLoginCodeEvent(code))
                            code.logE(prefix = "weChat.resp.code")
                        }
                        TYPE_SHARE -> {
                            Toasts.show("微信分享成功");
                        }
                    }
                }
            }
        }
        onBackPressed()
    }
}