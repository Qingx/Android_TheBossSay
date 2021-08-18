package net.cd1369.tbs.android.config

import androidx.annotation.DrawableRes
import net.cd1369.tbs.android.R

/**
 * Created by Qing on 2021/7/5 10:47 上午
 * @description
 * @email Cymbidium@outlook.com
 */
enum class BossLabelItem(val code: String, val itemName: String, @DrawableRes val icon: Int) {
    Hot("hotBoss", "热门", R.drawable.ic_boss_label_hot),
    New("newBoss", "最新", R.drawable.ic_boss_label_new),
    Empty("without", "无标签", 0);

    companion object {
        fun get(code: String): BossLabelItem {
            val values = values()

            for (value: BossLabelItem in values) {
                if (value.code == code) {
                    return value
                }
            }
            return Empty
        }
    }
}

enum class MineItem(val code: String, val itemName: String, @DrawableRes val icon: Int) {
    Favorite("0", "我的收藏", R.drawable.ic_mine_favorite),
    History("1", "阅读记录", R.drawable.ic_mine_history),
    Share("2", "推荐给好友", R.drawable.ic_mine_share),
    About("3", "关于boss说", R.drawable.ic_mine_about),
    Contact("4", "联系我们", R.drawable.ic_mine_contact),

    //    Score("5", "给app评分", R.drawable.ic_mine_score)
//    Clear("6", "清除缓存", R.drawable.ic_mine_clear)
    HuangLi("7", "黄历日程", R.drawable.ic_mine_huangli),
    XingZuo("8", "星座运势", R.drawable.ic_mine_xingzuo);

    companion object {
        fun get(code: String): MineItem {
            val values = values()

            for (value: MineItem in values) {
                if (value.code == code) {
                    return value
                }
            }
            return Favorite
        }
    }
}

enum class ContactItem(val code: Int, val itemName: String, @DrawableRes val icon: Int) {
    Contact0(0, "某篇文章冒犯了您的权益或涉及侵权", R.drawable.ic_contact0),
    Contact1(1, "期望新增更多的BOSS", R.drawable.ic_contact1),
    Contact2(2, "给APP提建议", R.drawable.ic_contact2),
    Contact3(3, "更多合作", R.drawable.ic_contact3);

    companion object {
        fun get(code: Int): ContactItem {
            val values = values()

            for (value: ContactItem in values) {
                if (value.code == code) {
                    return value
                }
            }
            return Contact0
        }
    }
}