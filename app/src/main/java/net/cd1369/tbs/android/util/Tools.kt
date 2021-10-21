package net.cd1369.tbs.android.util

import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.provider.Settings.EXTRA_APP_PACKAGE
import android.provider.Settings.EXTRA_CHANNEL_ID
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import cn.wl.android.lib.config.WLConfig
import cn.wl.android.lib.utils.*
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.chad.library.adapter.base.BaseViewHolder
import com.google.gson.JsonElement
import com.irozon.sneaker.Sneaker
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.layout_push_message.view.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.config.Const
import net.cd1369.tbs.android.config.DataConfig
import net.cd1369.tbs.android.config.TbsApp
import net.cd1369.tbs.android.config.TbsApp.getContext
import net.cd1369.tbs.android.config.UserConfig
import net.cd1369.tbs.android.data.model.LabelModel
import net.cd1369.tbs.android.ui.home.ArticleActivity
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs
import kotlin.math.max


/**
 * Created by Xiang on 2020/7/3 12:28
 *
 * @email Cymbidium@outlook.com
 */
object Tools {
    fun addTodayRead() {
//        val lastTime = UserConfig.get().lastReadTime
//        val nowTime = Times.current()
//        UserConfig.get().lastReadTime = lastTime
//
//        val todayZero: Long = DateFormat.getTodayZero(nowTime)

        UserConfig.get().updateUser {
//            if (todayZero >= lastTime) {
//                it.readNum = 0
//            }

            it.readNum = max((it.readNum ?: 0) + 1, 0)
        }
    }

    internal fun List<LabelModel>?.isLabelsEmpty(): Boolean {
        return !(this != null && this.size > 1)
    }

    fun createTempId(): String {
        return "temp${UUID.randomUUID().leastSignificantBits}".replace("-", "")
    }

    //是否展示小红点
    fun showRedDots(id: String, time: Long): Boolean {
        return time >= DataConfig.get().getBossTime(id)
    }

    internal fun Int.formatCount(): String {
        return when {
            this <= 99 -> {
                "0k"
            }
            this <= 999 -> {
                "0.${this / 100}k"
            }
            this <= 9999 -> {
                "${this / 1000}.${this % 1000 / 100}k"
            }
            else -> {
                "${this / 10000}.${this % 10000 / 1000}w"
            }
        }
    }

    internal fun <T> T?.logE(
        tag: String = "OkHttp",
        prefix: String = "msg",
        infix: String = "："
    ): T? {
        this ?: return this

        if (WLConfig.isDebug()) {
            val json = Gsons.getGson().toJson(this)
            Log.e(tag, prefix + infix + json)
        }
        return this
    }

    infix fun <T> Collection<T>?.toInlineMap(runConcat: (T) -> String): Map<String, T> {
        if (this == null) return HashMap()

        val hashMap = HashMap<String, T>()

        for (t in this) {
            val key = runConcat(t)
            hashMap[key] = t
        }

        return hashMap
    }

    fun addUrlQuery(url: String, mapper: (mapper: HashMap<String, String>) -> Unit): String {
        var newUrl = url

        if (newUrl.isEmpty()) {
            return ""
        }

        val temp = hashMapOf<String, String>()
        mapper.invoke(temp)

        if (temp.isNotEmpty()) {
            newUrl += if (newUrl.lastIndexOf('?') > 0) {
                "&${temp.entries.joinToString(separator = "&") { "${it.key}=${it.value}" }}"
            } else {
                "?${temp.entries.joinToString(separator = "&") { "${it.key}=${it.value}" }}"
            }
        }

        return newUrl
    }

    /**
     * 获取后缀名
     * @param path String?
     * @param defValue String
     * @return String
     */
    fun getEndName(path: String?, defValue: String): String {
        if (path.isNullOrEmpty()) return defValue

        val lastIndexOf = path.lastIndexOf('.')

        if (lastIndexOf < 0) return defValue

        return path.substring(lastIndexOf)
    }

