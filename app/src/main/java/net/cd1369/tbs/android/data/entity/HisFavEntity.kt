package net.cd1369.tbs.android.data.entity

import com.chad.library.adapter.base.entity.MultiItemEntity

data class HisFavEntity(
    val id: String,
    val articleId: String,
    val content: String,
    val bossHead: String,
    val bossName: String,
    val createTime: Long,
    val articleType: String, //1:言论 2:资讯
    val type: String, //1:文章 2:每日一言
    val hidden: Boolean,
    val bossRole: String,
    var isCollect: Boolean,
    var isPoint: Boolean,
    val cover: String
) : MultiItemEntity {
    fun toDaily(): DailyEntity {
        return DailyEntity(
            articleId, bossHead, bossName, bossRole, content, createTime, isCollect, isPoint
        )
    }

    override fun getItemType(): Int {
        return type.toInt()
    }

    override fun toString(): String {
        return "HisFavEntity(id='$id', articleId='$articleId', content='$content', bossHead='$bossHead', bossName='$bossName', createTime=$createTime, articleType='$articleType', type='$type', hidden=$hidden, bossRole='$bossRole', isCollect=$isCollect, isPoint=$isPoint, cover='$cover')"
    }
}