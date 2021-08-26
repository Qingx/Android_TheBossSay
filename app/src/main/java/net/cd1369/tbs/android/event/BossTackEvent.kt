package net.cd1369.tbs.android.event

/**
 * Created by Xiang on 2021/8/26 21:40
 * @description
 * @email Cymbidium@outlook.com
 */
class BossTackEvent(
    val id: String,
    val isFollow: Boolean,
    val labels: MutableList<String>,
    val fromBossContent: Boolean = false
)