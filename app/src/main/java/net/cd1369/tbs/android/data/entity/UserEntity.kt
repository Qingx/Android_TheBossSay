package net.cd1369.tbs.android.data.entity

import java.io.Serializable

data class UserEntity(
    val avatar: String, //头像
    val deviceId: String, //设备id
    val id: String,  //id
    var nickName: String, //账号昵称
    var phone: String, //手机号
    var collectNum: Int? = 0, //收藏数
    var readNum: Int? = 0, //今日阅读
    var traceNum: Int? = 0, //追踪数
    val type: String, //0->游客 1->正式
    val wxHead: String,
    val wxName: String
) : Serializable {
    companion object {
        val empty: UserEntity = UserEntity("", "", "", "", "", 0, 0, 0, "0", "", "")
    }
}