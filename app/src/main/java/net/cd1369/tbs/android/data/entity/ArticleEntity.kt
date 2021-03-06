package net.cd1369.tbs.android.data.entity

import com.chad.library.adapter.base.entity.MultiItemEntity
import net.cd1369.tbs.android.util.Tools
import net.cd1369.tbs.android.util.inThreeDayTime
import java.io.Serializable

data class ArticleEntity(
    val bossId: String,
    val bossVO: BossInfoEntity,
    val collect: Int? = 0,
    val readCount: Int? = 0,
    val content: String,
    val createTime: Long?,
    val articleTime: Long?,
    val descContent: String,
    val files: List<String>? = listOf(),
    val id: String,
    var isCollect: Boolean? = false,
    var isPoint: Boolean? = false,
    var point: Int = 0,
    val status: Int,
    val title: String,
    val originLink: String = "暂无",
    var isRead: Boolean = false,
    val hidden: Boolean,
    var releaseTime: Long?,
    var filterType: String?
) : MultiItemEntity, Serializable {

    val isInThreeDay: Boolean
        get() = inThreeDayTime(releaseTime ?: articleTime ?: 0L)

    val isMsgType: Boolean
        get() {
            return isInThreeDay && (filterType?.let { it == "1" } ?: false)
        }

    val isTalkType: Boolean
        get() {
            return isInThreeDay && (filterType?.let { it == "2" } ?: false)
        }

    fun getTime(): Long = releaseTime ?: articleTime ?: 0L

    override fun getItemType(): Int {
        return when {
            isAD() -> AD_TYPE
            files.isNullOrEmpty() -> 0
            else -> 1
        }
    }

    /**
     * 判断当前item是否为广告
     * @return Boolean
     */
    fun isAD() = AD_ID == id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArticleEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        var empty = ArticleEntity(
            "",
            BossInfoEntity.empty,
            0,
            0,
            "",
            0,
            0,
            "",
            mutableListOf(),
            "",
            isCollect = false,
            isPoint = false,
            point = 0,
            status = 0,
            title = "",
            originLink = "",
            isRead = false,
            hidden = false,
            filterType = "",
            releaseTime = 0
        )

        const val AD_TYPE = 100
        const val AD_ID = "AD_ID"

        var tempAD = ArticleEntity(
            "",
            BossInfoEntity.empty,
            0,
            0,
            "",
            0,
            0,
            "",
            mutableListOf(),
            AD_ID,
            isCollect = false,
            isPoint = false,
            point = 0,
            status = 0,
            title = "",
            originLink = "",
            isRead = false,
            hidden = false,
            filterType = "",
            releaseTime = 0,
        )
    }

}