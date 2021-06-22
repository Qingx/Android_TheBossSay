package net.cd1369.tbs.android.config

import net.cd1369.tbs.android.data.repository.UserRepository


object TbsApi {
    fun user(): UserRepository {
        return UserHolder.user
    }
    internal object UserHolder {
        val user: UserRepository = UserRepository()
    }
}