    /**
     * 判断当前路径是否为远程路径
     * @param url String?
     * @return Boolean
     */
    fun isRemoteUrl(url: String?): Boolean = url?.startsWith("http") ?: false

    /**
     * 通过浏览器打开网页
     */
    fun jumpUriByWeb(context: Context, url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    fun isShouldHideInput(view: View?, motionEvent: MotionEvent): Boolean {
        if (view != null && (view is EditText)) {
            val leftTop: IntArray = intArrayOf(0, 0)
            view.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + view.height
            val right = left + view.width
            return !(motionEvent.x > left && motionEvent.x < right && motionEvent.y > top && motionEvent.y < bottom)
        }
        return false
    }

    fun hideInputMethod(context: Context, view: View?): Boolean =
        when (val imm: InputMethodManager? =
            context.getSystemService(Context.INPUT_METHOD_SERVICE)
                    as? InputMethodManager) {
            null -> false
            else -> {
                view?.clearFocus()
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
            }
        }

    fun isShouldHideInputMore(view: View?, motionEvent: MotionEvent): Boolean {
        if (view != null && (view is CardView)) {
            val leftTop: IntArray = intArrayOf(0, 0)
            view.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + view.height
            val right = left + view.width
            return !(motionEvent.x > left && motionEvent.x < right && motionEvent.y > top && motionEvent.y < bottom)
        }
        return false
    }

    /**
     * 适用于dialog的倒计时
     */
    fun countDown(time: Int, dialog: DialogFragment?, doDown: (int: Int) -> Unit): Disposable {
        return Observable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { t: Long -> (time - t).toInt() }
            .takeUntil { t: Int -> t <= 0 }
            .subscribe {
                doDown(it)
                if (it <= 1) {
                    dialog?.dismiss()
                }
            }
    }

    /**
     * 全局倒计时
     */
    fun countDown(time: Int, doDown: (int: Int) -> Unit): Disposable {
        try {
            doDown.invoke(time)
        } catch (e: Exception) {
        }

        return Observable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { t: Long -> (time - t).toInt() }
            .takeUntil { t: Int -> t <= 0 }
            .subscribe {
                doDown(it)
            }
    }

    fun copyText(context: Context?, string: String?, alertMsg: String = "复制成功") {
        val cmb: ClipboardManager =
            context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData: ClipData = ClipData.newPlainText("copy", string)
        cmb.setPrimaryClip(clipData)

        if (!TextUtils.isEmpty(alertMsg)) {
            Toasts.show(alertMsg)
        }
    }

    /**
     * 跳转系统通知权限界面
     * @param context Context
     */
    fun gotoSystemNotice(context: Context) {
        try {
            // 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
            val intent = Intent()
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
            intent.putExtra(EXTRA_APP_PACKAGE, context.packageName)
            intent.putExtra(EXTRA_CHANNEL_ID, context.applicationInfo.uid)

            //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)

            // 小米6 -MIUI9.6-8.0.0系统，是个特例，通知设置界面只能控制"允许使用通知圆点"——然而这个玩意并没有卵用，我想对雷布斯说：I'm not ok!!!
            //  if ("MI 6".equals(Build.MODEL)) {
            //      intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            //      Uri uri = Uri.fromParts("package", getPackageName(), null);
            //      intent.setData(uri);
            //      // intent.setAction("com.android.settings/.SubSettings");
            //  }
            val topActivity = ActStack.get().topActivity
            topActivity?.startActivity(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
            val intent = Intent()

            //下面这种方案是直接跳转到当前应用的设置界面。
            //https://blog.csdn.net/ysy950803/article/details/71910806
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            val topActivity = ActStack.get().topActivity
            topActivity?.startActivity(intent)
        }
    }
}

/**
 * 判断是否安装微信
 */
fun isWeChatInstalled(context: Context): Boolean {
    val packageManager = context.packageManager
    val infoList = packageManager.getInstalledPackages(0)
    val info = infoList.singleOrNull {
        it.packageName == "com.tencent.mm"
    }

    return info != null
}

fun pathQuery(url: String, queryCreator: (HashMap<String, String>) -> Unit): String {
    if (url.isNullOrEmpty()) return ""

    var sb = StringBuilder(url)
    val hashMap = HashMap<String, String>()
    queryCreator.invoke(hashMap)

    var iterator = hashMap.iterator()
    if (iterator.hasNext()) {
        var next = iterator.next()

        sb.append('?')
        sb.append(next.key)
        sb.append('=')
        sb.append(next.value)
    }

    while (iterator.hasNext()) {
        var next = iterator.next()

        sb.append('&')
        sb.append(next.key)
        sb.append('=')
        sb.append(next.value)
    }

    return sb.toString()
}

/**
 * 分享至微信会话
 * @param resources Resources
 */
internal fun doShareSession(
    resources: Resources,
    cover: String = "",
    url: String = Const.SHARE_URL,
    title: String = "Boss说-追踪老板的言论",
    des: String = "深度学习大佬的言论文章，找寻你的成功暗门"
) {
    val webPage = WXWebpageObject()
    webPage.webpageUrl = url

    if (cover == "") {
        val bmp: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_logo)

        val thumbBmp = Bitmap.createScaledBitmap(bmp!!, 150, 150, true)
        bmp.recycle()

        val msg = WXMediaMessage(webPage)
        msg.title = title
        msg.description = des
        msg.thumbData = WeChatUtil.bmpToByteArray(thumbBmp, true)

        val req = SendMessageToWX.Req()
        req.transaction = WeChatUtil.buildTransaction("webPage")
        req.message = msg
        req.scene = SendMessageToWX.Req.WXSceneSession

        TbsApp.getWeChatApi().sendReq(req)
    } else {
        loadBitmap(cover) {
            val bmp = it

            val thumbBmp = Bitmap.createScaledBitmap(bmp!!, 150, 150, true)
            try {
                bmp.recycle()
            } catch (e: java.lang.Exception) {

            }

            val msg = WXMediaMessage(webPage)
            msg.title = title
            msg.description = des
            msg.thumbData = WeChatUtil.bmpToByteArray(thumbBmp, true)

            val req = SendMessageToWX.Req()
            req.transaction = WeChatUtil.buildTransaction("webPage")
            req.message = msg
            req.scene = SendMessageToWX.Req.WXSceneSession

            TbsApp.getWeChatApi().sendReq(req)
        }
    }
}

/**
 * 分享至微信朋友圈
 * @param resources Resources
 */
internal fun doShareTimeline(
    resources: Resources, cover: String = "",
    url: String = Const.SHARE_URL,
    title: String = "Boss说-追踪老板的言论",
    des: String = "深度学习大佬的言论文章，找寻你的成功暗门"
) {
    val webPage = WXWebpageObject()
    webPage.webpageUrl = url
    if (cover == "") {
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.ic_logo)

        val thumbBmp = Bitmap.createScaledBitmap(bmp!!, 150, 150, true)
        bmp.recycle()

        val msg = WXMediaMessage(webPage)
        msg.title = title
        msg.description = des
        msg.thumbData = WeChatUtil.bmpToByteArray(thumbBmp, true)

        val req = SendMessageToWX.Req()
        req.transaction = WeChatUtil.buildTransaction("webPage")
        req.message = msg
        req.scene = SendMessageToWX.Req.WXSceneSession

        TbsApp.getWeChatApi().sendReq(req)
    } else {
        loadBitmap(cover) {
            val bmp = it

            val thumbBmp = Bitmap.createScaledBitmap(bmp!!, 150, 150, true)
            try {
                bmp.recycle()
            } catch (e: java.lang.Exception) {

            }

            val msg = WXMediaMessage(webPage)
            msg.title = title
            msg.description = des
            msg.thumbData = WeChatUtil.bmpToByteArray(thumbBmp, true)

            val req = SendMessageToWX.Req()
            req.transaction = WeChatUtil.buildTransaction("webPage")
            req.message = msg
            req.scene = SendMessageToWX.Req.WXSceneTimeline

            TbsApp.getWeChatApi().sendReq(req)
        }
    }
}

