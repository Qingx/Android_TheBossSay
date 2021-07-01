package net.cd1369.tbs.android.data.entity

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * Created by Qing on 2021/6/30 9:06 上午
 * @description
 * @email Cymbidium@outlook.com
 */
class TestMultiEntity(
    val code: Int,
    val content: Int
) : MultiItemEntity {

    override fun getItemType(): Int {
        return if (code == 0) {
            when {
                content % 5 == 0 -> {
                    2
                }
                content % 2 == 0 -> {
                    0
                }
                else -> 1
            }
        } else {
            when {
                content % 2 == 0 -> 0
                else -> 1
            }
        }
    }
}