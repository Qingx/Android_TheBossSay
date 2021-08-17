package net.cd1369.tbs.android.push

import android.content.Context
import cn.jpush.android.api.CustomMessage
import cn.jpush.android.api.NotificationMessage
import cn.jpush.android.service.JPushMessageReceiver
import cn.wl.android.lib.utils.DateFormat
import cn.wl.android.lib.utils.Times
import com.google.gson.JsonParser
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApp.getContext
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.event.HotSearchEvent
import net.cd1369.tbs.android.event.JpushArticleEvent
import net.cd1369.tbs.android.ui.home.ArticleActivity
import net.cd1369.tbs.android.util.Tools
import net.cd1369.tbs.android.util.Tools.logE
import net.cd1369.tbs.android.util.runUiThread
import net.cd1369.tbs.android.util.showSneaker
import org.greenrobot.eventbus.EventBus
import kotlin.math.max


/**
 * Created by Xiang on 2020/12/28 11:25
 * @description
 * @email Cymbidium@outlook.com
 */
class PushReceiver : JPushMessageReceiver() {
    override fun onNotifyMessageOpened(p0: Context?, message: NotificationMessage) {
        super.onNotifyMessageOpened(p0, message)

        val extras = message.notificationExtras

        if (!extras.isNullOrEmpty()) {
            message.notificationExtras.logE(prefix = "极光推送:open")

            val jsonElement = JsonParser().parse(extras)
            val jsonObject = jsonElement.asJsonObject

            if (jsonObject.has("articleId")) {
                val articleId = jsonObject["articleId"].asString

                Tools.addTodayRead()

                ArticleActivity.start(getContext(), articleId)
            }
        }

    }

    override fun onNotifyMessageArrived(context: Context, message: NotificationMessage) {
        super.onNotifyMessageArrived(context, message)

        val extras = message.notificationExtras

        if (!extras.isNullOrEmpty()) {
            message.notificationExtras.logE(prefix = "极光推送:通知")

            val jsonElement = JsonParser().parse(extras)
            val jsonObject = jsonElement.asJsonObject

            if (jsonObject.has("articleId")) {
                val articleId = jsonObject["articleId"].asString
                val title = message.notificationTitle
                val content = message.notificationContent

                val bossId = jsonObject["bossId"].asString
                val time = jsonObject["updateTime"].asLong

                EventBus.getDefault().post(JpushArticleEvent(bossId, time))

                runUiThread {
                    showSneaker(title, content, articleId)
                }
            }
        }
    }

    override fun onMessage(context: Context, message: CustomMessage) {
        super.onMessage(context, message)
        message.logE(prefix = "极光推送:消息")

        val jsonElement = JsonParser().parse(message.extra)
        val jsonObject = jsonElement.asJsonObject

        DataConfig.get().hotSearch = message.message
        EventBus.getDefault().post(HotSearchEvent(message.message))

        if (jsonObject.has("hotSearch")) {
            val content = jsonObject["hotSearch"].asString
            DataConfig.get().hotSearch = content
            EventBus.getDefault().post(HotSearchEvent(content))
        }
    }
}