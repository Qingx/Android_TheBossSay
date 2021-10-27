package net.cd1369.tbs.android.data.entity

import java.io.Serializable

/**
 * Created by Xiang on 2021/10/26 11:31
 * @description
 * @email Cymbidium@outlook.com
 */
data class FolderEntity(
    val id: String,
    val name: String,
    val cover: String,
    var articleCount: Int,
    var bossCount: Int,
    val createTime: Long
):Serializable