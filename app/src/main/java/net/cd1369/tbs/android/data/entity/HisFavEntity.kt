package net.cd1369.tbs.android.data.entity

data class HisFavEntity(
    val id: String,
    val articleId: String,
    val content: String,
    val bossHead: String,
    val bossName: String,
    val createTime: Long,
    val articleType: String,
    val type: String,
    val hidden: Boolean,
    val bossRole: String,
    val isCollect: Boolean,
    val isPoint: Boolean
) {
    fun toDaily(): DailyEntity {
        return DailyEntity(
            articleId, bossHead, bossName, bossRole, content, createTime, isCollect, isPoint
        )
    }

    override fun toString(): String {
        return "HisFavEntity(id='$id', articleId='$articleId', content='$content', bossHead='$bossHead', bossName='$bossName', createTime=$createTime, articleType='$articleType', type='$type', hidden=$hidden, bossRole='$bossRole', isCollect=$isCollect, isPoint=$isPoint)"
    }
}