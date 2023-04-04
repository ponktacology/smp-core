package gg.traphouse.core.staff

import gg.traphouse.core.SyncCatcher
import gg.traphouse.core.TaskDispatcher
import gg.traphouse.core.UUIDCache
import gg.traphouse.core.player.PlayerNotFoundInCacheException
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.update
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class StaffSettingsRepository : UUIDCache, KoinComponent {

    private val database: Database by inject()
    private val Database.staffSettings get() = this.sequenceOf(StaffSettingsTable)
    private val cache = ConcurrentHashMap<UUID, StaffSettings>()

    fun getByOnlinePlayer(player: Player) = cache[player.uniqueId] ?: throw PlayerNotFoundInCacheException()

    override fun loadCache(uuid: UUID) {
        SyncCatcher.verify()

        val remoteStaffSettings = database.staffSettings.find { it.player eq uuid } ?: RemoteStaffSettings {
            this.player = uuid
            this.vanish = false
            this.god = false
            this.fly = false
        }.also {
            database.staffSettings.add(it)
        }

        cache[uuid] = remoteStaffSettings.toDomain()
    }

    override fun verifyCache(uuid: UUID) = cache.containsKey(uuid)

    override fun flushCache(uuid: UUID) {
        val settings = cache.remove(uuid) ?: return

        TaskDispatcher.dispatchAsync {
            database.staffSettings.update(settings.toRemote())
        }
    }

    fun flushCache() {
        cache.clear()
    }
}
