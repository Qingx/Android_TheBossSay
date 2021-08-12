package net.cd1369.tbs.android.data.entity

data class VersionEntity(
    val createTime: Int,
    val fileUrl: String,
    val forcedUpdating: Boolean,
    val id: String,
    val lowestVersion: Boolean,
    val versions: String
)