/**
 * 分享图片至微信会话
 * @param bitmap Bitmap
 */
internal fun doShareImgSession(bitmap: Bitmap) {
    val imgObject = WXImageObject(bitmap)
    val thumbBmp = Bitmap.createScaledBitmap(bitmap, 150, 150, true)
    bitmap.recycle()
    val msg = WXMediaMessage(imgObject).apply {
        this.thumbData = WeChatUtil.bmpToByteArray(thumbBmp, true)
    }

    val req = SendMessageToWX.Req()
    req.transaction = WeChatUtil.buildTransaction("image")
    req.message = msg
    req.scene = SendMessageToWX.Req.WXSceneSession

    TbsApp.getWeChatApi().sendReq(req)
}

/**
 * 分享图片至微信会话
 * @param bitmap Bitmap
 */
internal fun doShareImgTimeline(bitmap: Bitmap) {
    val imgObject = WXImageObject(bitmap)
    val thumbBmp = Bitmap.createScaledBitmap(bitmap, 150, 150, true)
    bitmap.recycle()
    val msg = WXMediaMessage(imgObject).apply {
        this.thumbData = WeChatUtil.bmpToByteArray(thumbBmp, true)
    }

    val req = SendMessageToWX.Req()
    req.transaction = WeChatUtil.buildTransaction("image")
    req.message = msg
    req.scene = SendMessageToWX.Req.WXSceneTimeline

    TbsApp.getWeChatApi().sendReq(req)
}

