package net.cd1369.tbs.android.event

/**
 * Created by Xiang on 2021/8/28 14:42
 * @description
 * @email Cymbidium@outlook.com
 */
class ArticleCollectEvent(
    val articleId: String = "",
    val doCollect: Boolean = false,
    val fromFolder: Boolean = false, //来自我的收藏列表
    val fromCollect: Boolean = false, //来自收藏夹列表
)