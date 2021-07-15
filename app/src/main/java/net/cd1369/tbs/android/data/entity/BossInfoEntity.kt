package net.cd1369.tbs.android.data.entity

import java.io.Serializable

data class BossInfoEntity(
    val collect: Int? = 0,
    val createTime: Long,
    val date: Int,
    val head: String,
    val id: String,
    val info: String,
    var isCollect: Boolean,
    val isPoint: Boolean,
    val name: String,
    val point: Int? = 0,
    val readCount: Int,
    val role: String,
    val totalCount: Int? = 0,
    val updateCount: Int? = 0,
    val updateTime: Long
) : Serializable