fun loadBitmap(url: String, call: (Bitmap) -> Unit) {
    Glide.with(WLConfig.getContext())
        .asBitmap()
        .load(url)
        .listener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                call.invoke(ImageUtils.getBitmap(R.drawable.ic_logo))
                return true
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                call.invoke(resource!!)
                return true
            }
        })
        .preload()
}


/**
 * 显示抖动动画
 *
 * @param view
 */
fun startShakeAnim(view: View) {
    val mShakeAnim = ValueAnimator.ofFloat(
        0f, -1f,
        0f, 1f,
        0f, -1f,
        0f, 1f,
        0f, -1f,
        0f, 1f,
        0f, -1f,
        0f, 1f,
        0f, -1f,
        0f, 1f,
        0f
    )

    mShakeAnim?.duration = 550
    mShakeAnim?.addUpdateListener { animation ->
        val value = animation.animatedValue as Float
        view.translationX = ConvertUtils.dp2px(12f) * value
    }
    mShakeAnim?.interpolator = AccelerateDecelerateInterpolator()
    mShakeAnim?.start()
}

var lastClickTime = 0L

infix fun View?.doClick(doClick: ((v: View) -> Unit)?) {
    if (doClick != null) {
        this?.setOnClickListener {
            val current = Times.current()
            if (current - lastClickTime >= 400L) {
                lastClickTime = current

                doClick.invoke(it)
            }
        }
    } else {
        this?.setOnClickListener(null)
    }
}

infix fun View?.doubleClick(doDown: ((v: View) -> Unit)?) {
    if (doDown != null) {
        this?.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick(v: View) {
                doDown.invoke(v)
            }
        })
    } else this?.setOnClickListener(null)
}

inline val BaseViewHolder.V: View get() = this.itemView

fun runUiThread(run: () -> Unit) {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        run.invoke()
    } else {
        Handler(Looper.getMainLooper()).post(run)
    }
}

/**
 * 延时执行
 * @receiver Int
 * @param runDelay Function0<Unit>
 * @return Disposable
 */
internal infix fun Int.runDaley(runDelay: () -> Unit): Disposable {
    return Observable.timer(this.toLong(), TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            runDelay.invoke()
        }
}

