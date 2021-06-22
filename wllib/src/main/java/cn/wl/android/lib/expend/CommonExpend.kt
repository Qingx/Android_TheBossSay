package cn.wl.android.lib.expend

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import cn.wl.android.lib.config.WLConfig
import com.blankj.utilcode.util.ConvertUtils

inline fun Number.dp2px() = ConvertUtils.dp2px(this.toFloat())

inline fun Number.px2dp() = ConvertUtils.px2dp(this.toFloat())

inline fun wlColor(@ColorRes colorRes: Int) = WLConfig.getContext().resources.getColor(colorRes)

inline fun wlString(@StringRes stringRes: Int) =
    WLConfig.getContext().resources.getString(stringRes)