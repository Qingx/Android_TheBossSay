package net.cd1369.tbs.android.data.entity

data class BossLabelEntity(
    val createTime: Int,
    val id: String,
    val keyValue: String,
    val name: String,
    val parentId: String,
    val sort: Int,
    val type: Int
) {
    companion object {
        val empty = BossLabelEntity(0, "-1", "", "", "", 0, 0)
    }
}