package net.cd1369.tbs.android.data.entity

import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

data class ArticleEntity(
    val bossId: String,
    val bossVO: BossInfoEntity,
    val collect: Int? = 0,
    val content: String,
    val createTime: Long,
    val descContent: String,
    val files: List<String>? = listOf(),
    val id: String,
    var isCollect: Boolean? = false,
    val isPoint: Boolean? = false,
    val point: Int? = 0,
    val status: Int,
    val title: String
) : MultiItemEntity, Serializable {
    override fun getItemType(): Int {
        return when {
            files.isNullOrEmpty() -> {
                0
            }
            files.size <= 2 -> {
                1
            }
            else -> {
                2
            }
        }
    }

    companion object {
        var empty = ArticleEntity(
            "",
            BossInfoEntity.empty,
            0,
            "",
            0,
            "",
            mutableListOf(),
            "",
            false,
            false,
            0,
            0,
            ""
        )
    }
}