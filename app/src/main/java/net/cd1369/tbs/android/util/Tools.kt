package net.cd1369.tbs.android.util

import android.animation.ValueAnimator
import android.app.Dialog
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
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import cn.wl.android.lib.config.WLConfig
import cn.wl.android.lib.utils.Gsons
import cn.wl.android.lib.utils.Times
import cn.wl.android.lib.utils.Toasts
import com.blankj.utilcode.util.ConvertUtils
import com.chad.library.adapter.base.BaseViewHolder
import com.google.gson.JsonElement
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs

/**
 * Created by Xiang on 2020/7/3 12:28
 *
 * @email Cymbidium@outlook.com
 */
object Tools {

    fun createTempId(): String {
        return "temp${UUID.randomUUID().leastSignificantBits}".replace("-", "")
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
    fun countDown(time: Int, dialog: Dialog?, doDown: (int: Int) -> Unit): Disposable {
        return Observable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { t: Long -> (time - t).toInt() }
            .takeUntil { t: Int -> t <= 0 }
            .subscribe {
                doDown(it)
                if (it <= 0) {
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

fun getUpdateTime(startTime: Long): String {
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

internal fun String.avatar(): String {
    if (!this.startsWith("http")) {
        return if (this.startsWith('/')) {
            WLConfig.getDownUrl() + this.substring(1)
        } else {
            WLConfig.getDownUrl() + this
        }
    }
    return this
}

///**
// * 极光推送
// * @param type String
// * @param title CharSequence
// * @param content CharSequence
// */
//fun showSneaker(type: String, title: CharSequence, content: CharSequence) {
//    val topActivity = ActStack.get().topActivity ?: return
//    val sneaker = Sneaker.with(topActivity)
//
//    val view = LayoutInflater.from(getContext())
//        .inflate(R.layout.layout_push_message, sneaker.getView(), false)
//    view.layout_card.layoutParams.width =
//        ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(32f)
//
//    GlideApp.displayHead(PushMessage.get(type).icon, view.image_type)
//    view.text_title.text = title
//    view.text_content.text = content
//
//    view.layout_card doClick {
//        MessageCenterActivity.start(getContext())
//    }
//
//    sneaker.setDuration(3500)
//    sneaker.sneakCustom(view)
//}

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