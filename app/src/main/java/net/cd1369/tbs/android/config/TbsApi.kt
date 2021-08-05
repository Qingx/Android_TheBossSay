package net.cd1369.tbs.android.config

import net.cd1369.tbs.android.data.repository.BossRepository
import net.cd1369.tbs.android.data.repository.UserRepository


object TbsApi {
    fun user(): UserRepository {
        return TbsHolder.user
    }

    fun boss(): BossRepository {
        return TbsHolder.boss
    }

    internal object TbsHolder {
        val user: UserRepository = UserRepository()
        val boss: BossRepository = BossRepository()
    }

    val globalRefresh by lazy {
        user().obtainRefreshUser()
            .publish()
            .refCount()
    }
}
