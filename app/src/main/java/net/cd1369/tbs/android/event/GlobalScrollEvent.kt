package net.cd1369.tbs.android.event

import net.cd1369.tbs.android.config.PageItem

/**
 * Created by Xiang on 2021/8/27 17:59
 * @description
 * @email Cymbidium@outlook.com
 */
class GlobalScrollEvent {
    companion object {
        var talkPage = PageItem.Tack.code
        var lastPage = PageItem.Tack.code
        var homePage = PageItem.Talk.code
        var tackLabel = "-1"
        var squareLabel = "-1"
        var bossLabel = "-1"
    }
}