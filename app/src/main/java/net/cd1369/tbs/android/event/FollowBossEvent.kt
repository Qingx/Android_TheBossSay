package net.cd1369.tbs.android.event

/**
 * Created by Xiang on 2021/7/14 14:31
 * @description
 * @email Cymbidium@outlook.com
 */
class FollowBossEvent(
    val id: String? = "",
    val isFollow: Boolean = false,
    val needLoading: Boolean = true,
)