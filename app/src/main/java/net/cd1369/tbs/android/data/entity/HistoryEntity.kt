package net.cd1369.tbs.android.data.entity

/**
 * Created by Xiang on 2021/7/14 17:38
 * @description
 * @email Cymbidium@outlook.com
 */
data class HistoryEntity(
    val articleId: String,
    val articleTitle: String,
    val bossHead: String,
    val bossName: String,
    val id: String,
    val updateTime: Long,
    val hidden: Boolean
)