fun MutableList<Long>.findNearVal(value: Long): Long {
    val positions = this

    if (positions.isNotEmpty()) {
        var low = 0
        var ind = 0
        var end = positions.lastIndex

        while (low <= end) {
            ind = (low + end).ushr(1) // safe from overflows
            val midVal = positions[ind]
            val cmp = midVal - value

            when {
                cmp < 0 -> low = ind + 1
                cmp > 0 -> end = ind - 1
                else -> return midVal
            }
        }

        val lowVal = positions.getOrNull(low) ?: return positions.last()
        val endVal = positions.getOrNull(end) ?: return positions.first()

        val isLeft = abs(lowVal - value) < abs(endVal - value)
        return if (isLeft) lowVal else endVal
    }
    return value
}

/**
 * 获取时间差并转换为xx天xx小时xx分钟xx秒
 * @param startTime Long
 * @param endTime Long
 * @return String
 */
fun getTimeDifference(startTime: Long, endTime: Long = Times.current()): String {
    return if (endTime >= startTime) {
        val diff = endTime - startTime
        val days =
            diff / (1000 * 60 * 60 * 24)
        val hours =
            (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        val minutes =
            (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60)
        val seconds =
            (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000

        "${days}天${hours}小时${minutes}分钟${seconds}秒"
    } else {
        "时间戳异常"
    }
}

fun getArticleItemTime(startTime: Long): String {
    val endTime = Times.current()

    if (endTime >= startTime) {
        val diff = endTime - startTime
        val days =
            diff / (1000 * 60 * 60 * 24)
        val hours =
            (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        val minutes =
            (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60)
        val seconds =
            (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000

        return when {
            days > 0 -> {
                DateFormat.date2yymmdd(startTime)
            }
            hours > 0 -> {
                "${hours}小时前更新"
            }
            minutes > 0 -> {
                "${minutes}分钟前更新"
            }
            else -> {
                "${seconds}秒前更新"
            }
        }
    } else {
        return "时间戳异常"
    }
}

fun getBossItemTime(startTime: Long): String {
    val endTime = Times.current()

    if (endTime >= startTime) {
        val diff = endTime - startTime
        val days =
            diff / (1000 * 60 * 60 * 24)
        val hours =
            (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        val minutes =
            (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60)
        val seconds =
            (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000

        return if (days > 0) {
            "${days}天前更新"
        } else if (hours > 0) {
            "最近${hours}小时更新"
        } else if (minutes > 0) {
            "${minutes}分钟前更新"
        } else {
            "${seconds}秒前更新"
        }
    } else {
        return "时间戳异常"
    }
}

/**
 * 获取时间差并转换为xx天xx小时xx分钟xx秒
 * @param startTime Long
 * @param endTime Long
 * @return String
 */
fun getTimeHasDifference(diff: Long): String {
    val days =
        diff / (1000 * 60 * 60 * 24)
    val hours =
        (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
    val minutes =
        (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60)
    val seconds =
        (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000

    return "${days}天${hours}小时${minutes}分钟${seconds}秒"
}

internal fun String?.fullUrl(): String {
    if (!this.isNullOrEmpty()) {
        if (!this.startsWith("http")) {
            return if (this.startsWith('/')) {
                WLConfig.getBaseUrl() + this.substring(1)
            } else {
                WLConfig.getBaseUrl() + this
            }
        }
    }
    return this ?: ""
}

internal fun String?.fullDownloadUrl(): String {
    if (!this.isNullOrEmpty()) {
        if (!this.startsWith("http")) {
            return if (this.startsWith('/')) {
                WLConfig.getDownUrl() + this.substring(1)
            } else {
                WLConfig.getDownUrl() + this
            }
        }
    }
    return this ?: ""
}

/**
 * 极光推送
 * @param title CharSequence
 * @param content CharSequence
 */
fun showSneaker(title: CharSequence, content: CharSequence, articleId: String) {
    val topActivity = ActStack.get().topActivity ?: return
    val sneaker = Sneaker.with(topActivity)

    val view = LayoutInflater.from(getContext())
        .inflate(R.layout.layout_push_message, sneaker.getView(), false)
    view.layout_card.layoutParams.width =
        ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(32f)

    GlideApp.displayHead(R.drawable.ic_logo, view.image_type)
    view.text_title.text = title
    view.text_content.text = content

    view.layout_card doClick {
        ArticleActivity.start(getContext(), articleId)
    }

    sneaker.setDuration(3500)
    sneaker.sneakCustom(view)
}

///**
// * 切换定位
// * @param mBMap BaiduMap
// */
//fun getMyLocate(mBMap: BaiduMap) {
//    val last = LocationManager.getLast()
//    if (last == LocationManager.Empty) {
//        Tools.toast("获取位置信息失败")
//    } else {
//        val newLatLng = MapStatusUpdateFactory.newLatLng(last)
//        mBMap.setMapStatus(newLatLng)
//
//    }
//}

//@SuppressLint("CheckResult")
//fun tryScanCar(mActivity: BaseCommonActivity, doConfirm: (carId: String) -> Unit) {
//    val flag = BaseScanProxy.SCAN_QR
//
//    ScanQrAndCNActivity.start(mActivity, flag)
//        .compose(mActivity.bindDestroy())
//        .subscribe { t ->
//            if (t.isSuccess) {
//                val name: String = t.name
//                doConfirm(name)
//            }
//        }
//}

internal inline fun <reified T> jsonToBean(jsonElement: JsonElement): T {
    return Gsons.getGson().fromJson(jsonElement, T::class.java)
}

/**
 * Returns a list containing all elements except first elements that satisfy the given [predicate].
 *
 */
internal inline fun <T> Iterable<T>.skipWhile(predicate: (T) -> Boolean): List<T> {
    var yielding = false
    val list = ArrayList<T>()
    for (item in this)
        if (yielding)
            list.add(item)
        else if (predicate(item)) {
            list.add(item)
            yielding = true
        }
    return list
}

fun numberAdd0(number: Int): String {
    return when {
        number <= 0 -> {
            "00"
        }
        number < 10 -> {
            "0$number"
        }
        else -> {
            number.toString()
        }
    }
}

internal fun secondsToMS(seconds: Int): String {
    val minute = seconds / 60
    val second = seconds % 60

    return "${numberAdd0(minute)}:${numberAdd0(second)}"
}

/**
 * 创建二维码
 */
fun createQrCode(
    resources: Resources,
    content: String,
    @DrawableRes centerImage: Int
): Bitmap? {
    val center = BitmapFactory.decodeResource(resources, centerImage)
    return ZXingUtils.createQRImage(content, 480, 480, center)
}

/**
 * 创建二维码
 */
fun createQrCode(
    resources: Resources,
    content: String
): Bitmap? {
    return ZXingUtils.createQRImage(content, 480, 480, null)
}

/**
 * 创建二维码
 */
fun createQrCodeWith(
    resources: Resources,
    content: String,
    @DrawableRes centerImage: Int
): Bitmap? {
    val center = BitmapFactory.decodeResource(resources, centerImage)
    return ZXingUtils.createQRImage(content, 480, 480, center)
}

fun jumpSysShare(context: Context, content: String) {
    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, content)
//    shareIntent = Intent.createChooser(shareIntent, "Here is the title of Select box")
    context.startActivity(shareIntent)
}

internal fun isSameDay(time: Long): Boolean {
    val last = Calendar.getInstance()
    last.timeInMillis = time
    val current = Calendar.getInstance()
    current.timeInMillis = Times.current()

    return last.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)
            && last.get(Calendar.YEAR) == current.get(Calendar.YEAR)
}

fun inThreeDayTime(time: Long): Boolean {
    val end = Times.current()
    val sta = end - 3L * DateFormat.DAY_1
    return time in sta..end
}