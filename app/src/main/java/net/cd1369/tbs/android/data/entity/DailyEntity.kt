package net.cd1369.tbs.android.data.entity

import java.io.Serializable

/**
 * Created by Xiang on 2021/10/21 10:54
 * @description
 * @email Cymbidium@outlook.com
 */
data class DailyEntity(
    val id: String,
    val bossHead: String,
    val bossName: String,
    val bossRole: String,
    val content: String,
    val createTime: Long,
    var isCollect: Boolean,
    var isPoint: Boolean
) : Serializable