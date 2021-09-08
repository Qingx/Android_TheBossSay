package net.cd1369.tbs.android.config


/**
 * 仿三项运算
 * @receiver Boolean
 * @param trueVal T
 * @param falseVal T
 * @return T
 */
fun <T> Boolean.elif(trueVal: T, falseVal: T): T = if (this) trueVal else falseVal

/**
 * 仿三项运算, 延迟加载
 * @receiver Boolean
 * @param trueVal T
 * @param falseVal T
 * @return T
 */
fun <T> Boolean.elif(trueCall: () -> T, falseCall: () -> T): T =
    if (this) trueCall.invoke() else falseCall.invoke()