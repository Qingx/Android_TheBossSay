package net.cd1369.tbs.android.data.entity

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * Created by Qing on 2021/6/30 9:06 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class TestMultiEntity(
    val content: Int
) : MultiItemEntity {

    override fun getItemType(): Int {
        return if (content % 2 == 0) 0 else 1
    }
}