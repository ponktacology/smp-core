package me.smp.core.staff

import me.smp.core.SyncCatcher
import me.smp.core.UUIDCache
import me.smp.core.player.PlayerNotFoundInCacheException
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
        cache[uuid] = database.staffSettings.find { it.player eq uuid } ?: StaffSettings {
            this.player = uuid
            this.vanish = false
            this.god = false
            this.fly = false
        }.also {
            database.staffSettings.add(it)
        }
    }

    override fun verifyCache(uuid: UUID) = cache.containsKey(uuid)

    override fun flushCache(uuid: UUID) {
        cache.remove(uuid)
    }

    fun flushCache() {
        cache.clear()
    }

    fun updateSettings(settings: StaffSettings) {
        database.staffSettings.update(settings)
    }
}
