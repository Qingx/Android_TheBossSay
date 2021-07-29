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
            isAD() -> AD_TYPE
            files.isNullOrEmpty() -> 0
            files.size <= 2 -> 1
            else -> 2
        }
    }

    /**
     * 判断当前item是否为广告
     * @return Boolean
     */
    fun isAD() = AD_ID == id

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
            isCollect = false,
            isPoint = false,
            point = 0,
            status = 0,
            title = ""
        )

        const val AD_TYPE = 100
        const val AD_ID = "AD_ID"

        var tempAD = ArticleEntity(
            "",
            BossInfoEntity.empty,
            0,
            "",
            0,
            "",
            mutableListOf(),
            AD_ID,
            isCollect = false,
            isPoint = false,
            point = 0,
            status = 0,
            title = ""
        )
    }

}