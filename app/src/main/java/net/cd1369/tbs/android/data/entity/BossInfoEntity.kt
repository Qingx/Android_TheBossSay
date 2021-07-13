package net.cd1369.tbs.android.data.entity

data class BossInfoEntity(
    val collect: Int,
    val createTime: Long,
    val date: Int,
    val head: String,
    val id: String,
    val info: String,
    val isCollect: Boolean,
    val isPoint: Boolean,
    val name: String,
    val point: Int,
    val readCount: Int,
    val role: String,
    val totalCount: Int,
    val updateCount: Int,
    val updateTime: Long
)