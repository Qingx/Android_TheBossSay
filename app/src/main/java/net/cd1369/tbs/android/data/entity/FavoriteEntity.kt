package net.cd1369.tbs.android.data.entity

data class FavoriteEntity(
    val count: Int,
    val createTime: Int,
    val id: String,
    var list: List<ArticleEntity>? = mutableListOf<ArticleEntity>(),
    val name: String,
    val userId: String
)